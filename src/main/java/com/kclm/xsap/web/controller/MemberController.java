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
import org.springframework.web.multipart.MultipartFile;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberVO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.service.MemberService;
import com.kclm.xsap.utils.FileUploadUtil;

@Controller
@RequestMapping("/member")
public class MemberController {

	//文件夹名
	private static final String folderName = "member/";
	
	@Autowired
	MemberService memberService;
	
	/* ============会员操作========= */
	/* =======页面跳转 */
	//=》会员列表概览
	@RequestMapping("/x_member_list.do")
	public String x_member_list() {
		return folderName + "x_member_list";
	}
	//=》添加会员
	@RequestMapping("/x_member_add.do")
	public String x_member_add() {
		return folderName + "x_member_add";
	}
	//=》批量导入会员
	@RequestMapping("/x_member_import.do")
	public String x_member_import() {
		return folderName + "x_member_import";
	}
	
	//=》会员详情
	@RequestMapping("/x_member_list_details.do")
	public String x_member_list_details(Long id, Model model) {
		model.addAttribute("ID", id);
		return folderName + "x_member_list_details";
	}
	
	/* =======业务处理 */
	//会员信息概览 - 预加载
	@ResponseBody
	@RequestMapping("/memberList.do")
	public List<MemberVO> memberList() {
		List<MemberVO> memberList = memberService.listMemberView();;
		return memberList;
	}

	//======= 会员完整信息项 - start
	//会员基本信息 - 预加载
	@ResponseBody
	@RequestMapping("/memberDetail.do")
	public TMember memberDetail(Long id) {
		TMember member = memberService.getMember(id);
		return member;
	}

	//会员卡信息 - 预加载
	@ResponseBody
	@RequestMapping("/cardInfo.do")
	public List<MemberCardDTO> cardInfo(Long id){
		List<MemberCardDTO> cardRecords = memberService.listCardRecords(id);
		return cardRecords;
	}
	
	//预约记录 - 预加载
	@ResponseBody
	@RequestMapping("/reserveInfo.do")
	public List<ReserveRecordDTO> reserveInfo(Long id){
		 List<ReserveRecordDTO> reserveRecords = memberService.listReserveRecords(id);
		return reserveRecords;
	}
	
	//上课记录 - 预加载
	@ResponseBody
	@RequestMapping("/classInfo.do")
	public List<ClassRecordDTO> calssInfo(Long id){
		 List<ClassRecordDTO> classRecords = memberService.listClassRecords(id);
		return classRecords;
	}
	
	//消费记录 - 预加载
	@ResponseBody
	@RequestMapping("/consumeInfo.do")
	public List<ConsumeRecordDTO> consumeInfo(Long id){
		 List<ConsumeRecordDTO> consumeRecords = memberService.listConsumeRecords(id);
		return consumeRecords;
	}
	
	//======= 会员完整信息项 - end
	
	//会员编辑 - 预加载
	@ResponseBody
	@RequestMapping("/x_member_edit.do")
	public TMember x_member_edit(Long id) {
		TMember member = memberService.getMember(id);
		return member;
	}
	
	//会员添加
	@ResponseBody
	@RequestMapping("/memberAdd.do")
	public TMember memberAdd(TMember member) {
		//为了页面数据判断。version 4，数据更新成功；
		TMember forCheck = new TMember();
		
		System.out.println("----------");
		System.out.println(member);
		System.out.println("----------");
		
		//手机号已存在的提示
		TMember oldMember = null;
		if(member.getPhone().length() > 0) {
			oldMember = memberService.findByPhone(member.getPhone());
		}
		if(oldMember != null) {
			forCheck.setNote("手机号跟其他会员重了");
			return forCheck;
		}
		
		//数据录入
		memberService.save(member);
		//拿到刚录入数据库的用户id
		TMember findOne = memberService.findByPhone(member.getPhone());
		
		forCheck.setId(findOne.getId());
		forCheck.setVersion(4);
		return forCheck;
	}
	
	//会员编辑
	@ResponseBody
	@RequestMapping("/memberEdit.do")
	public TMember memberEdit(TMember member) {
		//为了页面数据判断。version 4，数据更新成功；
		TMember forCheck = new TMember();
		
		Long id = member.getId();;
		System.out.println("---id " + id);		
		System.out.println("---前端表单数据 " + member);
		
		TMember oldMember = memberService.getMember(id);
		member.setCreateTime(oldMember.getCreateTime());
		member.setLastModifyTime(LocalDateTime.now());
		member.setVersion(oldMember.getVersion());
		//判断手机号是否存在
		TMember checkPhone = memberService.findByPhone(member.getPhone());
		if(checkPhone != null) {
			//校检手机号的提示信息
			if(!checkPhone.getPhone().equals(oldMember.getPhone()) ) {
				forCheck.setNote("手机号跟其他会员重了");
				//返回原值
				return forCheck;
			}
		}	
		memberService.update(member);
		
		forCheck.setVersion(4);
		return forCheck;
	}
	
	//单个会员删除
	@RequestMapping("/deleteOne.do")
	public String deleteOne(Long id) {
		System.out.println("delete id : " + id);
			memberService.deleteById(id);
		return "forward:x_member_list.do";
	}
	
	//1、修改图像信息
	@RequestMapping(value = "/modifyMemberImg.do",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public TMember modifyMemberImg(@RequestParam("avatarFile") MultipartFile avatarFile,Long id) {
		System.out.println("=====avatar: " + avatarFile);
		System.out.println("=====id: " + id);
		//查询到当前的员工信息
		TMember oldMember = memberService.getMember(id);
		if(!avatarFile.isEmpty()) {
			//上传文件
			String fileName;
			try {
				fileName = FileUploadUtil.uploadFiles(avatarFile);
				//设置图片全名
				oldMember.setAvatarUrl(fileName);
			} catch (Exception e) {
				System.out.println("-----------图片信息有误！-----------");
				e.printStackTrace();
			}
		}
		TMember member = memberService.update(oldMember);
		System.out.println("---------member---------");
		System.out.println(member);
		System.out.println("---------");
		return member;
	}
	
	
	//会员搜索
	@ResponseBody
	@RequestMapping("/toSearch.do")
	public Map<String, List<TMember>> toSearch(String condition) {
		List<TMember> mberList = new ArrayList<TMember>();
		if(condition != null) {
			mberList = memberService.findByKeyword(condition);			
		}
		mberList = memberService.findAll();
		
		Map<String , List<TMember>> search = new HashMap<>();
		search.put("value", mberList);
		return search;
	}
	
}
