package com.java.fx.service.UsersService;

import com.java.fx.Usuarios_y_Roles.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UsuarioService {

    public static void crearUsuariosMasivamente(List<Usuario> usuarios) {
        try {
            URL url = new URL("http://localhost:8080/admin/usuarios/lote");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(usuarios);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            int respuesta = conn.getResponseCode();
            System.out.println("Código de respuesta: " + respuesta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

