package edu.hlb191.src;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterLossless;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class GeoTag {
	public static void main(String[] args) {
		File file = new File("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\TTC06200_CP.TIF");
		File file2 = new File("D:\\edu\\investigacion-hlb\\workspace\\HLB-191\\src\\main\\java\\edu\\hlb191\\src\\dest.tif");
		try {
			changeExifMetadata(file,file2);
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImageWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This example illustrates how to add/update EXIF metadata in a JPEG file.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	public static void changeExifMetadata(final File jpegImageFile, final File dst)
			throws IOException, ImageReadException, ImageWriteException {
		
		try (FileOutputStream fos = new FileOutputStream(dst); OutputStream os = new BufferedOutputStream(fos);) {
			BufferedImage image = ImageIO.read(jpegImageFile);
			TiffOutputSet outputSet = null;
			// note that metadata might be null if no metadata is found.
			final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
				final TiffImageMetadata exif = (TiffImageMetadata) metadata;

				if (null != exif) {
					// TiffImageMetadata class is immutable (read-only).
					// TiffOutputSet class represents the Exif data to write.
					//
					// Usually, we want to update existing Exif metadata by
					// changing
					// the values of a few fields, or adding a field.
					// In these cases, it is easiest to use getOutputSet() to
					// start with a "copy" of the fields read from the image.
					outputSet = exif.getOutputSet();
				}
			
			

			// if file does not contain any exif metadata, we create an empty
			// set of exif metadata. Otherwise, we keep all of the other
			// existing tags.
			if (null == outputSet) {
				outputSet = new TiffOutputSet();
			}

			{
				// Example of how to add a field/tag to the output set.
				//
				// Note that you should first remove the field/tag if it already
				// exists in this directory, or you may end up with duplicate
				// tags. See above.
				//
				// Certain fields/tags are expected in certain Exif directories;
				// Others can occur in more than one directory (and often have a
				// different meaning in different directories).
				//
				// TagInfo constants often contain a description of what
				// directories are associated with a given tag.
				//
			//	final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
				// make sure to remove old value if present (this method will
				// not fail if the tag does not exist).
			//	exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
			//	exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE, new RationalNumber(3, 10));
				
			}

			{
				// Example of how to add/update GPS info to output set.

				// New York City
				final double latitude = -27.0027416666667; // 74 degrees W (in Degrees East)
				final double longitude = -56.6176133333333; // 40 degrees N (in Degrees
				// North)
			    final Map<String, Object> params = new TreeMap<>();
			    params.put(ImagingConstants.PARAM_KEY_EXIF, outputSet);
				outputSet.setGPSInDegrees(longitude, latitude);
				Imaging.writeImage(image, os, ImageFormats.TIFF, params);

			}

			// printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);

			//new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);

		}
	}
}