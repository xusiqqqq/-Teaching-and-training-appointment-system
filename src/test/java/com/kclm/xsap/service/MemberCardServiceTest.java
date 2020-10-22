package com.kclm.xsap.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.MemberLogDTO;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TRechargeRecord;
import com.kclm.xsap.mapper.TCourseMapper;

@SpringBootTest
public class MemberCardServiceTest {

	@Autowired
	MemberCardService cardService;
	
	@Autowired
	TCourseMapper courseMapper;
	
	@Test
	public void save() {
		TMemberCard card = new TMemberCard();
		card.setName("体验卡");
		card.setPrice(BigDecimal.valueOf(12.5));
		card.setDescription("免费使用10次");
		card.setNote("值得拥有");
		card.setType("有限次数100");
		card.setTotalCount(30);
		card.setTotalDay(15);
		List<TCourse> courseList = courseMapper.selectList(new QueryWrapper<TCourse>().in("id", 1,2));
		card.setCourseList(courseList);
		cardService.save(card);
	}

	@Test
	public void deleteById() {
		cardService.deleteById(14L);
	}

	@Test
	public void update() {
		TMemberCard card = new TMemberCard();
		card.setId(3L);
		card.setName("优惠卡");
		cardService.update(card );
	}

	@Test
	public void findAll() {
		List<TMemberCard> list = cardService.findAll();
		for (TMemberCard card : list) {
			System.out.println("---"+card);
		}
	}

	@Test
	public void findAllByPage() {
		List<TMemberCard> list = cardService.findAllByPage(1, 3);
		for (TMemberCard card : list) {
			System.out.println("---"+card);
		}
	}

	@Test
	public void recharge() {
		TRechargeRecord recharge = new TRechargeRecord();
		recharge.setMemberId(1L);
		recharge.setCardId(1L);
		recharge.setAddCount(5);
		recharge.setAddDay(10);
		recharge.setReceivedMoney(BigDecimal.valueOf(120.9));
		recharge.setPayMode("支付宝");
		recharge.setOperator("蚂蚁");
		cardService.recharge(recharge);
	}

	@Test
	public void consume() {
		TConsumeRecord consume = new TConsumeRecord();
		consume.setMemberId(2L);
		consume.setCardId(1L);
		consume.setCardCountChange(3);
		consume.setCardDayChange(6);
		consume.setMoneyCost(BigDecimal.valueOf(9.9));
		consume.setOperateType("扣款");
		consume.setOperator("熊");
		cardService.consume(consume );
	}

	//-----涉及convert
	@Test
	public void listOperateLog() {
		List<MemberLogDTO> operateLogList = cardService.listOperateLog(1L, 1L);
		for (MemberLogDTO log : operateLogList) {
			System.out.println("---------");
			System.out.println(log);
			System.out.println("---------");
		}
	}
}
