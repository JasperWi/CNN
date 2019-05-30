package Layers;

import java.awt.Point;
import java.io.Serializable;

public class ReluLayer implements Layer, Serializable{
	Layer inputLayer;
	int input_x = -1;
	int input_y;
	int input_z;
	boolean[][][] zeros;
	public double[][][] input;
	
	public void setInputLayer(Layer inputLayer) {
		this.inputLayer = inputLayer;
	}

	@Override
	public double[][][] getOutput() {
		double[][][] input = inputLayer.getOutput();
		this.input = input;
		if (input_x == -1) {
			input_z = input.length;
			input_x = input[0].length;
			input_y = input[0][0].length;
			zeros = new boolean[input.length][input[0].length][input[0][0].length];
		}
		double output[][][] = new double[input.length][input[0].length][input[0][0].length];
		for(int c_channel = 0; c_channel < input.length;c_channel++) {
			for(int x = 0; x < input[0].length;x++) {
				for(int y = 0; y < input[0][0].length;y++) {
					if (input[c_channel][x][y] < 0) {
						output[c_channel][x][y] = 0;
						zeros[c_channel][x][y] = true;
					} else {
						output[c_channel][x][y] = input[c_channel][x][y];
					}
				}
			}
		}
		return output;
	}

}
