# Body Motion Simulation

This project is a real-time simulation of body motion in two-dimensional space. It includes graphical visualization of position and velocity over time, interactive body management, and support for elastic collisions.

## Features

- Add and configure multiple bodies dynamically via the GUI
- Visualize:
    - Position vs. time (X and Y axes)
    - Velocity vs. time (Vx and Vy)
- Real-time collision detection and response
- Pause and resume simulation

## Usage

1. Launch the application.
2. Click "Add Body" and set parameters:
    - Mass
    - Initial position (X, Y)
    - Initial velocity (Vx, Vy)
    - Initial acceleration (Ax, Ay)
3. Set restitution.
3. Press "Start" to run the simulation and observe movement.

## Running the Project

### Using an IDE

- Open the project in IntelliJ IDEA or Eclipse
- Ensure JavaFX SDK is added to your module path
- Run `Main.java` as a JavaFX application

### Using Command Line

```bash