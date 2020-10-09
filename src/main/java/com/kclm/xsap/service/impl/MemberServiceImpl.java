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
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.dto.convert.ClassRecordConvert;
import com.kclm.xsap.dto.convert.ConsumeRecordConvert;
import com.kclm.xsap.dto.convert.MemberConvert;
import com.kclm.xsap.dto.convert.ReserveRecordConvert;
import com.kclm.xsap.dto.convert.MemberCardConvert;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TConsumeRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.MemberService;

@Service
@Transactional
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
	private TClassRecordMapper classMapper;

	@Autowired
	private TConsumeRecordMapper consumeMapper;
	
	@Autowired
	private TScheduleRecordMapper scheduleMapper;
	
	@Autowired
	private TCourseMapper courseMapper;
	
	@Autowired
	private TEmployeeMapper employeeMapper;
	
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
	
	//文件读取，存库
	@Override
	public boolean saveByBundle(String filePath) {
		return false;
	}
	
	//文件读取，存库
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
	
	//会员卡信息
	@Override
	public List<MemberCardDTO> findAllCardRecords(Long id) {
		List<MemberCardDTO> cardDtoList = new ArrayList<>();
		MemberCardDTO cardDto;
		
		//查询到当前会员绑定的所有会员卡
		List<TMemberBindRecord> bindList = bindMapper.selectList(new QueryWrapper<TMemberBindRecord>()
				.eq("member_id", id));
		TMemberBindRecord bindRecord;
		for(int i = 0; i < bindList.size(); i++) {
			bindRecord = bindList.get(i);
			//会员卡可用次数
			TMemberBindRecord bind = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					.eq("member_id", id).eq("card_id", bindRecord.getCardId()));
			Integer validTimes = bind.getValidCount();
			
			//会员卡到期日
			LocalDateTime createTime = bind.getCreateTime();
			LocalDateTime endTime = createTime.plusDays(bind.getValidDay());
			
			//获取每张会员卡信息
			TMemberCard memberCard = cardMapper.selectById(bindRecord.getCardId());
			
			//组成一条会员卡信息DTO
			cardDto = MemberCardConvert.INSTANCE.entity2Dto(validTimes, endTime, memberCard);
			cardDtoList.add(cardDto);
		}
		return cardDtoList;
	}

	//上课记录
	@Override
	public List<ClassRecordDTO> listClassRecords(Long id) {
		//获取上课记录。这里获取的是会员的上课记录
		List<TClassRecord> classList = classMapper.selectList(new QueryWrapper<TClassRecord>()
				.eq("member_id", id));
		//获取排课计划
		List<Long> idList = new ArrayList<>();
		for (int i = 0; i < classList.size(); i++) {
			 idList.add(classList.get(i).getScheduleId());
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
		TClassRecord classed ;
		TScheduleRecord schedule;
		TCourse course;
		TMemberCard card;
		List<ClassRecordDTO> classDtoList = new ArrayList<>();
		for(int i = 0; i < classList.size(); i++) {
			classed = classList.get(i);
			schedule = scheduleList.get(i);
			course = courseList.get(i);
			String teacherName = employeeMapper.selectById(schedule.getTeacherId()).getName();
			for(int j = 0; j < cardList.size() ; j++) {
				card = cardList.get(j);
				//DTO转换
				ClassRecordDTO classRecordDTO = ClassRecordConvert.INSTANCE.entity2Dto(classed, null,course, schedule, card,teacherName, null);
				//转换完成一条记录，就存放一条记录
				classDtoList.add(classRecordDTO);
			}
		}
		return classDtoList;
	}

	//预约记录
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
			ReserveRecordDTO reserveDto = ReserveRecordConvert.INSTANCE.entity2Dto(course, schedule, reserve,null);
			//转换完成一条记录，就存放一条记录
			reserveDtoList.add(reserveDto);
		}
		return reserveDtoList;
	}

	//消费记录
	@Override
	public List<ConsumeRecordDTO> listConsumeRecords(Long id) {
		/* 查询前，先对上课记录进行消费录入 */
		
		//查出所有确认已上课事务记录，进行消费记录的录入
		List<TClassRecord> classList = classMapper.selectList(
				new QueryWrapper<TClassRecord>().eq("check_status", 1));
		TConsumeRecord consume = new TConsumeRecord();
		for (TClassRecord classed : classList) {
			consume.setMemberId(classed.getMemberId());
			//查出卡号
			Long cardId = cardMapper.selectOne(new QueryWrapper<TMemberCard>()
					.eq("name", classed.getCardName())).getId();
			consume.setCardId(cardId);
			//查询出某课程单词课需花费的次数
			TScheduleRecord scheduleRecord = scheduleMapper.selectById(classed.getScheduleId());
			TCourse course = courseMapper.selectById(scheduleRecord.getCourseId());
			consume.setCardCountChange(course.getTimesCost());
			
			//为系统自动处理时，天数不进行消耗处理
			consume.setCardDayChange(0);
			
			consume.setOperateType("上课支出");
			consume.setOperator("系统自动处理");
			//查出会员卡的次数单价，取值四舍五入
			TMemberCard card = cardMapper.selectById(consume.getCardId());
			BigDecimal price = new BigDecimal(card.getPrice().toString());
			BigDecimal count = new BigDecimal(card.getTotalCount().toString());
			BigDecimal unitPrice = price.divide(count, 2, RoundingMode.HALF_UP);
			//消费的次数
			BigDecimal countCost = new BigDecimal(course.getTimesCost().toString());
			consume.setMoneyCost(unitPrice.multiply(countCost));
			//录入一条消费记录
			consumeMapper.insert(consume);
			//消费操作
			TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					.eq("card_id", consume.getCardId()).eq("member_id", consume.getMemberId()));
			bindRecord.setValidCount(bindRecord.getValidCount() + consume.getCardCountChange());
			bindRecord.setValidDay(bindRecord.getValidDay() + consume.getCardDayChange());
			bindRecord.setReceivedMoney(consume.getMoneyCost());
			bindMapper.update(bindRecord, null);
		}
		
		/* 以下是查询 */
		
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
			 //查询剩余卡次
			 TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					 .eq("card_id", memberCard.getId()).eq("member_id", id));
			 Integer timesRemainder = bindRecord.getValidCount();
			 //DTO组合
			 ConsumeRecordDTO consumeDto = ConsumeRecordConvert.INSTANCE.entity2Dto(consumeRecord, memberCard,timesRemainder);
			 //存放所有DTO数据
			 consumeDtoList.add(consumeDto);
		}
		
		return consumeDtoList;
	}

}
