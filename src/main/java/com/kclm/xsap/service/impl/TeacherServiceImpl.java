package com.kclm.xsap.service.impl;

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
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.TeacherService;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService{

	@Autowired
	TEmployeeMapper employeeMapper;
		
	@Override
	public boolean save(TEmployee emp) {
		employeeMapper.insert(emp);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		employeeMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TEmployee emp) {
		employeeMapper.updateById(emp);
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
				.eq("check_status", 1).inSql("schedule_id",
				"SELECT id FROM t_schedule_record WHERE teacher_id =" + id));
		
		//2、获取排课计划信息
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
			for(int j = 0; j < cardList.size() ; j++) {
				card = cardList.get(j);
				//DTO转换
				ClassRecordDTO classRecordDTO = ClassRecordConvert.INSTANCE
						.entity2Dto(classed, null, course, schedule, card, null, null);
				//转换完成一条记录，就存放一条记录
				classDtoList.add(classRecordDTO);
			}
		}
		
		return classDtoList;
	}

}
