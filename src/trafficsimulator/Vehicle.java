package trafficsimulator;


import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Vehicle{
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private boolean direction;
    private Image style;
    public static final int VEH_WIDTH = 100;
    public static final int VEH_HEIGHT = 50;

    public Vehicle(int x, int y, int speed, boolean direction) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        style = getRandomCar(direction);

    }

    public void paintVehicle(Graphics g){
        g.drawImage(style, x ,y, VEH_WIDTH, VEH_HEIGHT,null);
    }

    private Image getRandomCar(boolean direction){
        if(direction){
            return CarsFWD.values()[new Random().nextInt(CarsFWD.values().length)].image;
        }else{
            return CarsBWD.values()[new Random().nextInt(CarsBWD.values().length)].image;
        }

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
