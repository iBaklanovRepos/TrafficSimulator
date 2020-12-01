package trafficsimulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Highway extends JPanel {

    private int forward;
    private int backward;
    private Random random = new Random();

    public static final int ROAD_WIDTH = 2000;
    public static final int LANE_HEIGHT = 150;

    private ArrayList<Vehicle> forwardCars = new ArrayList<Vehicle>();
    private ArrayList<Vehicle> backwardCars = new ArrayList<Vehicle>();

    public Highway(int forward, int backward) {
        super();
        this.forward = forward;
        this.backward = backward;
    }

    public void addVehicle(boolean direction, int speed) {
        if (direction) {
            int y = (new Random().nextInt(forward) + 1) * LANE_HEIGHT - LANE_HEIGHT / 2;
            forwardCars.add(new Vehicle(TrafficSimulator.frame.getWidth(), y, speed, true));
        } else {
            int y = (new Random().nextInt(backward) + 1 + forward) * LANE_HEIGHT - LANE_HEIGHT / 2;
            backwardCars.add(new Vehicle(-100, y, speed, false));
        }
    }

    public void step() {
        for (int i = 0; i < backwardCars.size(); i++) {
            Vehicle v = backwardCars.get(i);
            if (!isCollisionBackward(v)) {
                v.setX(v.getX() + v.getSpeed());
            } else {
                if (backward > 1) {
                    overtakeBackward(v);
                }
            }
            if (v.getX() > TrafficSimulator.frame.getWidth() + 100) {
                backwardCars.remove(i);
            }
        }

        for (int i = 0; i < forwardCars.size(); i++) {
            Vehicle v = forwardCars.get(i);
            if (!isCollisionForward(v)) {
                v.setX(v.getX() - v.getSpeed());
            } else {
                if (forward > 1) {
                    overtakeForward(v);
                }

            }
            if (v.getX() < -100) {
                forwardCars.remove(i);
            }
        }
    }

    public boolean isCollisionBackward(Vehicle vehicle) {
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

    public boolean isCollisionForward(Vehicle vehicle) {
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

    public void overtakeForward(Vehicle vehicle) {
        boolean isAbleToOvertakeRight = true;
        boolean isAbleToOvertakeLeft = true;
        if (vehicle.getY() < LANE_HEIGHT) {
            isAbleToOvertakeRight = false;
            for (Vehicle leftForwardVehicle : forwardCars) {
                if (!vehicle.equals(leftForwardVehicle)) {
                    if ((leftForwardVehicle.getY() == vehicle.getY() + LANE_HEIGHT) &&
                            (vehicle.getX() - vehicle.getSpeed() - Vehicle.VEH_WIDTH < leftForwardVehicle.getX()) &&
                            leftForwardVehicle.getX() < vehicle.getX()) {
                        isAbleToOvertakeLeft = false;
                    }
                }
            }
        } else if (vehicle.getY() > LANE_HEIGHT * (forward - 1)) {
            isAbleToOvertakeLeft = false;
            for (Vehicle rightForwardVehicle : forwardCars) {
                if (!vehicle.equals(rightForwardVehicle)) {
                    if ((rightForwardVehicle.getY() == vehicle.getY() - LANE_HEIGHT) &&
                            (vehicle.getX() - vehicle.getSpeed() - Vehicle.VEH_WIDTH < rightForwardVehicle.getX()) &&
                            rightForwardVehicle.getX() < vehicle.getX()) {
                        isAbleToOvertakeRight = false;
                    }
                }
            }
        } else {
            for (Vehicle veh : forwardCars) {
                if (!vehicle.equals(veh)) {
                    if ((veh.getY() == vehicle.getY() - LANE_HEIGHT) &&
                            (vehicle.getX() - vehicle.getSpeed() - Vehicle.VEH_WIDTH < veh.getX()) &&
                            veh.getX() < vehicle.getX()) {
                        isAbleToOvertakeRight = false;
                    } else if ((veh.getY() == vehicle.getY() + LANE_HEIGHT) &&
                            (vehicle.getX() - vehicle.getSpeed() - Vehicle.VEH_WIDTH < veh.getX()) &&
                            veh.getX() < vehicle.getX()) {
                        isAbleToOvertakeLeft = false;
                    }
                }
            }
        }
        if (isAbleToOvertakeLeft) {
            vehicle.setY(vehicle.getY() + LANE_HEIGHT);
        } else if (isAbleToOvertakeRight) {
            vehicle.setY(vehicle.getY() - LANE_HEIGHT);
        }

    }

    public void overtakeBackward(Vehicle vehicle) {
        boolean isAbleToOvertakeRight = true;
        boolean isAbleToOvertakeLeft = true;
        if (vehicle.getY() < LANE_HEIGHT * (forward + 1)) {
            isAbleToOvertakeLeft = false;
            for (Vehicle veh : backwardCars) {
                if (!vehicle.equals(veh)) {
                    if ((veh.getY() == vehicle.getY() + LANE_HEIGHT) &&
                            (vehicle.getX() + vehicle.getSpeed() + Vehicle.VEH_WIDTH > veh.getX()) &&
                            veh.getX() > vehicle.getX()) {
                        isAbleToOvertakeRight = false;
                    }
                }
            }
        } else if (vehicle.getY() > LANE_HEIGHT * (forward + backward - 1)) {
            isAbleToOvertakeRight = false;
            for (Vehicle veh : backwardCars) {
                if (!vehicle.equals(veh)) {
                    if ((veh.getY() == vehicle.getY() - LANE_HEIGHT) &&
                            (vehicle.getX() + vehicle.getSpeed() + Vehicle.VEH_WIDTH > veh.getX()) &&
                            veh.getX() > vehicle.getX()) {
                        isAbleToOvertakeLeft = false;
                    }
                }
            }
        } else {
            for (Vehicle veh : backwardCars) {
                if (!vehicle.equals(veh)) {
                    if ((veh.getY() == vehicle.getY() - LANE_HEIGHT) &&
                            (vehicle.getX() + vehicle.getSpeed() + Vehicle.VEH_WIDTH > veh.getX()) &&
                            veh.getX() > vehicle.getX()) {
                        isAbleToOvertakeLeft = false;
                    } else if ((veh.getY() == vehicle.getY() + LANE_HEIGHT) &&
                            (vehicle.getX() + vehicle.getSpeed() + Vehicle.VEH_WIDTH > veh.getX()) &&
                            veh.getX() > vehicle.getX()) {
                        isAbleToOvertakeRight = false;
                    }
                }
            }
        }
        if (isAbleToOvertakeLeft) {
            vehicle.setY(vehicle.getY() - LANE_HEIGHT);
        } else if (isAbleToOvertakeRight) {
            vehicle.setY(vehicle.getY() + LANE_HEIGHT);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), LANE_HEIGHT * (forward + backward));
        g.setColor(Color.WHITE);
        for (int i = 0; i < forward; i++) {
            for (int j = 0; j < getWidth(); j += 45) {
                g.fillRect(j, LANE_HEIGHT + LANE_HEIGHT * i, 30, 5);
            }
        }
        g.fillRect(0, LANE_HEIGHT * forward, getWidth(), 5);
        g.fillRect(0, LANE_HEIGHT * forward + 10, getWidth(), 5);
        for (int i = forward; i < backward + forward; i++) {
            for (int j = 0; j < getWidth(); j += 45) {
                g.fillRect(j, LANE_HEIGHT * i, 30, 5);
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
