package main;

import processing.core.*;
import snakes.Box;

/**
 * Main class of the simulation. It handles the window. It is based on
 * Processing library, so its really easy to use.
 * @author Jakub Medek
 */
public class MainSketch extends PApplet{ 
    Box box;
    
    /**
     * Sets some basic settings of the window (its size).
     */
    @Override
    public void settings() {
        //size(1920, 1080);
        fullScreen();
    }
    
    /**
     * Sets the enviroment (limits framerate) and makes the simulation object.
     */
    @Override
    public void setup() {
        frameRate(60);
        
        int snakeCount = 10;
        int maxFood = 12;
        float foodSize = 10;
        float bodySize = 20;
        float padding = 20;
        float snakeMaxHealth = 400;
        float snakeHealthFromMeal = 400;
        PApplet applet = this;
        box = new Box(snakeCount, maxFood, foodSize, bodySize, padding, snakeMaxHealth, snakeHealthFromMeal, applet);
    }
    
    /**
     * Should get called about 60 times per second (this is set by frameRate 
     * funcion). Updates the simulation.
     */
    @Override
    public void draw() {
        background(0);
        
        box.manageFood();
        box.manageSnakes();
        box.displayInfo();
    }
    
    /**
     * Gets called, when some key is pressed.
     */
    @Override
    public void keyPressed() {
        box.keyPressed(key);
    }
}