package com.kclm.xsap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    //获取start和end时间之内注销用户的数量
    @Select("select count(*) from t_member where is_deleted=1  and last_modify_time between #{start} and #{end}")
    Integer getDeletedCountsBetween(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    //获取当月所有的流失用户的信息
    @Select("select * from t_member where is_deleted=1 and last_modify_time>#{start}")
    List<MemberEntity> getDeletedMemberCurrentMonth(@Param("start") LocalDateTime start);

    @Select("select count(distinct(m.id)) from t_member m,t_reservation_record rr where m.id=rr.member_id and m.is_deleted=0 and rr.create_time>#{oneMonthAgo}")
    Integer getActiveMembers(@Param("oneMonthAgo") LocalDateTime oneMonthAgo);

}
