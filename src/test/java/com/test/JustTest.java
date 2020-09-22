package com.test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.XsapApplication;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.mapper.TReservationRecordMapper;

@SpringBootTest(classes = XsapApplication.class)
public class JustTest {

	@Autowired
	private TReservationRecordMapper reserveMapper;
	
	@Test
	public void findCase() {
		List<TReservationRecord> list = reserveMapper.selectList(new QueryWrapper<TReservationRecord>().eq("status", 1)
				.inSql("schedule_id","SELECT id FROM t_schedule_record WHERE teacher_id = 1"));
		for (TReservationRecord tr : list) {
			System.out.println(tr);
			System.out.println("----");
		}
		List<Long> idList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			 idList.add(list.get(i).getScheduleId());
		}
		String string = idList.toString();
		System.out.println("---------"+string.substring(string.lastIndexOf("[")+1, string.lastIndexOf("]")));
		System.out.println("String: " + string);
		System.out.println(idList);
	}

	@Test
	public void testTime() {
		LocalTime now = LocalTime.now();
		System.out.println("now:" + now);
		now = now.plusMinutes(64);
		System.out.println("after:"+now);
	}
	
}

