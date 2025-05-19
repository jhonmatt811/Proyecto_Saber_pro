package com.icfes_group;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.icfes_group")
@EnableAsync
@EnableCaching
public class IcfesMainApplication {

    public static void main(String[] args) {
        // Carga el .env
        Dotenv dotenv = Dotenv.configure()
                .filename(".env") // busca el archivo .env en la raíz del proyecto
                .load();

        // Sobreescribir propiedades del sistema para que Spring las encuentre
        System.setProperty("MAIL_HOST", dotenv.get("MAIL_HOST"));
        System.setProperty("MAIL_PORT", dotenv.get("MAIL_PORT"));
        System.setProperty("MAIL_SENDER", dotenv.get("MAIL_SENDER"));
        System.setProperty("MAIL_PASSWD", dotenv.get("MAIL_PASSWD"));
        
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWD", dotenv.get("DB_PASSWD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

        SpringApplication.run(IcfesMainApplication.class, args);
    }
}
