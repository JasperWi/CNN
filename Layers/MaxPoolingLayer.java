package Layers;

import java.awt.Point;
import java.io.Serializable;

public class MaxPoolingLayer implements Layer, Serializable{
	int window_size;
	int stride;
	Layer inputLayer;
	int input_x = -1;
	int input_y;
	int input_z;
	Point[][][] max;
	public double[][][] input;
	
	public void setInputLayer(Layer inputLayer) {
		this.inputLayer = inputLayer;
	}
	
	public MaxPoolingLayer(int windowsize, int stride) {
		this.window_size = windowsize;
		this.stride = stride;
	}
	
	
	@Override
	public double[][][] getOutput() {
		double[][][] input = inputLayer.getOutput();
		this.input = input;
		int ex = 0;
		int ey = 0;
		if (input[0].length % stride > 0) ex = 1;
		if (input[0][0].length % stride > 0) ey = 1;
		double[][][] output = new double[input.length][(int)(input[0].length / stride)+ ex][(int)(input[0][0].length / stride) + ey];
		if (input_x == -1) {
			input_z = input.length;
			input_x = input[0].length;
			input_y = input[0][0].length;
			max = new Point[output.length][output[0].length][output[0][0].length];
		}
		for(int c_channel=0; c_channel < input.length;c_channel++) {
			int ox = -1;
			for(int x=0; x < input[0].length;x+=stride) {
				int oy = -1;
				ox++;
				for(int y=0; y < input[0][0].length;y+=stride) {
					oy++;
					double max_value = input[c_channel][x][y];
					Point max_point = new Point(0, 0);
					for(int wx = 0;wx < window_size;wx++) {
						for(int wy = 0;wy < window_size;wy++) {
							if (x+wx < input[0].length && y+wy < input[0][0].length) {
								if (input[c_channel][x+wx][y+wy] > max_value) {
									max_value = input[c_channel][x+wx][y+wy];
									max_point = new Point(x+wx,y+wy);
								}
							}
						}
					}
					max[c_channel][ox][oy] = max_point;
					output[c_channel][ox][oy] = max_value;
				}
			}
		}
		return output;
	}
	
}
