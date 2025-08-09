import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// Simplie little class to make code more readable
public class Star{
    // initialize variables
    double starX;
    double starY;
    double starWidth;
    double starHeight;

    // constrcutor
    public Star(double starX, double starY, double starWidth, double starHeight) {
        // instantiate variables via constructor
        this.starX = starX;
        this.starY = starY;
        this.starWidth = starWidth;
        this.starHeight = starHeight;
    }

    // getter methods
    public double getX() {
        return starX;
    }
    public double getY() {
        return starY;
    }
    public double getWidth() {
        return starWidth;
    }
    public double getHeight() {
        return starHeight;
    }
    
}