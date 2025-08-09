import java.util.Set;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    // initialize variables
    double x;
    double y;
    boolean xIsOverlapping;
    boolean yIsOverlapping;
    boolean onGround;
    double prevX;
    double prevY;
    double gravity;
    double canvasWidth;
    double canvasHeight;
    double size;
    double maxSpeed;
    double timeWNotPressed;
    double xVelocity;
    double yVelocity;
    boolean left;
    boolean right;
    boolean top;
    boolean bottom;

    // constructor
    public Player(double x, double y, double size, double canvasWidth, double canvasHeight) {
        // instantiate variables
        this.x = x;
        this.y = y;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        gravity = 1.0/4000.0;
        maxSpeed = 0.2;
        this.size = size;
        timeWNotPressed = 0;
        left = false;
        right = false;
        top = false;
        bottom = false;
    }

    // use hashmap to recieve key inputs
    public void update(Set<String> input, ArrayList<Block> Arr) {
        // instantiate prevX & prevY
        prevX = x;
        prevY = y;
        // check for both arrow keys and WASD
        if(input.contains("A")) {
            if(xVelocity > -maxSpeed) {
                xVelocity -= maxSpeed * 1.0/60.0;
                left = true;
            }
        }
        if(input.contains("D")) {
            if(xVelocity < maxSpeed) {
                xVelocity += maxSpeed * 1.0/60.0;
                right = true;
            }
        }
        if(input.contains("W")) {
            if(yVelocity > -maxSpeed) {
                yVelocity -= maxSpeed * 1.0/60.0;
                top = true;
            }
            // reset time 'W' is not pressed to 0
            timeWNotPressed = 0;
        }

        // updates x & y while preventing player from colliding with walls and leaving the border
        ifColliding(Arr);

        // thruster control
        if(!input.contains("W")) {
            top = false;
            // friction
            yVelocity *= 0.95;
            timeWNotPressed += 1;
            // if we aren't on ground and yVelocity hasn't exceeded maxSpeed
            if(!onGround && Math.abs(yVelocity) < maxSpeed) {
                // change velocity by how much time W key hasn't been pressed (gravity accelerates with limit -> maxSpeed)
                yVelocity += timeWNotPressed * gravity;
            }
        }
        if(!input.contains("A")) {
            left = false;
        }
        if(!input.contains("D")) {
            right = false;
        }
        if(!input.contains("A") && !input.contains("D")) {
            // friction
            xVelocity *= 0.95;
        }


    }

    // collision detection (update later to Arr)
    public void ifColliding(ArrayList<Block> Arr) {
        for(Block iterateBlock : Arr) {
            // initialize variables
            onGround = true; 
            xIsOverlapping = false;
            yIsOverlapping = false;
            
            // initialize variable
            double nextX = x + xVelocity;
            double nextY = y + yVelocity;

            // check x collisions
            for (Block block : Arr) {
                if (nextX + size > block.getX() && nextX < block.getX() + block.getWidth() &&
                    y + size > block.getY() && y < block.getY() + block.getHeight()) {
                    xIsOverlapping = true;
                    break;
                }
            }

            // update x movement
            if (xIsOverlapping) {
                xVelocity = 0;
                x = prevX; 
            } else {
                x = nextX;
            }

            // check y collisions
            for (Block block : Arr) {
                if (x + size > block.getX() && x < block.getX() + block.getWidth() &&
                    nextY + size > block.getY() && nextY < block.getY() + block.getHeight()) {
                    yIsOverlapping = true;
                    break;
                }
            }

            // update y movement
            if (yIsOverlapping) {
                yVelocity = 0;
                y = prevY;
                onGround = true;
                timeWNotPressed = 0;
            } else {
                y = nextY;
                onGround = false;
            }

        }

    }

    // setter methods
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    // getter methods
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getSize() {
        return size;
    }
    public boolean getTop() {
        return top;
    }
    public boolean getLeft() {
        return left;
    }
    public boolean getRight() {
        return right;
    }

}
