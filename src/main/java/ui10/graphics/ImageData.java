package ui10.graphics;

import java.nio.file.Path;

public class ImageData {

    public final Path path;

    private ImageData(Path path) {
        this.path = path;
    }

    public static ImageData of(Path path) {
        return new ImageData(path);
    }
}
