/*
 * Course: CSC-1120A
 * Lab 5 - Mean Image Median Revisited
 * Name: Keagan Weinstock
 * Last Updated: 02/12/2024
 */

package weinstockk;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;



/**
 * MeanImageMedian class provides static methods for calculating mean and median images,
 * as well as reading and writing images in the PPM format.
 */
public class MeanImageMedian {

    /**
     * Maximum color value
     */
    public static final int MAX_COLOR = 255;

    /**
     * Reads an image from the specified file path and returns a JavaFX Image object.
     *
     * @param imagePath The path to the image file.
     * @return A JavaFX Image object representing the image.
     * @throws IOException              If an I/O error occurs during image reading.
     * @throws IllegalArgumentException If the file format is not supported
     * (only supports ".ppm", ".jpg", and ".png").
     */
    public static Image readImage(Path imagePath) throws IOException, IllegalArgumentException {
        if (imagePath == null) {
            throw new IllegalArgumentException();
        }
        try {
            if (imagePath.toString().endsWith(".ppm")) {
                return readPPMImage(imagePath);
            } else if (imagePath.toString().endsWith(".jpg") || imagePath.toString()
                    .endsWith(".png")) {
                return SwingFXUtils.toFXImage(ImageIO.read(imagePath.toFile()), null);
            } else if (imagePath.toString().endsWith(".msoe")) {
                return readMSOEImage(imagePath);
            } else {
                throw new IOException();
            }
        } catch(IOException e) {
            throw new IOException();
        }
    }

    /**
     * Writes the given JavaFX Image object to the specified file path.
     *
     * @param imagePath The path to the image file.
     * @param image     The JavaFX Image object to be written.
     * @throws IOException              If an I/O error occurs during image writing.
     * @throws IllegalArgumentException If the file format is not supported
     * (only supports ".ppm", ".jpg", and ".png").
     */
    public static void writeImage(Path imagePath, Image image)
            throws IOException, IllegalArgumentException {
        if (imagePath == null || image == null) {
            throw new IllegalArgumentException();
        }

        try {
            if (imagePath.toString().endsWith(".ppm")) {
                writePPMImage(imagePath, image);
            } else if (imagePath.toString().endsWith(".jpg")) {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", imagePath.toFile());
            } else if (imagePath.toString().endsWith(".png")) {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imagePath.toFile());
            } else if (imagePath.toString().endsWith(".msoe")) {
                writeMSOEImage(imagePath, image);
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * Calculates the median of all the images passed to the method.
     * <br />
     * Each pixel in the output image consists is calculated as the median
     * red, green, and blue components of the input images at the same location.
     * @param inputImages Images to be used as input
     * @return An image containing the median color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than one, or  if any of the input images differ in size.
     * @deprecated use {@link #generateImage(Image[] images, String operation)} instead
     */
    @Deprecated
    public static Image calculateMedianImage(Image[] inputImages) throws IllegalArgumentException {
        if (inputImages == null || inputImages.length <= 2) {
            throw new IllegalArgumentException("Invalid input images");
        }

        int width = (int) inputImages[0].getWidth();
        int height = (int) inputImages[0].getHeight();
        int length = inputImages.length;

        for (int i = 1; i < inputImages.length; i++) {
            if (inputImages[i].getWidth() != width || inputImages[i].getHeight() != height) {
                throw new IllegalArgumentException("Input images must have the same size");
            }
        }

        WritableImage writeImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {
                int[] red = new int[length];
                int[] green = new int[length];
                int[] blue = new int[length];
                int[] alpha = new int[length];

                for (int i = 0; i < length; i++) {
                    int argb = inputImages[i].getPixelReader().getArgb(x, y);
                    red[i] = argbToRed(argb);
                    green[i] = argbToGreen(argb);
                    blue[i] = argbToBlue(argb);
                    alpha[i] = argbToAlpha(argb);
                }

                int r = findMedian(red);
                int g = findMedian(green);
                int b = findMedian(blue);
                int a = findMedian(alpha);

                writeImage.getPixelWriter().setArgb(x, y, argbToInt(a, r, g, b));
            }
        }
        return writeImage;
    }



    /**
     * Calculates the mean of all the images passed to the method.
     * <br />
     * Each pixel in the output image consists is calculated as the average of the
     * red, green, and blue components of the input images at the same location.
     * @param inputImages Images to be used as input
     * @return An image containing the mean color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than one, or  if any of the input images differ in size.
     * @deprecated use {@link #generateImage(Image[] images, String operation)} instead
     */
    @Deprecated
    public static Image calculateMeanImage(Image[] inputImages) throws IllegalArgumentException {
        if (inputImages == null || inputImages.length < 2) {
            throw new IllegalArgumentException("Invalid input images");
        }

        int width = (int) inputImages[0].getWidth();
        int height = (int) inputImages[0].getHeight();
        WritableImage writableImage = new WritableImage(width, height);

        int[][] sumRed = new int[width][height];
        int[][] sumGreen = new int[width][height];
        int[][] sumBlue = new int[width][height];
        int[][] sumAlpha = new int[width][height];

        for (Image image : inputImages) {
            if (image == null || image.getWidth() != width || image.getHeight() != height) {
                throw new IllegalArgumentException();
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = image.getPixelReader().getArgb(x, y);
                    sumRed[x][y] += argbToRed(argb);
                    sumGreen[x][y] += argbToGreen(argb);
                    sumBlue[x][y] += argbToBlue(argb);
                    sumAlpha[x][y] += argbToAlpha(argb);
                }
            }
        }

        int numImages = inputImages.length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int meanRed = sumRed[x][y] / numImages;
                int meanGreen = sumGreen[x][y] / numImages;
                int meanBlue = sumBlue[x][y] / numImages;
                int meanAlpha = sumAlpha[x][y] / numImages;
                writableImage.getPixelWriter()
                        .setArgb(x, y, argbToInt(meanAlpha, meanRed, meanGreen, meanBlue));
            }
        }

        return writableImage;
    }

    /**
     * Reads an image in PPM format. The method only supports
     * the plain PPM (P3) format with 24-bit color
     * and does not support comments in the image file.
     * @param imagePath the path to the image to be read
     * @return An image object containing the image read from the file.
     *
     * @throws IllegalArgumentException Thrown if imagePath is null.
     * @throws IOException Thrown if the image format is invalid or
     * there was trouble reading the file.
     */
    public static Image readPPMImage(Path imagePath) throws IOException, IllegalArgumentException {
        if (imagePath == null) {
            throw new IllegalArgumentException();
        }
        WritableImage anImage;

        try (Scanner in = new Scanner(imagePath)) {
            if(in.nextLine().equals("P3")) {
                int horizontal = in.nextInt();
                int vertical = in.nextInt();
                anImage = new WritableImage(horizontal, vertical);
                in.nextLine();
                int maxValue = Integer.parseInt(in.nextLine());

                for (int y = 0; y < vertical; y++) {
                    for (int x = 0; x < horizontal; x++) {
                        int r = in.nextInt();
                        int g = in.nextInt();
                        int b = in.nextInt();
                        int argb = argbToInt(maxValue, r, g, b);
                        anImage.getPixelWriter().setArgb(x, y, argb);

                    }
                }

            } else {
                throw new IOException();
            }

        } catch (IOException e) {
            throw new IOException();
        }
        return anImage;
    }

    /**
     * Writes an image in PPM format. The method only supports
     * the plain PPM (P3) format with 24-bit color
     * and does not support comments in the image file.
     * @param imagePath the path to where the file should be written
     * @param image the image containing the pixels to be written to the file
     *
     * @throws IllegalArgumentException Thrown if imagePath is null.
     * @throws IOException Thrown if the image format is invalid or
     * there was trouble reading the file.
     */
    public static void writePPMImage(Path imagePath, Image image)
            throws IOException, IllegalArgumentException {
        if (imagePath == null) {
            throw new IllegalArgumentException();
        }
        if (image == null) {
            throw new IOException();
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        File output = new File(String.valueOf(imagePath));
        try (PrintWriter pw = new PrintWriter(output)) {
            pw.println("P3");
            pw.println(width + " " + height);
            pw.println(MAX_COLOR);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = image.getPixelReader().getArgb(x, y);
                    int r = argbToRed(argb);
                    int g = argbToGreen(argb);
                    int b = argbToBlue(argb);
                    pw.print(r + " " + g + " " + b + "   ");
                }
                pw.println();
            }

        } catch (IOException e) {
            throw new IOException();
        }
    }

    private static Image readMSOEImage(Path imagePath)
            throws IOException, IllegalArgumentException {
        if (imagePath == null) {
            throw new IllegalArgumentException();
        }
        WritableImage anImage;

        try (Scanner in = new Scanner(imagePath)) {
            if(in.nextLine().equals("1297305413")) {
                int horizontal = in.nextInt();
                int vertical = in.nextInt();
                anImage = new WritableImage(horizontal, vertical);
                in.nextLine();

                for (int y = 0; y < vertical; y++) {
                    for (int x = 0; x < horizontal; x++) {
                        int argb = in.nextInt();
                        anImage.getPixelWriter().setArgb(x, y, argb);
                    }
                    in.nextLine();
                }

            } else {
                throw new IOException();
            }

        } catch (IOException e) {
            throw new IOException();
        }
        return anImage;
    }

    private static void writeMSOEImage(Path imagePath, Image image)
            throws IOException, IllegalArgumentException {
        if (imagePath == null) {
            throw new IllegalArgumentException();
        }
        if (image == null) {
            throw new IOException();
        }

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        File output = new File(String.valueOf(imagePath));
        try (PrintWriter pw = new PrintWriter(output)) {
            pw.println("1297305413");
            pw.println(width + " " + height);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = image.getPixelReader().getArgb(x, y);
                    pw.print(argb + " ");
                }
                pw.println();
            }

        } catch (IOException e) {
            throw new IOException();
        }

    }


    /**
     * Calculates the mean or median of all the images passed to the method.
     *
     * @param images Images to be used as input
     * @param operation picks between if it is mean or median
     * @return An image containing the mean color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than one, or  if any of the input images differ in size.
     */
    public static Image generateImage(Image[] images, String operation)
            throws IllegalArgumentException {
        if (images == null || images.length < 2) {
            throw new IllegalArgumentException("Invalid input images");
        }

        int width = (int) images[0].getWidth();
        int height = (int) images[0].getHeight();
        WritableImage writeImage = new WritableImage(width, height);

        if (operation.equalsIgnoreCase("median")) {
            int length = images.length;

            for (int i = 1; i < images.length; i++) {
                if (images[i].getWidth() != width || images[i].getHeight() != height) {
                    throw new IllegalArgumentException("Input images must have the same size");
                }
            }

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int[] red = new int[length];
                    int[] green = new int[length];
                    int[] blue = new int[length];
                    int[] alpha = new int[length];

                    for (int i = 0; i < length; i++) {
                        int argb = images[i].getPixelReader().getArgb(x, y);
                        red[i] = argbToRed(argb);
                        green[i] = argbToGreen(argb);
                        blue[i] = argbToBlue(argb);
                        alpha[i] = argbToAlpha(argb);
                    }

                    int r = findMedian(red);
                    int g = findMedian(green);
                    int b = findMedian(blue);
                    int a = findMedian(alpha);

                    writeImage.getPixelWriter().setArgb(x, y, argbToInt(a, r, g, b));
                }
            }

        } else if (operation.equalsIgnoreCase("mean")) {

            int[][] sumRed = new int[width][height];
            int[][] sumGreen = new int[width][height];
            int[][] sumBlue = new int[width][height];
            int[][] sumAlpha = new int[width][height];

            for (Image image : images) {
                if (image == null || image.getWidth() != width || image.getHeight() != height) {
                    throw new IllegalArgumentException();
                }

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int argb = image.getPixelReader().getArgb(x, y);
                        sumRed[x][y] += argbToRed(argb);
                        sumGreen[x][y] += argbToGreen(argb);
                        sumBlue[x][y] += argbToBlue(argb);
                        sumAlpha[x][y] += argbToAlpha(argb);
                    }
                }
            }

            int numImages = images.length;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int meanRed = sumRed[x][y] / numImages;
                    int meanGreen = sumGreen[x][y] / numImages;
                    int meanBlue = sumBlue[x][y] / numImages;
                    int meanAlpha = sumAlpha[x][y] / numImages;
                    writeImage.getPixelWriter()
                            .setArgb(x, y, argbToInt(meanAlpha, meanRed, meanGreen, meanBlue));
                }
            }

        } else {
            throw new IllegalArgumentException();
        }

        return writeImage;
    }

    private static int findMedian(int[] nums) {
        Arrays.sort(nums);
        int half = nums.length / 2;
        if(nums.length % 2 == 0) {
            return (nums[half] + nums[half + 1]) / 2;
        } else {
            return nums[half];
        }
    }

    /**
     * Calculates a value of a bunch of input images combined depending
     * on the transformation that is applied.
     *
     * @param images Images to be used as input
     * @param transformation changes what type of transformation happens with the pixels
     * @return An image containing the mean color value for each pixel in the input images
     *
     * @throws IllegalArgumentException Thrown if inputImages or any element of inputImages is null,
     * the length of the array is less than one, or  if any of the input images differ in size.
     */
    public static Image applyTransform(Image[] images, Transform transformation) {
        if (images == null || images.length <= 2) {
            throw new IllegalArgumentException("Invalid input images");
        }

        int width = (int) images[0].getWidth();
        int height = (int) images[0].getHeight();
        int length = images.length;

        for (int i = 1; i < images.length; i++) {
            if (images[i].getWidth() != width || images[i].getHeight() != height) {
                throw new IllegalArgumentException("Input images must have the same size");
            }
        }

        WritableImage writeImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {
                int[] red = new int[length];
                int[] green = new int[length];
                int[] blue = new int[length];
                int[] alpha = new int[length];

                for (int i = 0; i < length; i++) {
                    int argb = images[i].getPixelReader().getArgb(x, y);
                    red[i] = argbToRed(argb);
                    green[i] = argbToGreen(argb);
                    blue[i] = argbToBlue(argb);
                    alpha[i] = argbToAlpha(argb);
                }

                int r = transformation.apply(red);
                int g = transformation.apply(green);
                int b = transformation.apply(blue);
                int a = transformation.apply(alpha);

                writeImage.getPixelWriter().setArgb(x, y, argbToInt(a, r, g, b));
            }
        }
        return writeImage;
    }

    /**
     * Extract 8-bit Alpha value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Alpha value of the color.
     */
    private static int argbToAlpha(int argb) {
        final int bitShift = 24;
        return argb >> bitShift;
    }

    /**
     * Extract 8-bit Red value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Red value of the color.
     */
    private static int argbToRed(int argb) {
        final int bitShift = 16;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Extract 8-bit Green value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Green value of the color.
     */
    private static int argbToGreen(int argb) {
        final int bitShift = 8;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Extract 8-bit Blue value of color from 32-bit representation of the color in the format
     * described by the INT_ARGB PixelFormat type.
     * @param argb the 32-bit representation of the color
     * @return the 8-bit Blue value of the color.
     */
    private static int argbToBlue(int argb) {
        final int bitShift = 0;
        final int mask = 0xff;
        return (argb >> bitShift) & mask;
    }

    /**
     * Converts argb components into a single int that represents the argb value of a color.
     * @param a the 8-bit Alpha channel value of the color
     * @param r the 8-bit Red channel value of the color
     * @param g the 8-bit Green channel value of the color
     * @param b the 8-bit Blue channel value of the color
     * @return a 32-bit representation of the color in the
     * format described by the INT_ARGB PixelFormat type.
     */
    private static int argbToInt(int a, int r, int g, int b) {
        final int alphaShift = 24;
        final int redShift = 16;
        final int greenShift = 8;
        final int mask = 0xff;
        return a << alphaShift | ((r & mask) << redShift) | (g & mask) << greenShift | b & mask;
    }
}
