package snakes;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import nn.DNA;
import visualization.Graph;
import visualization.NetworkVisualization;
import processing.core.PApplet;
import processing.core.PVector;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.max;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

/**
 * Class which handles the whole simulation. It takes care of the snakes
 * and food. It also displays the food and checks for collisions between
 * food and snakes.
 * 
 * @author Jakub Medek
 */
public class Box {
    Food[] food;
    List<PVector> restrictions = new ArrayList<>();
    Snake[] snakes;
    NetworkVisualization networkVisualization;
    float foodSize;
    float padding;

    float snakeBodySize;
    float snakeMaxHealth;
    float snakeHealthFromMeal;

    int bestSnakeId = 0;
    boolean toggleShowNN;
    boolean toggleShowFramerate;
    boolean toggleShowGraph;
    boolean running;

    Graph graph;
    
    PApplet applet;

    /**
     * Constructor - sets the simulation enviroment
     * @param snakeCount Total number of snakes
     * @param maxFood Maximum number of food displayed
     * @param foodSize Size of food - used for drawing
     * @param bodySize Size of snake parts - used for drawing
     * @param padding Padding from the sides of the screen - restricts area 
     * where food and snakes are generated
     * @param snakeMaxHealth Maximum health of a snake. It gets assigned to a 
     * health variable and is reduced by one every frame.
     * @param snakeHealthFromMeal Extra health given by eating one meal.
     * @param applet Applet to display and get information about the scene.
     */
    public Box(int snakeCount, int maxFood, float foodSize, float bodySize, float padding, float snakeMaxHealth, float snakeHealthFromMeal, PApplet applet) {
        this.applet = applet;
        
        snakes = new Snake[snakeCount];
        for (int i = 0; i < snakes.length; i++) snakes[i] = new Snake(bodySize, padding, snakeMaxHealth, snakeHealthFromMeal, applet);
        this.foodSize = foodSize;
        this.padding = padding;
        for (int i = 0; i <= applet.width/10; i++) {
            restrictions.add(new PVector(i*10, 0));
            restrictions.add(new PVector(i*10, applet.height));
        }
        for (int i = 1; i < applet.height/10; i++) {
            restrictions.add(new PVector(0, i*10));
            restrictions.add(new PVector(applet.width, i*10));
        }
        this.snakeBodySize = bodySize;
        this.snakeMaxHealth = snakeMaxHealth;
        this.snakeHealthFromMeal = snakeHealthFromMeal;
        food = new Food[maxFood];
        for (int i = 0; i < food.length; i++) food[i] = new Food(new PVector(applet.random(applet.width-2*padding)+padding, applet.random(applet.height-2*padding)+padding), foodSize);
        networkVisualization = new NetworkVisualization(30);
        toggleShowNN = false;
        running = true;
        toggleShowFramerate = false;
        toggleShowGraph = true;

        graph = new Graph(0, applet.height*0.8f, applet.width*0.1f, applet.width*0.1f, applet.width/10);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                graph.addValue(snakes[bestSnakeId].getFitness());
            }
        }, 1000, 1000);
    }

    /**
     * Function which handles all the tasks with food.
     */
    public void manageFood() {
        if (running) checkSnakeFoodCollision();
        updateFood();
        displayFood();
    }

    /**
     * Function to check if any of the snakes has collided with food, which 
     * results in eating the food.
     */
    private void checkSnakeFoodCollision() {
        for (Snake snake : snakes) {
            PVector snakePosition = snake.body.get(0);
            for (int i = 0; i < food.length; i++) {
                PVector mealPosition = food[i].position;
                boolean canEat = dist(snakePosition.x, snakePosition.y, mealPosition.x, mealPosition.y) < (snake.bodySize + foodSize)*0.5;
                if (canEat) {
                    snake.eatFood();
                    food[i].position = new PVector(applet.random(applet.width-2*padding)+padding, applet.random(applet.height-2*padding)+padding);
                }
            }
        }
    }



    /**
     * Function to update position of all the food. Based on velocity.
     */
    private void updateFood() {
        if (running) for (Food meal : food) meal.update(padding, applet);
    }

    /**
     * Function to display all the food.
     */
    private void displayFood() {
        for (Food meal : food) meal.display(applet);
    }

    /**
     * Function which manages all the snakes - steering, moving, 
     * displaying, checking health and collisions.
     */
    public void manageSnakes() {
        if (running) {
            float bestFitness = 0;

            for (int i = 0; i < snakes.length; i++) {
                float actFitness = snakes[i].getFitness();
                if (actFitness > bestFitness) {
                    bestFitness = actFitness;
                    bestSnakeId = i;
                }
            }
        }

        for (int i = 0; i < snakes.length; i++) {
            Snake snake = snakes[i];
            if (running) {
                snake.steer(food, restrictions);
                snake.move();
                snake.updateHealth();
            }
            snake.display((i == bestSnakeId)&&(toggleShowNN), applet);
        }

        if (running) {
            checkSnakeWallCollision();
            checkSnakeBodyCollision();
            checkSnakeAlive();
        }
    }

    /**
     * Checks collisions between snake and wall. When snake collides into 
     * wall, snake gets replaced.
     */
    private void checkSnakeWallCollision() {
        for (int i = 0; i < snakes.length; i++) {
            PVector head = snakes[i].body.get(0);
            for (PVector restriction : restrictions) {
                float distance = dist(head.x, head.y, restriction.x, restriction.y);
                if (distance < snakes[i].bodySize*0.5) {
                    replaceSnake(i);
                }
            }
        }
    }

    /**
     * Checks collision between snake and its body. When snake collides into 
     * its body, snake gets replaced. 
     */
    private void checkSnakeBodyCollision() {
        for (int i = 0; i < snakes.length; i++) {
            List<PVector> body = snakes[i].body;
            PVector head = body.get(0);
            for (int j = 1; j < body.size(); j++) {
                PVector part = body.get(j);
                float distance = dist(head.x, head.y, part.x, part.y);
                if (distance < snakes[i].bodySize*0.95) {
                    replaceSnake(i);
                }
            }
        }
    }

    /**
     * Checks if snake is alive. If not, it gets replaced.
     */
    private void checkSnakeAlive() {
        for (int i = 0; i < snakes.length; i++) {
            if (!snakes[i].isAlive()) {
                replaceSnake(i);
            }
        }
    }

    /**
     * Function to replace a snake, if it died. Function takes two random 
     * snakes based on their fitness and combines their DNA together. Then 
     * it makes another snake based on that DNA.
     * @param id 
     */
    private void replaceSnake(int id) {
        float maxFitness = 0;
        for (Snake snake : snakes) maxFitness = max(maxFitness, snake.getFitness());
        int snakeAId = getRandomSnakeByFitness(-1);
        int snakeBId = getRandomSnakeByFitness(snakeAId);
        Snake snakeA = snakes[snakeAId];
        Snake snakeB = snakes[snakeBId];
        DNA child = snakeA.dna.crossover(snakeB.dna);
        child.mutate(30f/maxFitness);

        Snake childSnake = new Snake(snakeBodySize, padding, snakeMaxHealth, snakeHealthFromMeal, applet);
        snakes[id] = childSnake;
    }

    /**
     * Randomly selects a snake from snakes array. Random selection is 
     * based on fitness of the snakes. Snake with higher fitness has
     * higher chance of being selected.
     * @param id Index of snake, which can't be picked. Used when picking 
     * two snakes - at first you pick one snake and then the other one, but 
     * you send index of the first one, so it cannot be picked second time.
     * @return Id of randomly selected snake.
     */
    private int getRandomSnakeByFitness(int id) {
        float randomMax = 0;
        for (int i = 0; i < snakes.length; i++) {
            if (i != id) randomMax += snakes[i].getFitness();
        }
        float randomChoose = (float)Math.random()*randomMax;
        for (int i = 0; i < snakes.length; i++) {
            if (i != id) {
                randomChoose -= snakes[i].getFitness();
                if (randomChoose <= 0) return i;
            }
        }
        return -1;
    }

    /**
     * Function which has to be called when a key is pressed. It controls 
     * the information about the simulation and the simulation itself.
     * @param key Which key was pressed.
     */
    public void keyPressed(char key) {
        switch(key) {
            case 'n': toggleShowNN = !toggleShowNN; break;
            case 'f': toggleShowFramerate = !toggleShowFramerate; break;
            case 'g': toggleShowGraph = !toggleShowGraph; break;
            case ' ': running = !running; break;
        }
    }

    /**
     * Function, which displays additional information as graph, help, 
     * and wheter the program is running
     */
    public void displayInfo() {
        if (!running) {
            applet.textAlign(CENTER, CENTER);
            applet.fill(255);
            applet.textSize(30);
            applet.text("Paused", applet.width/2, 30);
        }
        if (toggleShowNN) networkVisualization.display(snakes[bestSnakeId].neuralNetwork.layers, applet);
        if (toggleShowGraph) graph.display(applet);
        if (toggleShowFramerate) {
            applet.textAlign(LEFT, TOP);
            applet.fill(255);
            applet.textSize(12);
            applet.text(applet.frameRate, 0, 0);
        }
        displayHelp();
    }

    /**
     * Function, which displays help in the right bottom corner.
     */
    private void displayHelp() {
        applet.textAlign(LEFT, CENTER);
        applet.fill(255);
        applet.textSize(12);
        applet.pushMatrix();
            applet.translate(applet.width-250, applet.height);
            applet.text("\"SPACE\": Pause the simulation", 0, -15);
            applet.text("\"n\": Show/hide network and best snake", 0, -30);
            applet.text("\"f\": Show/hide framerate", 0, -45);
            applet.text("\"g\": Show/hide graph", 0, -60);
        applet.popMatrix();
    }
}
