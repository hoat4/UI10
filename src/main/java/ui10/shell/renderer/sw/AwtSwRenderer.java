package ui10.shell.renderer.sw;

import jdk.incubator.foreign.MemorySegment;
import ui10.base.*;
import ui10.controls.TextElement;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.graphics.ColorFill;
import ui10.graphics.LinearGradient;
import ui10.graphics.Opacity;
import ui10.shell.awt.AWTDesktop;
import ui10.shell.awt.AWTRenderer;
import ui10.shell.renderer.sw.SWRasterizer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.*;
import java.util.List;

import static ui10.geom.Point.ORIGO;

public class AwtSwRenderer extends AWTRenderer {

    private final SWRasterizer swRasterizer = new SWRasterizer();

    public AwtSwRenderer(AWTDesktop desktop) {
        super(desktop);
    }

    @Override
    protected void initRoot(Element root) {
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
    protected boolean captureMouseEvent(ui10.geom.Point point, List<Element> destinationList) {
        return false;
    }

    @Override
    public ViewProvider createViewProvider() {
        return new AwtSwViewProvider();
    }

    private static class AwtSwViewProvider implements ViewProvider {

        @Override
        public ViewProviderResult makeView(Element model) {
            return switch (model) {
                case ColorFill c->NoViewResult.NO_VIEW;
                case LinearGradient c->NoViewResult.NO_VIEW;
                case Opacity c->NoViewResult.NO_VIEW;
                case Container c->NoViewResult.NO_VIEW;
                case LayoutElement c->NoViewResult.NO_VIEW;
                case TextElement c->new ViewResult(new SWTextElement(c));
                default -> NoViewResult.UNKNOWN_ELEMENT;
            };
        }
    }
}
