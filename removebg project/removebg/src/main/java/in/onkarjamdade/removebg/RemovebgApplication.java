package in.onkarjamdade.removebg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan(basePackages = "in.onkarjamdade.removebg")
@EnableFeignClients
public class RemovebgApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemovebgApplication.class, args);
	}

}
