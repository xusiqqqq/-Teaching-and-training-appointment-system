package com.kclm.xsap.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.dto.MemberDTO;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;

@SpringBootTest
public class MemberServiceTest {

	@Autowired
	MemberService memberService;
	
	@Test
	public void save() {
		TMember member = new TMember();
		memberService.save(member );
	}
	
	@Test
	public void deleteById() {
		memberService.deleteById(2L);
	}

	@Test
	public void update() {
		TMember member = new TMember();
		
		memberService.update(member );
	}

	@Test
	public void findAll() {
		List<TMember> list = memberService.findAll();
		for (TMember member : list) {
			System.out.println("----"+ member);
		}
	}

	@Test
	public void findByPhone() {
		TMember mber = memberService.findByPhone("11112");
		System.out.println(mber);
	}
	
	@Test
	public void findAllByPage() {
		List<TMember> list = memberService.findAllByPage(1, 3);
		for (TMember member : list) {
			System.out.println("----"+ member);
		}
	}

	@Test
	public void findByKeyword() {
		List<TMember> list = memberService.findByKeyword("可根据姓名或手机号查询");
		for (TMember member : list) {
			System.out.println("----"+ member);
		}
	}

	@Test
	public void bindCard() {
		TMemberBindRecord cardBind = new TMemberBindRecord();
		cardBind.setMemberId(2L);
		cardBind.setCardId(4L);
		cardBind.setValidCount(30);
		cardBind.setValidDay(30);
		cardBind.setReceivedMoney(BigDecimal.valueOf(380.85));
		cardBind.setPayMode("线下支付");
		memberService.bindCard(cardBind );
	}

	//涉及到文件读取，待实现
	@Test
	public void saveByBundle() {
		
	}

	//涉及到文件读取，待实现
	@Test
	public void bindByBunble() {
		
	}

	//--------涉及到convert
	@Test
	public void getMemberDetailById() {
		MemberDTO memberDto = memberService.getMemberDetailById(1L);
		System.out.println("会员详情："+ memberDto);
	}
	
}
