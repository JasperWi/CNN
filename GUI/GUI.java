package GUI;

import java.awt.EventQueue;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;

import net.miginfocom.swing.MigLayout;
import javax.swing.JPanel;
import javax.swing.Timer;

import DB.FaceRecognition;
import Layers.ConvolutionalLayer;
import Layers.ConvolutionalNeuralNetwork;
import Layers.InputLayer;
import Layers.Layer;
import Layers.MaxPoolingLayer;
import Layers.ReluLayer;

public class GUI {

	private JFrame frame;
	ConvolutionalNeuralNetwork cnn;
	JLabel label = new JLabel("");
	static NumberFormat nf = NumberFormat.getInstance();
	Boolean train = false;
	int SaveCustomImagPro = -1;
	boolean loadCNN;
	
	private void createNewCNN() {
		cnn = new ConvolutionalNeuralNetwork();
		ConvolutionalNeuralNetwork.LEARNINGRATE = 0.005;
		cnn.Layers.add(new ConvolutionalLayer(4,1));
		cnn.Layers.add(new MaxPoolingLayer(4, 4));
		cnn.Layers.add(new ReluLayer());
		cnn.Layers.add(new ConvolutionalLayer(2,2));
		int[] hiddenUnits = {30};
		cnn.connectLayers(53400, hiddenUnits);
		cnn.train(1);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI(true);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public GUI(boolean loadCNN) {
		this.loadCNN = loadCNN;
		initialize();
	    final Timer timer = new Timer(50, new ActionListener() {
	        @Override
	        public void actionPerformed(final ActionEvent e) {
	            frame.repaint();
	            updateInfoBar();
	        }
	    });
	    timer.start();
	}
	
	private void createdCNN() {
		if (loadCNN) {
			try {
				FileInputStream fileIn = new FileInputStream("./cnn.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				cnn = (ConvolutionalNeuralNetwork) in.readObject();
				cnn.DB = new FaceRecognition();
				cnn.train(1);
				in.close();
				fileIn.close();
			} catch (IOException i) {
				createNewCNN();
				i.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				createNewCNN();
				c.printStackTrace();
				return;
			} catch (Exception e) {
				createNewCNN();
				e.printStackTrace();
				return;
			}
		} else {
			createNewCNN();
		}
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		createdCNN();
		
		Thread t = new Thread() {
			public void run() {
				do {
					if (train && SaveCustomImagPro == -1) {
						cnn.train(10);
					} else {
						try {
							this.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} while (1==1);
			}
		};
			
		t.start();
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setLayout(new MigLayout("", "[200px:200px:200px,grow][grow]", "[50px:50px:50px,grow][grow][100px:100px:100px,grow]"));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.GRAY);
		frame.getContentPane().add(panel_1, "cell 0 0 2 1,grow");
		panel_1.setLayout(null);
		
		label.setForeground(Color.WHITE);
		label.setBounds(12, 0, 968, 33);
		panel_1.add(label);
		updateInfoBar();
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(panel, "cell 0 1 1 2,grow");
		panel.setLayout(null);
		
		DrawPanel drawPanel = new DrawPanel(cnn);
		drawPanel.setBackground(Color.BLACK);
		frame.getContentPane().add(drawPanel, "cell 1 1,grow");
		
		
		DrawLoss panel_2 = new DrawLoss(cnn);
		panel_2.setBackground(Color.BLACK);
		frame.getContentPane().add(panel_2, "cell 1 2,grow");
		frame.setBounds(100, 100, 1049, 724);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnStopTraining = new JButton("Start Training");
		btnStopTraining.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnStopTraining.getText() == "Stop Training") {
					train = false;
					btnStopTraining.setText("Start Training");
				} else {
					train = true;
					btnStopTraining.setText("Stop Training");
				}
			}
		});
		btnStopTraining.setFocusable(false);
		btnStopTraining.setBorderPainted(false);
		btnStopTraining.setBounds(10, 20, 180, 23);
		panel.add(btnStopTraining);
		
		JButton btnSaveCNN = new JButton("Save CNN");
		btnSaveCNN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnSaveCNN.getText() == "Save CNN") {
					train = false;
					btnSaveCNN.setText("Saving...");
					btnSaveCNN.paintImmediately(0, 0, 180, 23);
					try {
						FileOutputStream fileOut = new FileOutputStream("./cnn.ser");
						ObjectOutputStream out = new ObjectOutputStream(fileOut);
						out.writeObject(cnn);
						out.close();
						fileOut.close();
						btnSaveCNN.setText("Saved");
					} catch (IOException i) {
						i.printStackTrace();
						btnSaveCNN.setText("Error");
					}
					btnSaveCNN.paintImmediately(0, 0, 180, 23);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					btnSaveCNN.setText("Save CNN");
				}
			}
		});
		btnSaveCNN.setFocusable(false);
		btnSaveCNN.setBorderPainted(false);
		btnSaveCNN.setBounds(10, 444, 180, 23);
		panel.add(btnSaveCNN);
		
		JButton btnLoadCNN = new JButton("Open CNN");
		btnLoadCNN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnLoadCNN.getText() == "Open CNN") {
					train = false;
					btnLoadCNN.setText("Opening...");
					btnLoadCNN.paintImmediately(0, 0, 180, 23);
					GUI newwindow = new GUI(true);
					newwindow.frame.setVisible(true);
					frame.dispose();
					btnLoadCNN.paintImmediately(0, 0, 180, 23);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					btnLoadCNN.setText("Open CNN");
				}
			}
		});
		btnLoadCNN.setFocusable(false);
		btnLoadCNN.setBorderPainted(false);
		btnLoadCNN.setBounds(10, 400, 180, 23);
		panel.add(btnLoadCNN);
		
		JButton btnClearCNN = new JButton("Clear CNN");
		btnClearCNN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI window = new GUI(false);
				window.frame.setVisible(true);
				frame.dispose();
			}
		});
		btnClearCNN.setFocusable(false);
		btnClearCNN.setBorderPainted(false);
		btnClearCNN.setBounds(10, 500, 180, 23);
		panel.add(btnClearCNN);
		
		JButton button = new JButton(">|");
		button.setFocusable(false);
		button.setBorderPainted(false);
		button.setSelectedIcon(null);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cnn.DB.nextData();
				cnn.getOutput(cnn.DB.cvx);
			}
		});
		button.setBounds(10, 60, 180, 23);
		panel.add(button);
		
		JButton button2 = new JButton("Get Test Image");
		button2.setFocusable(false);
		button2.setBorderPainted(false);
		button2.setSelectedIcon(null);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread d = new Thread() {
					public void run() {
						try {
							printHeatMap(ImageIO.read(getClass().getClassLoader().getResourceAsStream("GUI/KKKtestImage.jpg")));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				d.start();
			}
		});
		button2.setBounds(10, 100, 180, 23);
		panel.add(button2);
	}
	

	void updateInfoBar() {
		double MSE = 0;
		if(cnn.Loss.size() >= 1000) {
			double sum = 0;
			for(int i=0;i<1000;i++) {
				sum = sum + cnn.Loss.get(cnn.Loss.size() - i -1);
			}
			MSE = sum / 1000;
		}
		String x = "";
		if (SaveCustomImagPro != -1) x = "Save Image: " + SaveCustomImagPro + "%";
		label.setText("Learningrate: " + cnn.LEARNINGRATE +    "          Faces while Learning: "+ (int)(1/((double)(cnn.DB.ratio)*2)*100) +  "%              MSE: " +  nf.format(MSE) + "                 CorrectInLastThousend: " + nf.format(cnn.CorrPro)+"%" + "         " + x);
	}
	
	void printHeatMap(BufferedImage image) {
	    File outputfile = new File("./test_Image.png");
	    try {
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double[] Oimage = new double[image.getWidth()*image.getHeight()*3];
		double[][][] Oimage2 = new double[3][image.getWidth()][image.getHeight()];
		image.getRaster().getPixels(0, 0,  image.getWidth(), image.getHeight(), Oimage);
		for (int y = 0;y < image.getHeight();y++) {
			for (int x = 0;x < image.getWidth();x++) {
				for (int c = 0;c < 3;c++) {
					Oimage2[c][x][y] = Oimage[c+x*3+y*3*image.getWidth()]/255;
				}
			}
		}
		SaveCustomImagPro = 0;
		BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = output.createGraphics();
		int[][] heat = new int[image.getWidth()][image.getHeight()];
		for (int x = 0;x < image.getWidth();x+= 1) {
			for (int y = 0;y < image.getHeight();y+= 1) {
				if (x < image.getWidth()-cnn.inputLayer.getOutput()[0].length && y < image.getHeight()- cnn.inputLayer.getOutput()[0][0].length) {
					double[][][] input = new double[3][cnn.inputLayer.getOutput()[0].length][cnn.inputLayer.getOutput()[0][0].length];
					for (int yy = 0;yy < cnn.inputLayer.getOutput()[0][0].length;yy++) {
						for (int xx = 0;xx < cnn.inputLayer.getOutput()[0].length;xx++) {
							for (int c = 0;c < 3;c++) {
								//if (c == 0) input[0][xx][yy] = 0;
								input[c][xx][yy] /*+*/= Oimage2[c][xx+x][yy+y]/*/3+*/;
							}
						}
					}
					cnn.getOutput(input);
					heat[x+cnn.inputLayer.getOutput()[0].length/2][y+cnn.inputLayer.getOutput()[0][0].length/2] = (int) ((cnn.outputLayer.output[0]-cnn.outputLayer.output[1]-0.5)*200);
					if (heat[x+cnn.inputLayer.getOutput()[0].length/2][y+cnn.inputLayer.getOutput()[0][0].length/2] < 0) heat[x+cnn.inputLayer.getOutput()[0].length/2][y+cnn.inputLayer.getOutput()[0][0].length/2] = 0;
				}
				int background = 20;
				g.setColor(new Color((int)((Oimage2[0][x][y]*background+Oimage2[1][x][y]*background+Oimage2[2][x][y]*background)+heat[x][y]),(int)(Oimage2[0][x][y]*background+Oimage2[1][x][y]*background+Oimage2[2][x][y]*background),(int)(Oimage2[0][x][y]*background+Oimage2[1][x][y]*background+Oimage2[2][x][y]*background)));
				g.fillRect(x, y, 1, 1);
				
			}
			SaveCustomImagPro = x*100/(image.getWidth()-InputLayer.input_x);
		}
	    try {
			ImageIO.write(output, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    SaveCustomImagPro = -1;
	}

}
