package com.java.fx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.java.fx.model.AccionesDeMejora.Modulo;
import com.java.fx.model.AccionesDeMejora.Programa;
import com.java.fx.model.AccionesDeMejora.SugerenciaMejora;
import com.java.fx.service.ResultadoService;
import com.java.fx.model.Resultado;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.ComboBox;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccionesMejoraController {
    //no implementadas


    //implementadas
    @Autowired private ResultadoService resultadoService; // Inyectar el servicio
    @FXML private ComboBox<Programa> comboProgramas;
    @FXML private ComboBox<Modulo> comboModulos;
    @FXML private TextArea txtSugerencia;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;

    @FXML
    public void initialize() {
        configurarComboBox();
        cargarDatosDesdeAPI();
    }

    private void cargarDatosDesdeAPI() {
        try {
            // Llamar al servicio con parámetros null (sin filtros)
            List<Resultado> resultados = resultadoService.obtenerResultados(null, null, null, null);

            // Usa Set para eliminar duplicados automáticamente
            Set<Programa> programasUnicos = new HashSet<>();
            Set<Modulo> modulosUnicos = new HashSet<>();

            for (Resultado resultado : resultados) {
                // Crear y agregar Programa si no existe
                if (resultado.getProgramaAM() != null) {
                    Programa programa = new Programa();
                    programa.setId(resultado.getProgramaAM().getId());
                    programa.setSnies(resultado.getProgramaAM().getSnies());
                    programa.setNombre(resultado.getProgramaAM().getNombre());
                    programasUnicos.add(programa); // El Set ignora duplicados gracias a equals/hashCode
                }
                // Crear y agregar Módulo si no existe
                if (resultado.getModuloAM() != null) {
                    Modulo modulo = new Modulo();
                    modulo.setId(resultado.getModuloAM().getId());
                    modulo.setNombre(resultado.getModuloAM().getNombre());
                    modulosUnicos.add(modulo); // El Set ignora duplicados
                }
            }
            // Llenar ComboBox de Programas
            comboProgramas.getItems().setAll(
                    resultados.stream()
                            .map(r -> {
                                Programa p = new Programa();
                                p.setSnies(r.getSniesProgramaAcademico());
                                p.setNombre(r.getPrograma());
                                return p;
                            })
                            .distinct() // Eliminar duplicados
                            .collect(Collectors.toList())
            );

            // Llenar ComboBox de Módulos
            comboModulos.getItems().setAll(
                    resultados.stream()
                            .map(r -> {
                                Modulo m = new Modulo();
                                m.setNombre(r.getModulo());
                                return m;
                            })
                            .distinct() // Eliminar duplicados
                            .collect(Collectors.toList())
            );

        } catch (IOException | InterruptedException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void configurarComboBox() {
        // Para ComboBox de Programas
        comboProgramas.setCellFactory(lv -> new ListCell<Programa>() {
            @Override
            protected void updateItem(Programa programa, boolean empty) {
                super.updateItem(programa, empty);
                setText(empty ? null : programa.getNombre());
            }
        });
        comboProgramas.setButtonCell(new ListCell<Programa>() {
            @Override
            protected void updateItem(Programa programa, boolean empty) {
                super.updateItem(programa, empty);
                setText(empty ? null : programa.getNombre());
            }
        });

        // Para ComboBox de Módulos
        comboModulos.setCellFactory(lv -> new ListCell<Modulo>() {
            @Override
            protected void updateItem(Modulo modulo, boolean empty) {
                super.updateItem(modulo, empty);
                setText(empty ? null : modulo.getNombre());
            }
        });
        comboModulos.setButtonCell(new ListCell<Modulo>() {
            @Override
            protected void updateItem(Modulo modulo, boolean empty) {
                super.updateItem(modulo, empty);
                setText(empty ? null : modulo.getNombre());
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleAdjuntarEvidencia() {
    }

    @FXML
    private void handleGuardarAccion() {
        try {
            // Validaciones
            if (comboProgramas.getValue() == null || comboModulos.getValue() == null) {
                mostrarAlerta("Error", "Selecciona un programa y un módulo.", Alert.AlertType.ERROR);
                return;
            }
            if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
                mostrarAlerta("Error", "Selecciona fechas válidas.", Alert.AlertType.ERROR);
                return;
            }
            if (txtSugerencia.getText().isBlank()) {
                mostrarAlerta("Error", "La sugerencia no puede estar vacía.", Alert.AlertType.ERROR);
                return;
            }

            // Crear objeto
            SugerenciaMejora sugerencia = new SugerenciaMejora();
            sugerencia.setPrograma(comboProgramas.getValue());
            sugerencia.setModulo(comboModulos.getValue());
            sugerencia.setSugerenciaMejora(txtSugerencia.getText());
            sugerencia.setYearInicio(dpFechaInicio.getValue().getYear());
            sugerencia.setYearFin(dpFechaFin.getValue().getYear());

            // Serializar
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(sugerencia);

            // Enviar al API
            resultadoService.enviarSugerencia(json);

            // Éxito
            mostrarAlerta("Éxito", "Sugerencia guardada.", Alert.AlertType.INFORMATION);
            handleLimpiarFormulario();

        } catch (JsonProcessingException e) {
            mostrarAlerta("Error", "Error en el JSON: " + e.getOriginalMessage(), Alert.AlertType.ERROR);
        } catch (IOException | InterruptedException e) {
            mostrarAlerta("Error", "Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handleLimpiarFormulario() {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}