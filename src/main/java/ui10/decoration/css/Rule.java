package ui10.decoration.css;

import ui10.base.*;
import ui10.controls.Label;
import ui10.controls.TableView;
import ui10.decoration.BorderSpec;
import ui10.geom.Fraction;
import ui10.geom.Insets;
import ui10.layout.Grid;
import ui10.layout.LinearLayout;
import ui10.decoration.DecorationContext;
import ui10.graphics.TextNode;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ui10.layout.Layouts.*;

public class Rule {

    public Selector selector;

    Object[] props = new Object[8];
    Object[] dcbProps = new Object[CSSProperty.DCB_PROPS];
    public int dcbMask;

    private final List<Transition<?>> transitions = new ArrayList<>();

    private <T> void put(CSSProperty<T> prop, T value) {
        if (prop.dcbIndex != -1) {
            dcbMask |= 1 << prop.dcbIndex;
            dcbProps[prop.dcbIndex] = value;
            return;
        }

        for (int i = 0; i < props.length; i += 2) {
            if (props[i] == null || props[i].equals(prop)) {
                props[i] = prop;
                props[i + 1] = value;
                return;
            }
        }

        int i = props.length;
        props = Arrays.copyOf(props, i * 3);
        props[i] = prop;
        props[i + 1] = value;
    }

    @SuppressWarnings("unchecked")
    <T> T get(CSSProperty<T> prop) {
        for (Transition<?> t : transitions)
            if (t.spec.property().equals(prop))
                return ((Transition<T>) t).value();

        if (prop.dcbIndex != -1)
            return (T) dcbProps[prop.dcbIndex];

        for (int i = 0; i < props.length; i += 2)
            if (props[i] == null)
                break;
            else if (props[i].equals(prop))
                return (T) props[i + 1];
        return null;
    }

    void parseProperty(String name, CSSParser parser) {
        switch (name) {
            case "background" -> put(CSSProperty.background, parser.parseFill());
            case "color" -> put(CSSProperty.textColor, parser.parseFill());
            case "margin" -> parseInsets(parser, CSSProperty.marginTop, CSSProperty.marginRight,
                    CSSProperty.marginBottom, CSSProperty.marginLeft);
            case "padding" -> parseInsets(parser, CSSProperty.paddingTop, CSSProperty.paddingRight,
                    CSSProperty.paddingBottom, CSSProperty.paddingLeft);
            case "border-radius" -> {
                Length l = parser.parseLength();
                put(CSSProperty.topLeftCornerRadius, l);
                put(CSSProperty.topRightCornerRadius, l);
                put(CSSProperty.bottomLeftCornerRadius, l);
                put(CSSProperty.bottomRightCornerRadius, l);
            }
            case "border-top-left-radius" -> put(CSSProperty.topLeftCornerRadius, parser.parseLength());
            case "border-top-right-radius" -> put(CSSProperty.topRightCornerRadius, parser.parseLength());
            case "border-bottom-left-radius" -> put(CSSProperty.bottomLeftCornerRadius, parser.parseLength());
            case "border-bottom-right-radius" -> put(CSSProperty.bottomRightCornerRadius, parser.parseLength());
            case "min-width" -> put(CSSProperty.minWidth, parser.parseLength());
            case "min-height" -> put(CSSProperty.minHeight, parser.parseLength());
            case "border" -> {
                BorderSpec b = parser.parseBorder();
                // így megfelel az öröklődés CSS specnek?
                put(CSSProperty.borderTop, b);
                put(CSSProperty.borderRight, b);
                put(CSSProperty.borderBottom, b);
                put(CSSProperty.borderLeft, b);
            }
            case "border-top" -> put(CSSProperty.borderTop, parser.parseBorder());
            case "border-right" -> put(CSSProperty.borderRight, parser.parseBorder());
            case "border-bottom" -> put(CSSProperty.borderBottom, parser.parseBorder());
            case "border-left" -> put(CSSProperty.borderLeft, parser.parseBorder());
            case "font-size" -> put(CSSProperty.fontSize, parser.parseLength());
            case "transition" -> put(CSSProperty.transition, parser.parseTransitionList());
            case "gap" -> put(CSSProperty.gap, parser.parseLength());
            case "text-align" -> put(CSSProperty.textAlign, parser.parseTextAlign());
            case "font-weight" -> put(CSSProperty.fontWeight, parser.parseFontWeight());
            case "flex-grow" -> put(CSSProperty.flexGrow, parser.parseNumberAsFraction());

            case "row-height" -> put(CSSProperty.rowHeight, parser.parseLength());
            case "cell-separator" -> put(CSSProperty.cellSeparator, parser.parseFill());

            default -> throw new UnsupportedOperationException("unknown CSS property: " + name);
        }
    }

    private void parseInsets(CSSParser parser, CSSProperty<Length> topProp, CSSProperty<Length> rightProp,
                             CSSProperty<Length> bottomProp, CSSProperty<Length> leftProp) {
        List<Length> lengths = parser.parseLengths();
        switch (lengths.size()) {
            case 1 -> lengths = List.of(lengths.get(0), lengths.get(0), lengths.get(0), lengths.get(0));
            case 2 -> lengths = List.of(lengths.get(0), lengths.get(1), lengths.get(0), lengths.get(1));
            case 4 -> {
            }
            default -> throw parser.scanner.new CSSParseException("invalid count of lengths: " + lengths);
        }
        put(topProp, lengths.get(0));
        put(rightProp, lengths.get(1));
        put(bottomProp, lengths.get(2));
        put(leftProp, lengths.get(3));
    }

    public void putAll(Rule other) {
        if (other.props[0] != null) {
            for (int i = 0; i < other.props.length; i += 2)
                if (other.props[i] == null)
                    break;
                else
                    putHelper(other, i);
        }

        if (other.dcbMask != 0)
            for (int i = 0; i < other.dcbProps.length; i++) {
                Object o = other.dcbProps[i];
                if (o != null) {
                    dcbProps[i] = o;
                    dcbMask |= 1 << i;
                }
            }
    }

    @SuppressWarnings("unchecked")
    private <T> void putHelper(Rule other, int i) {
        this.put((CSSProperty<T>) other.props[i], (T) other.props[i + 1]);
    }

    private <T> void apply1(CSSProperty<T> prop, Consumer<T> consumer) {
        T value = get(prop);
        // this should not check != null, because then it can't reset properties
        if (value != null)
            consumer.accept(value);
    }

    @SuppressWarnings("Convert2MethodRef")
    public void apply1(Element e, DecorationContext context) {
        apply1(CSSProperty.fontSize, len -> e.setProperty(TextNode.FONT_SIZE_PROPERTY, context.length(len)));
        apply1(CSSProperty.textColor, color -> e.setProperty(TextNode.TEXT_FILL_PROPERTY, color));
        apply1(CSSProperty.fontWeight, weight -> e.setProperty(TextNode.FONT_WEIGHT_PROPERTY, weight));
        apply1(CSSProperty.textAlign, textAlign -> e.setProperty(Label.TEXT_ALIGN_PROPERTY, textAlign));
        apply1(CSSProperty.gap, gap -> e.setProperty(Grid.GAP_PROPERTY, context.length(gap)));
        apply1(CSSProperty.flexGrow, h -> e.setProperty(LinearLayout.GROW_FACTOR, h));

        apply1(CSSProperty.rowHeight, h -> e.setProperty(TableView.ROW_HEIGHT_PROPERTY, context.length(h)));
        apply1(CSSProperty.cellSeparator, f -> e.setProperty(TableView.CELL_SEPARATOR_PROPERTY, f));
    }

    private <T> Element prop2(CSSProperty<T> prop, Element e, BiFunction<Element, T, Element> f) {
        T value = get(prop);
        if (value != null)
            return f.apply(e, value);
        else
            return e;
    }

    private <T> Element prop2(List<CSSProperty<T>> prop, Element e, BiFunction<Element, List<T>, Element> f) {
        List<T> values = prop.stream().map(this::get).collect(Collectors.toList());
        if (values.stream().anyMatch(Objects::nonNull))
            return f.apply(e, values);
        else
            return e;
    }

    public Element apply2(Element elem, DecorationContext context) {
        if (dcbMask != 0)
            return new DecorBox(elem, this, context);
        else
            return elem;
    }

    private Element makePadding(Element element, List<Length> lengths, DecorationContext context) {
        return padding(element, new Insets(
                lengths.get(0) == null ? 0 : context.length(lengths.get(0)), // top
                lengths.get(1) == null ? 0 : context.length(lengths.get(1)), // right
                lengths.get(2) == null ? 0 : context.length(lengths.get(2)), // bottom
                lengths.get(3) == null ? 0 : context.length(lengths.get(3))  // left
        ));
    }

    private Element makeRoundRect(Element element, List<Length> radiuses, DecorationContext context) {
        return roundRectangle(
                radiuses.get(0) == null ? 0 : context.length(radiuses.get(0)), // top left
                radiuses.get(1) == null ? 0 : context.length(radiuses.get(1)), // top right
                radiuses.get(2) == null ? 0 : context.length(radiuses.get(2)), // bottom left
                radiuses.get(3) == null ? 0 : context.length(radiuses.get(3)),  // bottom right
                element
        );
    }

    public void applyTransitionsOf(Element e) {
        List<TransitionSpec<?>> l = get(CSSProperty.transition);
        if (l == null) // ilyenkor törölni kéne
            return;

        if (!(e instanceof EnduringElement ee))
            throw new IllegalArgumentException("transition on transient element: " + e);

        for (TransitionSpec<?> t : l)
            handleTransitionSpec(e, t);
    }

    @SuppressWarnings("unchecked")
    private <T> void handleTransitionSpec(Element e, TransitionSpec<T> transitionSpec) {
        TransitionSpec.TransitionKey<T> transitionKey = new TransitionSpec.TransitionKey<>(transitionSpec.property());
        Transition<T> transition = e.getProperty(transitionKey);

        T currentValue = (T) get(transitionSpec.property());

        if (transition == null) {
            transition = new Transition<>((EnduringElement) e, transitionSpec, currentValue);
            e.setProperty(transitionKey, transition);
        }

        boolean start = transition.setEnd(currentValue);
        transitions.add(transition);

        if (start) {
            Transition<T> t = transition;
            if (t.activeAnimation != null)
                t.activeAnimation.cancel(false);

            EnduringElement r = (EnduringElement) e;
            if (r.uiContext() == null) {
                t.progress(Fraction.WHOLE);
            } else {
                transition.activeAnimation = r.uiContext().eventLoop().beginAnimation(transition.spec.duration(), f -> {
                    System.out.println(f);
                    t.progress(f);
                    r.invalidateDecoration();
                });
            }
        }
    }
}
