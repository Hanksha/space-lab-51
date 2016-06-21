package com.calderagames.spacelab.util;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class BufferLoader {

	public static ByteBuffer[] loadIcon(InputStream ips) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(ips);
		} catch(IOException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}

		ByteBuffer[] buffers = new ByteBuffer[3];
		buffers[0] = loadIconInstance(image, 128);
		buffers[1] = loadIconInstance(image, 32);
		buffers[2] = loadIconInstance(image, 16);

		return buffers;
	}

	public static IntBuffer[] loadCursorIntBuffer(InputStream ips) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(ips);
		} catch(IOException e) {
			ErrorLog.writeToErrorLog(e.getStackTrace());
			e.printStackTrace();
		}

		IntBuffer[] buffers = new IntBuffer[3];
		buffers[0] = loadCursorInstanceIntBuffer(image, 128);
		buffers[1] = loadCursorInstanceIntBuffer(image, 32);
		buffers[2] = loadCursorInstanceIntBuffer(image, 16);

		return buffers;
	}

	private static ByteBuffer loadIconInstance(BufferedImage image, int dimension) {

		BufferedImage scaledIcon = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = scaledIcon.createGraphics();

		double ratio = getIconRatio(image, scaledIcon);
		double width = image.getWidth() * ratio;
		double height = image.getHeight() * ratio;

		g.drawImage(image, (int) ((scaledIcon.getWidth() - width) / 2), (int) ((scaledIcon.getHeight() - height) / 2), (int) (width), (int) (height), null);

		g.dispose();

		return readImageAsByteBuffer(scaledIcon);
	}

	private static IntBuffer loadCursorInstanceIntBuffer(BufferedImage image, int dimension) {

		BufferedImage scaledIcon = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = scaledIcon.createGraphics();

		double ratio = getIconRatio(image, scaledIcon);
		double width = image.getWidth() * ratio;
		double height = image.getHeight() * ratio;

		g.drawImage(image, (int) ((scaledIcon.getWidth() - width) / 2), (int) ((scaledIcon.getHeight() - height) / 2), (int) (width), (int) (height), null);

		g.dispose();

		return readImageAsIntBuffer(scaledIcon);
	}

	private static double getIconRatio(BufferedImage originalImage, BufferedImage icon) {
		double ratio = 1;

		if(originalImage.getWidth() > icon.getWidth()) {
			ratio = (double) (icon.getWidth()) / originalImage.getWidth();
		}
		else {
			ratio = icon.getWidth() / originalImage.getWidth();
		}
		if(originalImage.getHeight() > icon.getHeight()) {
			double r2 = (double) (icon.getHeight()) / originalImage.getHeight();

			if(r2 < ratio) {
				ratio = r2;
			}
		}
		else {
			double r2 = icon.getHeight() / originalImage.getHeight();

			if(r2 < ratio) {
				ratio = r2;
			}
		}

		return ratio;
	}

	public static ByteBuffer readImageAsByteBuffer(BufferedImage image) {
		byte[] imageBuffer = new byte[image.getWidth() * image.getHeight() * 4];

		int counter = 0;
		for(int i = 0; i < image.getHeight(); i++) {
			for(int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);

				imageBuffer[counter] = (byte) ((colorSpace << 8) >> 24);
				imageBuffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				imageBuffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				imageBuffer[counter + 3] = (byte) (colorSpace >> 24);

				counter += 4;
			}
		}

		return ByteBuffer.wrap(imageBuffer);
	}

	public static IntBuffer readImageAsIntBuffer(BufferedImage image) {
		int[] imageBuffer = new int[image.getWidth() * image.getHeight()];

		int counter = 0;

		for(int i = image.getHeight() - 1; i >= 0; i--) {
			for(int j = 0; j < image.getWidth(); j++) {
				int colorSpace = image.getRGB(j, i);
				// A R G B
				// 00000000 00000000 00000000 00000000
				// R: (colorSpace << 8) >>> 24
				// G: (colorSpace << 8) >>> 24
				// B: (colorSpace << 16) >>> 24
				// A: colorSpace >> 24

				imageBuffer[counter] = colorSpace;

				counter += 1;
			}
		}

		return IntBuffer.wrap(imageBuffer);
	}
}
