package com.kclm.xsap.web.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.service.ClassService;
import com.kclm.xsap.service.CourseScheduleService;
import com.kclm.xsap.service.ReserveService;

@Controller
@RequestMapping("/reserve")
public class ReserveController {


	
}
