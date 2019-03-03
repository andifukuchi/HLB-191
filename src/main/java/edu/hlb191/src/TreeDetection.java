package edu.hlb191.src;

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

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

class TreeDetector {
	// average min tree image sizes to avoid false positive detection on branches
	// sections
	public enum CAPTURE_HEIGHTS {
		IMG_15_29_H {
			public int getMinSizeTree() {
				return 300;
			}

			public String getFolderName() {
				return "20";
			}
		},
		IMG_30_39_H {
			public int getMinSizeTree() {
				return 200;
			}

			public String getFolderName() {
				return "30";
			}
		},
		IMG_40_49_H {
			public int getMinSizeTree() {
				return 100;
			}

			public String getFolderName() {
				return "40";
			}
		},
		IMG_50_100_H {
			public int getMinSizeTree() {
				return 50;
			}

			public String getFolderName() {
				return "50_100";
			}
		};
		public abstract int getMinSizeTree();

		public abstract String getFolderName();
	}

	public void detectAndDisplay(Mat frame, CascadeClassifier faceCascade, File output, String fileName, int min_size) {
		Mat frameGray = new Mat();
		frame.copyTo(frameGray);
		Mat frameWithMarkings = new Mat();
		frame.copyTo(frameWithMarkings);
		// Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
		// Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_RGB2GRAY);

		// Imgproc.equalizeHist(frameGray, frameGray);

		// -- Detect trees
		MatOfRect faces = new MatOfRect();

		faceCascade.detectMultiScale(frameGray, faces, 1.03d, 5, 0, new Size(min_size, min_size), new Size(800, 800));
		List<Rect> listOfFaces = faces.toList();
		int i = 0;
		for (Rect face : listOfFaces) {
			//create an index image to reference locations
			Point center = new Point(face.x + face.width / 2, face.y + face.height / 2);
			Imgproc.ellipse(frameWithMarkings, center, new Size(face.width / 2, face.height / 2), 0, 0, 360,
					new Scalar(255, 0, 255));
			Imgproc.putText(frameWithMarkings, "" + i, center, Core.FONT_HERSHEY_PLAIN, 1.5, new Scalar(255,0,0));
			
			//cut image and save to output folder
			Mat croppedImage = new Mat(frame, face);
			// showImage(croppedImage);
			Imgcodecs.imwrite(output.getAbsolutePath() + "/" + fileName.replace(".tif", "") + "_" + i + ".tif",
					croppedImage);
			i++;
		}
		//save index image
		Imgcodecs.imwrite(output.getAbsolutePath() + "/" + fileName.replace(".tif", "") + "_index.tif",
				frameWithMarkings);
		// -- Show what you got
		// HighGui.imshow("Capture - Face detection", frame );
		//showImage(frameWithMarkings);
	}

	public void run(String[] args) {
		for (CAPTURE_HEIGHTS h : CAPTURE_HEIGHTS.values()) {

			String sourcePath = "./src/main/java/edu/hlb191/src/img/source/" + h.getFolderName();
			final File output = new File("./src/main/java/edu/hlb191/src/img/output/" + h.getFolderName());
			// create dirs if doesn't exists;
			if (!output.exists()) {
				output.mkdirs();
			}

			String filenameFaceCascade = "D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\treeDetection\\classifier\\cascade15s_32px_4.xml";

			CascadeClassifier faceCascade = new CascadeClassifier();

			if (!faceCascade.load(filenameFaceCascade)) {
				System.err.println("--(!)Error loading face cascade: " + filenameFaceCascade);
				System.exit(0);
			}

			FileVisitor<Path> fileProcessor = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// System.out.println("Processing file:" + file.toFile().getAbsolutePath());

					Mat frame = Imgcodecs.imread(file.toFile().getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
					// -- 3. Apply the classifier to the frame
					detectAndDisplay(frame, faceCascade, output, file.toFile().getName(), h.getMinSizeTree());
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

	private static void showImage(Mat img) {
		MatOfByte dest2 = new MatOfByte();
		Imgcodecs.imencode(".tif", img, dest2);
		byte ba[] = dest2.toArray();
		BufferedImage bi;
		try {
			bi = ImageIO.read(new ByteArrayInputStream(ba));
			showImage(bi, "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void showImage(BufferedImage img, String title) {

		ImageIcon icon = new ImageIcon(img);
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setLayout(new FlowLayout());
		frame.setSize(img.getWidth(), img.getHeight() + 100);
		final JLabel text = new JLabel();
		JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		framesPerSecond.setMajorTickSpacing(25);
		framesPerSecond.setMinorTickSpacing(1);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);
		framesPerSecond.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int fps = (int) source.getValue();
					text.setText("" + (fps / 100.0d));
				}

			}

		});
		frame.add(text);
		frame.add(framesPerSecond);

		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}

public class TreeDetection {
	public static void main(String[] args) {
		// Load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		new TreeDetector().run(args);
	}
}
