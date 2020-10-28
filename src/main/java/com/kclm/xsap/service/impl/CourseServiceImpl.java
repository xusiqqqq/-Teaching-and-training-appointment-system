
package com.kclm.xsap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.CourseDTO;
import com.kclm.xsap.dto.convert.CourseConvert;
import com.kclm.xsap.entity.CardCourseRecord;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService{

//	@Autowired
//	private CourseConvert courseConvert;

	@Autowired
	private TCourseMapper courseMapper;
	
	@Autowired
	private TReservationRecordMapper reserveMapper;
	
	@Autowired
	private TClassRecordMapper classMpper;
	
	@Autowired
	private TScheduleRecordMapper scheduleMapper;
	
	@Override
	public boolean save(TCourse course) {
		//检查新增课程的名称
		String checkName = course.getName().trim();
		TCourse findOne = courseMapper.selectOne(new QueryWrapper<TCourse>().eq("name", checkName));
		//若新增的课程名称存在于库中，不录入
		if(findOne != null) {
			System.out.println("-----同名的课程已存在！");
			return false;
		}
		
		//录入
		courseMapper.insert(course);

		//向中间表插入数据
		List<TMemberCard> cardList = course.getCardList();
		if(cardList != null && cardList.size() > 0) {
			for (int i = 0; i < cardList.size(); i++) {
				TMemberCard card = cardList.get(i);
				CardCourseRecord result = courseMapper.findBindCard(card.getId(), course.getId());
				if(result != null) {
					System.out.println("课程号："+ course.getId() +"----已经绑定过"+ card.getId() +"-----");
					continue ;
				}
				System.out.println("课程号："+ course.getId() + "----新增绑定"+ card.getId() +"-----");
				courseMapper.insertMix(card.getId(), course.getId());
			}			
		}

		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		TCourse course = courseMapper.selectById(id);
		if(course == null) {
			System.out.println("----------无此条课程记录");
			return false;
		}
		//删除中间表关联的键
		courseMapper.deleteBindCard(id);
		
		//查询此门课有没有其它关联的记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
				.inSql("schedule_id", "select id from t_schedule_record WHERE course_id = " + id));
		
		List<TClassRecord> classList = classMpper.selectList(new QueryWrapper<TClassRecord>()
				.inSql("schedule_id", "select id from t_schedule_record WHERE course_id = " + id));
		
		List<TScheduleRecord> scheduleList = scheduleMapper.selectList(new QueryWrapper<TScheduleRecord>().eq("course_id", id));
		
		if(reserveList != null || classList != null || scheduleList != null) {
			System.out.println("此课有其它关联记录，不宜删除！");
			return false;
		}
		
		//删除关联的其它表的记录 - 代价太大，改为提示
//		//1、预约记录表
//		reserveMapper.delete(new QueryWrapper<TReservationRecord>()
//				.inSql("schedule_id", "select id from t_schedule_record WHERE course_id = " + id));
//		
//		//2、上课记录表
//		classMpper.delete(new QueryWrapper<TClassRecord>().
//				inSql("schedule_id", "select id from t_schedule_record WHERE course_id = " + id));
//		
//		//3、排课表
//		scheduleMapper.delete(new QueryWrapper<TScheduleRecord>().eq("course_id", id));
		
		//本表
		courseMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TCourse course) {
		courseMapper.updateById(course);
		//更新中间表: 先删除已绑定的，再重新绑定
		courseMapper.deleteBindCard(course.getId());
		
		//向中间表插入数据
		List<TMemberCard> cardList = course.getCardList();
		if(cardList != null && cardList.size() > 0) {			
			for (TMemberCard card : cardList) {
				courseMapper.insertMix(card.getId(), course.getId());
			}
		}

		return true;
	}

	@Override
	public List<CourseDTO> findAllByPage(Integer currentPage, Integer pageSize) {
		IPage<TCourse> page = new Page<>(currentPage,pageSize);
		IPage<TCourse> pageList = courseMapper.selectPage(page , null);
		List<TCourse> courseList = pageList.getRecords();
		
		List<CourseDTO> courseDtoList = new ArrayList<CourseDTO>();
		for (TCourse course : courseList) {
			//======DTO存储
			CourseDTO courseDTO = new CourseDTO();
			courseDTO.setId(course.getId());
			courseDTO.setName(course.getName());
			courseDTO.setDuration(course.getDuration());
			courseDTO.setContains(course.getContains());
			courseDTO.setColor(course.getColor());
			courseDTO.setCardList(course.getCardList());
			courseDTO.setIntroduce(course.getIntroduce());
			courseDTO.setTimesCost(course.getTimesCost());
			courseDTO.setLimitSex(course.getLimitSex());
			courseDTO.setLimitAge(course.getLimitAge());
			courseDTO.setLimitCounts(course.getLimitCounts());
			courseDtoList.add(courseDTO);
		}
		return courseDtoList;
	}

	@Override
	public List<CourseDTO> findAll() {
		List<TCourse> courseList = courseMapper.selectList(null);
		List<CourseDTO> courseDtoList = new ArrayList<CourseDTO>();
		for (TCourse course : courseList) {
			//======DTO存储
			CourseDTO courseDTO = new CourseDTO();
			courseDTO.setId(course.getId());
			courseDTO.setName(course.getName());
			courseDTO.setDuration(course.getDuration());
			courseDTO.setContains(course.getContains());
			courseDTO.setColor(course.getColor());
			courseDTO.setCardList(course.getCardList());
			courseDTO.setIntroduce(course.getIntroduce());
			courseDTO.setTimesCost(course.getTimesCost());
			courseDTO.setLimitSex(course.getLimitSex());
			courseDTO.setLimitAge(course.getLimitAge());
			courseDTO.setLimitCounts(course.getLimitCounts());
			courseDtoList.add(courseDTO);
		}
		return courseDtoList;
	}

	//根据id查询课程信息
		@Override
		public TCourse findById(Long id) {
			TCourse course = courseMapper.selectById(id);
			return course;
		}

		//根据name查询课程信息
		@Override
		public TCourse findByName(String name) {
			TCourse findOne = courseMapper.selectOne(new QueryWrapper<TCourse>().eq("name", name));
			return findOne;
		}

		//找到某个会员卡绑定的所有课程信息
		@Override
		public List<TCourse> listByCardId(Long cardId) {
			List<TCourse> courseList = courseMapper.selectList(new QueryWrapper<TCourse>()
					.inSql("id", "select course_id from t_course_card where card_id = " + cardId));
			return courseList;
		}
	
}
