package GUI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import Layers.ConvolutionalLayer;
import Layers.ConvolutionalNeuralNetwork;
import Layers.MaxPoolingLayer;
import Layers.ReluLayer;

public class DrawPanel extends JPanel{
	ConvolutionalNeuralNetwork cnn;
	int border = 10;
	
	public DrawPanel(ConvolutionalNeuralNetwork cnn) {
		this.cnn = cnn;
	}
	
	public void paintComponent(Graphics g) {
		
		int LayerWidth = (getWidth() - border * 2) / (cnn.Layers.size()+4); 
		int LayerHeight = getHeight() - border * 2;
		
		
		
		g.setColor(Color.WHITE);
		g.drawString("Input:", border, border);
		drawLayer(border, border + 120, cnn.inputLayer.getOutput(), g, 1);
		
		
		for (int l = 0;l < cnn.Layers.size();l++) {
			g.setColor(Color.WHITE);
			g.drawLine((l+1)*LayerWidth, border, (l+1)*LayerWidth, LayerHeight - border);
			if(cnn.Layers.get(l).getClass() == cnn.MAXPOOLINGLAYER.getClass()) {
				g.drawString("MaxPooling:", border + LayerWidth * (l+1) + LayerWidth/2, border);
				MaxPoolingLayer pl = (MaxPoolingLayer) cnn.Layers.get(l);
				if (l > 0) drawLayer(border + LayerWidth * (l) + LayerWidth/2, border + 10, pl.input, g, 0.5);
			}
			if(cnn.Layers.get(l).getClass() == cnn.RELULAYER.getClass()) {
				g.drawString("ReLU:", border + LayerWidth * (l+1) + LayerWidth/2, border);
				ReluLayer rl = (ReluLayer) cnn.Layers.get(l);
				if (l > 0) drawLayer(border + LayerWidth * (l) + LayerWidth/2, border + 10, rl.input, g, 0.5);
			}
			if(cnn.Layers.get(l).getClass() == cnn.CONVOLUTIONALLAYER.getClass()) {
				ConvolutionalLayer cl = (ConvolutionalLayer) cnn.Layers.get(l);
				g.drawString("Convolution:", border + LayerWidth * (l+1) + LayerWidth/2, border);
				g.drawString("Kernels:", border + LayerWidth * (l+1), border + 20);
				if (l > 0) drawLayer(border + LayerWidth * (l) + LayerWidth/2, border + 10, cl.input, g, 0.5);
				drawLayer(border + LayerWidth * (l+1), border + 30, cl.feature, g, 3);
			}
		}
		g.setColor(Color.WHITE);
		g.drawString("Output:", border + LayerWidth * (cnn.Layers.size()+3), border);
		g.drawString("Hidden Layers:", border + LayerWidth * (cnn.Layers.size()+1)  + LayerWidth/2, border);
		g.drawLine((cnn.Layers.size()+1)*LayerWidth, border, (cnn.Layers.size()+1)*LayerWidth, LayerHeight - border);
		drawLayer(border + LayerWidth * cnn.Layers.size()-1 + LayerWidth/2, border + 10, cnn.outputLayer.input, g, 1);
		
		boolean corr = false;
		int corri = -1;
		int ei = -1;
		double max = 0;
		for(int i = 0; i < cnn.outputLayer.output.length; i++) {
		   if(cnn.outputLayer.output[i] > max && cnn.outputLayer.output[i] > 0.5) {
		      max = cnn.outputLayer.output[i];
		      corri = i;
		   }
		   if(cnn.DB.cvy[i] == 1) ei = i;
		}
		if(ei == corri) {
			corr = true;
		} 
		
		if(cnn.outputLayer.weights.size() != 1) {
			for(int l = 0; l < cnn.outputLayer.weights.size(); l++) {
				int x = border + LayerWidth *(cnn.Layers.size()+2)+10 + (l)*(LayerWidth/cnn.outputLayer.weights.size()*2);
				int x2 = border + LayerWidth * (cnn.Layers.size()+2)+10 + (l-1)*(LayerWidth/cnn.outputLayer.weights.size()*2);
				int layerLength = cnn.outputLayer.weights.get(l).length;
				int hscale = LayerHeight / (cnn.outputLayer.weights.get(l).length+1);
				if (cnn.outputLayer.weights.get(l).length > 50) {
					layerLength = 50;
					hscale = LayerHeight / (55);
					
				}
				for(int y = 0; y < layerLength; y++) {
					int hscale2 = LayerHeight / (cnn.outputLayer.weights.get(l)[0].length+1);
					int layerLength2 = cnn.outputLayer.weights.get(l)[0].length;
					if (cnn.outputLayer.weights.get(l)[0].length > 50) {
						layerLength2 = 50;
						hscale2 = LayerHeight / (55);
						g.setColor(Color.WHITE);
						g.fillOval( x2, hscale2*52+0, 3, 3);
						g.fillOval( x2, hscale2*52+6, 3, 3);
						g.fillOval( x2, hscale2*52+12, 3, 3);
					}
					for(int y2 = 0; y2 < layerLength2; y2++) {
						double weight = cnn.outputLayer.weights.get(l)[y][y2];
						if (weight > 1) {
							g.setColor(new Color(255,255,255));
						} else if(weight < -1) {
							g.setColor(new Color(0,0,0));
						}  else if(weight >= 0) {
							g.setColor(new Color((int)(150+weight*99),(int)(150+weight*99),(int)(150+weight*99)));
						} else {
							g.setColor(new Color((int)(weight*99*-1),(int)(weight*99*-1),(int)(weight*99*-1)));
						}
						g.drawLine(x, y*hscale + border + 20, x2, y2*hscale2 + border + 20);
					}
					
				}
			}
			for(int l = 0; l < cnn.outputLayer.netInput.size(); l++) {
				int x = border + LayerWidth *(cnn.Layers.size()+2)+10 + (l)*(LayerWidth/cnn.outputLayer.weights.size()*2);
				int layerLength = cnn.outputLayer.netInput.get(l).length;
				int hscale = LayerHeight / (cnn.outputLayer.netInput.get(l).length+1);
				if (cnn.outputLayer.netInput.get(l).length > 50) {
					layerLength = 50;
					hscale = LayerHeight / (55);
				}
				for(int y = 0; y < layerLength; y++) {
					g.setColor(Color.WHITE);
					g.fillOval(x-7, y*hscale-7+border+20, 14, 14);
					g.setColor(new Color((int)(sigmoid(cnn.outputLayer.netInput.get(l)[y])*255)));
					g.fillOval(x-6, y*hscale-6+border+20, 12, 12);
				}
					
			}
		}
		
		for (int i = 0;i < cnn.outputLayer.output.length;i++) {
			if (corr) {
				g.setColor(new Color(0, (int)((cnn.outputLayer.output[i]*0.5+0.5)*255), 0));
			} else {
				g.setColor(new Color((int)((cnn.outputLayer.output[i]*0.5+0.5)*255),0 , 0));
			}
			g.fillRect(border + LayerWidth * (cnn.Layers.size()+3) + 9, (i) * LayerHeight/(cnn.outputLayer.output.length+1) + border + 18, 6, 6);
			g.drawString((int)(cnn.outputLayer.output[i]*100) + "% " + cnn.DB.classes[i], border + LayerWidth * (cnn.Layers.size()+3) + 19, i * LayerHeight/(cnn.outputLayer.output.length+1) + border + 18 + 6);
		}
		
		draw3ColLayer(border, border +10, cnn.inputLayer.getOutput(), g, 1);
		draw3ColLayer(border, border +500, cnn.gr, g, 1);
		
	}
	
	void drawLayer(int xx, int yy ,double[][][] cl, Graphics g, double scale) {
		for (int z=0;z < cl.length;z++) {
			for (int x=0;x < cl[0].length;x++) {
				for (int y=0;y < cl[0][0].length;y++) {
					if (cl[z][x][y] >= -1 && cl[z][x][y] <= 1) {
						g.setColor(new Color((int)((cl[z][x][y]+1)*127.5),(int)((cl[z][x][y]+1)*127.5),(int)((cl[z][x][y]+1)*127.5)));
					} else if (cl[z][x][y] < -1) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.WHITE);
					}
					g.fillRect((int)(x*scale + xx),(int)( y*scale + z*(cl[0][0].length*scale+2) + yy),(int)( scale+1),(int)( scale+1));
				}
			}
		}
	}
	
	void drawLayer(int xx, int yy ,float[][][] cl, Graphics g, double scale) {
		for (int z=0;z < cl.length;z++) {
			for (int x=0;x < cl[0].length;x++) {
				for (int y=0;y < cl[0][0].length;y++) {
					if (cl[z][x][y] >= -1 && cl[z][x][y] <= 1) {
						g.setColor(new Color((int)((cl[z][x][y]+1)*127.5),(int)((cl[z][x][y]+1)*127.5),(int)((cl[z][x][y]+1)*127.5)));
					} else if (cl[z][x][y] < -1) {
						g.setColor(Color.BLACK);
					} else {
						g.setColor(Color.WHITE);
					}
					g.fillRect((int)(x*scale + xx),(int)( y*scale + z*(cl[0][0].length*scale+2) + yy),(int)( scale+1),(int)( scale+1));
				}
			}
		}
	}
	
	void draw3ColLayer(int xx, int yy ,double[][][] cl, Graphics g, double scale) {
			for (int x=0;x < cl[0].length;x++) {
				for (int y=0;y < cl[0][0].length;y++) {
					int red;
					int green;
					int blue;
					
					//red
					if (cl[0][x][y] >= -1 && cl[0][x][y] <= 1) {
						red = (int)((cl[0][x][y]+1)*127.5);
					} else if (cl[0][x][y] < -1) {
						red = 0;
					} else {
						red = 255;
					}
					
					//green
					if (cl[1][x][y] >= -1 && cl[1][x][y] <= 1) {
						green = (int)((cl[1][x][y]+1)*127.5);
					} else if (cl[1][x][y] < -1) {
						green = 0;
					} else {
						green = 255;
					}
					
					//blue
					if (cl[2][x][y] >= -1 && cl[2][x][y] <= 1) {
						blue = (int)((cl[2][x][y]+1)*127.5);
					} else if (cl[2][x][y] < -1) {
						blue = 0;
					} else {
						blue = 255;
					}
					g.setColor(new Color(red,green,blue));
					g.fillRect((int)(x*scale + xx),(int)( y*scale+ yy),(int)( scale+1),(int)( scale+1));
				}
			}
	}
	
	private double sigmoid(double value) {
		return 1 / (1 + Math.exp(-value));
	}
}
