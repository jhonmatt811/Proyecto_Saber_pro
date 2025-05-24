package com.java.fx.controller;

import com.java.fx.model.Resultado;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import javafx.util.StringConverter;

public class LineaController {

    @FXML private LineChart<Number, Number> graficaTendencias;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    public void inicializarDatos(List<Resultado> datos) {
        actualizarGrafica(datos);
    }

    private void actualizarGrafica(List<Resultado> datos) {
        graficaTendencias.getData().clear();

        // Serie para la gráfica
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Tendencia por Año");

        // Paso 1: Calcular promedios por año
        Map<Integer, Double> promediosPorAño = datos.stream()
                .filter(r ->
                        r.getPuntajeGlobal() != null &&
                                r.getPuntajeGlobal().matches("\\d+(\\.\\d+)?") // Filtra valores numéricos
                )
                .collect(Collectors.groupingBy(
                        Resultado::getYear, // Agrupa por año (clave: Integer)
                        Collectors.averagingDouble(r -> Double.parseDouble(r.getPuntajeGlobal())) // Valor: Double
                ));

        // Paso 2: Ordenar por año
        List<Map.Entry<Integer, Double>> datosOrdenados = new ArrayList<>(promediosPorAño.entrySet());
        datosOrdenados.sort(Map.Entry.comparingByKey()); // Orden ascendente por año

        // Paso 3: Configurar eje X
        if (!datosOrdenados.isEmpty()) {
            int minYear = datosOrdenados.get(0).getKey();
            int maxYear = datosOrdenados.get(datosOrdenados.size() - 1).getKey();
            xAxis.setLowerBound(minYear - 0.5);
            xAxis.setUpperBound(maxYear + 0.5);
            xAxis.setTickUnit(1);
            xAxis.setAutoRanging(false);
        }

        // Paso 4: Añadir puntos a la serie
        datosOrdenados.forEach(entry -> {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        });

        graficaTendencias.getData().add(series);

        // Paso 5: Formatear etiquetas del eje X
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object.intValue()); // Muestra el año como entero
            }

            @Override
            public Number fromString(String string) {
                return null; // No necesario
            }
        });
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) graficaTendencias.getScene().getWindow();
        stage.close();
    }
}