package com.project;

public class Body {
    private String name;
    private double x;
    private double mass;
    private double velocity;
    private double acceleration;

    public Body(String name, double x, double mass, double velocity, double acceleration) {
        this.name = name;
        this.x = x;
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.mass = mass;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setX(double x){
        this.x = x;
    }
    public void setMass(double mass) {
        this.mass = mass;
    }
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }
    public String getName() {
        return name;
    }
    public double getX(){
        return this.x;
    }
    public double getMass() {
        return mass;
    }
    public double getVelocity() {
        return velocity;
    }
    public double getAcceleration() {
        return acceleration;
    }

    @Override
    public String toString() {
        return "Body{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", mass=" + mass +
                ", velocity=" + velocity +
                ", acceleration=" + acceleration +
                '}';
    }

    public void update(double dt){
        this.velocity += this.acceleration * dt;
        this.x += this.velocity * dt;
    }

    public static void collide(Body b1, Body b2){
        double m1 = b1.mass;
        double m2 = b2.mass;
        double v1 = b1.velocity;
        double v2 = b2.velocity;

        b1.velocity = ((m1 - m2) / (m1 + m2)) * v1 + (2 * m2 / (m1 + m2)) * v2;
        b2.velocity = ((m2 - m1) / (m1 + m2)) * v2 + (2 * m1 / (m1 + m2)) * v1;
    }
}

