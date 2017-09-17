package nn;

/**
 * Class, which makes a neural network. It can calculate its output based 
 * on weights and input. Weights are generated randomly.
 * @author Jakub Medek
 */
public class NeuralNetwork {
    public float[][] layers;
    public float[][][] weights;

    /**
     * Approximate value for mathematical constant e.
     */
    public final float E = 2.71828182845905f;

    /**
     * Makes a neural network based on required number of layers and 
     * number of neurons in layer. It takes care of bias automaticly 
     * (provide the neuron count without bias).
     * @param layersCount An array to specify the NN. Length of the array 
     * means number of layers and each number means number of neurons in 
     * that layer (plus bias, constructor adds it automatically).
     */
    public NeuralNetwork(int[] layersCount) {
        layers = new float[layersCount.length][];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new float[layersCount[i] + ((i+1 == layers.length)?0:1)];
            layers[i][0] = 1;
        }

        weights = new float[layersCount.length-1][][];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new float[layers[i].length][layers[i+1].length];
        }
        for (float[][] matrix : weights) {
            for (float[] vector : matrix) {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] = (float)Math.random()*2-1;
                }
            }
        }
    }

    /**
     * Makes a neural network based on required number of layers, number 
     * of neurons in layer and 3D array of weights. It takes care of bias 
     * automaticly (provide the neuron count without bias).
     * @param layersCount An array to specify the NN. Length of the array 
     * means number of layers and each number means number of neurons in 
     * that layer (plus bias, constructor adds it automatically).
     * @param weights 3D array of weights, which is assigned to objects
     * array of weights instead of calculating them at random. Functions 
     * do not write in the array, so you can provide just a pointer.
     */
    public NeuralNetwork(int[] layersCount, float[][][] weights) {
        layers = new float[layersCount.length][];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new float[layersCount[i] + ((i+1 == layers.length)?0:1)];
            layers[i][0] = 1;
        }

        this.weights = weights;
    }

    /**
     * Makes a neural network based on required number of layers, number 
     * of neurons in layer and DNA object. It takes care of bias 
     * automaticly (provide the neuron count without bias).
     * @param layersCount An array to specify the NN. Length of the array 
     * means number of layers and each number means number of neurons in 
     * that layer (plus bias, constructor adds it automatically).
     * @param dna DNA object, which holds genes in array of bytes. Genes 
     * are used to create weights.
     */
    public NeuralNetwork(int[] layersCount, DNA dna) {
        layers = new float[layersCount.length][];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new float[layersCount[i] + ((i+1 == layers.length)?0:1)];
            layers[i][0] = 1;
        }

        weights = new float[layersCount.length-1][][];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new float[layers[i].length][layersCount[i+1]];
        }

        int index = 0;
        for (float[][] matrix : weights) {
            for (float[] vector : matrix) {
                for (int i = 0; i < vector.length; i++) {
                    vector[i] = (float)dna.genes[index++]/Byte.MAX_VALUE;
                }
            }
        }
    }

    /**
     * Calculates network output based on input (given as first layer). 
     * Uses matrixXvector multiplication to go through the layers.
     * @param input Input data to the network.
     * @return Output of the network, i.e. last layer of neurons. Be 
     * careful not to edit this array as it is only pointer, not copy.
     */
    public float[] feedForward(float[] input) {
        if (input.length != layers[0].length-1) return null;
        System.arraycopy(input, 0, layers[0], 1, input.length);
        for (int i = 0; i < weights.length; i++) {
            float[] nextLayer = multiplyMV(weights[i], layers[i]);
            for (int j = 0; j < nextLayer.length; j++) nextLayer[j] = activate(nextLayer[j]);
            if (i + 1 != weights.length) System.arraycopy(nextLayer, 0, layers[i+1], 1, nextLayer.length);
            else layers[i+1] = nextLayer;
        }

        return layers[layers.length - 1];
    }

    /**
     * Multiplyes matrix with vector. Matrix has dimensions NxM and vector N.
     * @param matrix Matrix to multiply.
     * @param vector Vector to multiply.
     * @return Result of the multiplication.
     */
    private float[] multiplyMV(float[][] matrix, float[] vector) {
        float[] result = new float[matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result[j] += matrix[i][j] * vector[i];
            }
        }
        return result;
    }

    /**
     * An activation function for neural network. Activation function 
     * is g(x) = 1/(1+e^(-x)).
     * @param value Value to be activated.
     * @return Result, after the activation.
     */
    private float activate(float value) {
        return (float)(1/(1+Math.pow(E, -value))); 
    }
}
