package trafficsimulator;

public class DirectionSwitcher implements Runnable {

    public static boolean currentDirection;
    private int phase;

    public DirectionSwitcher(int phase) {
        this.phase = phase;
    }

    @Override
    public void run() {
        while(TrafficSimulator.running) {
            if (currentDirection) {
                currentDirection = false;
                System.out.println("direction changed 1");
            } else {
                currentDirection = true;
                System.out.println("direction changed 2");
            }
            try {
                Thread.sleep(phase);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
