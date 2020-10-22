package com.kclm.xsap.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.MemberLogDTO;
import com.kclm.xsap.dto.convert.MemberLogConvert;
import com.kclm.xsap.entity.CardCourseRecord;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TMemberLog;
import com.kclm.xsap.entity.TRechargeRecord;
import com.kclm.xsap.mapper.TConsumeRecordMapper;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberLogMapper;
import com.kclm.xsap.mapper.TRechargeRecordMapper;
import com.kclm.xsap.service.MemberCardService;

@Service
@Transactional
public class MemberCardServiceImpl implements MemberCardService{

//	@Autowired
//	private MemberLogConvert memberLogConvert;

	@Autowired
	TMemberCardMapper cardMapper;
	
	@Override
	public boolean save(TMemberCard card) {
		//检查新增会员卡的名字
		String nameCheck = card.getName().trim();
		TMemberCard findOne = cardMapper.selectOne(new QueryWrapper<TMemberCard>().eq("name", nameCheck));
		//若新增的会员卡名称存在于库中，不录入
		if(findOne != null) {			
			System.out.println("-----同名的会员卡已存在！");
			return false;
		}
		
		//录入
		cardMapper.insert(card);
		
		//中间表插入数据
		List<TCourse> courseList = card.getCourseList();
		if(courseList !=null && courseList.size() > 0) {			
			for (int i = 0; i < courseList.size(); i++) {
				TCourse course = courseList.get(i);
				CardCourseRecord result = cardMapper.findBindCourse(card.getId(), course.getId());
				if(result != null) {
					System.out.println("卡号："+ card.getId() +"----已经绑定过"+ course.getId() +"-----");
					continue ;
				}
				System.out.println("卡号："+ card.getId() + "----新增绑定"+ course.getId() +"-----");
				cardMapper.insertMix(card.getId(), course.getId());
			}
		}
		
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		TMemberCard card = cardMapper.selectById(id);
		if(card == null) {
			System.out.println("---------此条会员卡信息不存在");
			return false;
		}
		//中间表删除关联的数据
		cardMapper.deleteBindCourse(id);
		
		//本表
		cardMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TMemberCard card) {		
		cardMapper.updateById(card);
		//更新中间表: 先删除已绑定的，再重新绑定
		cardMapper.deleteBindCourse(card.getId());
		//向中间表插入数据
		List<TCourse> courseList = card.getCourseList();
		
		if(courseList !=null && courseList.size() > 0) {
			for (TCourse course : courseList) {
				cardMapper.insertMix(card.getId(), course.getId());
			}
		}
		
		return true;
	}

	@Override
	public List<TMemberCard> findAll() {
		List<TMemberCard> cardList = cardMapper.selectList(null);
		return cardList;
	}

	@Override
	public List<TMemberCard> findAllByPage(Integer currentPage, Integer pageSize) {
		IPage<TMemberCard> page = new Page<>(currentPage,pageSize);
		IPage<TMemberCard> pageList = cardMapper.selectPage(page, null);
		List<TMemberCard> cardList = pageList.getRecords();
		return cardList;
	}

	@Autowired
	TRechargeRecordMapper rechargeMapper;
	
	@Autowired
	TConsumeRecordMapper consumeMapper;
	
	@Autowired
	TMemberLogMapper logMapper;
	
	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	//进行充值
	@Override
	public boolean recharge(TRechargeRecord recharge) {
		rechargeMapper.insert(recharge);
		
		if(recharge != null) {
			//把充值记录存入到操作记录表
			TMemberLog memberLog = new TMemberLog();
			memberLog.setMemberId(recharge.getMemberId());
			memberLog.setCardId(recharge.getCardId());
			memberLog.setType("充值");
			memberLog.setInvolveMoney(recharge.getReceivedMoney());
			memberLog.setOperator(recharge.getOperator());
			logMapper.insert(memberLog);
			//充值操作
			TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					.eq("card_id", recharge.getCardId()).eq("member_id", recharge.getMemberId()));
			if(bindRecord != null) {
				bindRecord.setValidCount(bindRecord.getValidCount() + recharge.getAddCount());
				bindRecord.setValidDay(bindRecord.getValidDay() + recharge.getAddDay());
				bindRecord.setReceivedMoney(bindRecord.getReceivedMoney().add(recharge.getReceivedMoney()));
				bindMapper.update(bindRecord, new QueryWrapper<TMemberBindRecord>()
						.eq("card_id", recharge.getCardId()).eq("member_id", recharge.getMemberId()));			
			}
			return true;
		}
		
		return false;
	}

	//进行消费
	@Override
	public boolean consume(TConsumeRecord consume) {
		if(consume != null) {
			//查出会员卡的次数单价，取值四舍五入
			TMemberCard card = cardMapper.selectById(consume.getCardId());
			BigDecimal price = new BigDecimal(card.getPrice().toString());
			BigDecimal count = new BigDecimal(card.getTotalCount().toString());
			BigDecimal unitPrice = price.divide(count, 2, RoundingMode.HALF_UP);
			//消费的次数
			BigDecimal countCost = new BigDecimal(consume.getCardCountChange().toString());
			
			consume.setMoneyCost(unitPrice.multiply(countCost));
			if(consume.getCardCountChange() == 0) {
				consume.setMoneyCost(BigDecimal.ZERO);
			}
			consumeMapper.insert(consume);
			
			//把消费记录存入到操作记录表
			TMemberLog memberLog = new TMemberLog();
			memberLog.setMemberId(consume.getMemberId());
			memberLog.setCardId(consume.getCardId());
			memberLog.setType(consume.getOperateType());
			memberLog.setInvolveMoney(consume.getMoneyCost());
			memberLog.setOperator(consume.getOperator());
			logMapper.insert(memberLog);
			//消费操作
			TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					.eq("card_id", consume.getCardId()).eq("member_id", consume.getMemberId()));
			Integer validCount = bindRecord.getValidCount() - consume.getCardCountChange();
			if(validCount < 0) {
				validCount = 0;
			}
			bindRecord.setValidCount(validCount);
			Integer validDay = bindRecord.getValidDay() - consume.getCardDayChange();
			if(validDay < 0) {
				validDay = 0;
			}
			bindRecord.setValidDay(validDay);
			bindRecord.setReceivedMoney(bindRecord.getReceivedMoney().subtract(consume.getMoneyCost()));
			bindMapper.update(bindRecord, new QueryWrapper<TMemberBindRecord>()
					.eq("card_id", consume.getCardId()).eq("member_id", consume.getMemberId()));
			return false;
		}
		return true;
	}

	//查询操作记录
	@Override
	public List<MemberLogDTO> listOperateLog(Long memberId,Long cardId) {
		TMemberCard memberCard = cardMapper.selectById(cardId);
		
		String note = "待补充";
		if(memberCard != null) {
			//会员卡备注
			note = memberCard.getNote();
		}
		
		//会员卡可用次数
		TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
				.eq("member_id", memberId).eq("card_id", cardId));
		Integer validTimes = 0;
		LocalDateTime endTime = null;
		if(bindRecord != null) {
			validTimes = bindRecord.getValidCount();
			//会员卡到期日
			endTime = bindRecord.getCreateTime();
			if(endTime != null) {
				endTime = endTime.plusDays(bindRecord.getValidDay());			
			}
		}
		
		//获取到会员指定的会员卡的操作记录
		List<TMemberLog> logList = logMapper.selectList(new QueryWrapper<TMemberLog>()
				.eq("member_id", memberId).eq("card_id", cardId));
		List<MemberLogDTO> logDtoList = new ArrayList<>();
		if(logList == null || logList.size() < 1) {
			System.out.println("-------当前用户的此张卡，无任何操作记录");
			return null;
		}
		for (TMemberLog log : logList) {
			//===========dto存储
			MemberLogDTO logDto = new MemberLogDTO();
			logDto.setId(log.getId());
			logDto.setOperateTime(log.getCreateTime());
			logDto.setOperateType(log.getType());
			logDto.setValidTimes(validTimes);
			logDto.setEndToDate(endTime);
			logDto.setInvolveMoney(log.getInvolveMoney());
			logDto.setOperator(log.getOperator());
			logDto.setCardNote(note);
			logDto.setStatus(bindRecord.getActiveStatus());
			//存到dto中
			logDtoList.add(logDto);
		}	
		
		return logDtoList;
	}

	//查询指定会员的绑卡信息
	@Override
	public TMemberBindRecord findBindRecord(Long memberId, Long cardId) {
		TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>().eq("member_id", memberId)
				.eq("card_id", cardId));
		return bindRecord;
	}

	//通过id查询单条记录
	@Override
	public TMemberCard findById(Long id) {
		TMemberCard card = cardMapper.selectById(id);
		return card;
	}

	//	通过name查找会员卡信息
	@Override
	public TMemberCard findByName(String name) {
		TMemberCard findOne = cardMapper.selectOne(new QueryWrapper<TMemberCard>().eq("name", name));
		return findOne;
	}

	//找到某个课程绑定的所有会员卡信息
	@Override
	public List<TMemberCard> listByCourseId(Long courseId) {
		List<TMemberCard> cardList = cardMapper.selectList(new QueryWrapper<TMemberCard>()
				.inSql("id","select card_id from t_course_card where course_id =" + courseId));
		return cardList;
	}

	//	更新会员绑卡记录	
	@Override
	public boolean updateBindRecord(TMemberBindRecord bind) {
		bindMapper.updateById(bind);
		return true;
	}

}
