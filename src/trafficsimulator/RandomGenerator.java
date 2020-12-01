package trafficsimulator;

import java.util.Random;

public class RandomGenerator implements Runnable{
    private static Random random;
    private String mode;



    public RandomGenerator(int phase, String mode){
        random = new Random();
        this.mode = mode;
        if(mode.equals("tunnel")) {
            DirectionSwitcher switcher = new DirectionSwitcher(phase);
            Thread thread = new Thread(switcher);
            thread.start();
        }
    }

    public static int getRandomSpeed(){
            return random.nextInt(10) + 1;
    }

    public static int getRandomTime(){
        if(TrafficSimulator.trafficType.equals("determined")){
            return TrafficSimulator.determInterval * 1000;
        }else {
            return random.nextInt(10) * 500;
        }
    }

    @Override
    public void run() {
        while (TrafficSimulator.running){
            if(mode.equals("highway")) {
                TrafficSimulator.highway.addVehicle(random.nextBoolean(), getRandomSpeed());
                try {
                    Thread.sleep(getRandomTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                TrafficSimulator.tunnel.addVehicle(DirectionSwitcher.currentDirection, getRandomSpeed());
                System.out.println(DirectionSwitcher.currentDirection);
                try {
                    Thread.sleep(getRandomTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
