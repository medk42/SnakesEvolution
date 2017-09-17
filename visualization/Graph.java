package visualization;

import processing.core.PApplet;
import static processing.core.PApplet.map;

/**
 * Class which displays a graph, based on values which you send in
 * one by one by a function. It is designed to work as a watch of 
 * improvement of for example genetic algorithm, because it deletes 
 * the old values and replaces them with the new ones
 * 
 * @author Jakub Medek
 */
public class Graph {
    float[] values;
    float x, y;
    float sizeW, sizeH;

    /**
     * Construvtor which just sets the values and makes an array.
     * @param x Up-left corner x coordinate.
     * @param y Up-left corner y coordinate.
     * @param sizeW Width of the graph.
     * @param sizeH Height of the graph.
     * @param valuesCount Number of values to be displayed at once.
     */
    public Graph(float x, float y, float sizeW, float sizeH, int valuesCount) {
        values = new float[valuesCount];
        this.x = x;
        this.y = y;
        this.sizeH = sizeH;
        this.sizeW = sizeW;
    }

    /**
     * Shifts values of the array and than adds an item at back.
     * @param item Value to be added.
     */
    public void addValue(float item) {
        for (int i = 0; i < values.length - 1; i++) values[i] = values[i+1];
        values[values.length-1] = item;
    }

    /**
     * Display the graph in a way, that it fits into specified space.
     * @param applet Applet to display the graph.
     */
    public void display(PApplet applet) {
        applet.noFill();
        applet.stroke(255);
        float maxValue = 0;
        for (int i = 0; i < values.length; i++) if (values[i] > maxValue) maxValue = values[i];
        applet.beginShape();
            for (int i = 0; i < values.length; i++) {
                applet.vertex(x+(float)i/values.length*sizeW, y+map(values[i], 0, (maxValue!=0)?maxValue:1, sizeH, 0));
            }
        applet.endShape();
    }
}