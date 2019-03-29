package edu.hlb191.src;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class EqualizeHist {
	
//Tetracam color mappings
//Red = NIR
//Green = Red
//Blue = Green
	
	private static void showImage(BufferedImage img, String title) {

	        ImageIcon icon=new ImageIcon(img);
	        JFrame frame=new JFrame();
	        frame.setTitle(title);
	        frame.setLayout(new FlowLayout());
	        frame.setSize(img.getWidth(),img.getHeight()+100);
	        final  JLabel text=new JLabel();
	        JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL,-100, 100, 0);
	        framesPerSecond.setMajorTickSpacing(25);
	        framesPerSecond.setMinorTickSpacing(1);
	        framesPerSecond.setPaintTicks(true);
	        framesPerSecond.setPaintLabels(true);
	        framesPerSecond.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					 JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            int fps = (int)source.getValue();
				            text.setText("" + (fps/100.0d));
				        }    
					
				}
	        	
	        });
	        frame.add(text);
	        frame.add(framesPerSecond);
	        
	        JLabel lbl=new JLabel();
	        lbl.setIcon(icon);
	        frame.add(lbl);
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        

	}
	private final class SliderListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            int fps = (int)source.getValue();
	            
	        }    
	    }
	}
	
	private final class MyPanel extends JPanel implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static void main(String[] args) {
		
		equalizeHistForImagesInFolder();
		
	}
	

	
	private static void showImage(Mat img) {
        MatOfByte dest2=new MatOfByte();
        Imgcodecs.imencode(".tif", img, dest2);
        byte ba[]=dest2.toArray();
        BufferedImage bi;
		try {
			bi = ImageIO.read(new ByteArrayInputStream(ba));
			showImage(bi,"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	private static void equalizeHist(String inputFileName, String outputFileName) {
		try {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			//Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			//Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\img\\output1\\20\\TTC06200_CP.TIF_3.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			Mat source = Imgcodecs.imread(inputFileName,	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			
			Mat hist_1 = new Mat();
			
			//Imgproc.cvtColor(source, source, Imgproc.COLOR_RGB2YCrCb);
			
			List<Mat> rgb = new ArrayList<Mat>();
			Core.split(source, rgb);
			Imgproc.equalizeHist(rgb.get(0),rgb.get(0));
			Imgproc.equalizeHist(rgb.get(1),rgb.get(1));
			Imgproc.equalizeHist(rgb.get(2),rgb.get(2));
			//CLAHE clahe = Imgproc.createCLAHE();
			//clahe.apply(rgb.get(0),rgb.get(0));
			//clahe.apply(rgb.get(1),rgb.get(1));
			//clahe.apply(rgb.get(2),rgb.get(2));
			
			Core.merge(rgb, hist_1);
			//Imgproc.cvtColor(hist_1, hist_1, Imgproc.COLOR_YCrCb2RGB);
			//Imgcodecs.imwrite("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP1.tif", hist_1);
			Imgcodecs.imwrite(outputFileName, hist_1);
			//showImage(hist_1);
			
		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}

	}
	
	private static void extractTreePixels(String inputFileName, String outputFileName) {
		try {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat source = Imgcodecs.imread(inputFileName,	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			//showImage(source);
			//Equalize Histogram by channel
			//this helps a lot to segregate the soil/ground from the tree leafs
			Mat hist_1 = new Mat();			
			List<Mat> rgb = new ArrayList<Mat>();
			Core.split(source, rgb);
			Imgproc.equalizeHist(rgb.get(0),rgb.get(0));
			Imgproc.equalizeHist(rgb.get(1),rgb.get(1));
			Imgproc.equalizeHist(rgb.get(2),rgb.get(2));
			Core.merge(rgb, hist_1);
			//showImage(hist_1);
			
			//Apply binary NDVI Green to distinguish soil/ground from tree/live vegetation
			//this generates a mask image to allow copying only the Region Of Interest (ROI) from the image
			// if NDVI values are extreme high or low then it is not vegetation then pixel is black
			// otherwise pixel is white. 
			Mat ndvi = applyBinaryNDVI(hist_1);
			//showImage(ndvi);
			//then since ndvi mask image doesn't look smooth (more like lots of noise) 
			//apply median filter to the mask to try eliminating the dots and make a better mask.
			// using a window of 9 for better smoothing
			Imgproc.medianBlur(ndvi, ndvi, 9);
			//now from mask ndvi area find max width and height of tree and also center of tree in image
			int[] arr = getEstimatedTreePosAndSize(ndvi);
			//create a new ellipse mask to copy only pixels inside the tree ellipse 
			//Mat cropMask = new Mat(source.size(),source.type());
			Mat cropMask =  Mat.zeros(source.size(),source.type());
			//draw the ellipse in the mask
			Imgproc.ellipse(cropMask, new Point(arr[0],arr[1]), new Size(arr[2]/2,arr[3]/2), 0, 0, 360, new Scalar(255, 255, 255),-1);
			
			Mat result = new Mat();
			//now copy from the original image only the ROI pixels
			source.copyTo(result,cropMask);
			//crop image into only the ellipse rectangle area size
			int x =  arr[0] - arr[2]/2;
			int y = arr[1] - arr[3]/2;
			Mat croppedImg = new Mat(result, new Rect(x,y,arr[2],arr[3]));
			
			//then we have a resulting image with only pixels from tree.
			//showImage(result);
			
			
			Imgcodecs.imwrite(outputFileName, croppedImg);
		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}
	}
	
	private static Mat applyBinaryNDVI(Mat source) {
		Mat dest = new Mat(source.size(),source.type());
		for (int i = 0; i < source.cols(); i++) {
			for (int j = 0; j < source.rows() ; j++) {
				//Color c = new Color(source.get(j, i));
				double col1 = source.get(j, i)[0]; //nir
				//int col1 = c.getGreen(); //red
				//int col1 = c.getBlue(); //green
				//int col2 = c.getGreen(); //red
				double col2 = source.get(j, i)[2]; //green
				//ndvi = (NIR - RED) / (NIR + RED)
				double grayColor[];
				double ndvi = 0d;
				if((col1 + col2)==0) {
					ndvi = -1.1d; //division by zero
				}
				else {
					ndvi = (col1 - col2) / ((double)(col1 + col2));
				}

				if (ndvi == -1.1d) {
					//non-vegetal - blue
					grayColor =  new double[]  {255, 255, 255};
					//grayColor = new Color(0,0,0);
				}
				else if(ndvi < 0.0) {
					//negative value, probably an artificial material
					//extreme stress - violet/purple
					grayColor =  new double[]  {255, 255, 255};
					//grayColor = new Color(0,0,0);
				}
				else if (ndvi == 1.0) {
					//non-vegetal - blue
					grayColor =  new double[]  {255, 255, 255};
					//grayColor = new Color(0,0,0);
				}
				else {
					//dead vegetation red
					grayColor =  new double[] {0, 0, 0};
				}
				dest.put(j, i, grayColor);
			}
		}
		return dest;
	}
	
	private static int[] getEstimatedTreePosAndSize(Mat source) {
		//size	
		int width = 0;
		int height = 0;
		//position
		int xCenter = 0;
		int yCenter = 0;
		//calc max width and y center
		for (int i = 0; i < source.rows(); i++) {
			int currentWidth = 0;
			for (int j = 0; j < source.cols(); j++) {
				if(source.get(i, j)[0]> 0 ) {
					currentWidth++;
				}
			}
			if(currentWidth > width) {
				width = currentWidth;
				// y center position will be in max width
				yCenter = i;
			}
		}
		for (int i = 0; i < source.cols(); i++) {
			int currentHeight = 0;
			for (int j = 0; j < source.rows(); j++) {
				if(source.get(j, i)[0]> 0 ) {
					currentHeight++;
				}
			}
			if(currentHeight > height) {
				height = currentHeight;
				// x center position will be in max height
				xCenter = i;
			}
		}
		//check that ellipse is all inside the image
		if(xCenter - width/2 < 0 || xCenter + width/2 > source.cols()) {
			//new center is corrected to fit inside image bounds
			xCenter = width/2;
		}
		if(yCenter - height/2 < 0 || yCenter + height/2 > source.rows()) {
			//new center is corrected to fit inside image bounds
			yCenter = height/2;
		}
		
		
		return new int[] {xCenter,yCenter,width,height};
}
	
	private static void equalizeHistForImagesInFolder() {
		String sourcePath = "./src/main/java/edu/hlb191/src/equalizeHist/source/" ;
		final File output = new File("./src/main/java/edu/hlb191/src/equalizeHist/output/" );
		// create dirs if doesn't exists;
		if (!output.exists()) {
			output.mkdirs();
		}


		FileVisitor<Path> fileProcessor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// System.out.println("Processing file:" + file.toFile().getAbsolutePath());

				//equalizeHist(file.toFile().getAbsolutePath(), file.toFile().getAbsolutePath().replace("source", "output"));
				extractTreePixels(file.toFile().getAbsolutePath(), file.toFile().getAbsolutePath().replace("source", "output"));
				return FileVisitResult.CONTINUE;
			}

		};
		try {
			Files.walkFileTree(Paths.get(sourcePath), fileProcessor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
