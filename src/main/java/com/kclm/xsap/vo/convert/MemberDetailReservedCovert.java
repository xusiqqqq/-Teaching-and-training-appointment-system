//package com.kclm.xsap.vo.convert;
//
//import com.kclm.xsap.entity.ReservationRecordEntity;
//import com.kclm.xsap.vo.MemberDetailReservedVo;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.Mappings;
//
///**
// * @author fangkai
// * @description
// * @create 2021-12-18 13:17
// */
//@Mapper(componentModel = "spring")
//public interface MemberDetailReservedCovert {
//
//    @Mappings({
//            @Mapping(source = "reserve.id", target = "reserveId"),
//            @Mapping(source = "reserve.courseEntity.name", target = "courseName"),
//            @Mapping(source = "reserve.s")
//    })
//    MemberDetailReservedVo entity2Vo(ReservationRecordEntity reserve);
//}
