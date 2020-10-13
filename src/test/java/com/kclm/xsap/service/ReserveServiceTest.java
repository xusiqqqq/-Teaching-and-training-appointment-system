package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TReservationRecord;

@SpringBootTest
public class ReserveServiceTest {

	@Autowired
	ReserveService reserveService;
	
	@Test
	public void save() {
		TReservationRecord reserve = new TReservationRecord();
		reserve.setScheduleId(1L);
		reserve.setMemberId(1L);
		reserve.setCardName("卡二");
		reserve.setNote("尝试预约");
		reserve.setReserveNums(3);
		reserveService.save(reserve);
	}

	@Test
	public void update() {
		TReservationRecord reserve = new TReservationRecord();
		reserve.setId(1L);
		reserve.setCardName("卡二·改");
		reserveService.update(reserve);
	}

	@Test
	public void listReserved() {
		List<ReserveRecordDTO> reserveList = reserveService.listReserved(1L);
		for (ReserveRecordDTO reserve : reserveList) {
			System.out.println("---"+ reserve);
		}
	}
	
	//-------涉及到convert
	@Test
	public void listReserveRecords() {
		List<ReserveRecordDTO> reserveList = reserveService.listReserveRecords(1L);
		for (ReserveRecordDTO reserve : reserveList) {
			System.out.println("---"+ reserve);
		}
	}
	
	//-------涉及到convert
	@Test
	public void listExportRecord() {
		List<ReserveRecordDTO> exportList = reserveService.listExportRecord(1L);
		for (ReserveRecordDTO export : exportList) {
			System.out.println("----" + export);
		}
	}

	//-------涉及到convert
	@Test
	public void listExportRecordRange() {
		LocalDate startDate = LocalDate.of(2020, 9, 22);
		LocalDate endDate = LocalDate.of(2020, 10, 4);
		List<ReserveRecordDTO> exportRecordRange = reserveService.listExportRecordRange(startDate, endDate);
		for (ReserveRecordDTO export : exportRecordRange) {
			System.out.println("------"+ export);
		}
	}
}
