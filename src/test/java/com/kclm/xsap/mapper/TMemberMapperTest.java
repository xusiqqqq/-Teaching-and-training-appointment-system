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
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberVO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TConsumeRecord;


@SpringBootTest
public class TMemberMapperTest {

	@Autowired
	private TMemberMapper memberMapper;
	
	@Test
	public void memberVO() {
		System.out.println(LocalDateTime.now());
//		List<MemberVO> listMemberVo = memberMapper.listMemberVo();
//		toPrint("memberVO", listMemberVo.size(), listMemberVo);
	}

	@Test
	public void memberCardView() {
		List<MemberCardDTO> memberCardView = memberMapper.listMemberCardView(1L);
		toPrint("会员卡信息", memberCardView.size(), memberCardView);
	}
	
	@Test
	public void listReserveView() {
		List<ReserveRecordDTO> listReserveView = memberMapper.listReserveView(1L);
		toPrint("预约记录", listReserveView.size(), listReserveView);
	}
	
	@Test
	public void listClassView() {
		List<ClassRecordDTO> listClassView = memberMapper.listClassView(1L);
		toPrint("上课记录", listClassView.size(), listClassView);
	}

	@Test
	public void listConsumeView() {
		List<ConsumeRecordDTO> listConsumeView = memberMapper.listConsumeView(1L);
		toPrint("消费记录", listConsumeView.size(), listConsumeView);
	}
	
		//====== 通用方法 ======//
		
		//打印到控制台-普通类型
		private void toPrint(String type,Integer num,Object obj) {
			System.out.println("-------------");
			System.out.println("  【" + type + "】共" + num + "条记录");
			if(obj != null)
				System.out.println("=》 " + obj);
			System.out.println("-------------");
		}
		//打印到控制台-集合类型
		private void toPrint(String type,Integer num,List<? extends Object> objList) {
			System.out.println("-------------");
			System.out.println("  【" + type + "】共" + num + "条记录");
			if(objList != null) {
				for (Object obj : objList) {
					System.out.println("=》 " + obj);
				}
			}
			System.out.println("-------------");
		}
	
}
