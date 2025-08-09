public class EnemyVertPath extends EnemyCirclePath{
    public EnemyVertPath(int pathRadius, double centerX, double centerY, double speed) {
        super(pathRadius, centerX, centerY, speed);
    }

    // Override update method to keep x-axis consistent and adjust Y axis in accordance to time.
    @Override
    public void update(double time) {
        x = centerX;
        y = centerY + pathRadius * Math.sin(speed * time);
    }

}