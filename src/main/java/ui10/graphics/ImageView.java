package ui10.graphics;

import ui10.base.Element;

public class ImageView extends Element {

    public final ImageData image;

    public ImageView(ImageData image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ImageView{" + "image=" + image + '}';
    }
}
