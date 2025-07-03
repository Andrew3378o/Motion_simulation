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

    Body b1 = new Body("A",
            new Vector(0, 100),
            new Vector(2, 0),
            new Vector(0, 0), 10);
    Body b2 = new Body("B",
            new Vector(100, 0),
            new Vector(0, 2),
            new Vector(0, 0), 10);


    private volatile boolean isRunning = false;
    private Thread simulationThread;
    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series5 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series6 = new XYChart.Series<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("MOTION SIMULATION");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final NumberAxis timeAxis = new NumberAxis();
        final NumberAxis velAxis = new NumberAxis();
        timeAxis.setLabel("TIME");
        velAxis.setLabel("VELOCITY");
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        timeAxis.setAutoRanging(true);
        velAxis.setAutoRanging(true);

        final LineChart<Number, Number> posChart = new LineChart<>(xAxis, yAxis);
        final LineChart<Number, Number> velChart = new LineChart<>(timeAxis, velAxis);

        velChart.setMaxSize(600, 600);
        velChart.setMinSize(600, 600);
        posChart.setMinSize(600, 600);
        posChart.setMaxSize(600, 600);

        velChart.setTitle("VELOCITY");
        posChart.setTitle("POSITION");
        posChart.setCreateSymbols(false);
        velChart.setCreateSymbols(false);

        series1.setName(b1.toString());
        series2.setName(b2.toString());
        series3.setName(" - horizontal velocity of A");
        series4.setName(" - vertical velocity of A");
        series5.setName(" - horizontal velocity of B");
        series6.setName(" - vertical velocity of B");
        posChart.getData().addAll(series1, series2);
        velChart.getData().addAll(series3, series4, series5, series6);

        Button startButton = new Button("START");
        Button stopButton = new Button("STOP");
        Button resetButton = new Button("RESET");

        startButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> stopSimulation());
        resetButton.setOnAction(e -> resetSimulation());

        HBox buttonBox = new HBox(10, startButton, stopButton, resetButton);
        HBox chartsBox = new HBox(10, posChart, velChart);
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

            b1 = new Body("A",
                    new Vector(0, 50),
                    new Vector(20, 0),
                    new Vector(0, 0), 5);
            b2 = new Body("B",
                    new Vector(50, 0),
                    new Vector(0, 20),
                    new Vector(0, 0), 1);

            while (isRunning) {
                long now = System.nanoTime();
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                time += dt;

                b1.update(dt);
                b2.update(dt);

                double finalTime = time;
                Platform.runLater(() -> {
                    series1.getData().add(new XYChart.Data<>(b1.getPosition().x, b1.getPosition().y));
                    series2.getData().add(new XYChart.Data<>(b2.getPosition().x, b2.getPosition().y));
                    series1.setName(b1.toString());
                    series2.setName(b2.toString());
                });

                Platform.runLater(() -> {
                    series3.getData().add(new XYChart.Data<>(finalTime, b1.getVelocity().x));
                    series4.getData().add(new XYChart.Data<>(finalTime, b1.getVelocity().y));
                    series5.getData().add(new XYChart.Data<>(finalTime, b2.getVelocity().x));
                    series6.getData().add(new XYChart.Data<>(finalTime, b2.getVelocity().y));
                });


                if (b1.dist(b2) < 1) {
                    collide(b1, b2);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
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
            series5.getData().clear();
            series6.getData().clear();
        });
    }
}