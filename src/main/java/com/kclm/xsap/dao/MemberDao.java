package com.kclm.xsap.dao;

import com.kclm.xsap.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.vo.MemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员表
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    List<MemberVo> selectMemberVoList();

    @Select("select last_modify_time from t_member where is_deleted = 1 and last_modify_time rlike ${yearOfSelect} order by last_modify_time desc")
    List<MemberEntity> selectMemberLogOutSpecifyYear(@Param("yearOfSelect") Integer endYear);


    @Select("select last_modify_time from t_member where is_deleted = 1 order by last_modify_time desc")
    List<MemberEntity> getMemberLogOutFromBeginYearToEndYear();

    @Select("select last_modify_time from t_member where is_deleted = 1 and year(last_modify_time)=2021 and month(last_modify_time)=12 order by last_modify_time")
    List<MemberEntity> selectCurrentMonthLogoutMemberInfo(@Param("year") Integer currentYear,@Param("month") Integer currentMonth);
}
