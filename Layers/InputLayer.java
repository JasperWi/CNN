package Layers;

import java.io.Serializable;

public class InputLayer implements Layer, Serializable{
	double[][][] input;
	
	public void setInputLayer(Layer inputLayer) {
	}
	
	public void setInput(double[][][] input) {
		this.input = input;
	}
	
	public double[][][] getOutput() {
		return input;
		
	}


}
