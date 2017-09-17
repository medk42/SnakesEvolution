package visualization;

import processing.core.PApplet;
import static processing.core.PApplet.map;
import static processing.core.PConstants.CENTER;

/**
 * Class which handles visualization of neural network.
 * 
 * @author Jakub Medek
 */
public class NetworkVisualization {
    float size;

    /**
     * Constructor which just sets the values.
     * @param size Size of one neuron.
     */
    public NetworkVisualization(float size) {
        this.size = size;
    }

    /**
     * Displays the network based on 2D array layers.
     * @param layers 2D array which contains data for visualization.
     * @param applet Applet to display the NN.
     */
    public void display(float[][] layers, PApplet applet) {
        applet.noStroke();
        applet.textSize(11*size/30);
        applet.textAlign(CENTER, CENTER);
        for (int layer = 0; layer < layers.length; layer++) {
            float x = (layer+1)*(applet.width/(layers.length+1));
            for (int neuron = 0; neuron < layers[layer].length; neuron++) {
                applet.fill(map(layers[layer][neuron], 0, 1, 255, 0), map(layers[layer][neuron], 0, 1, 0, 255), 100, 100);
                float y = (neuron+1)*(applet.height/(layers[layer].length+1));
                applet.ellipse(x, y, size, size);

                applet.fill(255);
                applet.text(layers[layer][neuron], x, y);
            }
        }
    }
}
