package DB;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FaceRecognition{
	
	public double[][][] cvx;
	public double[] cvy;
	public double[][][] nvx;
	public double[] nvy;
	public String[] classes = {"Face","no Face"};
	public int cx;
	int sample = 2;
	int sample2 = 4;
	Thread p = new Thread();
	public double ratio = 1.005;
	int width = 89;
	int height = 100;
	
	public FaceRecognition() {
		p = new Thread() {
			public void run() {
				sample++;
				sample2++;
				if (sample == 11956) sample = 1;
				if (sample2 == 1000) {
					sample2 = 1;
					if (ratio < 4) {
						ratio+= Math.pow(ratio,1.01)-ratio;
					}else {
						ratio= 4;
					}
				}
				double face = Math.random()*ratio;
				
				//Y
				if (face <= 0.5) {
					nvy[0] = 1;
					nvy[1] = 0;
					cx = 0;
				} else {
					nvy[0] = 0;
					nvy[1] = 1;
					cx = 1;
				}
				
				// X
				BufferedImage imgx = null;
				int[] nvx2 = new int[width*height*3];
				if (face <= 0.5) {
					try {
						String x = "";
						if (sample < 100000) {
							x = "0";
						}
						if (sample < 10000) {
							x = "00";
						}
						if (sample < 1000) {
							x = "000";
						}
						if (sample < 100) {
							x = "0000";
						}
						if (sample < 10) {
							x = "00000";
						}
						imgx = ImageIO.read(getClass().getClassLoader().getResourceAsStream("img_align_celeba/K" + x + sample + ".jpg"));
					} catch (IOException e) {
						System.out.println("Can't find DB face Index: " + sample);
					}
					imgx.getRaster().getPixels(0, 0, width, height, nvx2);
				} else if (Math.random() < 0.2) {
					int[] Color = new int[]{(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)};
					int col = 0;
					for (int i = 0; i < nvx2.length;i++) {
						nvx2[i] = Color[col];
						col++;
						if (col == 3) col = 0;
					} 
				} else if (Math.random() < 0.1) {
					int[] Color = new int[]{(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)};
					for (int i = 0; i < nvx2.length;i++) {
						nvx2[i] = Color[(int)(Math.random()*2)];
					} 
				}else if (Math.random() < 0.8) {
					try {
						imgx = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/" + sample2 + ".jpg"));
					} catch (IOException e) {
						System.out.println("Can't find DB Index: " + sample2);
					}
					imgx.getRaster().getPixels((int)(Math.random()*(200-width)), (int)(Math.random()*(200-height)), width, height, nvx2);
				} else {
					try {
						String x = "";
						if (sample < 100000) {
							x = "0";
						}
						if (sample < 10000) {
							x = "00";
						}
						if (sample < 1000) {
							x = "000";
						}
						if (sample < 100) {
							x = "0000";
						}
						if (sample < 10) {
							x = "00000";
						}
						imgx = ImageIO.read(getClass().getClassLoader().getResourceAsStream("img_align_celeba/K" + x + sample + ".jpg"));
					} catch (IOException e) {
						System.out.println("Can't find DB face Index: " + sample);
					}
					int[] nvx3 = new int[width*height*3];
					imgx.getRaster().getPixels(0, 0, width, height, nvx3);
					for (int i = 0; i < nvx3.length;i++) {
						nvx2[nvx3.length-i-1] = nvx3[i];
					}
					for (int y = 0;y < height;y++) {
						for (int x = 0;x < width;x++) {
							for (int c = 0;c < 3;c++) {
								//if (c == 0) nvx[0][x][y] = 0;
								nvx[2-c][x][y] = ((double)nvx2[c+x*3+y*3*width]) /255 /* / 3 */;
							}
						}
					} 
					return;
				}
				for (int y = 0;y < height;y++) {
					for (int x = 0;x < width;x++) {
						for (int c = 0;c < 3;c++) {
							//if (c == 0) nvx[0][x][y] = 0;
							nvx[c][x][y] = ((double)nvx2[c+x*3+y*3*width]) /255 /* / 3 */;
						}
					}
				} 
				
			}
		};
		cvy = new double[2];
		cvx = new double[3][width][height];
		nvy = new double[2];
		nvx = new double[3][width][height];
	}

	
	public void nextData() {
		cvx = nvx;
		cvy = nvy;
	    p.run();
	}
	
}
