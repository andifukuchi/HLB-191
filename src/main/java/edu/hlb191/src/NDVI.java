package edu.hlb191.src;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotoolkit.image.io.plugin.RawTiffImageReader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import sun.security.provider.PolicyParser.GrantEntry;

public class NDVI {
	
//Tetracam color mappings
//Red = NIR
//Green = Red
//Blue = Green
	public NDVI() {
		BufferedImage image;
		try {
			//load utility for tiff image support type in ImageIO api
			IIORegistry registry = IIORegistry.getDefaultInstance();
			registry.registerServiceProvider(new RawTiffImageReader.Spi());
			
			File input = new File("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif");
			//File input = new File("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC05740_CP.tif");
			//File input = new File("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC05732_CP.tif");
			
			
			image = ImageIO.read(input);
			//showImage(image, "Original");
			
			BufferedImage greyImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
			ImageContainer cont = new ImageContainer(image, greyImg, 0.8, 0.5);
			//applyColorSwap(cont);
			//File ouptut = new File("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\grayscale.tif");
			//ImageIO.write(image, "tif", ouptut);
			//writeImage(blueImg, "Blue Channel");
			applyMethod(cont);
			showImage(cont, "NDVI");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private class ImageContainer{
		double greenThreshold = 0.8;
		double orangeThreshold = 0.5;
		BufferedImage source;
		BufferedImage dest;
		public ImageContainer(BufferedImage s,BufferedImage d, double green,double orange) {
			greenThreshold = green;
			orangeThreshold = orange;
			dest = d;
			source = s;
		}
	}
	
	private static void showImage(ImageContainer cont, String title) {

	        ImageIcon icon=new ImageIcon(cont.dest);
	        JFrame frame=new JFrame();
	        frame.setTitle(title);
	        frame.setLayout(new FlowLayout());
	        frame.setSize(cont.dest.getWidth(),cont.dest.getHeight()+100);
	        final JLabel lbl=new JLabel();
	        lbl.setIcon(icon);
	        final  JLabel greenText=new JLabel();
	        greenText.setText(""+cont.greenThreshold);
	        final  JLabel orangeText=new JLabel();
	        orangeText.setText(""+cont.orangeThreshold);
	        JSlider greenSlider = new JSlider(JSlider.HORIZONTAL,-100, 100, 80);
	        greenSlider.setMajorTickSpacing(25);
	        greenSlider.setMinorTickSpacing(1);
	        greenSlider.setPaintTicks(true);
	        greenSlider.setPaintLabels(true);
	        greenSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					 JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            int fps = (int)source.getValue();
				            greenText.setText("" +(fps/100.0d) );
				            cont.greenThreshold = (fps/100.0d);
				            applyMethod(cont);
				            ImageIcon icon=new ImageIcon(cont.dest);
				            lbl.setIcon(icon);
				        }    
				}
	        	
	        });
	        JSlider orangeSlider = new JSlider(JSlider.HORIZONTAL,-100, 100, 50);
	        orangeSlider.setMajorTickSpacing(25);
	        orangeSlider.setMinorTickSpacing(1);
	        orangeSlider.setPaintTicks(true);
	        orangeSlider.setPaintLabels(true);
	        orangeSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					 JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            int fps = (int)source.getValue();
				            orangeText.setText("" +(fps/100.0d) );
				            cont.orangeThreshold = (fps/100.0d);
				            applyMethod(cont);
				            ImageIcon icon=new ImageIcon(cont.dest);
				            lbl.setIcon(icon);
				        }    
				}
	        	
	        });
	        frame.add(orangeText);
	        frame.add(orangeSlider);
	        frame.add(greenText);
	        frame.add(greenSlider);
	        

	        frame.add(lbl);
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        

	}
	private static void applyMethod(ImageContainer cont) {
		//applyNDVI(cont);
		//applyNDI(cont);
		//applyExR(cont);
		applyColorSwap(cont);
	}
	
	private static void applyNDVI(ImageContainer cont) {
		int width = cont.source.getWidth();
		int height = cont.source.getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color c = new Color(cont.source.getRGB(j, i));
				int col1 = c.getRed(); //nir
				//int col1 = c.getGreen(); //red
				//int col2 = c.getGreen(); //red
				int col2 = c.getBlue(); //green
				//ndvi = (NIR - RED) / (NIR + RED)
				Color grayColor;
				double ndvi = 0d;
				if((col1 + col2)==0) {
					ndvi = -1.1d; //division by zero
				}
				else {
					ndvi = (col1 - col2) / ((double)(col1 + col2));
				}

				if (ndvi == -1.1d) {
					//non-vegetal - blue
					grayColor = new Color(0,0,0);
					//grayColor = new Color(0,0,0);
				}
				else if(ndvi < 0.0) {
					//negative value, probably an artificial material
					//extreme stress - violet/purple
					grayColor = new Color(128,0,255);
					//grayColor = new Color(0,0,0);
				}
				else if (ndvi == 1.0) {
					//non-vegetal - blue
					grayColor = new Color(0,0,255);
					//grayColor = new Color(0,0,0);
				}
				else if(ndvi >= cont.greenThreshold) {
					//healthy vegetation green
					grayColor = new Color(0,255,0);
					//grayColor = new Color(0,0,0);
				}
				else if(ndvi >= cont.orangeThreshold) {
					//bad vegetation orange
					grayColor = new Color(255,128,0);
					
					//grayColor = new Color(0,0,0);
				}
				else {
					//dead vegetation red
					grayColor = new Color(255,0,0);
				}
				cont.dest.setRGB(j, i, grayColor.getRGB());
			}
		}
	}
	private static void applyNDI(ImageContainer cont) {
		int width = cont.source.getWidth();
		int height = cont.source.getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color c = new Color(cont.source.getRGB(j, i));
				int col1 = c.getGreen(); // red
				int col2 = c.getBlue(); // green
				
				int ndi = (int) (128 * (((col2-col1)/(double)(col2+col1)) + 1.0));
				Color grayColor = null;
				if(ndi == 256) {
					ndi = 255;
					grayColor = new Color(0,ndi,0);
				}
				else if(ndi > 255 || ndi < 0) {
					grayColor = new Color(0,0,255);//error blue
				}
				else if((ndi / 255.0) >= cont.greenThreshold) {
					grayColor = new Color(0,255,0);
				}
				else if((ndi / 255.0) >= cont.orangeThreshold) {
					grayColor = new Color(255,128,0);
				}
				else {
					grayColor = new Color(255,0,0);
				}
				cont.dest.setRGB(j, i, grayColor.getRGB());
			}
		}
	}
	
	private static void applyExR(ImageContainer cont) {
		int width = cont.source.getWidth();
		int height = cont.source.getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color c = new Color(cont.source.getRGB(j, i));
				int col1 = c.getGreen(); //red
				int col2 = c.getBlue(); // green
				
				double exr = col1*1.3 - col2*1.0;
				double exri = ((exr + 255.0)/586.5);
				Color grayColor = null;
				if(exri >= cont.greenThreshold) {
					grayColor = new Color(0,255,0);
				}
				else if(exri >= cont.orangeThreshold) {
					grayColor = new Color(255,128,0);
				}
				else {
					grayColor = new Color(255,0,0);
				}
			    //grayColor = new Color(0,(int)exrp,0);
				cont.dest.setRGB(j, i, grayColor.getRGB());
			}
		}
	}
	private static void applyColorSwap(ImageContainer cont) {
		int width = cont.source.getWidth();
		int height = cont.source.getHeight();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color c = new Color(cont.source.getRGB(j, i));
				//swap colors NIR to Blue, and place Red in RED, Green in Green
				Color grayColor = new Color(c.getGreen(),(int)(c.getBlue() * cont.orangeThreshold),(int) (c.getRed()*cont.greenThreshold));
				cont.dest.setRGB(j, i, grayColor.getRGB());
			}
		}
	}
	
	private static void writeImage(BufferedImage img, String title) throws IOException {
		File output = new File("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\"+title+".tif");
		ImageIO.write(img, "tif", output);
	}
	public static void main(String[] args) {
		NDVI obj = new NDVI();

	}
	
	
	
}
