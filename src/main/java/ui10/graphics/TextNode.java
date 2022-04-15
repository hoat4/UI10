package ui10.graphics;

import ui10.base.Element;
import ui10.base.LayoutContext1;
import ui10.base.LayoutContext2;
import ui10.base.RenderableElement;
import ui10.binding2.ChangeEvent;
import ui10.binding2.ElementEvent;
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
    private String text = "";
    private TextStyle cachedTextStyle;

    public TextNode() {
    }

    public TextNode(String text) {
        if (text == null)
            text = "";
        this.text = text;
    }

    @Override
    protected Set<Property<?>> subscriptions() {
        return Set.of(TEXT_FILL_PROPERTY, FONT_SIZE_PROPERTY, FONT_WEIGHT_PROPERTY);
    }

    @Override
    protected void onPropertyChange(ElementEvent changeEvent) {
        super.onPropertyChange(changeEvent);
        if (changeEvent.property().equals(FONT_SIZE_PROPERTY) || changeEvent.property().equals(FONT_WEIGHT_PROPERTY))
            cachedTextStyle = null;
    }

    public String text() {
        return text;
    }

    public TextNode text(String text) {
        String prevText = this.text;
        if (text == null)
            text = "";
        this.text = text;
        dispatchElementEvent(new ChangeEvent<>(TEXT_PROPERTY, prevText, text));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(Property<T> prop) {
        if (prop.equals(TEXT_PROPERTY))
            return (T) text;
        else
            return super.getProperty(prop);
    }

    @Override
    public <T> void setProperty(Property<T> prop, T value) {
        if (prop.equals(TEXT_PROPERTY))
            text((String) value);
        else
            super.setProperty(prop, value);
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
        if (cachedTextStyle == null)
            cachedTextStyle = makeTextStyle(this);
        return cachedTextStyle;
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
