package trafficsimulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Tunnel extends JPanel {
    private int trafficLightPhase;
    private LinkedList<Vehicle> forwardCars = new LinkedList<Vehicle>();
    private LinkedList<Vehicle> backwardCars = new LinkedList<Vehicle>();

    public Tunnel(int trafficLightPhase) {
        super();
        this.trafficLightPhase = trafficLightPhase;
    }

    public void addVehicle(boolean direction, int speed){
        if(direction) {
            forwardCars.add(new Vehicle(TrafficSimulator.frame.getWidth(), TrafficSimulator.frame.getHeight()/3 + 50, speed, true));
        }else{
            backwardCars.add(new Vehicle(-100, TrafficSimulator.frame.getHeight()/2, speed, false));
        }
    }

    public void step(){
        if(DirectionSwitcher.currentDirection){

        }
        for (int i = 0; i < backwardCars.size(); i++) {
            Vehicle v = backwardCars.get(i);
            if (!isCollisionBackward(v)) {
                v.setX(v.getX() + v.getSpeed());
            } else {

            }
            if (v.getX() > TrafficSimulator.frame.getWidth()) {
                backwardCars.remove(i);
            }
        }

        for (int i = 0; i < forwardCars.size(); i++) {
            Vehicle v = forwardCars.get(i);
            if (!isCollisionForward(v)) {
                v.setX(v.getX() - v.getSpeed());
            } else {

            }
            if (v.getX() < 0) {
                forwardCars.remove(i);
            }
        }

    }

    private boolean isCollisionBackward(Vehicle vehicle){
        for (Vehicle veh : backwardCars) {
            if (!vehicle.equals(veh)) {
                if (vehicle.getY() == veh.getY()) {
                    if ((vehicle.getX() + vehicle.getSpeed() > veh.getX() - (Vehicle.VEH_WIDTH + 10)) && (vehicle.getX() < veh.getX())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCollisionForward(Vehicle vehicle){
        for (Vehicle veh : forwardCars) {
            if (!vehicle.equals(veh)) {
                if (vehicle.getY() == veh.getY()) {
                    if ((vehicle.getX() - vehicle.getSpeed() < veh.getX() + (Vehicle.VEH_WIDTH + 10)) && (vehicle.getX() > veh.getX())) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String fileName = "../tunnel.jpg";
        ImageIcon icon = new ImageIcon(getClass().getResource(fileName));
        g.drawImage(icon.getImage(), (TrafficSimulator.frame.getWidth() - icon.getIconWidth()) / 2,TrafficSimulator.frame.getHeight()/4,null);

        for (int i = 0; i < forwardCars.size(); i++) {
            forwardCars.get(i).paintVehicle(g);
        }
        for (int i = 0; i < backwardCars.size(); i++) {
            backwardCars.get(i).paintVehicle(g);
        }
    }
}
