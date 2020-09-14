package com.kclm.xsap.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.entity.TCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TCourseMapperTest {

	@Autowired
	private TCourseMapper courseMapper;
	
	private TCourse course = new TCourse();
	
	//增加一条记录
	@Test
	public void save() {
		course.setName("数学");
		course.setDuration(45);
		course.setColor("blue");
		course.setContains(6);
		course.setLimitSex("男");
		course.setLimitAge(6);
		course.setIntroduce("数学课");
		course.setTimesCost(1);
		
		courseMapper.insert(course);
		toPrint("增加", 1,null);
	}
	
	//根据id更新
	@Test
	public void updateOne() {
		course = findById(2);
		
		course.setName("ds");
		course.setDuration(30);
		course.setContains(9);
		courseMapper.updateById(course);
		toPrint("更新", 1,null);
	}
	
	/* 涉及到外键，删除可能会出错 */
	//根据id删除一条记录
	@Test
	public void deleteOne() {
		courseMapper.deleteById(6);
		toPrint("删除",1,null);
	}
	//删除多条记录
	//根据id删除
	@Test
	public void deleteManyById() {
		List<Integer> idList = new ArrayList<>();
		idList.add(7);
		idList.add(8);
		idList.add(9);
		courseMapper.deleteBatchIds(idList);
		toPrint("删除", idList.size(), null);
	}
	//根据条件值删除
	@Test
	public void deleteManyByCase() {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put("name", "数学2");
		int deleteCount = courseMapper.deleteByMap(columnMap);
		toPrint("删除", deleteCount, null);
	}
	
	
	/*
	  	若查询出的结果中包含的实体数据集合无值，是由于未赋值其version字段 
	 */
	
	//查询一条记录
	@Test
	public void selectOne() {
		//根据id查询
		course = courseMapper.selectById(1);
		toPrint("查询", 1, course);
	}
	//查询多条数据
	//根据id查询
	@Test
	public void selectManyById() {
		List<Integer> idList = new ArrayList<>();
		idList.add(1);
		idList.add(2);
		idList.add(3);
		idList.add(4);
		idList.add(5);
		idList.add(6);
		idList.add(7);
		List<TCourse> ids = courseMapper.selectBatchIds(idList);
		toPrint("查询", ids.size(), ids);
	}
	//根据条件查询
	@Test
	public void selectManyByCase() {
		Map<String, Object> columnMap = new HashMap<>();
		columnMap.put("name", "数学");
//		columnMap.put("color", "red");
		List<TCourse> list = courseMapper.selectByMap(columnMap);
		toPrint("查询", list.size(), list);
	}
	
	//=====使用Wrapper
	//查询count
	@Test
	public void selectCount() {
		Integer count = courseMapper.selectCount(null);
		toPrint("查询", 1, count);
	}

	//采用map查询所有
	@Test
	public void selectAllOnMap() {
		List<Map<String, Object>> list = courseMapper.selectMaps(null);
		toPrint("查询", list.size(), list);
	}

	//分页展示
	@Test
	public void selectAllOnMapPage() {
		IPage<Map<String, Object>> page = new Page<>(2,5); //当前页基数：1
		IPage<Map<String, Object>> pageList = courseMapper.selectMapsPage(page, null);
		List<Map<String, Object>> list = pageList.getRecords();
		toPrint("查询", list.size(), list);
	}
	
	
	//====== 通用方法 ======//
	
	//根据id查询出实体数据
	public TCourse findById(Integer id) {
		course = courseMapper.selectById(id);
		return course;
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
