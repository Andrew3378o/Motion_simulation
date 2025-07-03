package com.project;

public class Body {
    private final String name;
    private Vector position;
    private Vector velocity;
    private Vector acceleration;
    private double mass;

    public Body(String name, Vector position, Vector velocity, Vector acceleration, double mass){
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    @Override
    public String toString() {
        return String.format("%s: pos = (%.2f;%.2f); v = (%.2f;%.2f); a = (%.2f;%.2f); m = %.2f", name, position.x, position.y, velocity.x, velocity.y, acceleration.x, acceleration.y, mass);
    }

    public void update(double dt){
        velocity = velocity.add(acceleration.multiply(dt));
        position = position.add(velocity.multiply(dt));
    }

    public double dist(Body other){
        return Math.sqrt(Math.pow((this.getPosition().x - other.getPosition().x), 2) +Math.pow((this.getPosition().y - other.getPosition().y), 2));
    }
    public static void collide(Body b1, Body b2){
        Vector delta = b1.position.subtract(b2.position);
        Vector n = delta.normalize();

        double v1n = b1.velocity.dot(n);
        double v2n = b2.velocity.dot(n);

        double m1 = b1.mass;
        double m2 = b2.mass;

        double v1nAfter = (v1n * (m1 - m2) + 2 * m2 * v2n) / (m1 + m2);
        double v2nAfter = (v2n * (m2 - m1) + 2 * m1 * v1n) / (m1 + m2);

        Vector v1nVecBefore = n.multiply(v1n);
        Vector v1nVecAfter = n.multiply(v1nAfter);

        Vector v2nVecBefore = n.multiply(v2n);
        Vector v2nVecAfter = n.multiply(v2nAfter);

        b1.velocity = b1.velocity.subtract(v1nVecBefore).add(v1nVecAfter);
        b2.velocity = b2.velocity.subtract(v2nVecBefore).add(v2nVecAfter);
    }
}

