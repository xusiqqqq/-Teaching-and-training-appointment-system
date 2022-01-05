package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kclm.xsap.consts.KeyNameOfCache;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.utils.exception.RRException;
import com.kclm.xsap.utils.file.UploadImg;
import com.kclm.xsap.vo.*;
import com.kclm.xsap.web.cache.MapCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 会员表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Slf4j
@Controller
@RequestMapping("/member")
public class MemberController {
    private static final String UPLOAD_IMAGES_MEMBER_IMG = "upload/images/member_img/";


    @Resource
    private MemberService memberService;

    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Resource
    private ReservationRecordService reservationRecordService;

    @Resource
    private ClassRecordService classRecordService;

    @Resource
    private ConsumeRecordService consumeRecordService;

    @Resource
    private MapCacheService mapCacheService;


    /**
     * 跳转会员列表页面
     *
     * @return x_member_list.html
     */
    @GetMapping("/x_member_list.do")
    public String toMemberList() {
        return "member/x_member_list";
    }

    /**
     * 跳转会员批量导入page
     * @return x_member_import.html
     */
    @GetMapping("/x_member_import.do")
    public String memberBatchImport() {
        return "member/x_member_import";
    }

    /**
     * 跳转添加会员页面
     *
     * @return 添加会员页面
     */
    @GetMapping("/x_member_add.do")
    public String memberAdd() {
        return "member/x_member_add";
    }

    /**
     * todo 会员详情页没有做完
     * 使用mva跳转查看详情
     *
     * @param id 要查看的id
     * @return 详情页面
     */
    @GetMapping("/x_member_list_details.do")
    public ModelAndView memberListDetails(@RequestParam("id") Integer id) {
        log.debug("id:{}", id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("ID", id);
        mv.setViewName("member/x_member_list_details");
        return mv;
    }

    /**
     * 返回会员列表数据
     *
     * @return data
     */
    @PostMapping("/memberList.do")
    @ResponseBody
    public List<MemberVo> memberList() {
        //is_deleted会自动加上
        List<MemberEntity> memberEntityList = memberService.list(new QueryWrapper<MemberEntity>().orderByDesc("create_time"));
        log.debug("\n==>打印数据库查出来的所有的会员信息==>{}", memberEntityList);
        //todo 重写这个方法



//---------------------------------------
//        List<Long> memberIds = memberEntityList.stream().map(MemberEntity::getId).collect(Collectors.toList());


        List<MemberVo> memberVoList = memberEntityList.stream().map(memberEntity -> {
            String[] cardName = memberBindRecordService.getCardName(memberEntity.getId());
            MemberVo memberVo = new MemberVo()
                    .setId(memberEntity.getId())
                    .setMemberName(memberEntity.getName() + "(" + memberEntity.getPhone() + ")")
                    .setGender(memberEntity.getSex())
                    .setCardHold(cardName);
            log.debug("每一次的memberVo{}", memberVo);
            return memberVo;
        }).collect(Collectors.toList());

        log.debug("以下为打印memberVoList=========================");
        memberVoList.forEach(System.out::println);


        log.debug("\n传到前端的Vo:memberVoList：{}", memberVoList);
        return memberVoList;
    }

    @PostMapping("/modifyMemberImg.do")
    @ResponseBody
    public R modifyMemberImg(@RequestParam("id") Long id,
                             @RequestParam("avatarFile") MultipartFile file) {
        log.debug("\n==>前台传入的老师id==>{}", id);

        if (file.isEmpty()) {
            return R.error("文件没有上传,请重新上传");
        }
        String fileName = UploadImg.uploadImg(file, UPLOAD_IMAGES_MEMBER_IMG);
        if (StringUtils.isBlank(fileName)) {
            return R.error("文件上传失败");
        }
        boolean isUpdateImg = memberService.update(new UpdateWrapper<MemberEntity>()
                .set("avatar_url", fileName)
                .set("last_modify_time", LocalDateTime.now())
                .setSql("version = version + 1")
                .eq("id", id));
        log.debug("\n==>打印更新会员头像是否成功==>{}", isUpdateImg);
        if (!isUpdateImg) {
            return R.error("更新会员头像失败！");
        }
        return R.ok("更新会员头像成功！").put("avatarUrl", fileName);
    }


    /**
     * 异步请求返回要编辑的信息
     *
     * @param id 要查看的会员id
     * @return 对应会员信息
     */
    @PostMapping("/x_member_edit.do")
    @ResponseBody
    public R memberEdit(@RequestParam("id") Integer id) {

        MemberEntity entityById = memberService.getById(id);

        log.debug("\n==>根据id查出的要返回的会员信息==>{}", entityById);
        return R.ok().put("data", entityById);
    }


    /**
     * 更新选中信息
     *
     * @param memberEntity 前端传入的要修改的会员的封装实体
     * @return r
     */
    @PostMapping("/memberEdit.do")
    @ResponseBody
    public R memberEdit(MemberEntity memberEntity) {
        Integer version = memberEntity.getVersion();
        memberEntity.setVersion(version + 1);
        boolean isUpdate = memberService.updateById(memberEntity);
        if (isUpdate) {
            return R.ok("更新成功！");
        } else {
            return R.error("更新失败！！");
        }
    }



    /**
     * 删除选中会员（逻辑删除）
     * todo ：修改！！！！修改激活
     * @param id 要删除的会员id
     */
    @PostMapping("/deleteOne.do")
    @ResponseBody
    @Transactional
    public void deleteOne(@RequestParam("id") Integer id) {
        boolean isDeleteMember = memberService.update(new UpdateWrapper<MemberEntity>().set("is_deleted", 1).set("last_modify_time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).setSql("version = version + 1").eq("id", id));
        log.debug("\n==>注销会员是否成功==>{}", isDeleteMember);
        if (isDeleteMember) {

            //充值后删除缓存中原数据
            mapCacheService.getCacheInfo().remove(KeyNameOfCache.CACHE_OF_MEMBER_CARD_INFO);
            log.debug("\n==>充值后删除map缓存中原数据");

            log.debug("\n==>注销会员id={}成功！！",id);

            //找出该会员持有的所有会员卡
            List<MemberBindRecordEntity> allBindListFromMemberId = memberBindRecordService.list(new QueryWrapper<MemberBindRecordEntity>().select("id").eq("member_id", id));
            allBindListFromMemberId.forEach(bind -> {
                int i = memberBindRecordService.updateStatus(bind.getId(), 0);
                if (i < 0) {
                    log.debug("\n==>更新该会员持有的会员卡为非激活是否成功==>{}", i);
                } else {
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            });

        } else {
            //1001表示会员模块异常
            throw new RRException("删除失败！！", 1001502);
        }
    }



    /**
     * 添加会员
     *
     * @param entity 前端传入的表单实体
     * @return 新添加的对象
     */
    @PostMapping("/memberAdd.do")
    @ResponseBody
    public R memberBind(@Valid MemberEntity entity,
                        BindingResult bindingResult) {
        log.debug("\n==>会员添加表单传入的数据memberEntity==>{}", entity);
        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            return R.error(400, "非法参数").put("errorMap", map);
        }
        //mp生成的save方法回自动将新插入的元素的id映射到entity上
        entity.setCreateTime(LocalDateTime.now());
        boolean isSaveEmployee = memberService.save(entity);
        log.debug("\n==>会员添加是否成功==>{}", isSaveEmployee);
        if (isSaveEmployee) {
            return R.ok("添加成功").put("data", entity);
        }
        return R.error("添加失败,请重试");
    }


    /**
     *
     * @return 所有会员
     */
    @GetMapping("/toSearcherAll.do")
    @ResponseBody
    public R toSearchWithAllMember() {
        List<MemberEntity> allMemberList = memberService.list();
        List<SearchMemberToBindVo> allMemberVos = allMemberList.stream().map(member ->
                //借用这个vo
                new SearchMemberToBindVo()
                        .setId(member.getId())
                        .setName(member.getName())
                        .setSex(member.getSex())
                        .setPhone(member.getPhone())
        ).collect(Collectors.toList());
        return R.ok().put("value", allMemberVos);
    }

    /**
     * 返回会员列表，前端使用bootstrap-suggest插件进行给出搜索建议
     *
     * @return 所有拥有会员卡的会员列表的分装对象    【返回有会员卡的会员】
     */
    @GetMapping("/toSearch.do")
    @ResponseBody
    public R toSearch() {

        //初始化值
        List<SearchMemberToBindVo> searchMemberToBindVoList = null;

        //查出所有会员【的id】
        List<MemberBindRecordEntity> allBindCards = memberBindRecordService.list(new QueryWrapper<MemberBindRecordEntity>().select("member_id"));
        //将所有会员的id取出来收集到Long集合
        List<Long> allMemberIdWhoIsHasCardIds = allBindCards.stream().map(MemberBindRecordEntity::getMemberId).collect(Collectors.toList());

        //如果没有会员信息，listByIds会报错,判断一下【少用mp】
        if (!allMemberIdWhoIsHasCardIds.isEmpty() ) {
            List<MemberEntity> memberList = memberService.listByIds(allMemberIdWhoIsHasCardIds);
            searchMemberToBindVoList = memberList.stream().map(member ->
                    new SearchMemberToBindVo()
                            .setId(member.getId())
                            .setName(member.getName())
                            .setSex(member.getSex())
                            .setPhone(member.getPhone())
            ).collect(Collectors.toList());
            log.debug("\n==>返回给前端的封装会员信息列表：searchMemberToBindVoList：=>{}", searchMemberToBindVoList);
        }

        //如果没有会员信息时
        if (null == searchMemberToBindVoList || searchMemberToBindVoList.isEmpty()) {
            return R.error("还没有会员信息").put("value", null);
        }

        return R.ok().put("value", searchMemberToBindVoList);
    }

    /**
     * 会员详情页的会员详情的预加载
     *
     * @param id 会员id
     * @return r -> 会员详情信息
     */
    @PostMapping("/memberDetail.do")
    @ResponseBody
    public R memberDetail(@RequestParam("id") Long id) {
        MemberEntity memberById = memberService.getById(id);
        log.debug("\n==>根据传入id查询到的会员详情信息：==>{}", memberById);
        return new R().put("data", memberById);
    }


    /**
     * 会员详情页的会员卡信息的预加载
     * 绑定记录才是真正的会员卡
     * @param id 前台传入的会员id
     * @return r -> 该会员的所有会员卡list
     */
    @PostMapping("/cardInfo.do")
    @ResponseBody
    public R cardInfo(@RequestParam("id") Long id) {

        //绑定记录才是真正的会员卡信息，我们应该操作绑定记录     这个方法是查询绑定记录表返回vo的list
        List<CardInfoVo> cardInfoVos = memberBindRecordService.getCardInfo(id);
        for (CardInfoVo vo : cardInfoVos) {
            vo.setDueTime(vo.getLastModifyTime() == null ? vo.getCreateTime() : vo.getLastModifyTime());
        }

        log.debug("\n==>后端封装的会员详情页的【会员卡】信息vo -list==>{}", cardInfoVos);

        return new R().put("data", cardInfoVos);

    }


    /**
     * 会员详情页的预约记录的预加载
     *
     * @param id 会员id
     * @return r -> 该会员的所有预约记录
     */
    @PostMapping("/reserveInfo.do")
    @ResponseBody
    public R reserveInfo(@RequestParam("id") Long id) {
        List<ReservationRecordEntity> reserveList = reservationRecordService.getReserveInfo(id);
        log.debug("\n==>从数据库查出的预约记录信息==>{}", reserveList);
        List<MemberDetailReservedVo> memberDetailReservedVoList = reserveList.stream().map(reserve ->
                new MemberDetailReservedVo()
                        .setReserveId(reserve.getId())
                        .setCourseName(reserve.getCourseEntity().getName())
                        .setReserveTime(LocalDateTime.of(reserve.getScheduleRecordEntity().getStartDate(), reserve.getScheduleRecordEntity().getClassTime()))
                        .setCardName(reserve.getCardName())
                        .setReserveNumbers(reserve.getReserveNums())
                        .setTimesCost(reserve.getCourseEntity().getTimesCost())
                        .setOperateTime(null == reserve.getLastModifyTime() ? reserve.getCreateTime() : reserve.getLastModifyTime())
                        .setOperator(reserve.getOperator())
                        .setReserveNote(reserve.getNote())
                        .setReserveStatus(reserve.getStatus())
        ).collect(Collectors.toList());
        log.debug("\n==>后端封装的会员详情页的【预约】信息的vo-list==>{}", memberDetailReservedVoList);
        return new R().put("data", memberDetailReservedVoList);
    }


    /**
     * 会员详情页的上课信息的预加载
     * @param id 会员id
     * @return r-> 上课信息的vo
     */
    @PostMapping("/classInfo.do")
    @ResponseBody
    public R classInfo(@RequestParam("id") Long id) {
        log.debug("会员详情页的上课记录的数据返回...");
        List<ClassInfoVo> classInfoVoList = classRecordService.getClassInfo(id);
        List<ClassInfoVo> classInfoVos = classInfoVoList.stream().map(info ->
                info.setClassTime(LocalDateTime.of(info.getScheduleStartDate(), info.getScheduleStartTime()))
        ).collect(Collectors.toList());
        log.debug("\n==>后台封装的会员详情页的【上课】信息的vo- list==>{}", classInfoVos);

        return new R().put("data", classInfoVos);
    }


    /**
     * 会员详情页的消费记录
     * @param id 会员id
     * @return r -> 消费记录数据
     */
    @PostMapping("/consumeInfo.do")
    @ResponseBody
    public R consumeInfo(@RequestParam("id")Long id) {
        List<ConsumeInfoVo> consumeInfoVoList = consumeRecordService.getConsumeInfo(id);
        for (ConsumeInfoVo vo : consumeInfoVoList) {
            vo.setOperateTime(vo.getLastModifyTime() == null ? vo.getCreateTime() : vo.getLastModifyTime());

            DecimalFormat format = new DecimalFormat("¤00.00");
            vo.setMoneyCost(format.format(vo.getMoneyCostBigD().negate()));

        }

        log.debug("\n==>后台封装的会员详情页的【消费记录】信息的vo-list==>{}", consumeInfoVoList);

        return new R().put("data", consumeInfoVoList);
    }




}
