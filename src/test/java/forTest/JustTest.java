package forTest;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.XsapApplication;

@SpringBootTest(classes = XsapApplication.class)
public class JustTest {

	@Test
	public void testDate() {
		LocalDate x = LocalDate.now();
		LocalDate y = x.minusYears(1);
		LocalDate z = x.plusYears(1);
		Long i = x.toEpochDay() - y.toEpochDay();
		Long j = z.toEpochDay() - x.toEpochDay();
		System.out.println("x "+ x +"-> y"+ y +"间隔(天) i："+i);
		System.out.println("z "+ z +"-> x"+ x +"间隔(天) j："+j);
	}
	
}
