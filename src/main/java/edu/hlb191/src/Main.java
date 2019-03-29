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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
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
		//sobel();
		//laplacian1();
		//canny();
		//cannyGray();
		//threshold();
		//laplacian();
		//hist2();
		//equalizeHist();
		equalizeHist1();
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
		showImage(img,"");
        
	}
	private static void showImage(Mat img,String title) {
        MatOfByte dest2=new MatOfByte();
        Imgcodecs.imencode(".tif", img, dest2);
        byte ba[]=dest2.toArray();
        BufferedImage bi;
		try {
			bi = ImageIO.read(new ByteArrayInputStream(ba));
			showImage(bi,title);
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
	
	private static void equalizeHist() {
		try {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			//Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\img\\output1\\20\\TTC06200_CP.TIF_3.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
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
			source.get(0,0);
			source.get(10, 10);
			Core.merge(rgb, hist_1);
			//Imgproc.cvtColor(hist_1, hist_1, Imgproc.COLOR_YCrCb2RGB);
			//Imgcodecs.imwrite("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP1.tif", hist_1);
			
			showImage(hist_1);
			
		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}

	}
	private static void equalizeHist1() {
		try {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			//Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\img\\output1\\20\\TTC06200_CP.TIF_3.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			//Mat source = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\img\\output1\\20\\TTC06200_CP.TIF_2.tif",	Imgcodecs.CV_LOAD_IMAGE_COLOR);
			
			showImage(source);
			Mat hist_1 = new Mat();			
			List<Mat> rgb = new ArrayList<Mat>();
			Core.split(source, rgb);
			Imgproc.equalizeHist(rgb.get(0),rgb.get(0));
			Imgproc.equalizeHist(rgb.get(1),rgb.get(1));
			Imgproc.equalizeHist(rgb.get(2),rgb.get(2));
			source.get(0,0);
			source.get(10, 10);
			Core.merge(rgb, hist_1);
			
			//showImage(hist_1);
			Mat ndvi = applyNDVI(hist_1);
			//showImage(ndvi);
			//Point center = new Point(ndvi.width() / 2, ndvi.height() / 2);
			//Imgproc.ellipse(ndvi, center, new Size(ndvi.width() / 4, ndvi.height() / 2), 0, 90, 180, new Scalar(255, 0, 255),-1);
			showImage(ndvi);
			//cropTree(ndvi);
			//cropTree1(ndvi);
			//canny(ndvi);
			Imgproc.medianBlur(ndvi, ndvi, 9);
			showImage(ndvi);
			//canny(ndvi);
			//cropTree1(ndvi);
			
			int[] arr = getEstimatedTreePosAndSize(ndvi);
			Mat cropMask = new Mat(source.size(),source.type());
			//Imgproc.ellipse(ndvi, new Point(arr[0],arr[1]), new Size(arr[2]/2,arr[3]/2), 0, 0, 360, new Scalar(255, 255, 255),-1);
			Imgproc.ellipse(cropMask, new Point(arr[0],arr[1]), new Size(arr[2]/2,arr[3]/2), 0, 0, 360, new Scalar(255, 255, 255),-1);
			
			//Imgproc.ellipse(ndvi, new Point(arr[3]/2,arr[2]/2), new Size(arr[2]/2,arr[3]/2), 0, 0, 360, new Scalar(255, 255, 255),-1);
			
			showImage(cropMask);
			
			
			Mat result = new Mat();
			
			source.copyTo(result,cropMask);
			//cut image into only the ellipse area
			int x =  arr[0] - arr[2]/2;
			int y = arr[1] - arr[3]/2;
			Mat croppedImg = new Mat(result, new Rect(x,y,arr[2],arr[3]));
			
			showImage(croppedImg);
		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}

	}
	private static Mat applyNDVI(Mat source) {
		Mat dest = new Mat(source.size(),source.type());
		for (int i = 0; i < source.cols(); i++) {
			for (int j = 0; j < source.rows() ; j++) {
				//Color c = new Color(source.get(j, i));
				double col1 = source.get(j, i)[0]; //nir
				//int col1 = c.getGreen(); //red
				//int col1 = c.getBlue(); //green
				//int col2 = c.getGreen(); //red
				double col2 = source.get(j, i)[2]; //green
				//int col2 = ( c.getGreen() + c.getBlue() ) / 2 ; // Avg red + green 
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
	
	
	static void canny(Mat source) {
		 final int MAX_LOW_THRESHOLD = 100;
	     final int RATIO = 3;
	     final int KERNEL_SIZE = 3;
	     final Size BLUR_SIZE = new Size(3,3);
	     int lowThresh = 40;
	    
	     Mat srcBlur = new Mat();
	     Mat detectedEdges = new Mat();
	     Mat dst = new Mat();
		 Imgproc.blur(source, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        dst = new Mat(source.size(), CvType.CV_8UC3, Scalar.all(0));
        source.copyTo(dst, detectedEdges);
        showImage(dst);
      
	}
	private static Mat cropTree1(Mat source) {
		Mat gray = new Mat();
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);

		Mat circles = new Mat();
		 Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
	                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
	                100.0, 30.0, gray.width()/8, gray.width()/2); // change the last two parameters
	                // (min_radius & max_radius) to detect larger circles
	        for (int x = 0; x < circles.cols(); x++) {
	            double[] c = circles.get(0, x);
	            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
	            // circle center
	            Imgproc.circle(source, center, 1, new Scalar(0,100,100), 3, 8, 0 );
	            // circle outline
	            int radius = (int) Math.round(c[2]);
	            Imgproc.circle(source, center, radius, new Scalar(255,0,0), 3, 8, 0 );
	        }
	        showImage(source);
	        return source;

	}
	private static Mat cropTree(Mat source) {
		Mat minBlue;
		Mat minBlue1;
		//Mat up = drawElipse(source,0,true, true);
		Mat up = drawRect(source,0,true, true);
		Mat up1 = drawRect(source,0,true, false);
		
		minBlue = up;
		minBlue1 = up1;
		double countUp = calcBluePercent(up);
		double countUp1 = calcBluePercent(up1);
		//double act = countRGB(up)[2];
		int step = 1;
		try {
			//do this until failure
			//while(act >= prev) {
			int incr = 0;
			/*while( incr < source.width()/4) {
				incr = step * 10;
				//up = drawElipse(source,incr,true, true);
				up = drawRect(source,incr,true, true);
				
				double curr = calcBluePercent(up);
				if(curr > countUp ) {
					minBlue = up;
					countUp = curr;
				}
				//incr =(source.width()/6) - (50*step);
				//up = drawElipse(source,incr,true, true);
				//prev = act;
				//act = calcBluePercent(up);
				//act = countRGB(up)[2];
				step ++;
				showImage(up, "Up X");
			}*/
			incr = 0;
			while( incr < source.height()/4) {
				incr = step * 10;
				up1 = drawRect(source,incr,true, false);
				
				double curr = calcBluePercent(up1);
				if(curr > countUp1 ) {
					minBlue1 = up1;
					countUp1 = curr;
				}
				
				step ++;
				showImage(up1, "Up1 Y");
			}
			
		}
		catch(Exception e) {
			//continue;
			int x = 2;
		}
		//showImage(up, "Up X");
		//showImage(minBlue, "Min blue");
		showImage(minBlue1, "Min blue1");
		/*Mat q1 = new Mat(source, new Rect(0,0,source.width(),source.height()/2));
		Imgproc.ellipse(q1, new Point(q1.width()/2, q1.height()), new Size(q1.width() / 4, q1.height()/2), 0, 0, 360, new Scalar(0, 255, 0),-1);
		showImage(q1, "Q1");
		
		Mat q2 = new Mat(source, new Rect(0,source.height()/2,source.width(),source.height()/2));
		Imgproc.ellipse(q2, new Point(q1.width()/2, 0), new Size(q2.width() / 4, q2.height()/2), 0, 0, 360, new Scalar(0, 255, 0),-1);
		showImage(q2, "Q2");
		*/
		//first split into 4 quadrants and see best fit
		//cut image and save to output folder
		Point center = new Point(source.width() / 2, source.height() / 2);
		
		
/*
		Mat q1 = new Mat(source, new Rect(0,0,source.width()/2,source.height()/2));
		Imgproc.ellipse(q1, new Point(q1.width(), q1.height()), new Size(q1.width() / 2, q1.height()), 0, 0, 360, new Scalar(255, 0, 255),-1);
		showImage(q1, "Q1");
		
		Mat q2 = new Mat(source, new Rect(source.width()/2,0,source.width()/2,source.height()/2));
		Imgproc.ellipse(q2, new Point(0, q2.height()), new Size(q2.width() / 2, q2.height()), 0, 0, 360, new Scalar(255, 0, 255),-1);
		showImage(q2, "Q2");
		
		Mat q3 = new Mat(source, new Rect(0,source.height()/2,source.width()/2,source.height()/2));
		Imgproc.ellipse(q3, new Point(q3.width(), 0), new Size(q3.width() / 2, q3.height()), 0, 0, 360, new Scalar(255, 0, 255),-1);
		showImage(q3, "Q3");
		
		Mat q4 = new Mat(source, new Rect(source.width()/2,source.height()/2,source.width()/2,source.height()/2));
		Imgproc.ellipse(q4, new Point(0, 0), new Size(q4.width() / 2, q4.height()), 0, 0, 360, new Scalar(255, 0, 255),-1);
		showImage(q4, "Q4");
		Mat dest = new Mat(source.size(),source.type());
		return dest;*/
		return null;
	}
	private static Mat drawElipse(Mat source, int incr,boolean isUp, boolean moveX) {
		
		if(isUp) {
			Mat q1 = new Mat(source, new Rect(0,0,source.width(),source.height()/2));
			if(moveX) {
				Imgproc.ellipse(q1, new Point(q1.width()/2, q1.height()), new Size((q1.width() / 4) + incr, (q1.height()/2 )), 0, 0, 360, new Scalar(0, 255, 0),-1);
			}
			else {
				Imgproc.ellipse(q1, new Point(q1.width()/2, q1.height()), new Size(q1.width() / 4, (q1.height()/2) + incr), 0, 0, 360, new Scalar(0, 255, 0),-1);
			}
			return q1;
		}
		else {
			Mat q2 = new Mat(source, new Rect(0,source.height()/2,source.width(),source.height()/2));
			if(moveX) {			
				Imgproc.ellipse(q2, new Point(q2.width()/2, 0), new Size((q2.width() / 4) + incr, q2.height()/2), 0, 0, 360, new Scalar(0, 255, 0),-1);
			}
			else {
				Imgproc.ellipse(q2, new Point(q2.width()/2, 0), new Size(q2.width() / 4, (q2.height()/2) + incr), 0, 0, 360, new Scalar(0, 255, 0),-1);
			}
			return q2;
		}

	}
	private static Mat drawRect(Mat source, int incr,boolean isUp, boolean moveX) {
		/*
		if(isUp) {
			Mat q1 = new Mat(source, new Rect(0,0,source.width(),source.height()/2));
			if(moveX) {
				Imgproc.rectangle(q1, new Point((q1.width()/4) - incr , 0),new Point((q1.width()/2 +q1.width()/4) + incr, q1.height()) , new Scalar(0, 255, 0),-1);
			}
			return q1;
		}
		*/
		//Rect rectCrop = new Rect(startX, startY, width, height);
		//Mat q1 = new Mat(source, new Rect((source.width()/4) - incr , 0,(source.width()/2 +source.width()/4) + incr, source.height()/2));
		if(moveX) {
			Mat q1 = new Mat(source, new Rect((source.width()/4) - incr , 0,(source.width()/2 - source.width()/4)+incr, source.height()/2));
			return q1;
		}
		else {
			Mat q1 = new Mat(source, new Rect(0 ,(source.height()/4) - incr,source.width()/2, (source.height()/2 - source.height()/4)+incr));
			return q1;
		}

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
	
	
	
	private static int[] getEstimatedTreePosAndSize1(Mat source) {
		//size	
		int width = 0;
		int height = 0;
		//position
		int xCenter = 0;
		int yCenter = 0;
		//calc max width and y center
		//only do it for the image center +- 25% 
		//start from second quarter of image
		int start = source.rows()/2 - source.rows()/4;
		//end on third quarter of image
		int end = source.rows()/2 + source.rows()/4;
		for (int i = start; i < end; i++) {
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
		//start from second quarter of image
		start = source.cols()/2 - source.cols()/4;
		//end on third quarter of image
		end = source.cols()/2 + source.cols()/4;
		for (int i = start; i < end; i++) {
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
		
		return new int[] {xCenter,yCenter,width,height};
}
	
	private static int[] getEstimatedTreePosAndSizeAvg(Mat source) {
		//size	
		int width = 0;
		int height = 0;
		//position
		int xCenter = 0;
		int yCenter = 0;
		//calc max width and y center
		int[] widths = new int[source.rows()];
		for (int i = 0; i < source.rows(); i++) {
			int currentWidth = 0;
			for (int j = 0; j < source.cols(); j++) {
				if(source.get(i, j)[0]> 0 ) {
					currentWidth++;
				}
			}
			widths[i]=currentWidth;
		}
		int[] heights = new int[source.cols()];
		for (int i = 0; i < source.cols(); i++) {
			int currentHeight = 0;
			for (int j = 0; j < source.rows(); j++) {
				if(source.get(j, i)[0]> 0 ) {
					currentHeight++;
				}
			}
			heights[i] = currentHeight;
		}
		
		//average width and heights 
		
		
		return new int[] {xCenter,yCenter,width,height};
}
	
	
	private static double calcBluePercent(Mat source) {
		double countRed = 0;
		double countGreen = 0;
		double countBlue = 0;
		for (int i = 0; i < source.cols(); i++) {
			for (int j = 0; j < source.rows() ; j++) {
				if(source.get(j, i)[0]> 0 ) {
					countRed++;
				}
				else if(source.get(j, i)[1]> 0) {
					countGreen++;
				}
				else if(source.get(j, i)[2]> 0) {
					countBlue++;
				}
			}
		}
		
		return countRed != 0 ? countBlue/countRed : 1;
	}
	private static double[] countRGB(Mat source) {
		double countRed = 0;
		double countGreen = 0;
		double countBlue = 0;
		for (int i = 0; i < source.cols(); i++) {
			for (int j = 0; j < source.rows() ; j++) {
				if(source.get(j, i)[0]> 0 ) {
					countRed++;
				}
				else if(source.get(j, i)[1]> 0) {
					countGreen++;
				}
				else if(source.get(j, i)[2]> 0) {
					countBlue++;
				}
			}
		}
		
		return new double[]{countRed,countGreen,countBlue};
	}
}
