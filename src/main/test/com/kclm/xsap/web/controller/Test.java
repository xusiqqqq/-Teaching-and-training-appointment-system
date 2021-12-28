package com.kclm.xsap.web.controller;

import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.entity.GlobalReservationSetEntity;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.utils.R;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author fangkai
 * @description
 * @create 2021-12-09 13:17
 */
@Slf4j
public class Test {

    @org.junit.jupiter.api.Test
    void test15(){
        Date date = new Date();

        LocalDate now = LocalDate.now();
        System.out.println(date);
        System.out.println();
        System.out.println(now);

        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println(zoneId);
    }

    @org.junit.jupiter.api.Test
    void test14() {
        for (int i = 1; i <= 0; i++) {
            System.out.println("*");

        }
    }

    @org.junit.jupiter.api.Test
    void test13() {
        LocalDateTime parse = LocalDateTime.parse("2020-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(parse);
    }

    @org.junit.jupiter.api.Test
    void test12() {
        ArrayList<BigDecimal> list = new ArrayList<>();
        list.add(new BigDecimal(1.1));
        list.add(new BigDecimal(2.11));
        list.add(new BigDecimal(2.1));
        list.add(new BigDecimal(1));
        BigDecimal reduce = list.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println(reduce);
    }

    @org.junit.jupiter.api.Test
    void test11() {
        LocalDate now = LocalDate.now();
        System.out.println(now);
        int dayOfMonth = now.getDayOfMonth();
        System.out.println(dayOfMonth);

        Calendar instance = Calendar.getInstance();
        System.out.println(instance);
    }

    @org.junit.jupiter.api.Test
    void test10() {
        LocalDate now = LocalDate.now().minusYears(15);
        long until = now.until(LocalDate.now(), ChronoUnit.YEARS);
        System.out.println(until);

    }

    @org.junit.jupiter.api.Test
    void test09() {
        ReservationRecordEntity en = new ReservationRecordEntity();
        System.out.println(en.getId());
        if (null == en.getId()) {
            System.out.println("**");
        }

        try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @org.junit.jupiter.api.Test
    void test08() {
        R r = getR();
        System.out.println(r);
    }

    public R getR(){
        return R.ok("success");
    }

    @org.junit.jupiter.api.Test
    void test07() {
        String format = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println(format);
    }


    @org.junit.jupiter.api.Test
    void test06() {
        CourseEntity test = new CourseEntity().setId(3L).setDuration(45L).setLimitCounts(34);
        R data = new R().put("data", test);
        System.out.println(data);
    }

    @org.junit.jupiter.api.Test
    void test05() {
        String format = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(format);
    }

    @org.junit.jupiter.api.Test
    void test04() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate now = LocalDate.now();
        String now1 = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(now);
        System.out.println(now1);
        String s = now.toString();
        String s1 = now1.toString();
        LocalDateTime parse = LocalDateTime.parse((s +" " + s1),dtf);
        System.out.println(parse);

    }

    @org.junit.jupiter.api.Test
    public void test01() {
        String s = "-1";
        String[] split = s.split(",");
        System.out.println(Arrays.toString(split));
    }


    @org.junit.jupiter.api.Test
    void test02() {
        String s = "";
        s = null;
        System.out.println(s.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void test03() {
        Long l = null;
        String.valueOf(l);
    }
}
