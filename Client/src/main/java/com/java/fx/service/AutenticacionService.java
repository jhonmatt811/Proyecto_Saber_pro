package com.java.fx.service;

import com.java.fx.controller.ResultadosController;
import com.java.fx.model.Resultado;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Servicio para autenticación y gestión de token JWT.
 */
@Service
public class AutenticacionService {
    private String authToken;

    /**
     * Realiza login en el backend y guarda el token.
     * @param email    usuario (email)
     * @param password contraseña
     * @return true si el login fue exitoso y se obtuvo token
     */
    public boolean login(String email, String password) {
        try {
            URL url = new URL("http://localhost:8080/usuarios/inicio-sesion");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            String payload = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\"}",
                    email, password
            );
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = con.getResponseCode();
            InputStream is = (status >= 200 && status < 300)
                    ? con.getInputStream()
                    : con.getErrorStream();

            JsonNode response = new ObjectMapper()
                    .readTree(is);
            if (response.has("token")) {
                this.authToken = response.get("token").asText();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene el token JWT actual.
     * Si no existe, retorna null.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Limpia el token actual, forzando nuevo login en la próxima petición.
     */
    public void clearToken() {
        this.authToken = null;
    }
}
