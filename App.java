/* 
 * Made by Tyler Waltner in May 2025 as final project for CSC-171 (Intro to Computer Science - Java) @ University of Rochester
 * Emails: twaltner@u.rochester.edu / waltnertyler@gmail.com
*/



import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Node;
import javafx.stage.Stage;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// What a strange package name!
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
public class App extends Application {
    // instantiate at class level
    int lives;
    int points;
    double fuel;
    boolean paused;
    double height;
    double width;
    double x;
    double y;
    double time; 
    @Override
    public void start(Stage primarystage) {
        // Initialize variables
        lives = 5;
        points = 0;
        paused = false;
        height = 800;
        width = 800;
        x = 0;
        y = 0;
        time = 0;
        fuel = 1.0;

        primarystage.setTitle("SPACE HUNTER - Tyler Waltner");
        FlowPane root = new FlowPane();
        // set spacing between each GUI control
        Scene scene = new Scene(root);     
        scene.setFill(Color.BLACK);   
        primarystage.setScene(scene);

        // Create stackpane to hold GUI controls + text for when you die, win, or lose
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(width, height);

        // Create canvas to display graphics
        Canvas canvas = new Canvas(width, height);
        stackPane.getChildren().add(canvas);

        // Add stackPane to root
        root.getChildren().add(stackPane);
        
        // Set the canvas in the center of BorderPane
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Generate little stars of random sizes in background
        Random rng = new Random();
        Star[] starArr = new Star[400];
        for(int i = 0; i < starArr.length; i++) {
            double r = 5 * rng.nextDouble();
            starArr[i] = new Star(width * rng.nextDouble(), height * rng.nextDouble(), r, r);
        }

        // Button to pause animation
        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(myEvent -> {
            // assign it to the opposite of itself to make it so button can switch it on & off
            paused = !paused;
            pauseButton.setText("Pause".equals(pauseButton.getText())?"Resume":"Pause");
        });

        // Lives tracker
        Label label = new Label();
        // Add label to root
        root.getChildren().add(label);

        // Text for fuel tracker
        Label fuelLabel = new Label("        Fuel Left: ");
        // Actual Fuel tracker
        ProgressBar fuelBar = new ProgressBar();
        fuelBar.setPrefWidth(100);

        // Points tracker
        Label pointsLabel = new Label();

        // Add all GUI controls to root
        root.getChildren().addAll(pauseButton, fuelLabel, fuelBar, pointsLabel);

        // initialize player
        Player player = new Player(10, height - 15, 15, width, height);

        // initialize walls
        ArrayList<Block> blocks = new ArrayList<>();
        // Top border
        blocks.add(new Block(0, -10, width, 10));
        // Left border
        blocks.add(new Block(-10, 0, 10, height));
        // Right border
        blocks.add(new Block(width, 0, 10, height));
        // Bottom border
        blocks.add(new Block(0, height, width, height));
        // left side block
        blocks.add(new Block(50, 50, 10, height));
        // top ledge
        blocks.add(new Block(50, 50, 50, 10));
        // top cover
        blocks.add(new Block(200, 0, 10, 50));
        blocks.add(new Block(200, 50, width, 10));
        // room 1
        blocks.add(new Block(200, 50, 10, 280));
        blocks.add(new Block(100, 330, 110, 10));
        // box under token 1
        blocks.add(new Block(50, 380, 50, 10));
        blocks.add(new Block(100, 380, 10, 100));
        // room 2
        blocks.add(new Block(50, 480, 350, 10));
        blocks.add(new Block(400, 130, 10, 360));
        // platform for token 3
        blocks.add(new Block(400, 130, 50, 10));
        // room 3
        blocks.add(new Block(440, 130, 10, 360));
        blocks.add(new Block(390, 480, 50, 10));
        blocks.add(new Block(width - 10, 50, 10, height));
        // room 4
        blocks.add(new Block(50, height - 10, width - 50, 10));
        blocks.add(new Block(400, 490, 10, 130));
        blocks.add(new Block(400, 650, 10, 150));
        blocks.add(new Block(160, 490, 10, 130));
        blocks.add(new Block(160, 650, 10, 150));
        blocks.add(new Block(160, 650, 280, 10));
        blocks.add(new Block(160, 620, 250, 10));
        
        // initialize tokens
        ArrayList<Token> tokens = new ArrayList<>();
        // a1
        tokens.add(new Token(75, 360));
        tokens.add(new Token(370, 460));
        tokens.add(new Token(415, 110));
        tokens.add(new Token(415, 630));

        // initialize safe zone
        SafeZone safeZone = new SafeZone(60, 490, 100, 400);

        // initialize enemies
        // circle pathed enemies
        ArrayList<EnemyCirclePath> circlePaths = new ArrayList<>();
        // room 1 group 1 
        circlePathsAdd(circlePaths, 120, 110, 3, -0.25, 2);
        // room 1 group 2
        circlePathsAdd(circlePaths, 120, 240, 3, -0.25, -2);
        // room 3 group 1
        circlePathsAdd(circlePaths, 610, 290, 7, 0, 1.8);
        // room 3 group 2
        circlePathsAdd(circlePaths, 610, 620, 7, 0, -1.8);

        // Vert pathed enemies
        ArrayList<EnemyVertPath> vertPaths = new ArrayList<>();
        // room 2 group 1
        vertPathsAdd(vertPaths, 120, 398, 8, 55, 2);
        // room 4 group 1
        vertPathsAdd(vertPaths, 180, 638, 7, 120, 2);

        // Horz pathed enemies
        ArrayList<EnemyHorzPath> horzPaths = new ArrayList<>();
        // room 3 group 1
        horzPathsAdd(horzPaths, 290, 70, 9, 75, 2);
        
        // hash set to detect key inputs
        Set<String> pressedKeys = new HashSet<>();
        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode().toString()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode().toString()));

        // Anonymous class to handle animation
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long nanoTime) {
                // temporarily pauses and ceases animation if paused variable is true
                if(paused) return;

                // update fuel value
                fuel -= 0.0007;

                // ensure fuel doesn't go to negatives
                if(fuel <= 0) {
                    // reset fuel back to full
                    fuel = 1.0;

                    // Reset player position to last collected token, lose a life
                    playerDie(player, tokens);
                    
                    if(lives > 0) {
                        // YOU DIED label
                        Label gameOver = new Label("No Fuel, you died :(\nYou have " + lives + " live(s) left.");
                        gameOver.setTextFill(Color.RED);
                        gameOver.setFont(new Font("Comic Sans", 40));
                        gameOver.setAlignment(Pos.BOTTOM_CENTER);

                        // Add label to StackPane
                        stackPane.getChildren().add(gameOver);

                        // pause
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(event -> {
                            // After pause, remove the label
                            stackPane.getChildren().remove(gameOver);
                        });
                        // resume
                        pause.play();
                    }

                }

                // update fuel bar
                fuelBar.setProgress(fuel);

                // update points tracker
                pointsLabel.setText("       Score: " + points); 

                // update lives in label
                label.setText("    You currently have: " + lives + " lives left.       ");

                // handle lives logic, if <= 0 lives cease animations
                if(lives <= 0) {
                    // STOP ANIMATION
                    this.stop();
                
                    // GAME OVER label
                    Label gameOver = new Label("GAME OVER :(\nFinal Score: " + points + "pts.");
                    gameOver.setTextFill(Color.ORANGERED);
                    gameOver.setFont(new Font("Comic Sans", 75));
                    gameOver.setAlignment(Pos.CENTER);
                    // add to stackPane, not root as root is our HBox at the bottom!
                    stackPane.getChildren().add(gameOver);

                }
                
                // Clear canvas, make all black for space
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, width, height);

                // Increment time variable
                time += 1.0/60.0;

                // Load in our previously created stars
                gc.setFill(Color.WHITE);
                for(int i = 0; i < starArr.length; i++) {
                    gc.fillOval(starArr[i].getX(), starArr[i].getY(), starArr[i].getHeight(), starArr[i].getHeight());
                }

                // display tokens & handle tokens logic
                gc.setFill(Color.GOLD);
                for(Token t : tokens) {
                    if(t.getDisplay()) {
                        // if display is true, display token
                        displayTokens(gc, t);

                        if(t.isColliding(player)) {
                            // if token colliding with player: set display to false, refill tank, add 50pts
                            t.setDisplayFalse();
                            points += 50;
                            fuel = 1.0;

                        }

                    } 

                }

                // display safe zone & handle safe zone logic
                gc.setFill(Color.GREEN);
                displaySafeZone(gc, safeZone);
                if(safeZone.isColliding(player)) {
                    // STOP ANIMATION
                    this.stop();
                
                    // GAME OVER label
                    Label gameOver = new Label("YOU WIN!! :)\nFinal Score: " + points + "pts.");
                    gameOver.setTextFill(Color.GREEN);
                    gameOver.setFont(new Font("Comic Sans", 75));
                    gameOver.setAlignment(Pos.CENTER);
                    // add to stackPane, not root as root is our HBox at the bottom!
                    stackPane.getChildren().add(gameOver);
                }

                // START LEVEL CODE
                // display walls
                gc.setFill(Color.GREY);
                for(Block b : blocks) {
                    displayBlocks(gc, b);
                }

                // dispay 'enemies'
                gc.setFill(Color.LIGHTBLUE);
                for(EnemyCirclePath e : circlePaths) {
                    // update x & y
                    e.update(time);
                    // display
                    displayCirclePath(gc, e);
                    // if player is touching enemy, run the player die command (negates 1 life)
                    if(e.isColliding(player)) {
                        // Reset player position to last collected token, lose a life
                        playerDie(player, tokens);

                        if(lives > 0) {
                            // YOU DIED label
                            Label gameOver = new Label("YOU DIED :(\nYou have " + lives + " live(s) left.");
                            gameOver.setTextFill(Color.RED);
                            gameOver.setFont(new Font("Comic Sans", 40));
                            gameOver.setAlignment(Pos.BOTTOM_CENTER);

                            // Add label to StackPane
                            stackPane.getChildren().add(gameOver);

                            // pause
                            PauseTransition pause = new PauseTransition(Duration.seconds(2));
                            pause.setOnFinished(event -> {
                                // After pause, remove the label
                                stackPane.getChildren().remove(gameOver);
                            });
                            // resume
                            pause.play();
                        }

                    }

                }

                gc.setFill(Color.BLUE);
                for(EnemyVertPath e : vertPaths) {
                    // update x & y
                    e.update(time);
                    // display
                    displayVertPath(gc, e);
                    // if player is touching enemy, run the player die command (negates 1 life)
                    if(e.isColliding(player)) {
                        // Reset player position to last collected token, lose a life
                        playerDie(player, tokens);

                        if(lives > 0) {
                            // YOU DIED label
                            Label gameOver = new Label("YOU DIED :(\nYou have " + lives + " live(s) left.");
                            gameOver.setTextFill(Color.RED);
                            gameOver.setFont(new Font("Comic Sans", 40));
                            gameOver.setAlignment(Pos.BOTTOM_CENTER);

                            // Add label to StackPane
                            stackPane.getChildren().add(gameOver);

                            // pause
                            PauseTransition pause = new PauseTransition(Duration.seconds(2));
                            pause.setOnFinished(event -> {
                                // After pause, remove the label
                                stackPane.getChildren().remove(gameOver);
                            });
                            // resume
                            pause.play();
                        }
                    }

                }
    
                for(EnemyHorzPath e : horzPaths) {
                    // update x & y
                    e.update(time);
                    // display
                    displayHorzPath(gc, e);
                    // if player is touching enemy, run the player die command (pauses game, negates 1 life, resets player @ start.)
                    if(e.isColliding(player)) {
                        // Reset player position to last collected token, lose a life
                        playerDie(player, tokens);

                        if(lives > 0) {
                            // YOU DIED label
                            Label gameOver = new Label("YOU DIED :(\nYou have " + lives + " live(s) left.");
                            gameOver.setTextFill(Color.RED);
                            gameOver.setFont(new Font("Comic Sans", 40));
                            gameOver.setAlignment(Pos.BOTTOM_CENTER);

                            // Add label to StackPane
                            stackPane.getChildren().add(gameOver);

                            // pause
                            PauseTransition pause = new PauseTransition(Duration.seconds(2));
                            pause.setOnFinished(event -> {
                                // After pause, remove the label
                                stackPane.getChildren().remove(gameOver);
                            });
                            // resume
                            pause.play();
                        }

                    }

                }
                // END LEVEL CODE

                // START PLAYER CODE 
                // update player
                player.update(pressedKeys, blocks);
                // display player
                displayPlayer(gc, player);
                // END PLAYER CODE

            }

        };

        // starts animating
        timer.start();

        // show stage
        primarystage.show();

    }

    // circlePathsAdd method
    public static void circlePathsAdd(ArrayList<EnemyCirclePath> circlePaths, double x, double y, double count, double deltaSpeed, double speed) {
        for(int i = 0; i < count; i++) {
            // make two circle enemies one on either side of axis
            circlePaths.add(new EnemyCirclePath(i * 25, x, y, speed));
            circlePaths.add(new EnemyCirclePath(-i * 25, x, y, speed));
            // adjust speed
            speed += deltaSpeed;

        }
            
    }

    // vertPathsAdd method
    public static void vertPathsAdd(ArrayList<EnemyVertPath> vertPaths, double x, double y, double count, int distance, double speed) {
        for(int i = 0; i < count; i++) {
            vertPaths.add(new EnemyVertPath(distance, x, y, speed));
            // shift next vert enemy by 30
            x += 30;
            // switch direction every time
            speed *= -1;

        }
            
    }

    // horzPathsAdd method
    public static void horzPathsAdd(ArrayList<EnemyHorzPath> horzPaths, double x, double y, double count, int distance, double speed) {
        for(int i = 0; i < count; i++) {
            horzPaths.add(new EnemyHorzPath(distance, x, y, speed));
            // shift next horz enemy by 30
            y += 30;
            // switch direction every time
            speed *= -1;
        
        }
            
    }

    // START DISPLAY METHODS
    // display walls
    public static void displayBlocks(GraphicsContext gc, Block block) {
        gc.fillRect(block.getX() , block.getY(), block.getWidth(), block.getHeight());
    }

    // display Token
    public static void displayTokens(GraphicsContext gc, Token token) {
        gc.fillOval(token.getX() , token.getY(), 15, 15);
    }

    // display safe zone
    public static void displaySafeZone(GraphicsContext gc, SafeZone safeZone) {
        gc.fillRect(safeZone.getX() , safeZone.getY(), safeZone.getWidth(), safeZone.getHeight());
    }

    // display different enemy types
    public static void displayHorzPath(GraphicsContext gc, EnemyHorzPath e) {
        gc.fillOval(e.getX(), e.getY(), 25, 25);
    }
    public static void displayVertPath(GraphicsContext gc, EnemyVertPath e) {
        gc.fillOval(e.getX(), e.getY(), 25, 25);
    }
    public static void displayCirclePath(GraphicsContext gc, EnemyCirclePath e) {
        gc.fillOval(e.getX(), e.getY(), 25, 25);
    }

    // display player
    public static void displayPlayer(GraphicsContext gc, Player player) {
        // thruster control
        gc.setFill(Color.RED);
        if (player.getTop()) {
            gc.fillRect(player.getX() + player.getSize() / 4, player.getY() + player.getSize(), player.getSize() / 2, player.getSize() / 4);
        }

        if (player.getLeft()) {
            gc.fillRect(player.getX() + player.getSize(), player.getY() + player.getSize() / 4, player.getSize() / 4, player.getSize() / 2);
        }
        
        if (player.getRight()) {
            gc.fillRect(player.getX() - player.getSize() / 4, player.getY() + player.getSize() / 4, player.getSize() / 4, player.getSize() / 2);
        }

        // update & display Player 
        gc.setFill(Color.ORANGE);
        gc.fillRect(player.getX(), player.getY(), player.getSize(), player.getSize());
        gc.setFill(Color.BLACK);

        // eyes
        gc.fillRect(player.getX() + player.getSize() / 5, player.getY() + player.getSize() / 3, player.getSize() / 5, player.getSize() / 5);
        gc.fillRect(player.getX() + player.getSize() * 3 / 5, player.getY() + player.getSize() / 3, player.getSize() / 5, player.getSize() / 5);
        
        // mouth
        gc.fillRect(player.getX() + player.getSize() / 5, player.getY() + player.getSize() * 2 / 3, player.getSize() * 3 / 5, player.getSize() / 6);
        
    }
    // END DISPLAY METHODS
    
    // playerDie method
    public void playerDie(Player player, ArrayList<Token> tokens) {
        lives -= 1;
        fuel = 1.0;
        double x = 25;
        double y = height - 15;
        for(Token t : tokens) {
            if(!t.getDisplay()) {
                x = t.getX();
                y = t.getY();
            }
        }

        player.setX(x);
        player.setY(y);
    
    }

    // main method
    public static void main(String[] args) {
        launch(args);
    }

}