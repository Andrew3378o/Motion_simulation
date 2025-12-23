package com.project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import static com.project.Body.*;

public class Main extends Application {

    double G = 1;
    double rest = 1;
    volatile boolean gravityEnabled = true;
    volatile boolean isRunning = false;
    Thread simulationThread;
    ArrayList<Body> bodies = new ArrayList<>();
    HashMap<Body, XYChart.Series<Number, Number>> positionSeries = new HashMap<>();
    HashMap<Body, XYChart.Series<Number, Number>> horizontalVelocitySeries = new HashMap<>();
    HashMap<Body, XYChart.Series<Number, Number>> verticalVelocitySeries = new HashMap<>();
    HashMap<Body, VBox> controlBoxes = new HashMap<>();
    HBox controlsBox = new HBox(10);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("MOTION SIMULATION");

        Body b1 = new Body("Body 1", new Vector(0, 0), new Vector(0, 0), new Vector(0, 0), 0, true);
        Body b2 = new Body("Body 2", new Vector(0, 0), new Vector(0, 0), new Vector(0, 0), 0, false);
        bodies.add(b1);
        bodies.add(b2);

        for (Body body : bodies) {
            positionSeries.put(body, new XYChart.Series<>());
            horizontalVelocitySeries.put(body, new XYChart.Series<>());
            verticalVelocitySeries.put(body, new XYChart.Series<>());
        }

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        NumberAxis timeAxis = new NumberAxis();
        NumberAxis velAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(100);
        xAxis.setTickUnit(5);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(5);

        timeAxis.setLabel("TIME");
        velAxis.setLabel("VELOCITY");

        ScatterChart<Number, Number> posChart = new ScatterChart<>(xAxis, yAxis);
        LineChart<Number, Number> velChart = new LineChart<>(timeAxis, velAxis);
        posChart.setTitle("POSITION");
        velChart.setTitle("VELOCITY");
        velChart.setCreateSymbols(false);
        posChart.setMinSize(600, 600);
        velChart.setMinSize(600, 600);

        for (Body body : bodies) {
            XYChart.Series<Number, Number> pos = positionSeries.get(body);
            XYChart.Series<Number, Number> vx = horizontalVelocitySeries.get(body);
            XYChart.Series<Number, Number> vy = verticalVelocitySeries.get(body);
            initializeChartSeries(posChart, velChart, body, pos, vx, vy);
        }

        Button startButton = new Button("START");
        Button stopButton = new Button("STOP");
        Button resetButton = new Button("RESET");
        Button addButton = new Button("ADD A BODY");
        Button exitButton = new Button("EXIT");

        startButton.setOnAction(_ -> startSimulation());
        stopButton.setOnAction(_ -> stopSimulation());
        resetButton.setOnAction(_ -> resetSimulation());
        addButton.setOnAction(_ -> addNewBody(posChart, velChart, controlsBox));
        exitButton.setOnAction(_ -> Platform.exit());

        TextField restitutionField = new TextField("1.0");
        Label errorText = new Label();
        errorText.setStyle("-fx-text-fill: #ff5555;");

        restitutionField.setPromptText("Enter Restitution (0.0 - 1.0)");
        restitutionField.textProperty().addListener((_, _, newVal) -> {
            try {
                rest = Double.parseDouble(newVal);
                errorText.setText("");
            } catch (NumberFormatException e) {
                errorText.setText("Invalid restitution: " + e.getMessage());
            }
        });

        CheckBox gravityCheck = new CheckBox("Gravity");
        gravityCheck.setSelected(true);
        gravityCheck.selectedProperty().addListener((_, _, newVal) -> gravityEnabled = newVal);

        VBox settingsBox = new VBox(10,
                new Label("Restitution:"), restitutionField,
                gravityCheck
        );

        HBox buttonBox = new HBox(10, startButton, stopButton, resetButton, addButton, exitButton);
        HBox chartsBox = new HBox(10, posChart, velChart);

        for (Body body : bodies) {
            VBox box = createBodyControlBox(body, body.getName());
            controlBoxes.put(body, box);
            controlsBox.getChildren().add(box);
        }
        controlsBox.getChildren().add(settingsBox);

        VBox mainContent = new VBox(10, buttonBox, chartsBox, controlsBox);
        ScrollPane root = new ScrollPane(mainContent);

        Scene scene = new Scene(root, 1300, 650);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/project/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
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
                    if (gravityEnabled) {
                        applyGravity(dt, bodies, G);
                    } else {
                        for (Body body : bodies) {
                            body.setAcceleration(new Vector(0, 0));
                        }
                    }

                    for (Body body : bodies) {
                        if (body.isMovable()) {
                            body.update(dt, rest);
                        } else {
                            body.setVelocity(new Vector(0, 0));
                            body.setAcceleration(new Vector(0, 0));
                        }
                    }
                }

                double finalTime = time;
                Platform.runLater(() -> {
                    int trailLength = 40;

                    for (Body body : bodies) {
                        XYChart.Series<Number, Number> posSeries = positionSeries.get(body);
                        posSeries.getData().add(new XYChart.Data<>(body.getPosition().x, body.getPosition().y));

                        if (posSeries.getData().size() > trailLength) {
                            posSeries.getData().removeFirst();
                        }

                        XYChart.Series<Number, Number> velXSeries = horizontalVelocitySeries.get(body);
                        XYChart.Series<Number, Number> velYSeries = verticalVelocitySeries.get(body);
                        velXSeries.getData().add(new XYChart.Data<>(finalTime, body.getVelocity().x));
                        velYSeries.getData().add(new XYChart.Data<>(finalTime, body.getVelocity().y));
                    }
                });

                synchronized (this) {
                    for (Body b1 : bodies) {
                        for (Body b2 : bodies) {
                            if (b1 != b2 && b1.dist(b2) < 1) {
                                collide(b1, b2, rest);
                            }
                        }
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

    // ... (решта методів: stopSimulation, resetSimulation, addNewBody, createBodyControlBox, updateBodyFromUI без змін) ...
    // Не забудьте скопіювати їх сюди, якщо ви перезаписуєте весь файл.
    // Я їх приховав для економії місця, оскільки там змін не було.

    private void stopSimulation() {
        isRunning = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }

    private void resetSimulation() {
        stopSimulation();
        for (Body body : bodies) {
            updateBodyFromUI(body, controlBoxes.get(body));
        }

        Platform.runLater(() -> {
            for (Body body : bodies) {
                positionSeries.get(body).getData().clear();
                horizontalVelocitySeries.get(body).getData().clear();
                verticalVelocitySeries.get(body).getData().clear();
            }
        });
    }

    private void addNewBody(ScatterChart<Number, Number> posChart, LineChart<Number, Number> velChart, HBox controlsBox) {
        Body body = new Body("Body " + (bodies.size() + 1), new Vector(0, 0), new Vector(0, 0), new Vector(0, 0), 1, true);
        bodies.add(body);

        XYChart.Series<Number, Number> pos = new XYChart.Series<>();
        XYChart.Series<Number, Number> vx = new XYChart.Series<>();
        XYChart.Series<Number, Number> vy = new XYChart.Series<>();
        positionSeries.put(body, pos);
        horizontalVelocitySeries.put(body, vx);
        verticalVelocitySeries.put(body, vy);

        initializeChartSeries(posChart, velChart, body, pos, vx, vy);

        VBox box = createBodyControlBox(body, body.getName());
        controlBoxes.put(body, box);
        controlsBox.getChildren().add(controlsBox.getChildren().size() - 1, box);
    }

    private void initializeChartSeries(ScatterChart<Number, Number> posChart, LineChart<Number, Number> velChart, Body body, XYChart.Series<Number, Number> pos, XYChart.Series<Number, Number> vx, XYChart.Series<Number, Number> vy) {
        pos.setName(body.getName());
        vx.setName("- horizontal velocity of " + body.getName());
        vy.setName("- vertical velocity of " + body.getName());

        pos.getData().add(new XYChart.Data<>(body.getPosition().x, body.getPosition().y));
        posChart.getData().add(pos);
        velChart.getData().addAll(vx, vy);
    }

    private VBox createBodyControlBox(Body body, String name) {
        VBox box = new VBox(5);
        Label errorText = new Label();
        errorText.setStyle("-fx-text-fill: #ff5555;");

        TextField[] fields = new TextField[6];
        String[] prompts = {
                "Enter X position", "Enter Y position",
                "Enter X velocity", "Enter Y velocity",
                "Enter X acceleration", "Enter Y acceleration"
        };

        Vector[] vectors = {body.getPosition(), body.getPosition(), body.getVelocity(), body.getVelocity(), body.getAcceleration(), body.getAcceleration()};
        boolean[] isX = {true, false, true, false, true, false};

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

        CheckBox movableCheck = new CheckBox("Is Movable");
        movableCheck.setSelected(body.isMovable());
        movableCheck.selectedProperty().addListener((_, _, newVal) -> body.setMovable(newVal));

        box.getChildren().addAll(new Label(name + " Mass:"), massField, movableCheck, new Label("State:"));

        for (int i = 0; i < 6; i++) {
            fields[i] = new TextField(String.valueOf(isX[i] ? vectors[i].x : vectors[i].y));
            fields[i].setPromptText(prompts[i]);
            final int idx = i;
            fields[i].textProperty().addListener((_, _, newVal) -> {
                try {
                    double val = Double.parseDouble(newVal);
                    if (isX[idx]) vectors[idx].x = val;
                    else vectors[idx].y = val;
                    errorText.setText("");
                } catch (Exception e) {
                    errorText.setText("Invalid input: " + e.getMessage());
                }
            });
            box.getChildren().add(fields[i]);
        }

        box.getChildren().add(errorText);
        return box;
    }

    private void updateBodyFromUI(Body body, VBox controlBox) {
        for (javafx.scene.Node node : controlBox.getChildren()) {
            if (node instanceof TextField field) {
                String prompt = field.getPromptText();
                try {
                    double value = Double.parseDouble(field.getText());

                    if (prompt.contains("mass")) body.setMass(value);
                    else if (prompt.contains("X position")) body.getPosition().x = value;
                    else if (prompt.contains("Y position")) body.getPosition().y = value;
                    else if (prompt.contains("X velocity")) body.getVelocity().x = value;
                    else if (prompt.contains("Y velocity")) body.getVelocity().y = value;
                    else if (prompt.contains("X acceleration")) body.getAcceleration().x = value;
                    else if (prompt.contains("Y acceleration")) body.getAcceleration().y = value;
                } catch (NumberFormatException ignored) {}
            } else if (node instanceof CheckBox checkBox) {
                if ("Is Movable".equals(checkBox.getText())) {
                    body.setMovable(checkBox.isSelected());
                }
            }
        }
    }
}