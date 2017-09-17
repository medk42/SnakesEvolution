package snakes;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import processing.core.PVector;

/**
 * Class which handles moving and displaying food.
 * 
 * @author Jakub Medek
 */
public class Food {
   public PVector position;
   public PVector velocity;
   public float foodSize;

    /**
     * Constructor, which sets position, velocity and size of food.
     * @param position Defines position of the food.
     * @param foodSize  Ddefines size of the food, for displaying and 
     * calculating distance.
     */
    public Food(PVector position, float foodSize) {
        this.position = position;
        this.velocity = PVector.random2D();
        this.foodSize = foodSize;
    }

    /**
     * Updating position based on speed, which handlees going over the edge 
     * by bouncing.
     * @param padding Padding from the edge of screen, where the food is 
     * not allowed to go.
     * @param applet Applet to get width and height from.
     */
    public void update(float padding, PApplet applet) {
        position.add(velocity);

        if (position.x >= applet.width-padding) {
            position.x -= velocity.x;
            velocity.x *= -1;
        }
        else if (position.x < padding) {
            position.x -= velocity.x;
            velocity.x *= -1;
        }

        if (position.y >= applet.height-padding) {
            position.y -= velocity.y;
            velocity.y *= -1;
        }
        else if (position.y < padding) {
            position.y -= velocity.y;
            velocity.y *= -1;
        }
    }

    /**
     * Displays the food as a rede dot. Based on position and food size.
     * @param applet Applet to draw food.
     */
    public void display(PApplet applet) {
        applet.fill(200, 100, 100);
        applet.noStroke();
        applet.ellipseMode(CENTER);
        applet.ellipse(position.x, position.y, foodSize, foodSize);
    }
}