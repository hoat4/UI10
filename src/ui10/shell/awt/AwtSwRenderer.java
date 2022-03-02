package ui10.shell.awt;

import jdk.incubator.foreign.MemorySegment;
import ui10.base.Control;
import ui10.base.EventContext;
import ui10.base.RenderableElement;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.input.pointer.MouseEvent;
import ui10.shell.renderer.sw.SWRasterizer;

import java.awt.*;
import java.awt.image.*;
import java.util.List;

import static ui10.geom.Point.ORIGO;

public class AwtSwRenderer extends AWTRenderer{

    private final SWRasterizer swRasterizer = new SWRasterizer();

    @Override
    protected void initRoot(RenderableElement root) {
        swRasterizer.initRoot(root);
    }

    @Override
    protected void draw(Graphics2D g) {
        final Size size = new Size(640, 480);
        final int[] array = new int[size.width()*size.height()];
        MemorySegment segment = MemorySegment.ofArray(array);
        swRasterizer.render(new Rectangle(ORIGO, size), segment);

        DataBuffer buffer = new DataBufferInt(array, array.length);
        ColorModel colorModel = ColorModel.getRGBdefault();
        SampleModel sm = colorModel.createCompatibleSampleModel(size.width(), size.height());
        WritableRaster raster = Raster.createWritableRaster(sm, buffer, new Point(0, 0));
        BufferedImage image = new BufferedImage(colorModel, raster, false, null);
        g.drawImage(image, 0, 0, size.width()*2, size.height()*2, null);
    }

    @Override
    protected boolean captureMouseEvent(MouseEvent e, EventContext eventContext, List<Control> destinationList) {
        return false;
    }
}
