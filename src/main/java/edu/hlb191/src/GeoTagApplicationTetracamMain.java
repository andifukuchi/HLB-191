package edu.hlb191.src;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
/**
 * parse Tetracam generated GPS file and apply geo tag location to image files
 * @author Andi Fukuchi
 *
 */
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.FileUtils;

/**
 * GPS data format: File name prefix, **error message** File name prefix, Lat,
 * Long,Time, Alt Data example: TTC06049,**Corrupt data for this image number**
 * TTC06050,-2700.1661,-05637.0530,133540.04, No Altitude Data
 * TTC06051,-2700.1661,-05637.0530,133542.02,100.7
 * 
 */
public class GeoTagApplicationTetracamMain {

	private static Map<String, TetracamGPSData> getGPSData(String gpsFilePath) {
		Map<String, TetracamGPSData> gpsData = new HashMap<String, TetracamGPSData>();
		try {
			File gpsList = new File(gpsFilePath);
			BufferedReader br = new BufferedReader(new FileReader(gpsList.getAbsoluteFile()));
			String line = br.readLine();

			while (null != line) {
				String[] splittedValues = line.split(",");
				if (5 == splittedValues.length) {
					try {
						String name = splittedValues[0];
						double lat = Double.parseDouble(splittedValues[1]);
						double lon = Double.parseDouble(splittedValues[2]);
						String time = splittedValues[3];
						double alt = 0;
						try {
							alt = Double.parseDouble(splittedValues[4]);
						} catch (NumberFormatException e) {
							// ignore altitude error..
							System.out.println("altitude error reading: " + name);
						}
						gpsData.put(name, new TetracamGPSData(name, lat, lon, alt, time));
					} catch (NumberFormatException e) {
						// ignore gps data if lat or lon fails for this file
						System.out.println("gps data error reading: " + splittedValues[0]);
					}

				}
				line = br.readLine();
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gpsData;

	}

	/**
	 * Bean to contain raw Tetracam GPS data and convert it to Common GPS normalized
	 * data.
	 * 
	 * @author Andi
	 *
	 */
	private static final class TetracamGPSData {
		private final String filePrefix;
		private final double lat;
		private final double lon;
		private final double alt;
		private final String time;

		public TetracamGPSData(String filePrefix, double lat, double lon, double alt, String time) {
			this.filePrefix = filePrefix;
			this.lat = lat;
			this.lon = lon;
			this.alt = alt;
			this.time = time;

		}

		public String toString() {
			return "filePrefix='" + filePrefix + "', " + "lat='" + lat + "', " + "lon='" + lon + "', " + "alt='" + alt
					+ "', " + "time='" + time + "'";
		}
		
		public double getLatitude() {
			//convert gps info here. 
			return convertToDegrees(lat);
		}
		public double getLongitude() {
			//convert gps info here. 
			return convertToDegrees(lon);
		}
		
		private double convertToDegrees(double value) {
			//convert to degrees
			double deg= value/100d;
			//extract integer part
			long degPart = (long) deg;
			//extract decimal part (in minutes) 
			double minPart = deg - degPart;
			//convert min to degrees 
			double minDeg = minPart*100d/60d;
			//add decimal part degrees to integer part
			return degPart + minDeg;
		}
	}

	public static void storeGPSData(String imgFolderPath, Map<String, TetracamGPSData> gpsData) {
		File path = new File(imgFolderPath);
		for (Object objFile : FileUtils.listFiles(path, new String[]{"TIF","jpeg"}, true)) {
			File file = (File) objFile;
			// System.out.println(""+ file.getName());

			String fileName = file.getName();
			String[] fileSplit = fileName.split("_");
			if (fileSplit.length >= 2) {
				String filePrefix = fileSplit[0];
				if (gpsData.containsKey(filePrefix)) {
					System.out.println("Processing" + file.getName() + " " + gpsData.get(filePrefix));
					applyGPSData(file, gpsData.get(filePrefix),imgFolderPath);
				}
			}

			// BufferedReader br = new BufferedReader(new
			// FileReader(file.getAbsoluteFile()));
			// String line = br.readLine();

			// br.close();

		}
		path = null;

	}

	public static void applyGPSData(File file, TetracamGPSData gpsInfo, String imgFolderPath) {
		try {
			changeExifMetadata(file,new File(imgFolderPath + "\\_" + file.getName()), gpsInfo.getLatitude(), gpsInfo.getLongitude());
		} catch (ImageReadException | ImageWriteException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		
		//Map<String, TetracamGPSData> gpsData = getGPSData("D:\\edu\\investigacion-hlb\\2018_08_02\\tetracam\\conv\\06447_07870.GPS");
		//Map<String, TetracamGPSData> gpsData = getGPSData("D:\\edu\\investigacion-hlb\\2018_08_02\\tetracam\\conv\\06048_06445.GPS");
		//storeGPSData("D:\\edu\\investigacion-hlb\\2018_08_02\\tetracam\\conv", gpsData);
		//storeGPSData("D:\\edu\\investigacion-hlb\\2018_08_02\\tetracam\\seleccionados\\", gpsData);
		//Map<String, TetracamGPSData> gpsData = getGPSData("D:\\edu\\investigacion-hlb\\2018_07_11\\conv2\\05626_06241.GPS");
		//storeGPSData("D:\\edu\\investigacion-hlb\\2018_07_11\\conv2", gpsData);
		Map<String, TetracamGPSData> gpsData = getGPSData("D:\\edu\\investigacion-hlb\\2018_04_13\\conv2\\05143_05621.GPS");
		storeGPSData("D:\\edu\\investigacion-hlb\\2018_04_13\\conv2", gpsData);
		
		
		
	}

	public static void changeExifMetadata(final File source, final File dest, double latitude, double longitude)
			throws IOException, ImageReadException, ImageWriteException {

		try (FileOutputStream fos = new FileOutputStream(dest); OutputStream os = new BufferedOutputStream(fos);) {
			BufferedImage image = ImageIO.read(source);
			TiffOutputSet outputSet = null;
			// note that metadata might be null if no metadata is found.
			final ImageMetadata metadata = Imaging.getMetadata(source);
			final TiffImageMetadata exif = (TiffImageMetadata) metadata;
			if (null != exif) {
				outputSet = exif.getOutputSet();
			}
			if (null == outputSet) {
				outputSet = new TiffOutputSet();
			}

			// Example of how to add/update GPS info to output set.
			//final double latitude = -27.0027416666667; // 74 degrees W (in Degrees East)
			//final double longitude = -56.6176133333333; // 40 degrees N (in Degrees
			final Map<String, Object> params = new TreeMap<>();
			params.put(ImagingConstants.PARAM_KEY_EXIF, outputSet);
			outputSet.setGPSInDegrees(longitude, latitude);
			Imaging.writeImage(image, os, ImageFormats.TIFF, params);
			//rename file and delete old

		}
		File renamed = new File(source.getCanonicalPath());
		if(source.delete()) {
			dest.renameTo(renamed);
		}
	}

}
