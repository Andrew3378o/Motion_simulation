package com.project;

public class Body {
    private String name;
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

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s: pos = (%.2f;%.2f); v = (%.2f;%.2f); a = (%.2f;%.2f); m = %.2f", name, position.x, position.y, velocity.x, velocity.y, acceleration.x, acceleration.y, mass);
    }

    public void update(double dt, double e){
        velocity = velocity.add(acceleration.multiply(dt));
        position = position.add(velocity.multiply(dt));
        if(position.y < 0){
            position.y = 0;
            velocity.y *= -e;
        }
        if(position.x < 0){
            position.x = 0;
            velocity.x *= -e;
        }
        if(position.y == 0 && e == 0){
            position.y = 0;
            velocity.x = 0;
            velocity.y = 0;
            acceleration.x = 0;
            acceleration.y = 0;
        }
        if(position.x == 0 && e == 0){
            position.x = 0;
            velocity.x = 0;
            velocity.y = 0;
            acceleration.x = 0;
            acceleration.y = 0;
        }
    }

    public double dist(Body other){
        return Math.sqrt(Math.pow((this.getPosition().x - other.getPosition().x), 2) + Math.pow((this.getPosition().y - other.getPosition().y), 2));
    }

    public static void collide(Body b1, Body b2, double restitution) {
        Vector delta = b1.position.subtract(b2.position);
        Vector n = delta.normalize();

        double v1n = b1.velocity.dot(n);
        double v2n = b2.velocity.dot(n);
        double relativeVelocity = v1n - v2n;

        if (relativeVelocity >= -1e-6) return;

        double m1 = b1.mass;
        double m2 = b2.mass;

        double newV1n = (m1 * v1n + m2 * v2n - m2 * (v1n - v2n) * restitution) / (m1 + m2);
        double newV2n = (m1 * v1n + m2 * v2n - m1 * (v2n - v1n) * restitution) / (m1 + m2);

        Vector v1nVecBefore = n.multiply(v1n);
        Vector v2nVecBefore = n.multiply(v2n);
        Vector v1nVecAfter = n.multiply(newV1n);
        Vector v2nVecAfter = n.multiply(newV2n);

        b1.velocity = b1.velocity.subtract(v1nVecBefore).add(v1nVecAfter);
        b2.velocity = b2.velocity.subtract(v2nVecBefore).add(v2nVecAfter);
    }
}

