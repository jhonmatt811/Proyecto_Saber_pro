package com.java.fx.controller;

import com.java.fx.model.Resultado;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import javafx.util.Duration;
import javafx.util.StringConverter;

//mostrar info al pasar sobre los puntos de la grafica
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LineaController {

    @FXML private LineChart<Number, Number> graficaTendencias;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private List<Resultado> datosCompletos;

    public void inicializarDatos(List<Resultado> datos) {
        this.datosCompletos = new ArrayList<>(datos); // Guarda copia de los datos
        actualizarGrafica(datos);
    }

    private void actualizarGrafica(List<Resultado> datos) {
        graficaTendencias.getData().clear();

        // Serie para la gráfica
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Tendencia por Año");

        // Configuración del eje Y (rango fijo de 0 a 300)
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(300);
        yAxis.setTickUnit(50);

        // Paso 1: Calcular promedios por año y ciclo
        Map<String, Double> promediosPorPeriodo = datos.stream()
                .filter(r ->
                        r.getPuntajeGlobal() != null &&
                                r.getPuntajeGlobal().matches("\\d+(\\.\\d+)?")
                )
                .collect(Collectors.groupingBy(
                        r -> r.getYear() + "-" + r.getCiclo(), // Clave compuesta año-ciclo
                        Collectors.averagingDouble(r -> Double.parseDouble(r.getPuntajeGlobal()))
                ));

        // Paso 2: Ordenar por año y ciclo
        List<Map.Entry<String, Double>> datosOrdenados = new ArrayList<>(promediosPorPeriodo.entrySet());
        datosOrdenados.sort((a, b) -> {
            String[] partesA = a.getKey().split("-");
            String[] partesB = b.getKey().split("-");
            int añoA = Integer.parseInt(partesA[0]);
            int cicloA = Integer.parseInt(partesA[1]);
            int añoB = Integer.parseInt(partesB[0]);
            int cicloB = Integer.parseInt(partesB[1]);

            if (añoA != añoB) return Integer.compare(añoA, añoB);
            return Integer.compare(cicloA, cicloB);
        });

        // Paso 3: Configurar eje X
        if (!datosOrdenados.isEmpty()) {
            int minYear = Integer.parseInt(datosOrdenados.get(0).getKey().split("-")[0]);
            int maxYear = Integer.parseInt(datosOrdenados.get(datosOrdenados.size() - 1).getKey().split("-")[0]);

            //mostrar puntos en intervalos de 5 años
            int lowerBound = (minYear / 5) * 5;
            int upperBound = ((maxYear / 5) + 1) * 5;

            xAxis.setLowerBound(lowerBound - 0.5);
            xAxis.setUpperBound(upperBound + 0.5);
            xAxis.setTickUnit(5);
            xAxis.setAutoRanging(false);
        }

        // Paso 4: Añadir puntos con tooltips
        datosOrdenados.forEach(entry -> {
            String[] partes = entry.getKey().split("-");
            int año = Integer.parseInt(partes[0]);
            int ciclo = Integer.parseInt(partes[1]);
            double promedio = entry.getValue();

            // Crear punto de datos
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(
                    año + (ciclo/10.0), // Valor X como año.ciclo (ej: 2023.1)
                    promedio
            );

            // Crear tooltip
            Tooltip tooltip = new Tooltip();
            tooltip.setText(String.format(
                    "Año: %d\nCiclo: %d\nPromedio: %.2f",
                    año, ciclo, promedio
            ));
            tooltip.setShowDelay(Duration.millis(100)); // Mostrar rápido
            tooltip.setHideDelay(Duration.seconds(2));  // Ocultar después de 2 segundos

            // Configurar nodo interactivo
            dataPoint.setNode(new HoveredThresholdNode(tooltip));
            series.getData().add(dataPoint);
        });

        graficaTendencias.getData().add(series);

        // Paso 5: Formatear etiquetas del eje X
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                // Convertir el valor numérico a año
                return String.valueOf(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
    }

    @FXML private TextField rangoAniosInput; // Inyectar el TextField

    @FXML
    private void handleAnalizarTendencias() {
        String rango = rangoAniosInput.getText().trim();

        if (rango.isEmpty()) {
            mostrarAlerta("Campo vacío", "Ingrese un rango de años.", Alert.AlertType.WARNING);
            return;
        }

        try {
            String[] años = rango.split("-");
            int añoInicio = Integer.parseInt(años[0].trim());
            int añoFin = Integer.parseInt(años[1].trim());

            if (añoInicio > añoFin) throw new IllegalArgumentException();

            List<Resultado> datosFiltrados = datosCompletos.stream()
                    .filter(r -> r.getYear() >= añoInicio && r.getYear() <= añoFin)
                    .collect(Collectors.toList());

            actualizarGrafica(datosFiltrados);

        } catch (ArrayIndexOutOfBoundsException e) {
            mostrarAlerta("Formato incorrecto", "Use el formato AñoInicio-AñoFin (ej: 2020-2023).", Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            mostrarAlerta("Valor inválido", "Solo se permiten números enteros.", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Rango inválido", "El año inicial no puede ser mayor al año final.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRestablecer() {
        actualizarGrafica(datosCompletos); // Vuelve a cargar todos los datos
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Clase para nodos interactivos
    private class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(Tooltip tooltip) {
            setPrefSize(15, 15);

            Circle circle = new Circle(5);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(2);

            // Tooltip permanente
            Tooltip.install(this, tooltip);

            // Comportamiento hover (solo para el color)
            setOnMouseEntered(e -> circle.setFill(Color.web("#4CAF50")));
            setOnMouseExited(e -> circle.setFill(Color.web("#FF5722")));

            getChildren().add(circle);
        }
    }
}