package com.kclm.xsap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.kclm.xsap.vo.BindCardCountsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberBindRecordDao extends BaseMapper<MemberBindRecordEntity> {

    //通过会员id，查出他所拥有的会员卡名称
    @Select("select c.name from t_member_card as c,t_member_bind_record as r where c.id=r.card_id and r.active_status=1 and c.status=0 and r.member_id=#{memberId}")
    String[] getMemberCardsNameByMemberId(@Param("memberId") Long memberId);

    //通过会员id，查出他所拥有的有效的卡
    @Select("select c.id from t_member_card as c,t_member_bind_record as r where c.id=r.card_id and r.active_status=1 and c.status=0 and r.member_id=#{memberId}")
    Integer[] getMemberCardsIdByMemberId(@Param("memberId") Long memberId);

    //获取每张卡绑定的数量
    @Select("select c.name,count(br.card_id) as value from t_member_bind_record br right join t_member_card c on br.card_id=c.id where c.status=0 GROUP BY c.id")
    List<BindCardCountsVo> getCardBindCounts();


    @Select("select br.* from t_member_bind_record br,t_member m where m.id=br.member_id and m.is_deleted=0")
    List<MemberBindRecordEntity> getValidBindList();
}
