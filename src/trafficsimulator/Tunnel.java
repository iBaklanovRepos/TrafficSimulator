package trafficsimulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Tunnel extends JPanel {
    private int trafficLightPhase;
    private LinkedList<Vehicle> forwardCars = new LinkedList<Vehicle>();
    private LinkedList<Vehicle> backwardCars = new LinkedList<Vehicle>();

    private static final int RIGHT_STOP_LINE = 1500;
    private static final int LEFT_STOP_LINE = 340;
    private static final int TUNNEL_RIGHT_BORDER = 1350;
    private static final int TUNNEL_LEFT_BORDER = 500;
    private static final int FORWARD_LANE_Y = 430;
    private static final int BACKWARD_LANE_Y = 530;
    private static final int TUNNEL_Y = 480;

    private String prevDirection = "forward";
    private ImageIcon redImage;
    private ImageIcon greenImage;
    private ImageIcon greenYellowImage;
    private ImageIcon redYellowImage;


    public Tunnel(int trafficLightPhase) {
        super();
        this.trafficLightPhase = trafficLightPhase;
        initTrafficLightPictures();
    }

    private void initTrafficLightPictures(){
        String redLight = "../red.jpg";
        String greenLight = "../green.jpg";
        String redYellowLight = "../red-yellow.jpg";
        String greenYellowLight = "../green-yellow.jpg";
        redImage = new ImageIcon(new ImageIcon(getClass().getResource(redLight)).getImage().getScaledInstance(50, 60, Image.SCALE_SMOOTH));
        greenImage = new ImageIcon(new ImageIcon(getClass().getResource(greenLight)).getImage().getScaledInstance(50, 60, Image.SCALE_SMOOTH));
        redYellowImage = new ImageIcon(new ImageIcon(getClass().getResource(redYellowLight)).getImage().getScaledInstance(50, 60, Image.SCALE_SMOOTH));
        greenYellowImage = new ImageIcon(new ImageIcon(getClass().getResource(greenYellowLight)).getImage().getScaledInstance(50, 60, Image.SCALE_SMOOTH));
    }

    public void addVehicle(boolean direction, int speed){
        if(direction) {
            forwardCars.add(new Vehicle(TrafficSimulator.frame.getWidth(), TrafficSimulator.frame.getHeight()/3 + 80, speed, true));
        }else{
            backwardCars.add(new Vehicle(-100, TrafficSimulator.frame.getHeight()/2, speed, false));
        }
    }

    public void step(){
        if(DirectionSwitcher.currentDirection.equals("forward")){

            for (int i = 0; i < backwardCars.size(); i++) {
                Vehicle v = backwardCars.get(i);
                if (!isCollisionBackward(v)) {
                    if(v.getX() < LEFT_STOP_LINE) {
                        v.setX(v.getX() + v.getSpeed());
                    }else if(v.getX() >= TUNNEL_LEFT_BORDER){
                        v.setY(BACKWARD_LANE_Y);
                        v.setX(v.getX() + v.getSpeed());
                    }
                }else{
                    v.setSpeed(backwardCars.get(i-1).getSpeed());
                }
                if (v.getX() > TrafficSimulator.frame.getWidth()) {
                    backwardCars.remove(i);
                }
            }

            for (int i = 0; i < forwardCars.size(); i++) {
                Vehicle v = forwardCars.get(i);
                if (!isCollisionForward(v) ) {
                    if(v.getX() > TUNNEL_RIGHT_BORDER) {
                        v.setX(v.getX() - v.getSpeed());
                    }else if(v.getX() <= TUNNEL_RIGHT_BORDER && v.getX() > TUNNEL_LEFT_BORDER){
                        v.setY(TUNNEL_Y);
                        v.setX(v.getX() - v.getSpeed());
                    }else if(v.getX() <= TUNNEL_LEFT_BORDER){
                        v.setY(FORWARD_LANE_Y);
                        v.setX(v.getX() - v.getSpeed());
                    }
                }else{
                    v.setSpeed(forwardCars.get(i-1).getSpeed());
                }
                if (v.getX() < 0) {
                    forwardCars.remove(i);
                }
            }




        }else if(DirectionSwitcher.currentDirection.equals("backward")){

            for (int i = 0; i < forwardCars.size(); i++) {
                Vehicle v = forwardCars.get(i);
                if (!isCollisionForward(v)) {
                    if(v.getX() > RIGHT_STOP_LINE) {
                        v.setX(v.getX() - v.getSpeed());
                    }else if(v.getX() <= TUNNEL_RIGHT_BORDER){
                        v.setY(FORWARD_LANE_Y);
                        v.setX(v.getX() - v.getSpeed());
                    }
                }else{
                    v.setSpeed(forwardCars.get(i-1).getSpeed());
                }
                if (v.getX() < 0) {
                    forwardCars.remove(i);
                }
            }

            for (int i = 0; i < backwardCars.size(); i++) {
                Vehicle v = backwardCars.get(i);
                if (!isCollisionBackward(v) ) {
                    if(v.getX() < TUNNEL_LEFT_BORDER) {
                        v.setX(v.getX() + v.getSpeed());
                    }else if(v.getX() >= TUNNEL_LEFT_BORDER && v.getX() < TUNNEL_RIGHT_BORDER){
                        v.setY(TUNNEL_Y);
                        v.setX(v.getX() + v.getSpeed());
                    }else if(v.getX() >= TUNNEL_RIGHT_BORDER){
                        v.setY(BACKWARD_LANE_Y);
                        v.setX(v.getX() + v.getSpeed());
                    }
                }else{
                    v.setSpeed(backwardCars.get(i-1).getSpeed());
                }
                if (v.getX() > TrafficSimulator.frame.getWidth()) {
                    backwardCars.remove(i);
                }
            }




        }else if(DirectionSwitcher.currentDirection.equals("pause")){

            for (int i = 0; i < forwardCars.size(); i++) {
                Vehicle v = forwardCars.get(i);
                if (!isCollisionForward(v) ) {
                    if(v.getX() > RIGHT_STOP_LINE) {
                        v.setX(v.getX() - v.getSpeed());
                    }else if(v.getX() <= TUNNEL_RIGHT_BORDER && v.getX() > TUNNEL_LEFT_BORDER){
                        v.setY(TUNNEL_Y);
                        v.setX(v.getX() - v.getSpeed());
                    }else if(v.getX() <= TUNNEL_LEFT_BORDER){
                        v.setY(FORWARD_LANE_Y);
                        v.setX(v.getX() - v.getSpeed());
                    }
                }else{
                    v.setSpeed(forwardCars.get(i-1).getSpeed());
                }
                if (v.getX() < 0) {
                    forwardCars.remove(i);
                }
            }

            for (int i = 0; i < backwardCars.size(); i++) {
                Vehicle v = backwardCars.get(i);
                if (!isCollisionBackward(v) ) {
                    if(v.getX() < LEFT_STOP_LINE) {
                        v.setX(v.getX() + v.getSpeed());
                    }else if(v.getX() >= TUNNEL_LEFT_BORDER  && v.getX() < TUNNEL_RIGHT_BORDER){
                        v.setY(TUNNEL_Y);
                        v.setX(v.getX() + v.getSpeed());
                    }else if(v.getX() >= TUNNEL_RIGHT_BORDER){
                        v.setY(BACKWARD_LANE_Y);
                        v.setX(v.getX() + v.getSpeed());
                    }
                }else{
                    v.setSpeed(backwardCars.get(i-1).getSpeed());
                }
                if (v.getX() > TrafficSimulator.frame.getWidth()) {
                    backwardCars.remove(i);
                }
            }



        }

    }

    public void changeAllCarSpeed(int newSpeed){
        if(DirectionSwitcher.currentDirection.equals("forward")){
            if(!forwardCars.isEmpty()) {
                for(Vehicle v : forwardCars){
                    v.setSpeed(newSpeed);
                }
            }
        }else if(DirectionSwitcher.currentDirection.equals("backward")){
            if(!backwardCars.isEmpty()) {
                for(Vehicle v : backwardCars){
                    v.setSpeed(newSpeed);
                }
            }
        }
    }

    public void changeHeadCarSpeed(int newSpeed){
        if(DirectionSwitcher.currentDirection.equals("forward")){
            if(!forwardCars.isEmpty()) {
                Vehicle firstCar = forwardCars.getFirst();
                firstCar.setSpeed(newSpeed);
            }
        }else if(DirectionSwitcher.currentDirection.equals("backward")){
            if(!backwardCars.isEmpty()) {
                Vehicle firstCar = backwardCars.getFirst();
                firstCar.setSpeed(newSpeed);
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
        g.drawImage(icon.getImage(), (TrafficSimulator.frame.getWidth() - icon.getIconWidth()) / 2 + 100,TrafficSimulator.frame.getHeight()/4,null);

        if(DirectionSwitcher.currentDirection.equals("forward")){
            prevDirection = "forward";
            g.drawImage(greenImage.getImage(), RIGHT_STOP_LINE - 40, FORWARD_LANE_Y - 120, null);
            g.drawImage(redImage.getImage(), LEFT_STOP_LINE + 100, BACKWARD_LANE_Y + 120, null);
        }else if(DirectionSwitcher.currentDirection.equals("backward")){
            prevDirection = "backward";
            g.drawImage(redImage.getImage(), RIGHT_STOP_LINE - 40, FORWARD_LANE_Y - 120, null);
            g.drawImage(greenImage.getImage(), LEFT_STOP_LINE + 100, BACKWARD_LANE_Y + 120, null);
        }else if(DirectionSwitcher.currentDirection.equals("pause")){
            if(prevDirection.equals("forward")){
                g.drawImage(greenYellowImage.getImage(), RIGHT_STOP_LINE - 40, FORWARD_LANE_Y - 120, null);
                g.drawImage(redYellowImage.getImage(), LEFT_STOP_LINE + 100, BACKWARD_LANE_Y + 120, null);
            }else if(prevDirection.equals("backward")){
                g.drawImage(redYellowImage.getImage(), RIGHT_STOP_LINE - 40, FORWARD_LANE_Y - 120, null);
                g.drawImage(greenYellowImage.getImage(), LEFT_STOP_LINE + 100, BACKWARD_LANE_Y + 120, null);
            }
        }

        for (int i = 0; i < forwardCars.size(); i++) {
            forwardCars.get(i).paintVehicle(g);
        }
        for (int i = 0; i < backwardCars.size(); i++) {
            backwardCars.get(i).paintVehicle(g);
        }

    }
}
