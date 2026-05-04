package br.sp.etec.sebrae.OficinaAprender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "br.sp.etec.sebrae.OficinaAprender")
public class OficinaAprenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OficinaAprenderApplication.class, args);
	}

}
