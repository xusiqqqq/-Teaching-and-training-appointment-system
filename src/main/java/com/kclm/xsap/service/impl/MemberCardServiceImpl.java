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
import com.kclm.xsap.entity.TConsumeRecord;
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

	@Autowired
	TMemberCardMapper cardMapper;
	
	@Override
	public boolean save(TMemberCard card) {
		cardMapper.insert(card);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		cardMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TMemberCard card) {
		cardMapper.updateById(card);
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
		bindRecord.setValidCount(bindRecord.getValidCount() + recharge.getAddCount());
		bindRecord.setValidDay(bindRecord.getValidDay() + recharge.getAddDay());
		bindRecord.setReceivedMoney(recharge.getReceivedMoney());
		bindMapper.update(bindRecord, null);
		return true;
	}

	//进行消费
	@Override
	public boolean consume(TConsumeRecord consume) {
		//查出会员卡的次数单价，取值四舍五入
		TMemberCard card = cardMapper.selectById(consume.getCardId());
		BigDecimal price = new BigDecimal(card.getPrice().toString());
		BigDecimal count = new BigDecimal(card.getTotalCount().toString());
		BigDecimal unitPrice = price.divide(count, 2, RoundingMode.HALF_UP);
		//消费的次数
		BigDecimal countCost = new BigDecimal(consume.getCardCountChange().toString());
		
		consume.setMoneyCost(unitPrice.multiply(countCost));
		consumeMapper.insert(consume);
		
		//把消费记录存入到操作记录表
		TMemberLog memberLog = new TMemberLog();
		memberLog.setMemberId(consume.getMemberId());
		memberLog.setCardId(consume.getCardId());
		memberLog.setType("消费");
		memberLog.setInvolveMoney(consume.getMoneyCost());
		memberLog.setOperator(consume.getOperator());
		logMapper.insert(memberLog);
		//消费操作
		TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
				.eq("card_id", consume.getCardId()).eq("member_id", consume.getMemberId()));
		bindRecord.setValidCount(bindRecord.getValidCount() + consume.getCardCountChange());
		bindRecord.setValidDay(bindRecord.getValidDay() + consume.getCardDayChange());
		bindRecord.setReceivedMoney(consume.getMoneyCost());
		bindMapper.update(bindRecord, null);
		return true;
	}

	//查询操作记录
	@Override
	public List<MemberLogDTO> listOperateLog(Long memberId,Long cardId) {
		MemberLogDTO logDto = new MemberLogDTO();
		
		TMemberCard memberCard = cardMapper.selectById(cardId);
		//会员卡的激活状态
		Integer status = memberCard.getStatus();
		
		//会员卡备注
		String note = memberCard.getNote();
		
		//会员卡可用次数
		TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
				.eq("member_id", memberId).eq("card_id", cardId));
		Integer validTimes = bindRecord.getValidCount();
		
		//会员卡到期日
		LocalDateTime endTime = bindRecord.getCreateTime();
		endTime = endTime.plusDays(bindRecord.getValidDay());
		
		//获取到会员指定的会员卡的操作记录
		List<TMemberLog> logList = logMapper.selectList(new QueryWrapper<TMemberLog>()
				.eq("member_id", memberId).eq("card_id", cardId));
		List<MemberLogDTO> logDtoList = new ArrayList<>();
		for (TMemberLog log : logList) {
			//dto转换
			logDto = MemberLogConvert.INSTANCE.entity2DTO(log, status, note, validTimes, endTime);
			//存到dto中
			logDtoList.add(logDto);
		}	
		
		return logDtoList;
	}

}
