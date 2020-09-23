package forTest;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
public class JustTest {

	@Test
	public void testDate() {
		LocalDate x = LocalDate.now();
		LocalDate y = x.minusYears(1);
		Long i = x.toEpochDay() - y.toEpochDay();
		System.out.println("间隔(天)："+i);
	}
	
}
