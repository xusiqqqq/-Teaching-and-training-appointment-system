package com.kclm.xsap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:xsap-config.properties")
public class XsapConfig {

	private Long gap_minute;
	
	public XsapConfig() {
		super();
		System.out.println("分钟--------------");
	}

	public XsapConfig(Long gap_minute) {
		super();
		this.gap_minute = gap_minute;
		System.out.println(gap_minute+"洒洒水分钟--------------");
	}

	
	public Long getGap_minute() {
		return gap_minute;
	}
	
	public void setGap_minute(Long gap_minute) {
		System.out.println("--------------");
		System.out.println(gap_minute);
		this.gap_minute = gap_minute;
	}
}
