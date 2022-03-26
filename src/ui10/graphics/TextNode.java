package ui10.graphics;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.RenderableElement;
import ui10.binding2.Property;
import ui10.decoration.DecorationContext;
import ui10.decoration.Fill;
import ui10.font.TextStyle;
import ui10.geom.Size;
import ui10.geom.shape.Shape;
import ui10.layout.BoxConstraints;
import ui10.shell.renderer.java2d.AWTTextStyle;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class TextNode extends RenderableElement {

    public static final Property<Integer> FONT_SIZE_PROPERTY = new Property<>(true);
    public static final Property<Fill> TEXT_FILL_PROPERTY = new Property<>(true);
    public static final Property<FontWeight> FONT_WEIGHT_PROPERTY = new Property<>(true);
    public static final Property<String> TEXT_PROPERTY = new Property<>(true, "");

    public TextLayout textLayout;

    private Fill fill;
    private Element fillElem;

    public TextNode() {
    }

    public TextNode(String text) {
        text(text);
    }

    @Override
    protected Set<Property<?>> subscriptions() {
        return Set.of(TEXT_FILL_PROPERTY);
    }

    public String text() {
        return getProperty(TEXT_PROPERTY);
    }

    public TextNode text(String text) {
        if (text == null)
            text = "";
        setProperty(TEXT_PROPERTY, text);
        return this;
    }

    public Element textFill() {
        Fill tf = getProperty(TEXT_FILL_PROPERTY);
        Objects.requireNonNull(tf);
        if (!Objects.equals(fill, tf)) {
            fillElem = tf.makeElement(new DecorationContext()); // TODO
            fill = tf;
        }
        return fillElem;
    }

    public TextStyle textStyle() {
        return makeTextStyle(this);
    }

    public static TextStyle makeTextStyle(Element e) {
        return AWTTextStyle.of(e.getProperty(FONT_SIZE_PROPERTY), e.getProperty(FONT_WEIGHT_PROPERTY) == FontWeight.BOLD);
    }

    @Override
    protected void enumerateStaticChildren(Consumer<Element> consumer) {
        consumer.accept(textFill());
    }

    @Override
    protected Size preferredSizeImpl(BoxConstraints constraints, LayoutContext1 context) {
        // TODO mit csináljunk, ha nem stimmel?
        return constraints.clamp(textStyle().textSize(text()).size());
    }

    @Override
    protected void onShapeApplied(Shape shape) {
        LayoutContext2.ignoring().placeElement(textFill(), shape);
    }

    @Override
    public String elementName() {
        return null; // should return an element name?
    }
}
