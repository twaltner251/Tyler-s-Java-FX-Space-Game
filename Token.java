public class Token extends Star {
    // boolean variable of whether or not to display token (depends on if collected or not)
    boolean display;
    public Token(double x, double y) {
        // call super (width and height don't matter)
        super(x, y, 0, 0);
        display = true;

    }

    // checks if colliding w/ player
    public boolean isColliding(Player player) {
        if(player.getX() < this.getX() + 15 && player.getX() + player.getSize() > this.getX() &&
        player.getY() < this.getY() + 15 && player.getY() + player.getSize() > this.getY()) {
            return true;
        }
        return false;
    }

    // Set display method
    public void setDisplayFalse() {
        display = false;

    }
    public void setDisplayTrue() {
        display = true;
    }

    // getter methods
    public boolean getDisplay() {
        return display;
    }
    
}