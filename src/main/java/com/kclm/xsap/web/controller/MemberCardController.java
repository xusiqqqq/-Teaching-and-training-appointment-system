package com.kclm.xsap.web.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberLogDTO;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TRechargeRecord;
import com.kclm.xsap.service.CourseService;
import com.kclm.xsap.service.MemberCardService;

@Controller
@RequestMapping("/card")
public class MemberCardController {

	//文件夹名
	private static final String folderName = "member/";
	
	@Autowired
	MemberCardService cardService;
	
	@Autowired
	CourseService courseService;

	/* ==========会员卡操作=========== */
	/*=======页面跳转 */
	//=》会员卡概览页
	@RequestMapping("/x_member_card.do")
	public String x_member_card() {
		return folderName + "x_member_card";
	}
	
	//=》会员卡添加页
	@RequestMapping("/x_member_add_card.do")
	public String x_member_add_card() {
		return folderName + "x_member_add_card";
	}
	
	//=》会员卡编辑页
	@RequestMapping("/x_member_card_edit.do")
	public String x_member_card_edit(Long id,Model model) {
		TMemberCard findOne = cardService.findById(id);
		System.out.println("编辑中: "+findOne);
		String type = findOne.getType();
		System.out.println(type);
		if(type == "次卡(有期限)") {
			findOne.setType("1");
		}else if(type == "次卡(无期限)") {
			findOne.setType("2");
		}else {
			findOne.setType("0");
		}
		//会员卡信息存域
		model.addAttribute("cardMsg", findOne);
		//会员卡关联的课程信息
		List<TCourse> courseList = courseService.listByCardId(id);
		System.out.println("----"+courseList);
		model.addAttribute("courseCarry", "");
		List<Long> ids = new ArrayList<Long>();
		if(courseList != null && courseList.size() > 0) {
			for (TCourse course : courseList) {
				ids.add(course.getId());
			}
			model.addAttribute("courseCarry", ids);
		}
		System.out.println("course:" + ids);
		
		return  folderName + "x_member_card_edit";
	}
	
	/* =======业务处理 */
	//会员卡概览 - 预加载 
	@ResponseBody
	@RequestMapping("/cardList.do")
	public List<TMemberCard> showCardList() {
		List<TMemberCard> cardList = cardService.findAll();
		return cardList;
	}
	
	//会员卡添加
	@RequestMapping("/cardAdd.do")
	public String cardAdd(TMemberCard card,@RequestParam(value = "courseListStr",required = false) List<Long> courseLists,Model model) {
		System.out.println("---------");
		System.out.println(card);
		System.out.println(courseLists);
		System.out.println("---------");
		List<TCourse> courseSet = new ArrayList<>();
		
		if(courseLists != null && courseLists.size() > 0) {
			for (Long cid : courseLists) {
				TCourse course = courseService.findById(cid);
				courseSet.add(course);
			}
		}
		
		//添加关联的课程
		card.setCourseList(courseSet);
		
		//数据录入
		boolean result = cardService.save(card);
		model.addAttribute("NAME_EXIST", "");
		if(!result) {
			model.addAttribute("NAME_EXIST", "已经存在此名称，请更换");
			return "forward:x_member_add_card.do";
		}
		return "forward:x_member_card.do";
	}
	
	//会员卡编辑
	@RequestMapping("/cardEdit.do")
	public String cardEdit(TMemberCard card,@RequestParam(value = "courseListStr",required = false) List<Long> courseLists,Model model) {
		Long id = card.getId();
		System.out.println("---前端id："+id);
		System.out.println("---前端数据："+card);

		//设置课程
		List<TCourse> courseSet = new ArrayList<>();
		
		if(courseLists != null && courseLists.size() > 0) {
			for (Long cid : courseLists) {
				TCourse course = courseService.findById(cid);
				courseSet.add(course);
			}
		}
		//添加关联的课程
		card.setCourseList(courseSet);
		
		//保存原编辑值
		TMemberCard oldCard = cardService.findById(id);
		card.setCreateTime(oldCard.getCreateTime());
		card.setLastModifyTime(LocalDateTime.now());
		card.setVersion(oldCard.getVersion());
		
		TMemberCard findOne = cardService.findByName(card.getName().trim());
		if(findOne != null) {
			model.addAttribute("NAME_EXIST", "");
			//当前的卡名有变化且已存在时
			if(!findOne.getName().equals(oldCard.getName())) {
				model.addAttribute("NAME_EXIST", "已经存在此名称，请更换");
				//返回原值
				return "forward:x_member_card_edit.do";				
			}
		}
		
		cardService.update(card);
		return "forward:x_member_card.do";
	}
	
	//删除单个记录
	@RequestMapping("/deleteOne.do")
	public String deleteOneCard(Long id) {
		System.out.println("delete id : " + id);
			cardService.deleteById(id);
		return "forward:x_member_card.do";
	}
	
	//=====会员卡信息操作 - start
		//充值操作
		@ResponseBody
		@RequestMapping("/rechargeOpt.do")
		public TRechargeRecord rechargeOpt(TRechargeRecord recharge) {
			//提示用。version 4：成功
			TRechargeRecord checkOnly = new TRechargeRecord();
			
			System.out.println("---------");
			System.out.println(recharge);
			recharge.setOperator("某某某操作");
			cardService.recharge(recharge);
		
			checkOnly.setVersion(4);			
			return checkOnly;
		}
		
		//扣费操作
		@ResponseBody
		@RequestMapping("/consumeOpt.do")
		public TConsumeRecord consumeOpt(TConsumeRecord consume) {
			//提示用。version 4：成功
			TConsumeRecord checkOnly = new TConsumeRecord();
			
			System.out.println("---------");
			System.out.println(consume);
			consume.setOperator("某某某操作");
			cardService.consume(consume);
		
			checkOnly.setVersion(4);			
			return checkOnly;
		}
		
		//激活切换操作
		@ResponseBody
		@RequestMapping("/activeOpt.do")
		public MemberCardDTO activeOpt(Long memberId,Long cardId,Integer status,Model model) {
			//提示用。activeStatus 4：成功
			MemberCardDTO cardDto = new MemberCardDTO();
			//针对会员的会员卡状态更新
			TMemberBindRecord findOne = cardService.findBindRecord(memberId, cardId);
			findOne.setActiveStatus(status);
			findOne.setLastModifyTime(LocalDateTime.now());
			cardService.updateBindRecord(findOne);
			
			String statusStr = status == 1 ? "激活" : "未激活"; 
			cardDto.setActiveStatus(4);
			cardDto.setType(statusStr);
			return cardDto;
		}
		
		//操作记录查询
		@ResponseBody
		@RequestMapping("/operateRecord.do")
		public List<MemberLogDTO> operateRecord(Long memberId,Long cardId) {
			List<MemberLogDTO> operateLogList = cardService.listOperateLog(memberId, cardId);
			return operateLogList;
		}
		
		//=====会员卡信息操作 - end
	
		//会员卡搜索 - 全部
		@ResponseBody
		@RequestMapping("/toSearch.do")
		public Map<String, List<TMemberCard>> toSearch() {
			List<TMemberCard> cardList = 	cardService.findAll();
			Map<String , List<TMemberCard>> search = new HashMap<>();
			search.put("value", cardList);
			return search;
		}
		
		//会员卡搜索 - 根据会员id
		@ResponseBody
		@RequestMapping("/toSearchByMemberId.do")
		public List<TMemberCard> searchByMemberId(Long memberId){
			List<TMemberBindRecord> bindCardList = cardService.memberBindCardList(memberId);
			
			List<TMemberCard> cardList =  new ArrayList<TMemberCard>();
			for (TMemberBindRecord bindCard : bindCardList) {
				TMemberCard card = cardService.findById(bindCard.getCardId());
				cardList.add(card);
			}
			return cardList;
		}
		
		//会员卡信息提示回馈
		@ResponseBody
		@RequestMapping("/cardTip.do")
		public TMemberBindRecord cardTipShow(Long memberId,String cardName) {
			//检测用。version 4，成功
			TMemberBindRecord checkFor = new TMemberBindRecord();
			
			Long cardId = cardService.findByName(cardName).getId();
			TMemberBindRecord bindRecord = cardService.findBindRecord(memberId, cardId);
			if(bindRecord == null) {
				checkFor.setNote("该会员没有绑定过此卡");
				return checkFor;
			}
			checkFor.setValidCount(bindRecord.getValidCount());
			checkFor.setVersion(4);
			return checkFor;
		}
		
}
