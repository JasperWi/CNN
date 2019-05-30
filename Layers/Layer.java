package Layers;

public interface Layer{
	int input_x = -1;
	int input_y = 0;
	int input_z = 0;
	
	void setInputLayer(Layer inputLayer);
	
	double[][][] getOutput();

}
