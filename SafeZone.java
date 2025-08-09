public class SafeZone extends Star {
    
    public SafeZone(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    // checks if colliding w/ player
    public boolean isColliding(Player player) {
        if(player.getX() < super.getX() + super.getWidth() && player.getX() + player.getSize() > super.getX() &&
        player.getY() < super.getY() + super.getHeight() && player.getY() + player.getSize() > super.getY()) {
            return true;
        }
        return false;
    }
}
