public class EnemyHorzPath extends EnemyCirclePath {
    public EnemyHorzPath(int pathRadius, double centerX, double centerY, double speed) {
        super(pathRadius, centerX, centerY, speed);
    }

    // Override update method to keep x-axis consistent and adjust Y axis in accordance to time.
    @Override
    public void update(double time) {
        x = centerX + pathRadius * Math.sin(speed * time);
        y = centerY;
    }

}