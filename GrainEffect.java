import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import BlendMode.*;

/**
 * This class overlays film grain to a still image or frames of a video.
 */
public class GrainEffect {
    /**
     * This is the master grain method. It provides a foundation for overloaded methods.
     * @param source An array list of image files, usually video frames, to have the grain overlay applied to.
     * @param grainFrames An array list of image files, to overlay blend over the source image files.
     * @param outputFolder An output folder location for the composite images.
     * @param opacity The opacity of the grain over the source.
     * @param durationMultiplier Multiplies the duration of the source to apply different grain frames to source images
     *                           (If a single image is used as a source, you can apply grain to it multiple times by
     *                           using this parameter).
     * @param maxThreads Hard limits the amount of threads used by the method.
     *                   If heap memory errors are occurring lower this number.
     *                   Performance has diminishing returns when maxThreads > physical CPU cores.
     *                   Default value in overloaded methods is 4.
     */
    private static void grain(ArrayList<File> source, ArrayList<File> grainFrames,
                              File outputFolder, double opacity, int durationMultiplier, int maxThreads) {

        ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);

        int length = source.size() * durationMultiplier;

        for (int i = 0; i < length; i++) {

            int grainSize = grainFrames.size();
            int sourceSize = source.size();

            int grainI = ((i % grainSize) + grainSize) % grainSize;
            int sourceI = ((i % sourceSize) + sourceSize) % sourceSize;
            int finalI = i;

            File grainFile = grainFrames.get(grainI);
            File sourceFile = source.get(sourceI);

            Runnable task = () -> grainProcessing(sourceFile, grainFile, finalI, outputFolder, opacity);

            executorService.execute(task);
        }
        executorService.shutdown();
    }

    /**
     * This is an overloaded method to add grain to a single source image.
     * @param source An image file to have the grain overlay applied to.
     * @param grainFrames An array list of image files, to overlay blend over the source image files.
     * @param outputFolder An output folder location for the composite images.
     * @param opacity The opacity of the grain over the source.
     * @param durationMultiplier Multiplies the duration of the source to apply different grain frames to source images
     *                           (If a single image is used as a source, you can apply grain to it multiple times by
     *                           using this parameter).
     * @param maxThreads Hard limits the amount of threads used by the method.
     *                   If heap memory errors are occurring lower this number.
     *                   Performance has diminishing returns when maxThreads > physical CPU cores.
     */
    public static void grain(File source, ArrayList<File> grainFrames,
                             File outputFolder, double opacity, int durationMultiplier, int maxThreads) {

        ArrayList<File> sourceArray = new ArrayList<>();
        sourceArray.add(source);

        grain(sourceArray, grainFrames, outputFolder, opacity, durationMultiplier, maxThreads);
    }
    /**
     * This is an overloaded method to add grain to a single source image,
     * when max threads is not specified (defaulted to 4).
     * @param source An image file to have the grain overlay applied to.
     * @param grainFrames An array list of image files, to overlay blend over the source image files.
     * @param outputFolder An output folder location for the composite images.
     * @param opacity The opacity of the grain over the source.
     * @param durationMultiplier Multiplies the duration of the source to apply different grain frames to source images
     *                           (If a single image is used as a source, you can apply grain to it multiple times by
     *                           using this parameter).
     */
    public static void grain(File source, ArrayList<File> grainFrames,
                             File outputFolder, double opacity, int durationMultiplier) {
        grain(source, grainFrames, outputFolder, opacity, durationMultiplier, 4);
    }


    /**
     * This is an overloaded method to add grain to an array of source images.
     * @param source An array list of image files, usually video frames, to have the grain overlay applied to.
     * @param grainFrames An array list of image files, to overlay blend over the source image files.
     * @param outputFolder An output folder location for the composite images.
     * @param opacity The opacity of the grain over the source.
     * @param maxThreads Hard limits the amount of threads used by the method.
     *                   If heap memory errors are occurring lower this number.
     *                   Performance has diminishing returns when maxThreads > physical CPU cores.
     */
    public static void grain(ArrayList<File> source, ArrayList<File> grainFrames,
                              File outputFolder, double opacity, int maxThreads) {
        grain(source, grainFrames, outputFolder, opacity, 1, maxThreads);
    }


    /**
     * This is an overloaded method to add grain to an array of source images,
     * when max threads is not specified (defaulted to 4).
     * @param source An array list of image files, usually video frames, to have the grain overlay applied to.
     * @param grainFrames An array list of image files, to overlay blend over the source image files.
     * @param outputFolder An output folder location for the composite images.
     * @param opacity The opacity of the grain over the source.
     */
    // grain method for video frames without threads given
    public static void grain(ArrayList<File> source, ArrayList<File> grainFrames,
                              File outputFolder, double opacity) {
        grain(source, grainFrames, outputFolder, opacity, 1, 4);
    }


    /**
     * This method provides the logic for individual composite image creation.
     * @param sourceFile An image file to have the grain overlay applied to.
     * @param grainFile An image file to overlay blend over the source image files.
     * @param i The iteration to name the output composite file.
     * @param outputFolder An output folder location for the composite images.
     * @param opacity The opacity of the grain over the source.
     */
    private static void grainProcessing(File sourceFile, File grainFile, int i, File outputFolder, double opacity) {
        try {
            BufferedImage source = ImageIO.read(sourceFile);
            BufferedImage grain = ImageIO.read(grainFile);
            File outputFile = new File(outputFolder, i + ".jpg");

            BufferedImage output = BlendMode.overlay(source, grain, opacity);

            ImageIO.write(output, "jpg", outputFile);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
