# Evolution with Genetic Neural Network
## Introduction
This Java program simulates genetic algorithm teaching a neural network to drive a Snake based on the following rules:
* **Snake needs to eat** - if snake doesn't eat for some time, it will starve to death
* **Snake can't crash into wall** - if snake is too close to the wall, it will die
* **Snake can't crash into itself** - if snake touches itself, it will die
* **Snakes can't interact with each other** - they live in a same box and share food, but can't see or affect each other

![](https://i.imgur.com/s0UfZSj.gif "Simulation after few minutes")

## Neural Network Design
Snake's vision is divided into 16 different sectors covering area of 120 degrees in front of the snake. Snake can also *see* how far an object is as demonsrated on the picture below.

There is 49 neurons in the input layer - 48 input neurons and a bias.
* **0**: Bias neuron
* **1-16**: Input neurons for seeing itself
* **17-32**: Input neurons for seeing food
* **33-48**: Input neurons for seeing walls

Then there are two hidden layers with 16 (plus bias) neurons and output layer with two neurons for steering - snake steers based on the difference between the two values.

![](https://i.imgur.com/10eEeCA.png "Neural Network Visualization")

## Controls
* **"ESC"**: to exit
* **"n"**: shows/hides the real-time graphical representation of the neural network behind the leading snake (also shows snake's field of view)
* **"f"**: displays current FPS (simulation is set to run at 60FPS)
* **"g"**: displays basic graph of score of the best snake over time
* **"SPACE"**: pauses/continues the simulation

## Running the project
### Running my build
Easiest way to run the project is to download it and run **"dist/ProcessingTemplate.jar"**. The only other file you need is **"dist/lib/core.jar"** because of the library.
### Building project on your own
Project requires you to compile it with [Processing](https://processing.org/) library (file **core.jar**).

## Resources
* [Original video](https://youtu.be/BBLJFYr7zB8 "Video"): Project is inspired by this video
* [Processing 3](https://processing.org/ "Processing"): Java graphic library
