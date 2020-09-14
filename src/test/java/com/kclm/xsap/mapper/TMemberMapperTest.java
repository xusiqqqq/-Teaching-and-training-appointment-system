package com.kclm.xsap.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.entity.TConsumeRecord;


@SpringBootTest
public class TMemberMapperTest {

	@Autowired
	private TConsumeRecordMapper consumeMapper;
	
	private TConsumeRecord consume = new TConsumeRecord();
	
	//增加一条记录
		@Test
		public void save() {
		consume.setOperateType("扣款11");
		consume.setCardCountChange(1);
		consume.setCardDayChange(1);
		consume.setOperator("北极熊");
		consume.setNote("note-1");
		consumeMapper.insert(consume);
		toPrint("增加", 1,null);
		}
		
	//根据id更新
		@Test
		public void updateOne() {
		consume = findById(1);
		consume.setOperateType("扣款11-1");
		consume.setCardCountChange(11);
		consume.setCardDayChange(11);
		consume.setOperator("北极熊-1");
		consume.setNote("note-1-1");
		consumeMapper.updateById(consume);
		toPrint("更新", 1,null);
		}
		
	//根据id删除一条记录
		@Test
		public void deleteOne() {
		consumeMapper.deleteById(5);
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
		consumeMapper.deleteBatchIds(idList);
		toPrint("删除", idList.size(), null);
		}
	//根据条件值删除
		@Test
		public void deleteManyByCase() {
		Map<String, Object> columnMap = new HashMap<String, Object>();
		columnMap.put("operator", "海鸥");
		int deleteCount = consumeMapper.deleteByMap(columnMap);
		toPrint("删除", deleteCount, null);
		}
		
	//查询一条记录
		@Test
		public void selectOne() {
			//根据id查询
			consumeMapper.selectById(3);
			toPrint("查询", 1, consume);
		}
	//查询多条数据
		//根据id查询
		@Test
		public void selectManyById() {
			List<Integer> idList = new ArrayList<>();
			idList.add(3);
			idList.add(4);
			idList.add(5);
			List<TConsumeRecord> ids = consumeMapper.selectBatchIds(idList);
			toPrint("查询", ids.size(), ids);
		}
		//根据条件查询
		@Test
		public void selectManyByCase() {
			Map<String, Object> columnMap = new HashMap<>();
			columnMap.put("operator", "企鹅");
			List<TConsumeRecord> list = consumeMapper.selectByMap(columnMap);
			toPrint("查询", list.size(), list);
		}
		
	//=====使用Wrapper
		//查询count
		@Test
		public void selectCount() {
			Integer count = consumeMapper.selectCount(null);
			toPrint("查询", 1, count);
		}

		//采用map查询所有
		@Test
		public void selectAllOnMap() {
		List<Map<String, Object>> list = consumeMapper.selectMaps(null);
		toPrint("查询", list.size(), list);
		}
		//分页展示
		@Test
		public void selectAllOnMapPage() {
			IPage<Map<String, Object>> page = new Page<>(2,5); //当前页基数：1
			IPage<Map<String, Object>> pageList = consumeMapper.selectMapsPage(page, null);
			List<Map<String, Object>> list = pageList.getRecords();
			toPrint("查询", list.size(), list);
		}

		//====== 通用方法 ======//
		
		//根据id查询出实体数据
		public TConsumeRecord findById(Integer id) {
			consume = consumeMapper.selectById(id);
			return consume;
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
