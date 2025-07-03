package com.project;

public class Vector {
    public double x, y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public Vector multiply(double scalar){
        return new Vector(this.x * scalar, this.y * scalar);
    }

    public double dot(Vector other){
        return this.x * other.x + this.y * other.y;
    }

    public Vector subtract(Vector other){
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector normalize(){
        double length = Math.sqrt(x*x + y*y);
        if(length == 0) return new Vector(0,0);
        return new Vector(x/length, y/length);
    }
}
