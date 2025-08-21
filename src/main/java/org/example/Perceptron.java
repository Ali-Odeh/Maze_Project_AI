package org.example;
import java.util.*;


public class Perceptron {

    double[] weights;
    double bias;
    double learningRate = 0.1;

    public Perceptron(int inputSize) {
        weights = new double[inputSize];
        bias = 0;
    }

    public void train(double[][] inputs, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < inputs.length; i++) {
                int prediction = predict(inputs[i]);
                int error = labels[i] - prediction;
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * error * inputs[i][j];
                }
                bias += learningRate * error;
            }
        }
    }

    public int predict(double[] input) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * input[i];
        }
        return sum >= 0 ? 1 : 0;
    }

}
