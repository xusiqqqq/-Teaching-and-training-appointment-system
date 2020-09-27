package com.kclm.xsap.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.entity.TGlobalReservationSet;


@SpringBootTest
public class TGlobalReservationSetMapperTest {

	@Autowired
	private TGlobalReservationSetMapper globalMapper;
	
	private TGlobalReservationSet global = new TGlobalReservationSet();
	
	//增加一条记录
		@Test
		public void save() {
		global.setStartTime(1);
		globalMapper.insert(global);
		toPrint("增加", 1,null);
		}
		
	//根据id更新
		@Test
		public void updateOne() {
		global = findById(1);
		global.setStartTime(11);
		globalMapper.updateById(global);
		toPrint("更新", 1,null);
		}
		
	//根据id删除一条记录
		@Test
		public void deleteOne() {
		globalMapper.deleteById(5);
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
		globalMapper.deleteBatchIds(idList);
		toPrint("删除", idList.size(), null);
		}
	//根据条件值删除
		@Test
		public void deleteManyByCase() {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put("start_time", "10");
		int deleteCount = globalMapper.deleteByMap(columnMap);
		toPrint("删除", deleteCount, null);
		}
		
	//查询一条记录
		@Test
		public void selectOne() {
			//根据id查询
			globalMapper.selectById(3);
			toPrint("查询", 1, global);
		}
	//查询多条数据
		//根据id查询
		@Test
		public void selectManyById() {
			List<Integer> idList = new ArrayList<>();
			idList.add(3);
			idList.add(4);
			idList.add(5);
			List<TGlobalReservationSet> ids = globalMapper.selectBatchIds(idList);
			toPrint("查询", ids.size(), ids);
		}
		//根据条件查询
		@Test
		public void selectManyByCase() {
			Map<String, Object> columnMap = new HashMap<>();
			columnMap.put("start_time", "11");
			List<TGlobalReservationSet> list = globalMapper.selectByMap(columnMap);
			toPrint("查询", list.size(), list);
		}
		
	//=====使用Wrapper
		//查询count
		@Test
		public void selectCount() {
			Integer count = globalMapper.selectCount(null);
			toPrint("查询", 1, count);
		}

		//采用map查询所有
		@Test
		public void selectAllOnMap() {
		List<Map<String, Object>> list = globalMapper.selectMaps(null);
		toPrint("查询", list.size(), list);
		}
		//分页展示
		@Test
		public void selectAllOnMapPage() {
			IPage<Map<String, Object>> page = new Page<>(2,5); //当前页基数：1
			IPage<Map<String, Object>> pageList = globalMapper.selectMapsPage(page, null);
			List<Map<String, Object>> list = pageList.getRecords();
			toPrint("查询", list.size(), list);
		}

		//====== 通用方法 ======//
		
		//根据id查询出实体数据
		public TGlobalReservationSet findById(Integer id) {
			global = globalMapper.selectById(id);
			return global;
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
