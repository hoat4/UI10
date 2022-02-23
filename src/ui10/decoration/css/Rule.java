package ui10.decoration.css;

import ui10.geom.Insets;
import ui10.geom.Size;
import ui10.renderer.java2d.AWTTextStyle;
import ui10.base.Attribute;
import ui10.base.Element;
import ui10.base.Pane;
import ui10.base.RenderableElement;
import ui10.decoration.Border;
import ui10.decoration.DecorationContext;
import ui10.graphics.TextNode;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ui10.layout.Layouts.*;

public class Rule {

    private final Map<CSSProperty<?>, Object> map = new HashMap<>();

    private final List<Transition<?>> transitions = new ArrayList<>();

    private <T> void put(CSSProperty<T> prop, T value) {
        map.put(prop, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(CSSProperty<T> prop) {
        for (Transition<?> t : transitions)
            if (t.spec.property().equals(prop))
                return ((Transition<T>) t).value();

        return (T) map.get(prop);
    }

    void parseProperty(String name, CSSParser parser) {
        switch (name) {
            case "background" -> put(CSSProperty.background, parser.parseFill());
            case "color" -> put(CSSProperty.textColor, parser.parseFill());
            case "margin" -> parseInsets(parser, CSSProperty.marginTop, CSSProperty.marginRight,
                    CSSProperty.marginBottom, CSSProperty.marginLeft);
            case "padding" -> parseInsets(parser, CSSProperty.paddingTop, CSSProperty.paddingRight,
                    CSSProperty.paddingBottom, CSSProperty.paddingLeft);
            case "border-radius" -> put(CSSProperty.cornerRadius, parser.parseLength());
            case "min-width" -> put(CSSProperty.minWidth, parser.parseLength());
            case "min-height" -> put(CSSProperty.minHeight, parser.parseLength());
            case "border" -> put(CSSProperty.border, parser.parseBorder());
            case "font-size" -> put(CSSProperty.fontSize, parser.parseLength());
            case "transition" -> put(CSSProperty.transition, parser.parseTransitionList());
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
            default -> throw new CSSScanner.CSSParseException("invalid count of lengths: " + lengths);
        }
        put(topProp, lengths.get(0));
        put(rightProp, lengths.get(1));
        put(bottomProp, lengths.get(2));
        put(leftProp, lengths.get(3));
    }

    public void defaultsFrom(Rule other) {
        other.map.forEach(map::putIfAbsent);
    }

    private <T> void apply1(CSSProperty<T> prop, Consumer<T> consumer) {
        T value = get(prop);
        if (value != null)
            consumer.accept(value);
    }

    public void apply1(Element e, DecorationContext context) {
        apply1(CSSProperty.fontSize, fontSize -> ((TextNode) e).textStyle(AWTTextStyle.of(context.length(fontSize))));
        apply1(CSSProperty.textColor, textColor -> ((TextNode) e).fill(textColor.makeElement(context)));
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
        elem = prop2(List.of(CSSProperty.paddingTop, CSSProperty.paddingRight,
                CSSProperty.paddingBottom, CSSProperty.paddingLeft), elem, (e, padding) -> makePadding(e, padding, context));

        elem = prop2(CSSProperty.background, elem, (e, background) -> stack(background.makeElement(context), e));
        elem = prop2(CSSProperty.border, elem, (e, border) ->
                new Border(new Insets(context.length(border.len())), border.fill().makeElement(context), e));
        elem = prop2(CSSProperty.cornerRadius, elem, (e, cornerRadius) ->
                roundRectangle(context.length(cornerRadius), e));

        elem = prop2(List.of(CSSProperty.minWidth, CSSProperty.minHeight), elem, (e, minSizes) -> minSize(e, new Size(
                minSizes.get(0) == null ? 0 : context.length(minSizes.get(0)), // min-width
                minSizes.get(1) == null ? 0 : context.length(minSizes.get(1)) // min-height
        )));

        elem = prop2(List.of(CSSProperty.marginTop, CSSProperty.marginRight,
                CSSProperty.marginBottom, CSSProperty.marginLeft), elem, (e, margin) -> makePadding(e, margin, context));

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

    public void applyTransitionsOf(Element e) {
        List<TransitionSpec<?>> l = get(CSSProperty.transition);
        if (l == null) // ilyenkor törölni kéne
            return;

        if (!(e instanceof RenderableElement))
            throw new IllegalArgumentException("transition on transient element: " + e);

        for (TransitionSpec<?> t : l)
            handleTransitionSpec(e, t);
    }

    @SuppressWarnings("unchecked")
    private <T> void handleTransitionSpec(Element e, TransitionSpec<T> transitionSpec) {
        Transition<T> transition = null;
        for (Attribute a : e.attributes()) {
            if (a instanceof Transition && ((Transition<?>) a).spec.equals(transitionSpec)) {
                transition = (Transition<T>) a;
            }
        }

        T currentValue = (T) map.get(transitionSpec.property());

        if (transition == null) {
            transition = new Transition<>((Pane) e, transitionSpec, currentValue);
            e.attributes().add(transition);
        }

        boolean start = transition.setEnd(currentValue);
        transitions.add(transition);

        if (start) {
            Transition<T> t = transition;
            if (t.activeAnimation != null)
                t.activeAnimation.cancel(false);

            RenderableElement r = (RenderableElement) e;
            transition.activeAnimation = r.rendererData.eventLoop().beginAnimation(transition.spec.duration(), f -> {
                t.progress(f);
                r.requestLayout();
            });
        }
    }
}
