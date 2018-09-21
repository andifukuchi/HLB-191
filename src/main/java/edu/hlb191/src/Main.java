package edu.hlb191.src;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.geotoolkit.image.io.plugin.RawTiffImageReader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
	
//Tetracam color mappings
//Red = NIR
//Green = Red
//Blue = Green
	public Main() {
		BufferedImage image;
		int width;
		int height;
		try {
			//load utility for tiff image support type in ImageIO api
			IIORegistry registry = IIORegistry.getDefaultInstance();
			registry.registerServiceProvider(new RawTiffImageReader.Spi());
			
			File input = new File("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif");
			image = ImageIO.read(input);
			showImage(image, "Original");
			width = image.getWidth();
			height = image.getHeight();
			BufferedImage redImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			BufferedImage greenImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			BufferedImage blueImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			BufferedImage grayImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < height; i++) {

				for (int j = 0; j < width; j++) {

					Color c = new Color(image.getRGB(j, i));
					int red = (int) (c.getRed() * 0.299);
					int green = (int) (c.getGreen() * 0.587);
					int blue = (int) (c.getBlue() * 0.114);
					Color gray = new Color(red,green,blue);
					int grayScale = (int) (c.getRed()*0.333 + c.getGreen()*0.333 + c.getBlue()*0.333 );
					Color grayColor = new Color(grayScale,grayScale,grayScale);
					/*Color redColor = new Color(c.getRed(),0,0);
					Color greenColor = new Color(0,c.getGreen(),c.getBlue());
					Color blueColor = new Color(0,0,c.getBlue());*/
					Color redColor = new Color(c.getRed(),c.getRed(),c.getRed());
					Color greenColor = new Color(c.getGreen(),c.getGreen(),c.getGreen());
					Color blueColor = new Color(c.getBlue(),c.getBlue(),c.getBlue());
					redImg.setRGB(j, i, redColor.getRGB());
					greenImg.setRGB(j, i, greenColor.getRGB());
					blueImg.setRGB(j, i, blueColor.getRGB());
					grayImg.setRGB(j, i, grayColor.getRGB());
					//image.setRGB(j, i, gray.getRGB());
				}
			}

			//File ouptut = new File("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\grayscale.tif");
			//ImageIO.write(image, "tif", ouptut);
			showImage(redImg, "Red Channel");
			showImage(greenImg, "Green Channel");
			showImage(blueImg, "Blue Channel");
			writeImage(redImg, "Red Channel");
			writeImage(greenImg, "Green Channel");
			writeImage(blueImg, "Blue Channel");
			//showImage(grayImg);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private static void showImage(BufferedImage img, String title) {

	        ImageIcon icon=new ImageIcon(img);
	        JFrame frame=new JFrame();
	        frame.setTitle(title);
	        frame.setLayout(new FlowLayout());
	        frame.setSize(img.getWidth(),img.getHeight());
	        JLabel lbl=new JLabel();
	        lbl.setIcon(icon);
	        frame.add(lbl);
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	
	private static void writeImage(BufferedImage img, String title) throws IOException {
		File output = new File("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\"+title+".tif");
		ImageIO.write(img, "tif", output);
	}
	public static void main(String[] args) {
		Main obj = new Main();
		//threshold();
		//laplacian();
	}
	
	private static void threshold() {
	      try{

	          System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	          Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	          //Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\TTC06130_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	          
	          
	          Mat destination = new Mat(source.rows(),source.cols(),source.type());

	          //destination = source;
	          Imgproc.threshold(source,destination,80,255,Imgproc.THRESH_TOZERO);
	          
	          //Imgcodecs.imwrite("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\threshold.tif", destination);
	         
	          showImage(destination);
	          
	       } catch (Exception e) {
	          System.out.println("error: " + e.getMessage());
	       }
	}
	public static void laplacian() {
		try {
	         int kernelSize = 9;
	         System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	         
	         //Mat source = Highgui.imread("grayscale.jpg",  Highgui.CV_LOAD_IMAGE_GRAYSCALE);
	         Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\grayscale.tif",  Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
	         Mat destination = new Mat(source.rows(),source.cols(),source.type());

	         Mat kernel = new Mat(kernelSize,kernelSize, CvType.CV_32F) {
	            {
	               put(0,0,0);
	               put(0,1,-1);
	               put(0,2,0);

	               put(1,0-1);
	               put(1,1,4);
	               put(1,2,-1);

	               put(2,0,0);
	               put(2,1,-1);
	               put(2,2,0);
	            }
	         };	      
	         
	         Imgproc.filter2D(source, destination, -1, kernel);
	         //Highgui.imwrite("output.jpg", destination);
	         showImage(destination);
	         
	      } catch (Exception e) {
	         System.out.println("Error: " + e.getMessage());
	      }
	}
	
	private static void showImage(Mat img) throws IOException {
        MatOfByte dest2=new MatOfByte();
        Imgcodecs.imencode(".tif", img, dest2);
        byte ba[]=dest2.toArray();
        BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
        showImage(bi,"");
	}

}
