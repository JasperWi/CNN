package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JPanel;

import Layers.ConvolutionalNeuralNetwork;

public class DrawLoss extends JPanel {
	ConvolutionalNeuralNetwork cnn;
	int YE; 
	int YS; 
	int XS;
	int XE;
	NumberFormat nf = NumberFormat.getInstance();
	
	DrawLoss(ConvolutionalNeuralNetwork cnn) {
		this.cnn = cnn;
		nf.setMinimumFractionDigits(1);
	}
	
	public void paintComponent(Graphics g) {
		ArrayList<Double> NewLoss = new ArrayList<Double>();
		if (cnn.Loss.size() > 600) {
			int i2 = 0;
			double sum = 0;
			for (int i = 1;i < cnn.Loss.size();i++) {
				sum = sum + cnn.Loss.get(i);
				if (i2 == (int) (cnn.Loss.size()*0.01)) {
					NewLoss.add(sum / i2);
					i2 = 0;
					sum = 0;
				}
				i2++;
			}
		} else {
			NewLoss = cnn.Loss;
		}
		YE = getHeight() - 10;
		YS = 10;
		XS = 40;
		XE = getWidth() - 50;
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(new Color(255,255,255));
		if (NewLoss.size() > 2) g.drawString( cnn.epoch  + "  /  " + (nf.format(NewLoss.get(NewLoss.size()-1))) , XE - 100, 20);
		g.setColor(new Color(100,100,100));
		if (NewLoss.size() > 2) {
			double scale = (YE / Collections.max(NewLoss));
			for(int i = 1;i < NewLoss.size();i++) {
				int X1 = XE / NewLoss.size() * i;
				int X2 = XE / NewLoss.size() * (i+1);
				int Y1 = (int) (YE - NewLoss.get(i-1) * scale);
				int Y2 = (int) (YE - NewLoss.get(i) * scale);
				g.drawLine(X1,Y1,X2,Y2);
			}
		}
	}
	
}