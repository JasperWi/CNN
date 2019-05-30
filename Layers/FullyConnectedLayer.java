package Layers;

import java.io.Serializable;
import java.util.ArrayList;

public class FullyConnectedLayer implements Layer, Serializable{
	int outputUnits;
	int inputUnits;
	public ArrayList<double[][]> weights;
	Layer inputLayer;
	public double[] rearranged_input;
	public double[] output;
	int input_x = -1;
	int input_y;
	int input_z;
	public double[][][] input;
	int[] hiddenUnits;
	public ArrayList<double[]> netInput;
	
	public void setInputLayer(Layer inputLayer) {
		this.inputLayer = inputLayer;
	}
	
	
	
	public FullyConnectedLayer(int inputUnits, int[] hiddenUnits,int outputUnits) {
		this.outputUnits = outputUnits;
		this.inputUnits = inputUnits;
		this.hiddenUnits = hiddenUnits;
		weights = new ArrayList<double[][]>();
		netInput = new ArrayList<double[]>();
		initializeWeights();
	}
	
	
	private void initializeWeights() {
		if (hiddenUnits != null) {
			for(int h=0;h<hiddenUnits.length+1;h++) {
				if (h == 0) {
					weights.add(new double[hiddenUnits[h]][inputUnits]);
					for(int o=0;o<hiddenUnits[h];o++) {
						for(int i = 0;i < inputUnits;i++) {
							weights.get(h)[o][i] = Math.random() * 2 - 1;
						}
					}
				} else if(h == hiddenUnits.length) {
					weights.add(new double[outputUnits][hiddenUnits[h-1]]);
					for(int o=0;o<outputUnits;o++) {
						for(int i = 0;i < hiddenUnits[h-1];i++) {
							weights.get(h)[o][i] = Math.random() * 2 - 1;
						}
					}
				} else {
					weights.add(new double[hiddenUnits[h]][hiddenUnits[h-1]]);
					for(int o=0;o<hiddenUnits[h];o++) {
						for(int i = 0;i < hiddenUnits[h-1];i++) {
							weights.get(h)[o][i] = Math.random() * 2 - 1;
						}
					}
				}
			}
			for(int h=0;h<hiddenUnits.length;h++) { 
				netInput.add(new double[hiddenUnits[h]]);
			}
		} else {
			weights.add(new double[outputUnits][inputUnits]);
			for(int o=0;o<outputUnits;o++) {
				for(int i = 0;i < inputUnits;i++) {
					weights.get(0)[o][i] = Math.random() * 2 - 1;
				}
			}
		}
	}

	@Override
	public double[][][] getOutput() {
		double[][][] input = inputLayer.getOutput();
		this.input = input;
		if (input.length * input[0][0].length * input[0].length != inputUnits) {
			System.out.println("FullyConnectedLayer expects for an input: " + (input.length * input[0][0].length * input[0].length) + "  not: " + inputUnits);
			System.exit(0);
		}
		if (input_x == -1) {
			input_z = input.length;
			input_x = input[0].length;
			input_y = input[0][0].length;
		}
		
		
		rearranged_input = new double[input.length * input[0][0].length * input[0].length];
		int n = 0;
		for(int c_channel = 0; c_channel < input.length;c_channel++) {
			for(int x = 0; x < input[0].length;x++) {
				for(int y = 0; y < input[0][0].length;y++) {
					rearranged_input[n] = input[c_channel][x][y];
					n++;
				}
			}
		}
		
		double[] output;
		if (hiddenUnits == null) {
			output = new double[outputUnits];
			for(int o=0;o<outputUnits;o++) {
				double value = 0;
				for(int i = 0;i < inputUnits;i++) {
					value = value + rearranged_input[i] * weights.get(0)[o][i];
				}
				output[o] = sigmoid(value);
			}
		} else {
			output = rearranged_input;
			for (int l = 0;l < hiddenUnits.length+1;l++) {
				double[] nextOutput;
				if (l == hiddenUnits.length) {
					nextOutput = new double[outputUnits];
				} else {
					nextOutput = new double[hiddenUnits[l]];
				}
				
				for(int o=0;o<nextOutput.length;o++) {
					double value = 0;
					for(int i = 0;i < output.length;i++) {
						value = value + output[i] * weights.get(l)[o][i];
					}
					nextOutput[o] = sigmoid(value);
					if(l != hiddenUnits.length) netInput.get(l)[o] = value;
				}
				output = nextOutput;
			}
		}
		
		
		double[][][] rearranged_output = new double[1][1][outputUnits];
		rearranged_output[0][0] = output;
		this.output = output;
		return rearranged_output;
	}


	private double sigmoid(double value) {
		return 1 / (1 + Math.exp(-value));
	}

}
