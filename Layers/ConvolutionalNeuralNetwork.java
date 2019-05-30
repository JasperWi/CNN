package Layers;

import java.util.ArrayList;
import java.util.Arrays;
import DB.FaceRecognition;

public class ConvolutionalNeuralNetwork implements java.io.Serializable {
	public static final MaxPoolingLayer MAXPOOLINGLAYER = new MaxPoolingLayer(0, 0);
	public static final ConvolutionalLayer CONVOLUTIONALLAYER = new ConvolutionalLayer(0, 0);
	public static final ReluLayer RELULAYER = new ReluLayer();
	public ArrayList<Layer> Layers = new ArrayList<Layer>();
	public InputLayer inputLayer = new InputLayer();
	public FullyConnectedLayer outputLayer;
	public ArrayList<Double> Loss = new ArrayList<Double>();
	public int epoch = 0;
	public boolean isTraining = false;
	public int iteration;
	public int toDoIteration;
	public ArrayList<Boolean> isCorrect = new ArrayList<Boolean>();
	public double CorrPro;
	
	public double[][][] gr = new double[3][89][100];

	public static double LEARNINGRATE;
	public transient FaceRecognition DB;

	public ConvolutionalNeuralNetwork() {
		DB = new FaceRecognition();
	}

	public void connectLayers(int FullyConnectedLayerInputUnits, int[] FullyConnectedLayerHiddenUnits) {
		Layers.get(0).setInputLayer(inputLayer);
		for (int i = 1; i < Layers.size(); i++) {
			Layers.get(i).setInputLayer(Layers.get(i - 1));
		}
		outputLayer = new FullyConnectedLayer(FullyConnectedLayerInputUnits, FullyConnectedLayerHiddenUnits,
				DB.cvy.length);
		outputLayer.setInputLayer(Layers.get(Layers.size() - 1));
	}

	public double[] getOutput(double[][][] input) {
		inputLayer.setInput(input);
		return outputLayer.getOutput()[0][0];
	}

	public void train(int iterations) {
		isTraining = true;
		DB.nextData();
		toDoIteration = iterations;
		if (Loss.size() > 4000000)
			averageLoss();
		if (isCorrect.size() > 400000)
			isCorrect.clear();
		for (iteration = 0; iteration < iterations; iteration++) {
			DB.nextData();
			trainOnInput(DB.cvx, DB.cvy);
			iteration++;
			epoch++;
		}
		isTraining = false;
	}

	void trainOnInput(double[][][] input, double[] expectedOutput) {
		inputLayer.setInput(input);
		double[] output = outputLayer.getOutput()[0][0];
		ArrayList<float[][][]> gradient = new ArrayList<float[][][]>();
		calcPro(output);
		gradient.add(new float[1][1][outputLayer.outputUnits]);
		// Calculate the Gradient
		// Fully Connected Layer
		if (outputLayer.hiddenUnits == null) {
			double loss = 0;
			for (int i = 0; i < output.length; i++) {
				gradient.get(0)[0][0][i] = (float) (sigmoidDerivative(inverseSigmoid(output[i]))
						* (expectedOutput[i] - output[i]));
				loss += Math.pow(expectedOutput[i] - output[i], 2);
			}
			Loss.add(loss);
			gradient.add(new float[outputLayer.input_z][outputLayer.input_x][outputLayer.input_y]);
			int n = 0;
			for (int z = 0; z < outputLayer.input_z; z++) {
				for (int x = 0; x < outputLayer.input_x; x++) {
					for (int y = 0; y < outputLayer.input_y; y++) {
						float value = 0;
						for (int i = 0; i < output.length; i++) {
							value = (float) (value + gradient.get(0)[0][0][i] * outputLayer.weights.get(0)[i][n]);
						}
						gradient.get(1)[z][x][y] = value;
						n++;
					}
				}
			}
		} else {
			float[] outputLayergradient = new float[outputLayer.outputUnits];
			double loss = 0;
			for (int i = 0; i < output.length; i++) {
				outputLayergradient[i] = (float) (sigmoidDerivative(inverseSigmoid(output[i]))
						* (expectedOutput[i] - output[i]));
				loss += Math.pow(expectedOutput[i] - output[i], 2);
			}
			Loss.add(loss);
			for (int l = outputLayer.hiddenUnits.length; l > -1; l--) {
				float[] nextOutputLayergradient;
				;
				if (l == 0) {
					nextOutputLayergradient = new float[outputLayer.rearranged_input.length];
				} else {
					nextOutputLayergradient = new float[outputLayer.hiddenUnits[l - 1]];
				}

				for (int i = 0; i < nextOutputLayergradient.length; i++) {
					float value = 0;
					for (int n = 0; n < outputLayergradient.length; n++) {
						value += outputLayergradient[n] * outputLayer.weights.get(l)[n][i];
						if (l != 0) {
							outputLayer.weights.get(l)[n][i] += LEARNINGRATE * outputLayergradient[n]
									* sigmoid(outputLayer.netInput.get(l - 1)[i]);
						} else {
							outputLayer.weights.get(l)[n][i] += LEARNINGRATE * outputLayergradient[n]
									* outputLayer.rearranged_input[i];
						}
					}
					if (l != 0) {
						nextOutputLayergradient[i] = (float) (sigmoidDerivative(outputLayer.netInput.get(l - 1)[i])
								* value);
					} else {
						nextOutputLayergradient[i] = (float) (sigmoidDerivative(outputLayer.rearranged_input[i])
								* value);
					}

				}
				outputLayergradient = nextOutputLayergradient;
			}
			gradient.add(new float[outputLayer.input_z][outputLayer.input_x][outputLayer.input_y]);
			int n = 0;
			for (int z = 0; z < outputLayer.input_z; z++) {
				for (int x = 0; x < outputLayer.input_x; x++) {
					for (int y = 0; y < outputLayer.input_y; y++) {
						gradient.get(1)[z][x][y] = outputLayergradient[n];
						n++;
					}
				}
			}
		}

		// Rest
		for (int l = Layers.size() - 1; l > -1; l--) {
			if (Layers.get(l).getClass() == MAXPOOLINGLAYER.getClass()) {
				gradient.add(
						getGradientMaxPoolingLayer(gradient.get(Layers.size() - l), (MaxPoolingLayer) Layers.get(l)));
			}
			if (Layers.get(l).getClass() == RELULAYER.getClass()) {
				gradient.add(getGradientReluLayer(gradient.get(Layers.size() - l), (ReluLayer) Layers.get(l)));
			}
			if (Layers.get(l).getClass() == CONVOLUTIONALLAYER.getClass()) {
				gradient.add(getGradientConvolutionalLayer(gradient.get(Layers.size() - l),
						(ConvolutionalLayer) Layers.get(l)));
			}
		}
		if (DB.cvy[0] == 1) {
		for(int c = 0; c < 3;c++) {
			for(int x = 0; x < 89;x++) {
				for(int y = 0; y < 100;y++) {
					gr[c][x][y] += gradient.get(gradient.size()-1)[c][x][y]*10;
				}
			}
		}
		}
		// Update weights
		// Fully Connected Layer
		if (outputLayer.hiddenUnits == null) {
			for (int i = 0; i < outputLayer.output.length; i++) {
				for (int j = 0; j < outputLayer.weights.get(0)[0].length; j++) {
					outputLayer.weights.get(0)[i][j] += LEARNINGRATE * gradient.get(0)[0][0][i]
							* outputLayer.rearranged_input[j];
				}
			}
		}

		// Convolutional Layer
		for (int l = Layers.size() - 1; l > -1; l--) {
			if (Layers.get(l).getClass() == CONVOLUTIONALLAYER.getClass()) {
				ConvolutionalLayer layer = (ConvolutionalLayer) Layers.get(l);
				for (int z = 0; z < layer.features; z++) {
					for (int x = 0; x < layer.feature_size; x++) {
						for (int y = 0; y < layer.feature_size; y++) {
							float value = 0;
							for (int lz = 0; lz < layer.input_z; lz++) {
								for (int lx = 0; lx < layer.input_x; lx++) {
									for (int ly = 0; ly < layer.input_y; ly++) {
										if (!(x + lx - layer.feature_depth < 0 || y + ly - layer.feature_depth < 0
												|| x + lx - layer.feature_depth >= layer.input_x
												|| y + ly - layer.feature_depth >= layer.input_y)) {
											value += layer.input[lz][lx][ly]
													* gradient.get(Layers.size() - l)[lz + z * layer.input.length][x
															+ lx - layer.feature_depth][ly + y - layer.feature_depth];
										}
									}
								}
							}
							layer.feature[z][x][y] += value * LEARNINGRATE;
						}
					}
				}
			}
		}
	}

	float[][][] getGradientConvolutionalLayer(float[][][] gradientL, ConvolutionalLayer layer) {
		float[][][] gradient = new float[layer.input_z][layer.input_x][layer.input_y];
		for (int z = 0; z < layer.input_z; z++) {
			for (int x = 0; x < layer.input_x; x++) {
				for (int y = 0; y < layer.input_y; y++) {
					float value = 0;
					for (int fz = 0; fz < layer.features; fz++) {
						for (int fx = 0; fx < layer.feature_size; fx++) {
							for (int fy = 0; fy < layer.feature_size; fy++) {
								if (x + 1 - fz > 0 && y + 1 - fy > 0 && x + 1 - fx < layer.input_x && y + 1 - fy < layer.input_y) {
									value += gradientL[fz + z * layer.features][x][y] * layer.feature[fz][fx][fy];
								}
							}
						}
					}
					gradient[z][x][y] = value;
				}
			}
		}
		return gradient;
	}

	float[][][] getGradientReluLayer(float[][][] gradientL, ReluLayer layer) {
		float[][][] gradient = new float[layer.input_z][layer.input_x][layer.input_y];
		for (int z = 0; z < layer.input_z; z++) {
			for (int x = 0; x < layer.input_x; x++) {
				for (int y = 0; y < layer.input_y; y++) {
					if (layer.zeros[z][x][y] == false) {
						gradient[z][x][y] = gradientL[z][x][y];
					} else {
						gradient[z][x][y] = 0;
					}
				}
			}
		}
		return gradient;
	}

	float[][][] getGradientMaxPoolingLayer(float[][][] gradientL, MaxPoolingLayer layer) {
		float[][][] gradient = new float[layer.input_z][layer.input_x][layer.input_y];
		int ox = 0;
		int oy = 0;
		for (int z = 0; z < layer.input_z; z++) {
			ox = -1;
			for (int x = 0; x < layer.input_x; x += layer.stride) {
				oy = -1;
				ox++;
				for (int y = 0; y < layer.input_y; y += layer.stride) {
					oy++;
					for (int wx = 0; wx < layer.window_size; wx++) {
						for (int wy = 0; wy < layer.window_size; wy++) {
							if (wx+x == layer.max[z][ox][oy].getX() && wy+y == layer.max[z][ox][oy].getY()) {
								gradient[z][x + wx][y + wy] = gradientL[z][ox][oy];
							}
						}
					}

				}
			}
		}
		return gradient;
	}

	private double sigmoidDerivative(double value) {
		double s = sigmoid(value);
		return s * (1 - s);
	}

	private double sigmoid(double value) {
		return 1 / (1 + Math.exp(-value));
	}

	private double inverseSigmoid(double value) {
		if (value <= 0) {
			return -10;
		} else {
			return Math.log(value / (1 - value));
		}
	}

	private void averageLoss() {
		ArrayList<Double> NewLoss = new ArrayList<Double>();
		int i2 = 0;
		double sum = 0;
		for (int i = 1; i < Loss.size(); i++) {
			sum = sum + Loss.get(i);
			if (i2 >= (int) (Loss.size() / 1000)) {
				NewLoss.add(sum / i2);
				i2 = 0;
				sum = 0;
			}
			i2++;
		}
		Loss.clear();
		Loss = NewLoss;
	}

	private void calcPro(double[] output) {
		boolean corr = false;
		int corri = -1;
		int ei = -1;
		double max = 0;
		for (int i = 0; i < outputLayer.outputUnits; i++) {
			if (output[i] > max && output[i] > 0.5) {
				max = output[i];
				corri = i;
			}
			if (DB.cvy[i] == 1)
				ei = i;
		}
		if (ei == corri)
			corr = true;
		isCorrect.add(corr);
		if (isCorrect.size() > 1000) {
			int sum = 0;
			for (int i = 0; i < 1000; i++) {
				if (isCorrect.get(isCorrect.size() - 1 - i))
					sum++;
			}
			CorrPro = ((double) (sum) / (double) (1000)) * 100;
		}
	}

}
