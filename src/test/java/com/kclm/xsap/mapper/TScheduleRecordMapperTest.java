package com.kclm.xsap.mapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.XsapApplication;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.entity.TScheduleRecord;


@SpringBootTest(classes = XsapApplication.class)
public class TScheduleRecordMapperTest {

	private static final Long minute_set = 45L ;
	
	@Autowired
	private TScheduleRecordMapper scheduleMapper;
	
	private TScheduleRecord schedule = new TScheduleRecord();
	
	@Test
	public void test_of() {
		LocalTime testNow = LocalTime.of(11, 15);
		LocalDate startDate = LocalDate.of(2020, 10, 30);
		List<TScheduleRecord> findList = scheduleMapper.selectList(new QueryWrapper<TScheduleRecord>()
				.eq("teacher_id", 1 ).eq("start_date", startDate).orderByAsc(true, "start_date","class_time"));
		//判断当前时间是否有效。1，有效
		Integer flag = 0;
		for(int i = 0; i < findList.size() -1; i++) {
			TScheduleRecord findOne = scheduleMapper.selectById(findList.get(i));
			TScheduleRecord findNext = scheduleMapper.selectById(findList.get(i+1));		
			LocalTime classTimeOne = findOne.getClassTime();
			LocalTime classTimeNext = findNext.getClassTime();
			//课程-前-指定分钟内不能新增排课
			LocalTime preTimeOne = classTimeOne.minusMinutes(minute_set);
			//课程-后-指定分钟内不能新增排课
			LocalTime postTimeOne = classTimeOne.plusMinutes(minute_set);		
			LocalTime preTimeNext = classTimeNext.minusMinutes(minute_set);
			LocalTime postTimeNext = classTimeNext.plusMinutes(minute_set);
			
			//若当前时间在首个时间段之前，有效
			if(i == 0 && testNow.isBefore(preTimeOne)) {
				System.out.println("---" + testNow + " 在 " +  classTimeOne +" 之前有效");
				//表明输入的时间有效
				flag = 1;
				break;
			}
			
			//若当前时间在前面时间段之后，在后面时间段之前，则有效
			if(testNow.isAfter(postTimeOne) && testNow.isBefore(preTimeNext)) {
				System.out.println("------perfect------");
				System.out.println("---" + testNow + " 在 " + classTimeOne +" ~ " + classTimeNext +" 之间有效");
				System.out.println("-------===========------");
				//表明输入的时间有效
				flag = 1;
				break;
			}
			
			//若当前时间在最后一个时间段里面
			if(i == findList.size() - 2 && testNow.isAfter(postTimeNext)) {
				System.out.println("---" + testNow + " 在 " + classTimeNext +" 之后有效");
				//表明输入的时间有效
				flag = 1;
				break;
			}
			
		}
		if(flag == 1) {
			System.out.println("nice_time----------" + testNow);
		}else {
			System.out.println("bad_time------距离当前时间"+ minute_set +"分钟内已有课程安排----" + testNow);
		}
		
	}
	
	
	@Test
	public void oneScheduleView() {
		CourseScheduleDTO oneScheduleView = scheduleMapper.oneScheduleView(1L);
		toPrint("排课记录",1, oneScheduleView);
	}

	@Test
	public void listClassView() {
		List<ClassRecordDTO> listClassView = scheduleMapper.listClassView(28L);
		toPrint("排课记录", listClassView.size(), listClassView);
	}
	
	//增加一条记录
		@Test
		public void save() {
		schedule.setStartDate(LocalDate.now());
		schedule.setClassTime(LocalTime.now());
		schedule.setLimitAge(8);
		schedule.setLimitSex("男");
		scheduleMapper.insert(schedule);
		toPrint("增加", 1,null);
		}
		
	//根据id更新
		@Test
		public void updateOne() {
		schedule = findById(1);
		schedule.setStartDate(LocalDate.now());
		schedule.setClassTime(LocalTime.now());
		schedule.setLimitAge(9);
		schedule.setLimitSex("女");
		scheduleMapper.updateById(schedule);
		toPrint("更新", 1,null);
		}
		
	//根据id删除一条记录
		@Test
		public void deleteOne() {
		scheduleMapper.deleteById(5);
		toPrint("删除",1,null);
		}
	//删除多条记录
		//根据id删除
		@Test
		public void deleteManyById() {
		List<Integer> idList = new ArrayList<>();
		idList.add(6);
		idList.add(7);
		idList.add(8);
		scheduleMapper.deleteBatchIds(idList);
		toPrint("删除", idList.size(), null);
		}
	//根据条件值删除
		@Test
		public void deleteManyByCase() {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put("limit_sex", "男");
		int deleteCount = scheduleMapper.deleteByMap(columnMap);
		toPrint("删除", deleteCount, null);
		}
		
	//查询一条记录
		@Test
		public void selectOne() {
			//根据id查询
			scheduleMapper.selectById(3);
			toPrint("查询", 1, schedule);
		}
	//查询多条数据
		//根据id查询
		@Test
		public void selectManyById() {
			List<Integer> idList = new ArrayList<>();
			idList.add(3);
			idList.add(4);
			idList.add(5);
			List<TScheduleRecord> ids = scheduleMapper.selectBatchIds(idList);
			toPrint("查询", ids.size(), ids);
		}
		//根据条件查询
		@Test
		public void selectManyByCase() {
			Map<String, Object> columnMap = new HashMap<>();
			columnMap.put("limit_sex", "女");
			List<TScheduleRecord> list = scheduleMapper.selectByMap(columnMap);
			toPrint("查询", list.size(), list);
		}
		
	//=====使用Wrapper
		//查询count
		@Test
		public void selectCount() {
			Integer count = scheduleMapper.selectCount(null);
			toPrint("查询", 1, count);
		}

		//采用map查询所有
		@Test
		public void selectAllOnMap() {
		List<Map<String, Object>> list = scheduleMapper.selectMaps(null);
		toPrint("查询", list.size(), list);
		}
		//分页展示
		@Test
		public void selectAllOnMapPage() {
			IPage<Map<String, Object>> page = new Page<>(2,5); //当前页基数：1
			IPage<Map<String, Object>> pageList = scheduleMapper.selectMapsPage(page, null);
			List<Map<String, Object>> list = pageList.getRecords();
			toPrint("查询", list.size(), list);
		}

		//====== 通用方法 ======//
		
		//根据id查询出实体数据
		public TScheduleRecord findById(Integer id) {
			schedule = scheduleMapper.selectById(id);
			return schedule;
		}
		
		//打印到控制台-普通类型
		private void toPrint(String type,Integer num,Object obj) {
			System.out.println("-------------");
			System.out.println("  【" + type + "】" + num + "条记录");
			if(obj != null)
				System.out.println("=》 " + obj);
			System.out.println("-------------");
		}
		//打印到控制台-集合类型
		private void toPrint(String type,Integer num,List<? extends Object> objList) {
			System.out.println("-------------");
			System.out.println("  【" + type + "】" + num + "条记录");
			if(objList != null) {
				for (Object obj : objList) {
					System.out.println("=》 " + obj);
				}
			}
			System.out.println("-------------");
		}
	
}
