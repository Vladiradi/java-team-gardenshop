package telran.project.gardenshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.liquibase.enabled=false",
		"jwt.signing.key=someDummyKeyForTests"
})
class JavaTeamGardenshopApplicationTests {

	@Test
	void contextLoads() {
	}

}
