package com.kclm.xsap.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.dto.convert.ClassRecordConvert;
import com.kclm.xsap.dto.convert.ConsumeRecordConvert;
import com.kclm.xsap.dto.convert.MemberConvert;
import com.kclm.xsap.dto.convert.ReserveRecordConvert;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TConsumeRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.MemberService;

public class MemberServiceImpl implements MemberService{

	@Autowired
	private TMemberMapper memberMapper;
	
	@Autowired
	private TMemberCardMapper cardMapper;
	
	@Autowired
	private TMemberBindRecordMapper bindMapper;
	
	@Autowired
	private TReservationRecordMapper reserveMapper;
	
	@Autowired
	private TConsumeRecordMapper consumeMapper;
	
	@Autowired
	private TScheduleRecordMapper scheduleMapper;
	
	@Autowired
	private TCourseMapper courseMapper;
	
	@Override
	public boolean save(TMember member) {
		memberMapper.insert(member);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		memberMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TMember member) {
		memberMapper.updateById(member);
		return true;
	}

	@Override
	public List<TMember> findAll() {
		List<TMember> memberList = memberMapper.selectList(null);
		return memberList;
	}

	@Override
	public List<TMember> findAllByPage(Integer currentPage, Integer pageSize) {
		IPage<TMember> page = new Page<>(currentPage,pageSize);
		IPage<TMember> pageList = memberMapper.selectPage(page , null);
		return pageList.getRecords();
	}

	@Override
	public List<TMember> findByKeyword(String condition) {
		List<TMember> memberList = memberMapper.selectList(new QueryWrapper<TMember>()
				.like("phone", condition).or().like("name", condition));
		return memberList;
	}

	@Override
	public boolean bindCard(TMemberBindRecord cardBind) {
		bindMapper.insert(cardBind);
		return false;
	}

	/* 待处理  - begin*/
	
	//文件上传
	@Override
	public boolean saveByBundle(String filePath) {
		return false;
	}
	
	//文件上传
	@Override
	public boolean bindByBunble(String filePath) {
		return false;
	}

	/* 待处理  - end*/
	
	@Override
	public MemberDTO getMemberDetailById(Long id) {
		TMember member = memberMapper.selectById(id);
		//会员卡信息
		List<MemberCardDTO> cardRecords = findAllCardRecords(id);
		//上课记录
		List<ClassRecordDTO> classRecords = listClassRecords(id);
		//预约记录
		List<ReserveRecordDTO> reserveRecords = listReserveRecords(id);
		//消费记录
		List<ConsumeRecordDTO> consumeRecords = listConsumeRecords(id);
		//组合DTO
		MemberDTO memberDto = MemberConvert.INSTANCE.entity2Dto(member);
		memberDto.setCardMessage(cardRecords);
		memberDto.setClassRecord(classRecords);
		memberDto.setReserveRecord(reserveRecords);
		memberDto.setConsumeRecord(consumeRecords);
		return memberDto;
	}
	
	
	@Override
	public List<MemberCardDTO> findAllCardRecords(Long id) {
		List<MemberCardDTO> cardDtoList = new ArrayList<>();
		MemberCardDTO cardDto = new MemberCardDTO();
		
		List<TMemberBindRecord> bindList = bindMapper.selectList(new QueryWrapper<TMemberBindRecord>()
				.eq("member_id", id));
		TMemberBindRecord bindRecord;
		for(int i = 0; i < bindList.size(); i++) {
			bindRecord = bindList.get(i);
			//会员卡可用次数
			TMemberBindRecord bind = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					.eq("member_id", id).eq("card_id", bindRecord.getCardId()));
			Integer validTimes = bind.getValidCount();
			
			//组合会员卡可用次数
			cardDto.setTotalCount(validTimes);
			
			//会员卡到期日
			LocalDateTime createTime = bind.getCreateTime();
			createTime = createTime.plusDays(bind.getValidDay());
			
			//组合卡到期日
			cardDto.setDueTime(createTime);
			
			//获取每张会员卡信息
			TMemberCard memberCard = cardMapper.selectById(bindRecord.getCardId());
			//dto组合会员卡名
			cardDto.setName(memberCard.getName());
			//dto组合会员卡类型
			cardDto.setType(memberCard.getType());
			//dto组合会员卡状态
			cardDto.setStatus(memberCard.getStatus());
			//组成一条会员卡信息
			cardDtoList.add(cardDto);
		}
		return cardDtoList;
	}

	@Override
	public List<ClassRecordDTO> listClassRecords(Long id) {
		//获取上课记录。这里获取的是会员的上课记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
				.eq("status", 1).eq("member_id", id));
		//获取排课计划
		List<Long> idList = new ArrayList<>();
		for (int i = 0; i < reserveList.size(); i++) {
			 idList.add(reserveList.get(i).getScheduleId());
		}
		List<TScheduleRecord> scheduleList = scheduleMapper.selectBatchIds(idList);
		//清空idList数据，以供下面复用
		idList.clear();
		
		//3、获取课程信息
		for (int i = 0; i < scheduleList.size(); i++) {
			 idList.add(scheduleList.get(i).getCourseId());
		}
		List<TCourse> courseList = courseMapper.selectBatchIds(idList);
		//清空idList数据，以供下面复用
		idList.clear();
		
		//4、获取会员卡信息
		for (int i = 0; i < courseList.size(); i++) {
			 idList.add(courseList.get(i).getId());
		}
		List<TMemberCard> cardList = cardMapper.selectBatchIds(idList);
		//5、组合成DTO数据信息
		//5.1 sql结果对应关系
		//1条 上课记录 =》 1条 排课记录（1 条 会员记录） =》1条 课程记录 =》  n条 会员卡记录
		TReservationRecord reserve ;
		TScheduleRecord schedule;
		TCourse course;
		TMemberCard card;
		List<ClassRecordDTO> classDtoList = new ArrayList<>();
		for(int i = 0; i < reserveList.size(); i++) {
			reserve = reserveList.get(i);
			schedule = scheduleList.get(i);
			course = courseList.get(i);
			for(int j = 0; j < cardList.size() ; j++) {
				card = cardList.get(j);
				//DTO转换
				ClassRecordDTO classRecordDTO = ClassRecordConvert.INSTANCE.entity2Dto(reserve, course, schedule, card);
				//转换完成一条记录，就存放一条记录
				classDtoList.add(classRecordDTO);
			}
		}
		return classDtoList;
	}

	//跟上课记录不同的地方在于，预约状态不限制，预约的会员卡仅能一次预约一门课，一门课在被预约的状态下，同一个会员不能二次预约
	@Override
	public List<ReserveRecordDTO> listReserveRecords(Long id) {
		List<TReservationRecord> reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
				.eq("member_id", id));
		//获取排课计划
		List<Long> idList = new ArrayList<>();
		for (int i = 0; i < reserveList.size(); i++) {
			 idList.add(reserveList.get(i).getScheduleId());
		}
		List<TScheduleRecord> scheduleList = scheduleMapper.selectBatchIds(idList);
		//清空idList数据，以供下面复用
		idList.clear();
		
		//3、获取课程信息
		for (int i = 0; i < scheduleList.size(); i++) {
			 idList.add(scheduleList.get(i).getCourseId());
		}
		List<TCourse> courseList = courseMapper.selectBatchIds(idList);
		//清空idList数据，以供下面复用
		idList.clear();
		
		//4、组合成DTO数据信息
		//4.1 sql结果对应关系
		//1条 预约记录（包含已预约的会员卡名） =》 1条 排课记录（1 条 会员记录） =》1条 课程记录
		TReservationRecord reserve ;
		TScheduleRecord schedule;
		TCourse course;
		List<ReserveRecordDTO> reserveDtoList = new ArrayList<>();
		for(int i = 0; i < reserveList.size(); i++) {
			reserve = reserveList.get(i);
			schedule = scheduleList.get(i);
			course = courseList.get(i);			
			//DTO转换
			ReserveRecordDTO reserveDto = ReserveRecordConvert.INSTANCE.entity2Dto(course, schedule, reserve);
			//转换完成一条记录，就存放一条记录
			reserveDtoList.add(reserveDto);
		}
		return reserveDtoList;
	}

	@Override
	public List<ConsumeRecordDTO> listConsumeRecords(Long id) {
		List<TConsumeRecord> consumeList = consumeMapper.selectList(new QueryWrapper<TConsumeRecord>()
				.eq("member_id", id));
		List<Long> idList = new ArrayList<>();
		for(int i = 0; i < consumeList.size(); i++) {
			idList.add(consumeList.get(i).getCardId());
		}
		//根据每条消费记录查询到的会员卡信息
		List<TMemberCard> cardList = cardMapper.selectBatchIds(idList);
		
		List<ConsumeRecordDTO> consumeDtoList = new ArrayList<>();
		TConsumeRecord consumeRecord;
		TMemberCard memberCard;
		for(int i = 0; i < consumeList.size(); i++) {
			 consumeRecord = consumeList.get(i);
			 memberCard = cardList.get(i);
			 //DTO组合
			 ConsumeRecordDTO consumeDto = ConsumeRecordConvert.INSTANCE.entity2Dto(consumeRecord, memberCard);
			 //存放所有DTO数据
			 consumeDtoList.add(consumeDto);
		}
		
		return consumeDtoList;
	}

}
