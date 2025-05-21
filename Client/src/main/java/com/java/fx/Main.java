package com.java.fx;

//Main principal

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.java.fx")
public class Main extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Iniciar Spring Boot
		var context = SpringApplication.run(Main.class);

		// Cargar el FXML
		var fxml = new FXMLLoader(getClass().getResource("/MainLogin.fxml"));
		fxml.setControllerFactory(context::getBean); // Importantísimo

		var scene = new Scene(fxml.load());

		String titulo = context.getBean("titular", String.class);
		stage.getIcons().add(new Image(getClass().getResource("/img/images.png").toExternalForm()));

		stage.setScene(scene);

		stage.setTitle(titulo);
		stage.setMaximized(true);
		stage.show();

	}

	@Bean
	public String titular() {
		return "Login";
	}
}