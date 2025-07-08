package com.project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {

    Body b1 = new Body("A", new Vector(0, 0), new Vector(0, 0), new Vector(0, 0), 0);
    Body b2 = new Body("B", new Vector(0, 0), new Vector(0, 0), new Vector(0, 0), 0);
    double rest = 1;

    volatile boolean isRunning = false;
    Thread simulationThread;
    XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series5 = new XYChart.Series<>();
    XYChart.Series<Number, Number> series6 = new XYChart.Series<>();
    VBox controlBox1 = createBodyControlBox(b1, b1.getName());
    VBox controlBox2 = createBodyControlBox(b2, b2.getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("MOTION SIMULATION");

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        NumberAxis timeAxis = new NumberAxis();
        NumberAxis velAxis = new NumberAxis();
        timeAxis.setLabel("TIME");
        velAxis.setLabel("VELOCITY");
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        timeAxis.setAutoRanging(true);
        velAxis.setAutoRanging(true);

        ScatterChart<Number, Number> posChart = new ScatterChart<>(xAxis, yAxis);
        LineChart<Number, Number> velChart = new LineChart<>(timeAxis, velAxis);

        velChart.setMaxSize(600, 600);
        velChart.setMinSize(600, 600);
        posChart.setMinSize(600, 600);
        posChart.setMaxSize(600, 600);

        velChart.setTitle("VELOCITY");
        posChart.setTitle("POSITION");
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
        Button exitButton = new Button("EXIT");

        startButton.setOnAction(_ -> startSimulation());
        stopButton.setOnAction(_ -> stopSimulation());
        resetButton.setOnAction(_ -> resetSimulation());
        exitButton.setOnAction(_ -> Platform.exit());

        TextField restitutionField = new TextField("1.0");
        Text errorText = new Text();
        restitutionField.setPromptText("Enter Restitution (0.0 - 1.0)");
        restitutionField.textProperty().addListener((_, _, newVal) -> {
            try {
                rest = Double.parseDouble(newVal);
            } catch (NumberFormatException e) {
                errorText.setText("Invalid restitution: " + e.getMessage());
            }
        });

        HBox settingsBox = new HBox(10, new Text("Restitution:"), restitutionField);
        HBox buttonBox = new HBox(10, startButton, stopButton, resetButton, exitButton);
        HBox chartsBox = new HBox(10, posChart, velChart);
        HBox controlsBox = new HBox(10, controlBox1, controlBox2, settingsBox);

        VBox mainContent = new VBox(10, buttonBox, chartsBox, controlsBox);
        ScrollPane root = new ScrollPane(mainContent);

        Scene scene = new Scene(root, 1300, 650);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/project/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private VBox createBodyControlBox(Body body, String name) {
        Text errorText = new Text();
        VBox controlBox = new VBox(5);

        TextField massField = new TextField(String.valueOf(body.getMass()));
        massField.setPromptText("Enter mass of " + name);
        massField.textProperty().addListener((_, _, newVal) -> {
            try {
                body.setMass(Double.parseDouble(newVal));
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid mass: " + e.getMessage());
            }
        });

        TextField xPosField = new TextField(String.valueOf(body.getPosition().x));
        xPosField.setPromptText("Enter X position");
        xPosField.textProperty().addListener((_, _, newVal) -> {
            try {
                Vector pos = body.getPosition();
                pos.x = Double.parseDouble(newVal);
                body.setPosition(pos);
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid X position: " + e.getMessage());
            }
        });

        TextField yPosField = new TextField(String.valueOf(body.getPosition().y));
        yPosField.setPromptText("Enter Y position");
        yPosField.textProperty().addListener((_, _, newVal) -> {
            try {
                Vector pos = body.getPosition();
                pos.y = Double.parseDouble(newVal);
                body.setPosition(pos);
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid Y position: " + e.getMessage());
            }
        });

        TextField xVelField = new TextField(String.valueOf(body.getVelocity().x));
        xVelField.setPromptText("Enter X velocity");
        xVelField.textProperty().addListener((_, _, newVal) -> {
            try {
                Vector vel = body.getVelocity();
                vel.x = Double.parseDouble(newVal);
                body.setVelocity(vel);
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid X velocity: " + e.getMessage());
            }
        });

        TextField yVelField = new TextField(String.valueOf(body.getVelocity().y));
        yVelField.setPromptText("Enter Y velocity");
        yVelField.textProperty().addListener((_, _, newVal) -> {
            try {
                Vector vel = body.getVelocity();
                vel.y = Double.parseDouble(newVal);
                body.setVelocity(vel);
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid Y velocity: " + e.getMessage());
            }
        });

        TextField xAccField = new TextField(String.valueOf(body.getAcceleration().x));
        xAccField.setPromptText("Enter X acceleration");
        xAccField.textProperty().addListener((_, _, newVal) -> {
            try {
                Vector acc = body.getAcceleration();
                acc.x = Double.parseDouble(newVal);
                body.setAcceleration(acc);
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid X acceleration: " + e.getMessage());
            }
        });

        TextField yAccField = new TextField(String.valueOf(body.getAcceleration().y));
        yAccField.setPromptText("Enter Y acceleration");
        yAccField.textProperty().addListener((_, _, newVal) -> {
            try {
                Vector acc = body.getAcceleration();
                acc.y = Double.parseDouble(newVal);
                body.setAcceleration(acc);
                errorText.setText("");
            } catch (Exception e) {
                errorText.setText("Invalid Y acceleration: " + e.getMessage());
            }
        });

        controlBox.getChildren().addAll(
                new Text(name + " Mass:"),
                massField,
                new Text("Position:"),
                xPosField,
                yPosField,
                new Text("Velocity:"),
                xVelField,
                yVelField,
                new Text("Acceleration:"),
                xAccField,
                yAccField,
                errorText
        );

        return controlBox;
    }

    private void startSimulation() {
        if (isRunning) return;
        isRunning = true;
        simulationThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            double time = 0;
            while (isRunning) {
                long now = System.nanoTime();
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                time += dt;

                synchronized (this) {
                    b1.update(dt, rest);
                    b2.update(dt, rest);
                }

                double finalTime = time;
                Platform.runLater(() -> {
                    series1.getData().add(new XYChart.Data<>(b1.getPosition().x, b1.getPosition().y));
                    series2.getData().add(new XYChart.Data<>(b2.getPosition().x, b2.getPosition().y));
                    series1.setName(b1.toString());
                    series2.setName(b2.toString());
                    series3.getData().add(new XYChart.Data<>(finalTime, b1.getVelocity().x));
                    series4.getData().add(new XYChart.Data<>(finalTime, b1.getVelocity().y));
                    series5.getData().add(new XYChart.Data<>(finalTime, b2.getVelocity().x));
                    series6.getData().add(new XYChart.Data<>(finalTime, b2.getVelocity().y));
                });

                if (b1.dist(b2) < 1) {
                    synchronized (this) {
                        Body.collide(b1, b2, rest);
                    }
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        simulationThread.setDaemon(true);
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

        updateBodyFromUI(b1, controlBox1);
        updateBodyFromUI(b2, controlBox2);

        Platform.runLater(() -> {
            series1.getData().clear();
            series2.getData().clear();
            series3.getData().clear();
            series4.getData().clear();
            series5.getData().clear();
            series6.getData().clear();
            series1.setName(b1.toString());
            series2.setName(b2.toString());
            series1.getData().add(new XYChart.Data<>(b1.getPosition().x, b1.getPosition().y));
            series2.getData().add(new XYChart.Data<>(b2.getPosition().x, b2.getPosition().y));
        });
    }

    private void updateBodyFromUI(Body body, VBox controlBox) {
        for (javafx.scene.Node node : controlBox.getChildren()) {
            if (node instanceof TextField field) {
                String prompt = field.getPromptText();
                double value;
                try {
                    value = Double.parseDouble(field.getText());
                } catch (NumberFormatException e) {
                    continue;
                }

                if (prompt.contains("mass")) {
                    body.setMass(value);
                }
                else if (prompt.contains("X position")) {
                    body.getPosition().x = value;
                }
                else if (prompt.contains("Y position")) {
                    body.getPosition().y = value;
                }
                else if (prompt.contains("X velocity")) {
                    body.getVelocity().x = value;
                }
                else if (prompt.contains("Y velocity")) {
                    body.getVelocity().y = value;
                }
                else if (prompt.contains("X acceleration")) {
                    body.getAcceleration().x = value;
                }
                else if (prompt.contains("Y acceleration")) {
                    body.getAcceleration().y = value;
                }
            }
        }
    }
}