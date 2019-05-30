package Layers;

import java.awt.Point;
import java.io.Serializable;


public class ConvolutionalLayer implements Layer, Serializable{
	public double[][][] feature;
	int features;
	int feature_size;
	int feature_depth;
	Layer inputLayer;
	public int input_x = -1;
	int input_y;
	int input_z;
	public double[][][] input;
	
	public void setInputLayer(Layer inputLayer) {
		this.inputLayer = inputLayer;
	}
	
	public ConvolutionalLayer(int features, int feature_depth) {
		this.features = features;
		this.feature_size = feature_depth * 2 + 1;
		this.feature_depth = feature_depth;
		feature = new double[features][feature_size][feature_size];
		initializeWeights();
	}

	private void initializeWeights() {
		for(int c_feature = 0;c_feature < features;c_feature++) {
			for(int x=0;x<feature_size;x++) {
				for(int y=0;y<feature_size;y++) {
					feature[c_feature][x][y] = Math.random()*2-1;
				}
			}
		}
	}
	

	@Override
	public double[][][] getOutput() {
		input = inputLayer.getOutput();
		if (input_x == -1) {
			input_z = input.length;
			input_x = input[0].length;
			input_y = input[0][0].length;
		}
		double[][][] output = new double[features*input.length][input[0].length][input[0][0].length];
		int c_outputchannel = 0;
		for(int c_inputChannel = 0; c_inputChannel < input.length;c_inputChannel++) {
			for(int c_feature = 0;c_feature < features;c_feature++) {
				for(int x=0; x < input[0].length;x++) {
					for(int y=0; y < input[0][0].length;y++) {
						double value = 0;
						for(int fx=-feature_depth; fx < feature_depth+1;fx++) {
							for(int fy=-feature_depth; fy < feature_depth+1;fy++) {
								if (!(x+fx < 0 || y+fy < 0 ||x+fx >= input[0].length || y+fy >= input[0][0].length)) {
									value = value + feature[c_feature][fx+feature_depth][fy+feature_depth] * input[c_inputChannel][x+fx][y+fy];
								}
							}
						}
						output[c_outputchannel][x][y] = value /* / (feature_size*feature_size) */;
					}
				}
				c_outputchannel++;
			}
		}
		return output;
	}

}
