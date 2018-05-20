package snakes;

import java.util.ArrayList;
import java.util.List;
import nn.DNA;
import nn.NeuralNetwork;
import processing.core.PVector;
import processing.core.PApplet;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.constrain;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.radians;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.RGB;
import static processing.core.PConstants.TWO_PI;

/**
 * Class which handles one snake. It takes care of updating his position, 
 * handling his health and "thinking" (using neural network).
 * 
 * @author Jakub Medek
 */
public class Snake {
    DNA dna;
    NeuralNetwork neuralNetwork;
    List<PVector> body = new ArrayList<>();
    float heading;
    float bodySize;
    float health, healthFromMeal;
    int liveLength = 0;

    /**
     * Constructor, which assings random DNA, position, heading and size 
     * of body
     * @param bodySize Size of snake parts - used for drawing
     * @param padding Padding from the sides of the screen - restricts area 
     * where snake is generated
     * @param maxHealth Maximum health of a snake. It gets assigned to a 
     * health variable and is reduced by one every frame.
     * @param healthFromMeal Extra health given by eating one meal.
     * @param applet Applet to get width, height and random values.
     */
    public Snake(float bodySize, float padding, float maxHealth, float healthFromMeal, PApplet applet) {
        this.health = maxHealth;
        this.healthFromMeal = healthFromMeal;
        dna = new DNA(1091);
        PVector originalPosition = new PVector(applet.random(applet.width-2*padding)+padding, applet.random(applet.height-2*padding)+padding);
        body.add(originalPosition);
        
        body.add(PVector.add(originalPosition, new PVector(1, 1)));
        body.add(PVector.add(originalPosition, new PVector(bodySize*2, bodySize*2)));
        
        heading = applet.random(TWO_PI);
        this.bodySize = bodySize;
        int[] layersCount = new int[]{48, 16, 16, 2};



        neuralNetwork = new NeuralNetwork(layersCount, dna);
    }

    /**
     * Function to call when snake bumps into food. It gives him another 
     * body part and increases his health.
     */
    public void eatFood() {
        health += healthFromMeal;
        PVector lastPart = body.get(body.size()-1);
        body.add(lastPart.copy());
    }

    /**
     * Function to steer the snake - change its heading. It takes vision 
     * from raycast function and feed it forward through its neural 
     * network. Then it steers based on output of the network.
     * @param food ArrayList of food, used for raycast function. Snake 
     * needs to know, if it's looking at food.
     * @param restrictions ArrayList of restriction, used for steering 
     * the snake away from walls. Also used in raycast function.
     */
    public void steer(Food[] food, List<PVector> restrictions) {
        float[] vision = rayCast(food, restrictions);
        float[] control = neuralNetwork.feedForward(vision);
        float steering = control[0] - control[1];

        heading += radians(constrain(steering*100,-10,10));
        if (heading >= TWO_PI) heading -= TWO_PI;
        else if (heading < 0) heading += TWO_PI;
    }

    /**
     * It generates vision for the snake. Snake can see its body, food 
     * and walls.
     * @param food ArrayList of food. Snake needs to know, if it's looking 
     * at some.
     * @param restrictions ArrayList of restrictions. Snakes needs to know
     * if it's looking at some.
     * @return Returns a vision of snake as an 48 long float array.
     */
    private float[] rayCast(Food[] food, List<PVector> restrictions) {
        float[] vision = new float[48];

        PVector head = body.get(0);
        for (int i = 1; i < body.size(); i++) {
            PVector bodyPart = body.get(i);
            float vectorHeading = PVector.sub(bodyPart, head).heading() + HALF_PI;
            if (vectorHeading < 0) vectorHeading += TWO_PI;
            float angle = vectorHeading - this.heading;
            if (abs(angle) < PI/3) {
                angle += PI/3;
                int id = (int) (angle*8/PI*3);
                if (id < 16) {
                    float calculatedVision = 50/dist(head.x, head.y, bodyPart.x, bodyPart.y);
                    if (calculatedVision > vision[id]) vision[id] = calculatedVision;
                }
            }
        }

        for (Food meal : food) {
            float vectorHeading = PVector.sub(meal.position, head).heading() + HALF_PI;
            if (vectorHeading < 0) vectorHeading += TWO_PI;
            float angle = vectorHeading - this.heading;
            if (abs(angle) < PI/3) {
                angle += PI/3;
                int id = (int) (angle*8/PI*3);
                if (id < 16) {
                    float calculatedVision = 300/dist(head.x, head.y, meal.position.x, meal.position.y);
                    if (calculatedVision > vision[id+16]) vision[id+16] = calculatedVision;
                }
            }
        }

        for (PVector restriction : restrictions) {
            float vectorHeading = PVector.sub(restriction, head).heading() + HALF_PI;
            if (vectorHeading < 0) vectorHeading += TWO_PI;
            float angle = vectorHeading - this.heading;
            if (angle > TWO_PI) angle -= TWO_PI;
            if (abs(angle) < PI/3) {
                angle += PI/3;
                int id = (int) (angle*8/PI*3);
                if (id < 16) {
                    float calculatedVision = 50/dist(head.x, head.y, restriction.x, restriction.y);
                    if (calculatedVision > vision[id+32]) vision[id+32] = calculatedVision;
                }
            }
        }

        return vision;
    }

    /**
     * Returns a fitness of the snake. Calculated by 100 times its lenght 
     * plus its health.
     * @return Fitness of the snake.
     */
    public float getFitness() {
        return (body.size()-3)*300;
    }

    /**
     * Updates health reducing it by 1.
     */
    public void updateHealth() {
        health--;
        liveLength++;
    }

    /**
     * Returns whether the snake is alive or not.
     * @return State - true for alive and false for dead.
     */
    public boolean isAlive() {
        return (health > 0);
    }

    /**
     * Function to move the snake. The head moves according to the steer 
     * and all the other body parts move towards the next one.
     */
    public void move() {
        float speed = 3; //change speed based on size
        PVector velocity = new PVector(0, -speed);
        velocity.rotate(heading);
        body.get(0).add(velocity);
        for (int i = 1; i < body.size(); i++) {
            PVector lastPart = body.get(i-1);
            PVector actPart = body.get(i);
            float mag = dist(lastPart.x, lastPart.y, actPart.x, actPart.y) - bodySize;
            PVector deltaPosition = PVector.sub(lastPart, actPart);
            deltaPosition.normalize();
            deltaPosition.mult(mag);
            actPart.add(deltaPosition);
        }
    }

    /**
     * Function to display the snake. It takes the color from the last 
     * byte in DNA.
     * @param isBest True, if this snake is the best snake - snake with 
     * highest fitness.
     * @param applet
     */
    public void display(boolean isBest, PApplet applet) {
        applet.colorMode(HSB);
        applet.fill(dna.genes[1090]+128, 255, 255);
        applet.noStroke();
        applet.ellipseMode(CENTER);
        if (isBest) applet.fill(255);

        for (PVector bodyPart : body) applet.ellipse(bodyPart.x, bodyPart.y, bodySize, bodySize);

        applet.colorMode(RGB);

        if (isBest) {
            applet.pushMatrix();
                PVector head = body.get(0);
                applet.translate(head.x, head.y);
                applet.stroke(255);
                PVector act = PVector.fromAngle(heading-HALF_PI).mult(100);
                applet.line(0, 0, act.x, act.y);

                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);

                act = PVector.fromAngle(heading-HALF_PI).mult(100);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
                act.rotate(-PI / 3 / 8);
                applet.line(0, 0, act.x, act.y);
            applet.popMatrix();
        }
    }
}
