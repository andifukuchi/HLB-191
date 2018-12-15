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
import org.opencv.core.Size;
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
			
			File input = new File("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif");
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
					//int grayScale = (int) (c.getRed()*0.333 + c.getGreen()*0.333 + c.getBlue()*0.333 );
					//ndvi = (NIR - RED) / (NIR + RED)
					double ndvi = 0.0d;
					if((c.getRed() + c.getGreen())==0) {
						ndvi = 1.0d;
					}
					else {
						ndvi = (c.getRed() - c.getGreen()) / ((double)(c.getRed() + c.getGreen()));
						if(ndvi < 0.0d) {
							ndvi = 0.0d;
						}
					}
					Color grayColor;
					if (ndvi == 1.0) {
						//non-vegetal - blue
						//grayColor = new Color(0,0,255);
						grayColor = new Color(0,0,0);
					}
					else if(ndvi == 0.0) {
						//extreme stress - violet/purple
						//grayColor = new Color(128,0,255);
						grayColor = new Color(0,0,0);
					}
					else if(ndvi > 0.65) {
						//healthy vegetation green
						grayColor = new Color(0,255,0);
						//grayColor = new Color(0,0,0);
					}
					else if(ndvi > 0.5) {
						//bad vegetation orange
						//grayColor = new Color(255,128,0);
						
						grayColor = new Color(0,0,0);
					}
					else {
						//dead vegetation red
						grayColor = new Color(255,0,0);
					}
					//int ndviColor = (int)(ndvi*255);
					//Color grayColor = new Color(ndviColor,ndviColor,ndviColor);
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
			//showImage(redImg, "Red Channel");
			//showImage(greenImg, "Green Channel");
			//showImage(blueImg, "Blue Channel");
			//writeImage(redImg, "Red Channel");
			//writeImage(greenImg, "Green Channel");
			//writeImage(blueImg, "Blue Channel");
			showImage(grayImg, "NDVI");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
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

	
	private static void writeImage(BufferedImage img, String title) throws IOException {
		File output = new File("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\"+title+".tif");
		ImageIO.write(img, "tif", output);
	}
	public static void main(String[] args) {
		//Main obj = new Main();
		sobel();
		laplacian1();
		canny();
		cannyGray();
		//threshold();
		//laplacian();
		//hist2();
	}
	
	private static void threshold() {
	      try{

	          System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	          Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	          //Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\TTC06130_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	          //Mat source = Imgcodecs.imread("TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	          
	          Mat destination = new Mat(source.rows(),source.cols(),source.type());

	          //destination = source;
	          Imgproc.threshold(source,destination,80,255,Imgproc.THRESH_TOZERO);
	          
	          //Imgcodecs.imwrite("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\threshold.tif", destination);
	          Imgcodecs.imwrite("D:\\edu\\investigacion-hlb\\workspace\\src\\src\\main\\java\\edu\\hlb191\\src\\threshold.tif", destination);
	          showImage(destination);
	          
	       } catch (Exception e) {
	          System.out.println("error: " + e.getMessage());
	       }
	}
	
	private static void hist() {
		try {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat source = Imgcodecs.imread(
					"D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",
					Imgcodecs.CV_LOAD_IMAGE_COLOR);

			Mat hist_1 = new Mat();

			MatOfFloat ranges = new MatOfFloat(0f, 256f);
			MatOfInt histSize = new MatOfInt(25);
			Imgproc.calcHist(Arrays.asList(source), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
			
			showImage(hist_1);

		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}

	}
	
	private static void hist2() {
		System.out.println("Hello, OpenCV");

		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread(
				"D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",
				Imgcodecs.CV_LOAD_IMAGE_COLOR);
		//Mat src = new Mat(image.height(), image.width(), CvType.CV_8UC2);
		//Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

		List<Mat> hsvPlanes = new ArrayList<Mat>();
		Core.split(image, hsvPlanes);

		MatOfInt histSize = new MatOfInt(256);

		final MatOfFloat histRange = new MatOfFloat(0f, 256f);

		boolean accumulate = false;

		Map<String, Mat> hists = new HashMap<String, Mat>();
		hists.put("h", new Mat());
		hists.put("s", new Mat());
		hists.put("v", new Mat());
		int hist_w = 512;
		int hist_h = 600;
		long bin_w = Math.round((double) hist_w / 256);
		Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC1);

		int index = 0;
		List<Scalar> scalars = new ArrayList<Scalar>();
		scalars.add(new Scalar(255,   0,   0));
		scalars.add(new Scalar(0,   255,   0));
		scalars.add(new Scalar(0,     0, 255));
		for (Mat hist: hists.values()) {
			Imgproc.calcHist(hsvPlanes, new MatOfInt(0), new Mat(), hist, histSize, histRange, accumulate);
			Core.normalize(hist, hist, 3, histImage.rows(), Core.NORM_MINMAX);
			for (int i = 1; i < 256; i++) {
				Point p1 = new Point(bin_w * (i - 1), hist_h - Math.round(hist.get(i - 1, 0)[0]));
				Point p2 = new Point(bin_w * (i), hist_h - Math.round(hist.get(i, 0)[0]));
				Imgproc.line(histImage, p1, p2, scalars.get(index), 2, 8, 0);
			}
			System.out.print(index);
			index++;
		}
		
		showImage(histImage);
		
		//Highgui.imwrite("histogram3.jpg", histImage);
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

	
	/**
	 * Apply Sobel
	 *
	 * @param frame the current frame
	 * @return an image elaborated with Sobel derivation
	 */
	private Mat doSobel(Mat frame) {
	    // init
	    Mat grayImage = new Mat();
	    Mat detectedEdges = new Mat();
	    int scale = 1;
	    int delta = 0;
	    int ddepth = CvType.CV_16S;
	    Mat grad_x = new Mat();
	    Mat grad_y = new Mat();
	    Mat abs_grad_x = new Mat();
	    Mat abs_grad_y = new Mat();

	    // reduce noise with a 3x3 kernel
	    Imgproc.GaussianBlur(frame, frame, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);

	    // convert to grayscale
	    Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);

	    // Gradient X
	    // Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0, 3, scale,
	    // this.threshold.getValue(), Core.BORDER_DEFAULT );
	    Imgproc.Sobel(grayImage, grad_x, ddepth, 1, 0);
	    Core.convertScaleAbs(grad_x, abs_grad_x);

	    // Gradient Y
	    // Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1, 3, scale,
	    // this.threshold.getValue(), Core.BORDER_DEFAULT );
	    Imgproc.Sobel(grayImage, grad_y, ddepth, 0, 1);
	    Core.convertScaleAbs(grad_y, abs_grad_y);

	    // Total Gradient (approximate)
	    Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, detectedEdges);
	    // Core.addWeighted(grad_x, 0.5, grad_y, 0.5, 0, detectedEdges);

	    return detectedEdges;

	}
	
	
	static void sobel()  {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		Mat originalMat = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
       
		
	    Mat grayMat = new Mat();
	    Mat sobel = new Mat(); //Mat to store the final result

	    //Matrices to store gradient and absolute gradient respectively
	    Mat grad_x = new Mat();
	    Mat abs_grad_x = new Mat();

	    Mat grad_y = new Mat();
	    Mat abs_grad_y = new Mat();

	    //Converting the image to grayscale
	    Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

	    //Calculating gradient in horizontal direction
	    Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);

	    //Calculating gradient in vertical direction
	    Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);

	    //Calculating absolute value of gradients in both the direction
	    Core.convertScaleAbs(grad_x, abs_grad_x);
	    Core.convertScaleAbs(grad_y, abs_grad_y);

	    //Calculating the resultant gradient
	    Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

	    //Converting Mat back to Bitmap
	   // Utils.matToBitmap(sobel, currentBitmap);
	    //imageView.setImageBitmap(currentBitmap);
	    showImage(sobel);
	}
	 
	static void laplacian1()  {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		Mat originalMat = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
       
	    Mat grayMat = new Mat();
	    Mat dst = new Mat();
        int kernel_size = 3;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;

	    //Converting the image to grayscale
	    Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

	    Mat abs_dst = new Mat();
        Imgproc.Laplacian( grayMat, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT );
        // converting back to CV_8U
        Core.convertScaleAbs( dst, abs_dst );
	    //Converting Mat back to Bitmap
	   // Utils.matToBitmap(sobel, currentBitmap);
	    //imageView.setImageBitmap(currentBitmap);
	    showImage(abs_dst);
	}
	
	
	static void canny() {
		 final int MAX_LOW_THRESHOLD = 100;
	     final int RATIO = 3;
	     final int KERNEL_SIZE = 3;
	     final Size BLUR_SIZE = new Size(3,3);
	     int lowThresh = 40;
	     System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	     Mat src = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);

	     Mat srcBlur = new Mat();
	     Mat detectedEdges = new Mat();
	     Mat dst = new Mat();
		 Imgproc.blur(src, srcBlur, BLUR_SIZE);
         Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
         dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
         src.copyTo(dst, detectedEdges);
         showImage(dst);
       
	}
	
	static void cannyGray() {
		 final int MAX_LOW_THRESHOLD = 100;
	     final int RATIO = 3;
	     final int KERNEL_SIZE = 3;
	     final Size BLUR_SIZE = new Size(3,3);
	     int lowThresh = 0;
	     System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	     Mat src = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
	    // Mat grayMat = new Mat();
	     //Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_BGR2GRAY);

	     Mat srcBlur = new Mat();
	     Mat detectedEdges = new Mat();
	     Mat dst = new Mat();
		// Imgproc.blur(grayMat, srcBlur, BLUR_SIZE);
		 Imgproc.blur(src, srcBlur, BLUR_SIZE);
		 lowThresh = 40;
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        //dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
        //src.copyTo(dst, detectedEdges);
        showImage(detectedEdges);
      
	}
}
