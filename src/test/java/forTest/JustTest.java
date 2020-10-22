package forTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.XsapApplication;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = XsapApplication.class)
public class JustTest {

	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	@Test
	public void testDate() {
//		LocalDate x = LocalDate.now();
//		LocalDate y = x.minusYears(1);
//		LocalDate z = x.plusYears(1);
//		Long i = x.toEpochDay() - y.toEpochDay();
//		Long j = z.toEpochDay() - x.toEpochDay();
//		System.out.println("x "+ x +"-> y"+ y +"间隔(天) i："+i);
//		System.out.println("z "+ z +"-> x"+ x +"间隔(天) j："+j);
//		LocalDateTime d = LocalDateTime.now();
//		System.out.println(d);
//		System.out.println(d.toLocalDate());
		TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
				.eq("card_id", 1L).eq("member_id", 1L));
		System.out.println("---"+ bindRecord);
	}
	
	@Test
	public void json() {


	}
	
}
