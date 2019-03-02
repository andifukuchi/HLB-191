package edu.hlb191.src;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

class TreeDetector1 {
    public void detectAndDisplay(Mat frame, CascadeClassifier faceCascade) {
        Mat frameGray = new Mat();
        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);

        // -- Detect faces
        MatOfRect faces = new MatOfRect();
        //faceCascade.detectMultiScale(frameGray, faces);
        //params https://stackoverflow.com/questions/20801015/recommended-values-for-opencv-detectmultiscale-parameters
        //faceCascade.detectMultiScale(frameGray, faces, 1.05d,6, 0, new Size(200,200), new Size(500,500));
        //15s_32_faceCascade.detectMultiScale(frameGray, faces, 1.02d,3, 0, new Size(300,300), new Size(600,600));
        faceCascade.detectMultiScale(frameGray, faces, 1.05d,3, 0, new Size(32,32), new Size(600,600));
        List<Rect> listOfFaces = faces.toList();
        for (Rect face : listOfFaces) {
            Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);
            Imgproc.ellipse(frame, center, new Size(face.width / 2, face.height / 2), 0, 0, 360,
                    new Scalar(255, 0, 255));

            //Mat faceROI = frameGray.submat(face);
            //Rect rectCrop = new Rect(cropCenterX, cropCenterY, cropWidth, cropHeight);
            Mat croppedImage = new Mat(frame, face);
            showImage(croppedImage);
        }

        //-- Show what you got
        //HighGui.imshow("Capture - Face detection", frame );
        showImage(frame);
    }

    public void run(String[] args) {
        //String filenameFaceCascade = args.length > 2 ? args[0] : "../../data/haarcascades/haarcascade_frontalface_alt.xml";
        //String filenameEyesCascade = args.length > 2 ? args[1] : "../../data/haarcascades/haarcascade_eye_tree_eyeglasses.xml";
        //String filenameFaceCascade = args.length > 2 ? args[0] : "D:\\edu\\investigacion-hlb\\opencv_src\\opencv\\data\\haarcascades\\haarcascade_frontalface_alt.xml";
        //String filenameEyesCascade = args.length > 2 ? args[1] : "D:\\edu\\investigacion-hlb\\opencv_src\\opencv\\data\\haarcascades\\haarcascade_eye_tree_eyeglasses.xml";
        String filenameFaceCascade = args.length > 2 ? args[0] : "D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\treeDetection\\classifier\\cascade.xml";
        
        CascadeClassifier faceCascade = new CascadeClassifier();
        

        if (!faceCascade.load(filenameFaceCascade)) {
            System.err.println("--(!)Error loading face cascade: " + filenameFaceCascade);
            System.exit(0);
        }
        Mat frame = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
        //Mat frame = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC05214_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
       // Mat frame = Imgcodecs.imread("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06130_CP.tif",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
        
       //-- 3. Apply the classifier to the frame
       detectAndDisplay(frame, faceCascade);
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
}

public class TreeDetection2 {
    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        new TreeDetector1().run(args);
    }
}
