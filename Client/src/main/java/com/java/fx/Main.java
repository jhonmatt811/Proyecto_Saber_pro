package com.java.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.java.fx")
public class Main extends Application {

	private static String[] savedArgs;  // Guardar args para pasarlos a Spring
	private static ConfigurableApplicationContext springContext;  // Contexto de Spring disponible en toda la app

	public static void main(String[] args) {
		savedArgs = args; // Guardamos args
		launch(args); // Lanzar JavaFX
	}

	@Override
	public void init() {
		// Este metodo se ejecuta antes de start, para iniciar Spring Boot
		springContext = SpringApplication.run(Main.class, savedArgs);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Cargar FXML con la fábrica de controladores de Spring
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainLogin.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);

		Scene scene = new Scene(fxmlLoader.load());

		// Obtener título desde Spring context
		String titulo = springContext.getBean("titular", String.class);

		// Icono de la app
		Image icon = new Image(getClass().getResourceAsStream("/img/images.png"));
		stage.getIcons().add(icon);

		stage.setScene(scene);
		stage.setTitle(titulo);
		stage.setMaximized(true);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		// Cerrar el contexto de Spring cuando la app JavaFX termine
		springContext.close();
		super.stop();
	}

	// Bean para el título
	@Bean
	public String titular() {
		return "Login";
	}
}
