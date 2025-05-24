package com.java.fx.controller;
import com.java.fx.model.Resultado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GraficaModulosController
{

    @FXML private BarChart<String, Number> graficaPuntajes;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private ObservableList<Resultado> datos;

    public void inicializarDatos(List<Resultado> datos) {
        this.datos = FXCollections.observableArrayList(datos);
        actualizarGrafica();
    }

    private void actualizarGrafica() {
        graficaPuntajes.getData().clear();

        List<String> modulos = datos.stream()
                .map(Resultado::getModulo)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        xAxis.setCategories(FXCollections.observableArrayList(modulos));
        xAxis.setTickLabelRotation(-90);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Promedio");

        datos.stream()
                .filter(r -> r.getPuntajeModulo() != null && r.getPuntajeModulo().matches("\\d+(\\.\\d+)?"))
                .collect(Collectors.groupingBy(
                        Resultado::getModulo,
                        Collectors.averagingDouble(r -> Double.parseDouble(r.getPuntajeModulo()))
                ))
                .forEach((modulo, promedio) -> series.getData().add(new XYChart.Data<>(modulo, promedio)));

        graficaPuntajes.getData().add(series);
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) graficaPuntajes.getScene().getWindow();
        stage.close();
    }

    public void inicializarDatos(FilteredList<Resultado> datosFiltrados, String trim, String value, String value1) {
    }
}
