package trafficsimulator;

public class DirectionSwitcher implements Runnable {

    public static String currentDirection = "backward";
    private int phase;

    public DirectionSwitcher(int phase) {
        this.phase = phase;
    }

    @Override
    public void run() {
        while(TrafficSimulator.running) {
            if (currentDirection.equals("forward")) {
                currentDirection = "backward";
                TrafficSimulator.frame.setVisible(true);
                TrafficSimulator.frame.repaint();
            } else if(currentDirection.equals("backward")){
                currentDirection = "forward";
                TrafficSimulator.frame.setVisible(true);
                TrafficSimulator.frame.repaint();
            }
            try {
                Thread.sleep(phase);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String directionHolder = currentDirection;

            currentDirection = "pause";

            try{
                Thread.sleep(10000);
            }catch (Exception e){
                e.printStackTrace();
            }
            currentDirection = directionHolder;

        }
    }
}
