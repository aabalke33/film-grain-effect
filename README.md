# film-grain-effect
Overlays film grain to a still image or frames of a video.

This package applies a film grain image overlay to video frames or a still image.

REQUIRES [JAVA BLEND MODES PACKAGE](https://github.com/aabalke33/blend-modes/).

The intensity of the film grain can be controlled by means of an opacity parameter that is passed into the functions. See Usage for more information.
Multithreading is enabled by default.
Source                 |  Grain   |  Composite (100%)      |  Composite (33% Realistic)        
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
![source](https://github.com/aabalke33/film-grain-effect/assets/22086435/69265ff0-5ca4-4d51-9060-bae7855371ca)  |  ![grain](https://github.com/aabalke33/film-grain-effect/assets/22086435/c3935ffb-fd8a-49ee-9677-d83d3c3f0dfc)  |  ![100](https://github.com/aabalke33/film-grain-effect/assets/22086435/489bcfe3-a88e-48f4-9283-3529bf1e156b)  |  ![33](https://github.com/aabalke33/film-grain-effect/assets/22086435/d0a14051-f850-4cbb-9cc7-4c74c07f48cd)

## Usage
The grain methods take image data expressed as BufferedImage(s). An example is provided below for standards method of creating and inputting parameters.

Methods are overloaded. Allows methods for a single source image or an array of source images. Method overloading to provide an inputted max thread count is included as well (by default this package uses 4).

```java
// With maxThreads
  // For Single Image
  videoEffects.grain(sourceImage, grain, outputFolder, opacity, durationMultiplier, maxThreads);

  // For Array of Images
  videoEffects.grain(sourceImages, grain, outputFolder, opacity, maxThreads);

// Without maxThreads (Default 4)
  // For Single Image
  videoEffects.grain(sourceImage, grain, outputFolder, opacity, durationMultiplier);

  // For Array of Images
  videoEffects.grain(sourceImages, grain, outputFolder, opacity);

```

## Important Considerations
1. The current package requires an image file or array of image files to be inputted. A video file directly is not supported at this time.
2. Grain is anchored to top left corner, and does not scale with source image(s). If image(s) are larger than grain an error will occur.
3. Has to be 8 Bit per Channel Image(s).
4. Requires Java Blend Modes Package.

## Example

```java
import GrainEffect.GrainEffect;

import java.io.File;
import java.util.*;

public class Example {
    public static void main(String[] args) throws IOException {

        // File and Folder Paths as Strings
        String sourceFolder = "./resources/source/";
        String grainFolder = "./resources/grain/";
        String outputPath = "./resources/output/";

        // Get all image files in each folder and add them to an ArrayList of files
        ArrayList<File> sourceFrames = new ArrayList<>(Arrays.asList(
                Objects.requireNonNull(new File(sourceFolder).listFiles())));
        ArrayList<File> grainFrames = new ArrayList<>(Arrays.asList(
                Objects.requireNonNull(new File(grainFolder).listFiles())));

        // Create File object of output folder
        File outputFolder = new File(outputPath);

        // Grain Method: ArrayList of source images, grain images and File for output Folder.
        // ".3" grain opacity and 8 Threads for multithreading
        GrainEffect.grain(sourceFrames, grainFrames, outputFolder, .3, 8);
    }
}
```

## FAQ
1. **Why do I get a Memory Heap Error?** You have too many threads. Lower maxThreads until stable. The multithreading gains significantly diminish if maxThreads is greater than the Physical CPU Core count. maxThreads defaults to 4 since this is the average amount of CPU cores at creation.
2. **How does the durationMultiplier work?** The durationMultiplier increases the output duration (in frames) by the source duration (in frames) * durationMultiplier. This is useful if you want to create a clip of a still image for 24 frames and you want the grain to overlay each frame. In this situation, the multiplier would be 24. If the private master grain method is accessed and the durationMultiplier set on an Array of images, the number of output frames will be the number of source frames * the multipler.

## Roadmap
- Add support for video files directly instead of relying on frame sequences
- Add support for grain image resizing to the source
- Support 16bit and 24bit images

## Notes for Nerds
The original unpublished implementation used Executors incorrectly, required threads and subprocesses to be manually entered, and was terrible at abstracting multithreading. This was not only hard to read but also 1.45x slower.
