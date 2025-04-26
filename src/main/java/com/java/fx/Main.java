package com.java.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Iniciar Spring Boot
		var context = SpringApplication.run(Main.class);

		// Cargar el FXML
		var fxml = new FXMLLoader(getClass().getResource("/Main.fxml"));
		fxml.setControllerFactory(context::getBean); // Importantísimo

		var scene = new Scene(fxml.load());

		// Obtener el título desde Spring
		String titulo = context.getBean("titulo", String.class);

		stage.setScene(scene);
		stage.setTitle(titulo);
		stage.show();
	}

	// Definir el bean 'titulo' para Spring
	@Bean
	public String titulo() {
		return "Mi Portal Académico";
	}
}
