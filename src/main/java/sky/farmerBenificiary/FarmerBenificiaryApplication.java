package sky.farmerBenificiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import sky.farmerBenificiary.jwt.ApplicationConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"sky.farmerBenificiary"})
@EnableConfigurationProperties(value = {ApplicationConfiguration.class})
public class FarmerBenificiaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmerBenificiaryApplication.class, args);
	}

}
