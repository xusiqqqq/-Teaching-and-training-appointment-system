package com.kclm.xsap.service.impl;

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
import com.kclm.xsap.dto.convert.ClassRecordConvert;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.TeacherService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TeacherServiceImpl implements TeacherService{

	@Autowired
	TEmployeeMapper employeeMapper;

//	@Autowired
//	private ClassRecordConvert classRecordConvert;
		
	@Override
	public boolean save(TEmployee emp) {
		employeeMapper.insert(emp);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		TEmployee employee = employeeMapper.selectById(id);
		if(employee == null) {
			System.out.println("------无此条教师记录");
			return false;
		}
		employeeMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TEmployee emp) {
		employeeMapper.update(emp,new QueryWrapper<TEmployee>().eq("id",emp.getId()));
		return true;
	}

	@Override
	public List<TEmployee> findAll() {
		List<TEmployee> list = employeeMapper.selectList(null);
		return list;
	}

	@Override
	public List<TEmployee> findAllByPage(Integer currentPage, Integer pageSize) {
		//分页查询
		IPage<TEmployee> page = new Page<>(currentPage, pageSize);
		IPage<TEmployee> pageList = employeeMapper.selectPage(page , null);
		//获取到根据分页条件查询出的结果
		List<TEmployee> list = pageList.getRecords();
		return list;
	}

	/* 具体返回数据待定 */
	@Override
	public TEmployee getAnalysis(Long id) {
		TEmployee emp = employeeMapper.selectById(id);
		return emp;
	}

	@Autowired
	TClassRecordMapper classMapper;
	
	@Autowired
	TCourseMapper courseMapper;
	
	@Autowired
	TScheduleRecordMapper scheduleMapper;

	@Autowired
	TMemberCardMapper cardMapper;
	
	@Override
	public List<ClassRecordDTO> listClassRecord(Long id) {
		/* 对应的sql语句
		 SELECT * FROM t_reservation_record WHERE STATUS = 1 AND schedule_id IN 
	 	(SELECT id FROM t_schedule_record WHERE teacher_id = 1); 
		 */
		//1、获取上课记录，当用户确认已上课时，老师关于此位用户的上课记录才会产生。此处查找的是老师的上课记录
		List<TClassRecord> classList = classMapper.selectList(new QueryWrapper<TClassRecord>()
				.eq("check_status", 1).eq("reserve_check", 1).inSql("schedule_id",
				"SELECT id FROM t_schedule_record WHERE teacher_id =" + id));
		
		if(classList == null || classList.size() < 1) {
			System.out.println("-------当前教师没有上课记录");
			return null;
		}
		
		//2、获取排课计划信息
		List<TScheduleRecord> scheduleList = new ArrayList<TScheduleRecord>();
		for (int i = 0; i < classList.size(); i++) {
			TScheduleRecord scheduleRecord = scheduleMapper.selectById(classList.get(i).getScheduleId());
			scheduleList.add(scheduleRecord);
		}
		//3、获取课程信息
		List<TCourse> courseList = new ArrayList<TCourse>();
		for (int i = 0; i < scheduleList.size(); i++) {
			TCourse course = courseMapper.selectById(scheduleList.get(i).getCourseId());
			courseList.add(course);
		}
		
		//4、组合成DTO数据信息
		//4.1 sql结果对应关系
		//1条 上课记录 =》 1条 排课记录（1 条 会员记录） =》1条 课程记录 =》  n条 会员卡记录
		List<ClassRecordDTO> classDtoList = new ArrayList<>();
		for(int i = 0; i < classList.size(); i++) {
			TClassRecord classed = new TClassRecord();
			TScheduleRecord schedule = new TScheduleRecord();
			TCourse course = new TCourse();

			classed = classList.get(i);
			schedule = scheduleList.get(i);
			course = courseList.get(i);
			//======DTO存储
			ClassRecordDTO classRecordDTO = new ClassRecordDTO();
			classRecordDTO.setClassRecordId(classed.getId());
			classRecordDTO.setCourseName(course.getName());
			classRecordDTO.setClassTime(LocalDateTime.of(schedule.getStartDate(), schedule.getClassTime()));
			classRecordDTO.setCardName(classed.getCardName());
			classRecordDTO.setTimesCost(course.getTimesCost());
			//转换完成一条记录，就存放一条记录
			classDtoList.add(classRecordDTO);
		}
		
		return classDtoList;
	}

	//头像切换
	@Override
	public boolean avatarChange(TEmployee emp) {
		TEmployee employee = employeeMapper.selectById(emp.getId());
		if(employee == null) {
			log.debug("--------没有此员工");
			return false;
		}
		employeeMapper.updateById(employee);
		return true;
	}

}
