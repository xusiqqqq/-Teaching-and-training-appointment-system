package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.BeanValidationUtils;
import com.kclm.xsap.utils.ImageUploadUtils;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.CardInfoVo;
import com.kclm.xsap.vo.ClassInfoVo;
import com.kclm.xsap.vo.ConsumeInfoVo;
import com.kclm.xsap.vo.MemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {
    @Resource
    private MemberService memberService;
    @Resource
    private MemberBindRecordService memberBindRecordService;
    @Resource
    private MemberCardService memberCardService;

    @Resource
    private ReservationRecordService reservationService;

    @Resource
    private ClassRecordService classRecordService;

    @Resource
    private ScheduleRecordService scheduleService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private ConsumeRecordService consumeService;

    @GetMapping("/x_member_list.do")
    public String togoMemberList(){
        return "member/x_member_list";
    }

    /**
     * show所有的会员
     * @return
     */
    @PostMapping("/memberList.do")
    @ResponseBody
    public List<MemberVo> getMemberList(){
        return memberService.getAllMemberVo();
    }

    /**
     * 查询所有的会员
     * @return
     */
    @ResponseBody
    @GetMapping("/toSearcherAll.do")
    public R searcherAllMember(){
        return new R().put("value",memberService.list());
    }

    /**
     * 查询所有的会员
     * @return
     */
    @ResponseBody
    @GetMapping("/toSearch.do")
    public R searcherMember(){
        return new R().put("value",memberService.list());
    }


    @GetMapping("/x_member_list_details.do")
    public String togoMemberDetail(Model model, Long id){
        model.addAttribute("ID",id);
        return "member/x_member_list_details";
    }

    /**
     * show会员的详细信息
     * @param id
     * @return
     */
    @PostMapping("/memberDetail.do")
    @ResponseBody
    public R getMemberDetailsById(Long id){
        return new R().put("data",memberService.getById(id));
    }

    /**
     * 通过会员id，查出会员绑卡的全部信息
     * @param id
     * @return
     */
    @PostMapping("/cardInfo.do")
    @ResponseBody
    public R getMemberCardInfo(Long id){
        LambdaQueryWrapper<MemberBindRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberBindRecordEntity::getMemberId,id);
        List<MemberBindRecordEntity> list = memberBindRecordService.list(qw);
        List<CardInfoVo> cardList=new ArrayList<>();
        for (MemberBindRecordEntity bindRecord : list) {
            MemberCardEntity card = memberCardService.getById(bindRecord.getCardId());
            CardInfoVo cardInfoVo=new CardInfoVo();
            cardInfoVo.setBindId(bindRecord.getId());
            cardInfoVo.setName(card.getName());
            cardInfoVo.setType(card.getType());
            cardInfoVo.setTotalCount(bindRecord.getValidCount());
            if(bindRecord.getCreateTime()!=null){
                cardInfoVo.setDueTime(bindRecord.getCreateTime().plusDays(bindRecord.getValidDay()));
            }
            cardInfoVo.setActiveStatus(bindRecord.getActiveStatus());
            cardList.add(cardInfoVo);
        }
        return new R().put("data",cardList);
    }

    /**
     * 通过memberId，查出会员的所有的预约信息
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/reserveInfo.do")
    public R getReserveInfo(Long id){
        //课程	预约时间	会员卡	预约人数	使用次数	操作时间	操作人	预约备注	预约类型
        return R.ok().put("data",reservationService.getReserveByMemberId(id));
    }

    /**
     * 通过memberId，获取会员的上课信息
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/classInfo.do")
    public R getClassInfo(Long id){
        LambdaQueryWrapper<ClassRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ClassRecordEntity::getMemberId,id);
        List<ClassRecordEntity> list = classRecordService.list(qw);
        List<ClassInfoVo> voList=new ArrayList<>();
        for (ClassRecordEntity classRecord : list) {
            ClassInfoVo vo=new ClassInfoVo();
            vo.setClassRecordId(classRecord.getId());
            CourseEntity course = scheduleService.getCourseByScheduleId(classRecord.getScheduleId());

            vo.setCourseName(course.getName());
            ScheduleRecordEntity schedule = scheduleService.getById(classRecord.getScheduleId());
            vo.setClassTime(LocalDateTime.of(schedule.getStartDate(),schedule.getClassTime()));
            vo.setTeacherName(employeeService.getTeacherNameById(schedule.getTeacherId()));
            vo.setCardName(classRecord.getCardName());
            ReservationRecordEntity reserveRecord = classRecordService.getReserveByClassRecord(classRecord);

            if(reserveRecord!=null&&reserveRecord.getReserveNums()!=null){
                vo.setClassNumbers(reserveRecord.getReserveNums());
            }else{
                vo.setClassNumbers(1);
            }
            vo.setTimesCost(course.getTimesCost());
            vo.setComment(classRecord.getComment());
            vo.setCheckStatus(classRecord.getCheckStatus());
            voList.add(vo);
        }
        return R.ok().put("data",voList);
    }

    /**
     * 通过memberId，获取会员的所有消费信息
     * @param id
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/consumeInfo.do")
    public R getConsumeInfo(Long id){
        List<ConsumeInfoVo> voList=new ArrayList<>();
        List<MemberBindRecordEntity> list = memberBindRecordService.list(new LambdaQueryWrapper<MemberBindRecordEntity>().eq(MemberBindRecordEntity::getMemberId, id));
        for (MemberBindRecordEntity bindRecord : list) {
            MemberCardEntity card = memberCardService.getById(bindRecord.getCardId());
            LambdaQueryWrapper<ConsumeRecordEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(ConsumeRecordEntity::getMemberBindId,bindRecord.getId());
            List<ConsumeRecordEntity> consumeList = consumeService.list(qw);
            for (ConsumeRecordEntity consume : consumeList) {
                ConsumeInfoVo vo=new ConsumeInfoVo();
                vo.setConsumeId(consume.getId());
                vo.setCardName(card.getName());
                vo.setOperator(consume.getOperator());
                vo.setOperateTime(consume.getLastModifyTime()==null?consume.getCreateTime():consume.getLastModifyTime());
                vo.setCardCountChange(consume.getCardCountChange());


                QueryWrapper<ConsumeRecordEntity> qw2=new QueryWrapper<>();
                qw2.select("ifnull(sum(card_count_change),0) as cardChangeCountSum").eq("member_bind_id",bindRecord.getId())
                        .gt("create_time",consume.getCreateTime());
                Map<String,Object> queryMap=consumeService.getMap(qw2);
                Integer consumeCountSum =Integer.valueOf(queryMap.get("cardChangeCountSum").toString());
                vo.setTimesRemainder(bindRecord.getValidCount()+consumeCountSum);
                vo.setMoneyCost(consume.getMoneyCost().toString());
                vo.setMoneyCostBigD(consume.getMoneyCost());
                vo.setOperateType(consume.getOperateType());
                vo.setNote(consume.getNote());
                voList.add(vo);
            }
        }
        return R.ok().put("data",voList);
    }

    /**
     * 验证手机号
     * @param phone
     * @return
     */
    @GetMapping("/validPhone")
    @ResponseBody
    public R validPhone(String phone){
        if(phone.isEmpty()) return R.error("手机号不能为空");
        if(!phone.matches("^1[3-9]\\d{9}$")) return R.error("请输入正确的手机号");
        LambdaQueryWrapper<MemberEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberEntity::getPhone,phone);
        if(memberService.count(qw)>0) return R.error("该手机号已经被注册");
        return R.ok("该手机号可用");
    }


    /**
     * 会员添加
     * @return
     */
    @GetMapping("/x_member_add.do")
    public String togoMemberAdd(){
        return "member/x_member_add";
    }
    @PostMapping("/memberAdd.do")
    @Transactional
    @ResponseBody
    public R addMember(@Valid MemberEntity member, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);
        if(!member.getPhone().matches("^1[3-9]\\d{9}$")) return R.error("请输入正确的手机号");
        LambdaQueryWrapper<MemberEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberEntity::getPhone,member.getPhone());
        if(memberService.count(qw)>0) return R.error(401,"该手机号已经被其他用户注册");
        member.setCreateTime(LocalDateTime.now());
        member.setLastModifyTime(LocalDateTime.now());
        boolean b = memberService.save(member);
        if(b) return new R().put("data",member);
        else return R.error();
    }

    /**
     * 编辑更新会员
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/x_member_edit.do")
    public R togoMemberEdit(Long id){
        return new R().put("data",memberService.getById(id));
    }
    @Transactional
    @ResponseBody
    @PostMapping("/memberEdit.do")
    public R updateMember(@Valid MemberEntity member,BindingResult bindingResult){
        if(bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);
        else{
            boolean b = memberService.updateById(member);
            if(b)return R.ok("会员信息更新成功");
            else return R.error("发生未知错误");
        }
    }

    /**
     * 添加会员头像
     * @param id
     * @param avatarFile
     * @return
     */
    @ResponseBody
    @PostMapping("/modifyMemberImg.do")
    public R uploadMemberImg(Long id, MultipartFile avatarFile){
        if(avatarFile.isEmpty())return R.error("图片未上传");
        String newFileName = ImageUploadUtils.uploadMemberImg(avatarFile, "/upload/images/member_img/");
        if(newFileName==null) return R.error("未知错误");
        MemberEntity member = memberService.getById(id);
        //更新会员的头像url地址,存储会员头像
        member.setAvatarUrl(newFileName);
        memberService.updateById(member);
        return  R.ok().put("avatarUrl",member.getAvatarUrl());
    }

    /**
     * 会员注销
     * @param id
     * @return
     */
    @Transactional
    @PostMapping("/deleteOne.do")
    @ResponseBody
    public R deleteMember(Long id){
        //什么情况用户可以被删除
        //1、卡里没有余额
        //2、卡里没有次数

        List<ClassRecordEntity> recordList = classRecordService.list(new LambdaQueryWrapper<ClassRecordEntity>().eq(ClassRecordEntity::getMemberId, id).eq(ClassRecordEntity::getCheckStatus, 0));
        if(!recordList.isEmpty()) return R.error(400,"用户还有未扣费的课程");
        List<MemberBindRecordEntity> bindList = memberBindRecordService.list(new LambdaQueryWrapper<MemberBindRecordEntity>().eq(MemberBindRecordEntity::getMemberId, id));
        for (MemberBindRecordEntity bindRecord : bindList) {
            if(bindRecord.getValidCount()==0) return R.error(400,"用户的会员卡中还有剩余课次");
            if(bindRecord.getActiveStatus()!=0) return R.error(400,"用户的会员卡还处于激活状态");
        }
        MemberEntity member = memberService.getById(id);
        member.setLastModifyTime(LocalDateTime.now());
        boolean b1 = memberService.updateById(member);
        boolean b2 = memberService.removeById(id);
        if(b1&&b2) return R.ok("删除成功");
        else return R.error("删除失败");
    }
}
