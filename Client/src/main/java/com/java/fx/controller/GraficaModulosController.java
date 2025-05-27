package com.java.fx.controller;
import com.java.fx.model.Resultado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GraficaModulosController
{

    @FXML private BarChart<String, Number> graficaPuntajes;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    private ObservableList<Resultado> datos;

    @FXML private TextField umbralCriticoInput;
    private double umbralActual = Double.MAX_VALUE;

    public void inicializarDatos(List<Resultado> datos) {
        this.datos = FXCollections.observableArrayList(datos);
        actualizarGrafica();
    }

    @FXML
    private void filtrarModulosCriticos() {
        try {
            double umbral = Double.parseDouble(umbralCriticoInput.getText());
            this.umbralActual = umbral;
            actualizarGrafica();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un valor numérico válido", Alert.AlertType.ERROR);
        }
    }

    private void actualizarGrafica() {
        graficaPuntajes.getData().clear();

        // Limpiar y resetear el eje X
        xAxis.getCategories().clear();
        xAxis.setCategories(FXCollections.observableArrayList());

        Map<String, Double> promedios = datos.stream()
                .filter(r -> r.getPuntajeModulo() != null && r.getPuntajeModulo().matches("\\d+(\\.\\d+)?"))
                .collect(Collectors.groupingBy(
                        Resultado::getModulo,
                        Collectors.averagingDouble(r -> Double.parseDouble(r.getPuntajeModulo()))
                ));

        //restablecimiento
        List<String> modulos;
        if (umbralActual == Double.MAX_VALUE) {  // Si no hay filtro activo
            modulos = promedios.keySet()  // Mostrar todos los módulos
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());
        } else {  // Filtro activo
            modulos = promedios.entrySet().stream()
                    .filter(entry -> entry.getValue() < umbralActual)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .collect(Collectors.toList());
        }

        // Actualizar eje X
        ObservableList<String> categoriasObservables = FXCollections.observableArrayList(modulos);
        xAxis.setCategories(categoriasObservables);
        xAxis.setTickLabelRotation(-90);
        xAxis.requestAxisLayout();

        // Crear serie
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Promedio");

        if (umbralActual == Double.MAX_VALUE) {  // Mostrar todos
            promedios.forEach((modulo, promedio) ->
                    series.getData().add(new XYChart.Data<>(modulo, promedio))
            );
        } else {  // Mostrar filtrados
            promedios.entrySet().stream()
                    .filter(entry -> entry.getValue() < umbralActual)
                    .forEach(entry ->
                            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()))
                    );
        }

        graficaPuntajes.getData().add(series);
        graficaPuntajes.requestLayout();
    }

    @FXML
    private void restablecerFiltros() {
        umbralCriticoInput.clear();
        umbralActual = Double.MAX_VALUE;
        actualizarGrafica();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
