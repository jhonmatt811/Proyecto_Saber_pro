package com.icfes_group;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.icfes_group")
public class IcfesMainApplication {

	public static void main(String[] args) {
                Dotenv dotenv = Dotenv.configure()
                              .filename(".env")
                              .load();

                // Sobrescribir propiedades si quieres
                System.setProperty("DB_URL", dotenv.get("DB_URL"));
                System.setProperty("DB_USER", dotenv.get("DB_USER"));
                System.setProperty("DB_PASSWD", dotenv.get("DB_PASSWD"));

                SpringApplication.run(IcfesMainApplication.class, args);
	}

}
