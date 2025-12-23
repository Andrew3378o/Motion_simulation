package com.project;

import java.util.List;

public class Body {
    private String name;
    private Vector position;
    private Vector velocity;
    private Vector acceleration;
    private double mass;
    private boolean isMovable;

    public Body(String name, Vector position, Vector velocity, Vector acceleration, double mass, boolean isMovable){
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
        this.isMovable = isMovable;
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

    public void setMovable(boolean isMovable){
        this.isMovable = isMovable;
    }

    @Override
    public String toString() {
        return String.format("%s: pos = (%.2f;%.2f); v = (%.2f;%.2f); a = (%.2f;%.2f); m = %.2f", name, position.x, position.y, velocity.x, velocity.y, acceleration.x, acceleration.y, mass);
    }

    public void update(double dt, double e){
        if(!isMovable){
            velocity.x = 0;
            velocity.y = 0;
            acceleration.x = 0;
            acceleration.y = 0;
            return;
        }
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

    public double dist(Vector v){
        return Math.sqrt(Math.pow((position.x - v.x), 2) + Math.pow((position.y - v.y), 2));
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

    public static void applyGravity(double dt, List<Body> bodies, double G) {

        for (Body b1 : bodies) {
            double ax = 0;
            double ay = 0;

            for (Body b2 : bodies) {
                if (b1 == b2) continue;

                double dx = b2.getPosition().x - b1.getPosition().x;
                double dy = b2.getPosition().y - b1.getPosition().y;
                double distSq = dx * dx + dy * dy;
                double dist = Math.sqrt(distSq);

                if (dist < 2) dist = 2;
                distSq = dist * dist;

                double forceAccel = G * b2.getMass() / distSq;

                ax += forceAccel * (dx / dist);
                ay += forceAccel * (dy / dist);
            }
            b1.getVelocity().x += ax * dt;
            b1.getVelocity().y += ay * dt;
        }
    }

    public boolean isMovable() {
        return isMovable;
    }
}

