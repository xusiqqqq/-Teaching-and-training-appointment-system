package com.kclm.xsap.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年10月23日 上午9:36:15 
 * @description 此类用来给日程控件传递匹配的json对象数据
 *
 */
@Data
public class ScheduleVO {
//以下字段皆为视图控件关键字段
	
	private String title;
	
	private String start;
	
	private String end;
	
	private String color;
	
	private String textColor;
	
	private String url;
	/*
	  {
         "title":"日事件",
          "start": "2020-12-12 14:23:11",
		  "end": "2020-12-13 15:23:11"
           "color": "#ddee44",
           "url": "x_course_schedule_detail.html"
      }
	 */
	
}
