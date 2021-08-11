// InterfaceExperiment/graphics-ból másolva

/*

package ui10.image;

import jdk.incubator.foreign.MemorySegment;
import ui10.geom.Size;

import java.nio.ByteOrder;

public class RGBAImage implements WriteableImageBuffer<RGBColor> {

    private final MemorySegment segment;
    private final Size size;
    private final ByteOrder byteOrder;
    private final int stride;
    private final ColorSpace<RGBColor> colorSpace;

    public RGBAImage(MemorySegment segment, Size size, ByteOrder byteOrder, ColorSpace<RGBColor> colorSpace) {
        this.segment = segment;
        this.size = size;
        this.byteOrder = byteOrder;
        this.colorSpace = colorSpace;
        stride = size.w * 4;
    }

    @Override
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public Size size() {
        return size;
    }

    @Override
    public PixelFormat<RGBColor> pixelFormat() {
        return null;
    }

    @Override
    public RGBColor colorAt(int x, int y) {
        return RGBColor.ofIntRGBA(MemoryAccess.getIntAtOffset(segment, x + y * stride, byteOrder), colorSpace);
    }

    @Override
    public void setPixel(int x, int y, RGBColor fill) {
        MemoryAccess.setIntAtOffset(segment, x + y * stride, byteOrder, fill.toIntRGBA());
    }

    @Override
    public void setPixels(int x, int y, ImageBuffer<RGBColor> pixels) {
        Size s = pixels.size();
        if (x < 0 || y < 0 || x + s.w >= this.size.w || y + s.h >= this.size.h)
            throw new IllegalArgumentException();

        if (pixels instanceof RGBAImage img) {
            if (s.w == this.size.w)
                segment.asSlice(y * stride, img.size.h * stride).copyFrom(img.segment);
            else
                for (int i = 0; i < s.h; i++)
                    segment.asSlice(x + (y + i) * stride, img.stride).copyFrom(img.segment.asSlice(i * img.stride, img.stride));
        } else
            for (int i = 0; i < s.h; i++)
                for (int j = 0; j < s.w; j++)
                    setPixel(x + j, y + i, pixels.colorAt(j, i));
    }
}
*/