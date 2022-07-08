package ui10.shell.renderer.java2d;

import ui10.base.LayoutContext1;
import ui10.geom.Size;
import ui10.graphics.ImageView;
import ui10.layout.BoxConstraints;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class J2DImageView extends J2DRenderableElement<ImageView> {

    private final BufferedImage image;

    public J2DImageView(J2DRenderer renderer, ImageView node) {
        super(renderer, node);

        try (InputStream in = Files.newInputStream(node.image.path)) {
            this.image = ImageIO.read(in);
        } catch (IOException e) {
            // ilyenkor lehetne valami placeholder kirakni helyette
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        return new Size(image.getWidth(), image.getHeight());
    }

    @Override
    protected void validateImpl() {
    }

    @Override
    protected void drawImpl(Graphics2D g) {
        g.drawImage(image, shape.getBounds().x, shape.getBounds().y, null);
    }
}
