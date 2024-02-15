/*
 * Course: CSC-1120A
 * Lab 5 - Mean Image Median Revisited
 * Name: Sean Jones
 * Last Updated: 02/08/2024
 */
package tests;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import weinstockk.MeanImageMedian;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Unit tests for Utility class MeanImageMedian
 */
public class MeanImageMedianTests {
    /**
     * Tests reading of PPM files
     */
    @Test
    @DisplayName("read PPM Image")
    public void readPPM() {
        try {
            final int height = 3;
            final int width = 2;
            final int[][] expectedValues = {new int[] {-1, -16777216, -16711936},
                    new int[] {-1, -65536, -16776961}};
            Image i = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test1.ppm"));
            Assertions.assertEquals(height, i.getHeight());
            Assertions.assertEquals(width, i.getWidth());
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MeanImageMedian.readPPMImage(null));
            Assertions.assertThrows(IOException.class,
                    () -> MeanImageMedian.readPPMImage(Paths.get("foo.abc")));

            PixelReader pr = i.getPixelReader();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    Assertions.assertEquals(expectedValues[x][y], pr.getArgb(x, y));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read file");
        }
    }

    /**
     * Tests writing to PPM files
     */
    @Test
    @DisplayName("write PPM image")
    public void writePPM() {
        Image[] images = setupMean();
        for (Image i : images) {
            try {
                Path imagePath = Paths.get("src", "tests", "output1.ppm");
                MeanImageMedian.writePPMImage(imagePath, i);
                Image read = MeanImageMedian.readPPMImage(imagePath);
                Assertions.assertTrue(imageEquals(i, read));
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> MeanImageMedian
                                .writePPMImage(null, new Image(imagePath.toString(), false)));
                Assertions.assertThrows(IOException.class,
                        () -> MeanImageMedian
                                .writePPMImage(Paths.get("src", "tests", "output1.abc"), null));
            } catch (IOException e) {
                System.out.println("Could not write to file");
            }
        }
    }

    /**
     * Tests reading of PNG and MSOE files
     */
    @Test
    @DisplayName("Testing reading PNG/MSOE images")
    public void readImage() {
        Path path = Paths.get("src", "tests", "test.png");
        try {
            Image i = MeanImageMedian.readImage(path);
            Image j = new Image(new FileInputStream(path.toFile()));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MeanImageMedian.readImage(null));
            Assertions.assertThrows(IOException.class,
                    () -> MeanImageMedian.readImage(Paths.get("src", "tests", "test.abc")));
            Assertions.assertTrue(imageEquals(i, j));
            path = Paths.get("src", "tests", "test.msoe");
            i = MeanImageMedian.readImage(path);
            path = Paths.get("src", "tests", "test1.ppm");
            j = MeanImageMedian.readImage(path);
            Assertions.assertTrue(imageEquals(i, j));
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }

    /**
     * Tests writing of PNG and MSOE files
     */
    @Test
    @DisplayName("Testing writing PNG/MSOE images")
    public void writeImage() {
        try{
            Path output = Paths.get("src", "tests", "output.png");
            Image i = new Image(new FileInputStream(
                    Paths.get("src", "tests", "test.png").toFile()));
            MeanImageMedian.writeImage(output, i);
            Image j = new Image(new FileInputStream(output.toFile()));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MeanImageMedian.writeImage(null, i));
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MeanImageMedian.writeImage(output, null));
            Path bad = Paths.get("src", "tests", "output.abc");
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MeanImageMedian.writeImage(bad, i));
            Assertions.assertTrue(imageEquals(i, j));
        } catch(IOException e) {
            System.err.println("Could not write file: " + e.getMessage());
        }
    }


    /**
     * Tests the calculating of a mean image
     */
    @Test
    @DisplayName("Calculate Mean Image")
    public void medianImage() {
        final int[][] pixels = {new int[] {-8421505, -8454144, -8388864},
                new int[] {-1, -8421632, -8421377}};
        Image[] images = setupMean();
        Image mean = MeanImageMedian.generateImage(images, "mean");
        PixelReader pr = mean.getPixelReader();
        int height = (int) mean.getHeight();
        int width = (int) mean.getWidth();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Assertions.assertEquals(pixels[x][y], pr.getArgb(x, y));
            }
        }
    }

    /**
     * Tests the calculating of a median image
     */
    @Test
    @DisplayName("Calculate Median Image")
    public void calculateMedianImage() {
        final int[][] pixels = {new int[] {-1, -16777216, -16711936},
                new int[] {-1, -65536, -16776961}};
        Image[] images = setupMedianOdd();
        Image median = MeanImageMedian.generateImage(images, "median");
        PixelReader pr = median.getPixelReader();
        int height = (int) median.getHeight();
        int width = (int) median.getWidth();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Assertions.assertEquals(pixels[x][y], pr.getArgb(x, y));
            }
        }
    }

    private Image[] setupMean() {
        Image[] images = new Image[2];
        try {
            images[0] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test1.ppm"));
            images[1] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test2.ppm"));
        } catch (IOException e) {
            System.out.println("Could not read file " + e.getMessage());
        }
        return images;
    }

    private Image[] setupMedianEven1() {
        final int length = 4;
        Image[] images = new Image[length];
        try {
            images[0] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test1.ppm"));
            images[1] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test2.ppm"));
            images[2] = images[0];
            images[3] = images[0];
        } catch (IOException e) {
            System.out.println("Could not read file " + e.getMessage());
        }
        return images;
    }

    private Image[] setupMedianEven2() {
        final int length = 4;
        Image[] images = new Image[length];
        try {
            images[0] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test1.ppm"));
            images[1] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test2.ppm"));
            images[2] = images[0];
            images[3] = images[1];
        } catch (IOException e) {
            System.out.println("Could not read file " + e.getMessage());
        }
        return images;
    }

    private Image[] setupMedianOdd() {
        final int length = 5;
        Image[] images = new Image[length];
        try {
            images[0] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test1.ppm"));
            images[1] = MeanImageMedian.readPPMImage(Paths.get("src", "tests", "test2.ppm"));
            images[2] = images[0];
            images[3] = images[1];
            images[4] = images[0];
        } catch (IOException e) {
            System.out.println("Could not read file " + e.getMessage());
        }
        return images;
    }

    private boolean imageEquals(Image a, Image b) {
        int height = (int) a.getHeight();
        int width = (int) a.getWidth();
        PixelReader pa = a.getPixelReader();
        PixelReader pb = b.getPixelReader();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (pa.getArgb(x, y) != pb.getArgb(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}