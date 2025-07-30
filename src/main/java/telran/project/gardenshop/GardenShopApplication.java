package telran.project.gardenshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GardenShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(GardenShopApplication.class, args);
	}

}