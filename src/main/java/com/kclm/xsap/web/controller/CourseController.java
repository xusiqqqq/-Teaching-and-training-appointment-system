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

import com.kclm.xsap.dto.CourseDTO;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.service.CourseService;
import com.kclm.xsap.service.MemberCardService;

@Controller
@RequestMapping("/course")
public class CourseController {

	//文件夹名
	private static final String folderName = "course/";
	
	@Autowired
	CourseService courseService;
	
	@Autowired
	MemberCardService cardService;
	
	//加载课程信息
	@ResponseBody
	@RequestMapping("/courseList.do")
	public List<CourseDTO> courseData() {
		List<CourseDTO> courseList = courseService.findAll();
		System.out.println("--------courseList---------");
		for (CourseDTO courseDTO : courseList) {
			System.out.println(courseDTO);
		}
		System.out.println("-----------------");
		return courseList;
	}
	
	/*========页面跳转=======*/
	//=》课程概览页
	@RequestMapping("/x_course_list.do")
	public String x_course_list() {
		
		return folderName + "x_course_list";
	}
	
	//=》课程添加页
	@RequestMapping("/x_course_list_add.do")
	public String x_course_list_add() {
		return folderName + "x_course_list_add";
	}
	
	//=》课程编辑页
	@RequestMapping("/x_course_list_edit.do")
	public String x_course_list_edit(Long id,Model model) {
		 TCourse findOne = courseService.findById(id);
		 //课程信息存域
		 model.addAttribute("courseMsg", findOne);
		 //关联的会员卡信息
		 List<TMemberCard> cardList = cardService.listByCourseId(id);
		 System.out.println("-----" + cardList);
		 model.addAttribute("cardCarry", "");
			List<Long> ids = new ArrayList<Long>();
			if(cardList != null && cardList.size() > 0) {
				for (TMemberCard card : cardList) {
					ids.add(card.getId());
				}
				model.addAttribute("cardCarry", ids);
			}
			System.out.println("card:" + ids);
		 
		return  folderName + "x_course_list_edit";
	}
	
	
	/*========业务处理=======*/
	//课程添加
	@RequestMapping("/courseAdd.do")
	public String courseAdd(TCourse course,@RequestParam(value = "cardListStr",required = false) List<Long> cardLists,Model model,
			String limitAgeRadio,String limitCountsRadio) {
		System.out.println("---------");
		System.out.println(course);
		System.out.println("---------");
		
		//会员卡列表
		List<TMemberCard> cardSet = new ArrayList<>();
		if(cardLists != null && cardLists.size() > 0) {
			for (Long caid : cardLists) {
				TMemberCard card = cardService.findById(caid);
				cardSet.add(card);
			}
		}
		//添加关联的会员卡
		course.setCardList(cardSet);
		//不限制年龄
		if(limitAgeRadio.equals("-1")) {
			course.setLimitAge(0);
		}
		//不限制性别
		if(limitCountsRadio.equals("-1")) {
			course.setLimitCounts(0);
		}
		
		//数据录入
		boolean result = courseService.save(course);
		model.addAttribute("NAME_EXIST", "");
		if(!result) {
			model.addAttribute("NAME_EXIST", "名称已存在，请更换");
			return "forward:x_course_list_add.do";
		}
		
		return "forward:x_course_list.do";
	}
	
	//课程编辑
	@RequestMapping("/courseEdit.do")
	public String courseEdit(TCourse course,@RequestParam(value = "cardListStr",required = false) List<Long> cardLists,Model model,
			String limitAgeRadio,String limitCountsRadio) {
		Long id = course.getId();
		System.out.println("---前端id："+id);
		System.out.println("---前端数据："+course);

		//设置会员卡
		List<TMemberCard> cardSet = new ArrayList<>();
		if(cardLists != null && cardLists.size() > 0) {
			for (Long caid : cardLists) {
				TMemberCard card = cardService.findById(caid);
				cardSet.add(card);
			}
		}
		//添加关联的会员卡
		course.setCardList(cardSet);
		
		//不限制年龄
		if(limitAgeRadio.equals("-1")) {
			course.setLimitAge(0);
		}
		//不限制性别
		if(limitCountsRadio.equals("-1")) {
			course.setLimitCounts(0);
		}
		
		//保存原编辑值
		TCourse oldCourse = courseService.findById(id);
		course.setCreateTime(oldCourse.getCreateTime());
		course.setLastModifyTime(LocalDateTime.now());
		course.setVersion(oldCourse.getVersion());
		
		TCourse findOne = courseService.findByName(course.getName().trim());
		if(findOne != null) {
			model.addAttribute("NAME_EXIST", "");
			//当前的课程名有变化且已存在时
			if(!findOne.getName().equals(oldCourse.getName())) {
				model.addAttribute("NAME_EXIST", "名称已存在，请更换");
				//返回原值
				return "forward:x_course_list_edit.do";				
			}
		}
		
		courseService.update(course);
		return "forward:x_course_list.do";
	}
	
	//删除单个记录
	@ResponseBody
	@RequestMapping("/deleteOne.do")
	public String deleteOneCard(Long id) {
		System.out.println("delete id : " + id);
		boolean result = courseService.deleteById(id);
		if(result == false) {
			return "no";
		}
		
		return "yes";
	}
	
	//课程搜索
	@ResponseBody
	@RequestMapping("/toSearch.do")
	public Map<String, List<CourseDTO>> toSearch() {
		List<CourseDTO> courseList = new ArrayList<>();
		courseList = courseService.findAll();
		System.out.println("------------");
		for (CourseDTO courseDTO : courseList) {
			System.out.println(courseDTO);			
		}
		System.out.println("------------");
		Map<String , List<CourseDTO>> search = new HashMap<>();
		search.put("value", courseList);
		return search;
	}
	
	//拿到一条课程的数据
	@ResponseBody
	@RequestMapping("/getOneCourse.do")
	public TCourse getOneCourse(Long id) {
		TCourse course = courseService.findById(id);
		return course;
	}
}
