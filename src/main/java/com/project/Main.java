package com.project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.project.Body.collide;

public class Main extends Application {

    Body b1 = new Body("A", 10, 10, 10, -10);
    Body b2 = new Body("B", 0, 30, 5, 10);

    private volatile boolean isRunning = false;
    private Thread simulationThread;
    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series4 = new XYChart.Series<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Графік позиції об'єкта з керуванням");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final NumberAxis timeAxis = new NumberAxis();
        final NumberAxis velAxic = new NumberAxis();
        timeAxis.setLabel("Час (с)");
        velAxic.setLabel("Швидкість");
        xAxis.setLabel("Час (с)");
        yAxis.setLabel("Позиція");

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        timeAxis.setAutoRanging(true);
        velAxic.setAutoRanging(true);

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        final LineChart<Number, Number> lineChart1 = new LineChart<>(timeAxis, velAxic);

        lineChart1.setMaxSize(600, 600);
        lineChart1.setMinSize(600, 600);
        lineChart.setMinSize(600, 600);
        lineChart.setMaxSize(600, 600);

        lineChart1.setTitle("Швидкість об'єктів");
        lineChart.setTitle("Рух об'єктів");
        lineChart.setCreateSymbols(false);
        lineChart1.setCreateSymbols(false);

        series1.setName("Об'єкт A");
        series2.setName("Об'єкт B");
        series3.setName("Об'єкт A");
        series4.setName("Об'єкт B");
        lineChart.getData().addAll(series1, series2);
        lineChart1.getData().addAll(series3, series4);

        Button startButton = new Button("Почати");
        Button stopButton = new Button("Зупинити");
        Button resetButton = new Button("Скинути");

        startButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> stopSimulation());
        resetButton.setOnAction(e -> resetSimulation());

        HBox buttonBox = new HBox(10, startButton, stopButton, resetButton);
        HBox chartsBox = new HBox(10, lineChart, lineChart1);
        VBox root = new VBox(10, buttonBox, chartsBox);

        Scene scene = new Scene(root, 1300, 650);
        stage.setScene(scene);
        stage.show();
    }


    private void startSimulation() {
        if (isRunning) return;

        isRunning = true;
        simulationThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            double time = 0;

            b1 = new Body("A", 100, 10, 0, -100);
            b2 = new Body("B", 0, 30, 10, 100);

            while (isRunning) {
                long now = System.nanoTime();
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                time += dt;

                b1.update(dt);
                b2.update(dt);

                double finalTime = time;
                Platform.runLater(() -> {
                    series1.getData().add(new XYChart.Data<>(finalTime, b1.getX()));
                    series2.getData().add(new XYChart.Data<>(finalTime, b2.getX()));
                });

                Platform.runLater(() -> {
                    series3.getData().add(new XYChart.Data<>(finalTime, b1.getVelocity()));
                    series4.getData().add(new XYChart.Data<>(finalTime, b2.getVelocity()));
                });


                if (Math.abs(b1.getX() - b2.getX()) < 1) {
                    collide(b1, b2);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Симуляцію зупинено");
                }
            }
        });
        simulationThread.start();
    }

    private void stopSimulation() {
        isRunning = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }

    private void resetSimulation() {
        stopSimulation();
        Platform.runLater(() -> {
            series1.getData().clear();
            series2.getData().clear();
            series3.getData().clear();
            series4.getData().clear();
        });
    }
}