public class EnemyCirclePath {
    // initialize variables
    int pathRadius;
    double x;
    double y;
    double centerX; 
    double centerY;
    double speed;
    public EnemyCirclePath(int pathRadius, double centerX, double centerY, double speed) {
        // instantiate variables
        this.pathRadius = pathRadius;
        x = 0;
        y = 0;
        this.centerX = centerX;
        this.centerY = centerY;
        this.speed = speed;
    }

    // checks if colliding w/ player
    public boolean isColliding(Player player) {
        if(player.getX() < x + 25 && player.getX() + player.getSize() > x &&
        player.getY() < y + 25 && player.getY() + player.getSize() > y) {
            return true;
        }
        return false;
    }

    // setters
    public void update(double time) {
        // move along a circle for example
        x = centerX + pathRadius * Math.cos(speed * time);
        y = centerY + pathRadius * Math.sin(speed * time);
    }

    // getters
    public double getPathRadius() {
        return pathRadius;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

}