package ui10.ui6.decoration.css;

import ui10.ui6.decoration.BorderSpec;
import ui10.ui6.decoration.Fill;

class CSSProperty<T> {

    static final CSSProperty<Fill> textColor = new CSSProperty<>();
    static final CSSProperty<Length> fontSize = new CSSProperty<>();

    static final CSSProperty<Length> marginTop = new CSSProperty<>();
    static final CSSProperty<Length> marginRight = new CSSProperty<>();
    static final CSSProperty<Length> marginBottom = new CSSProperty<>();
    static final CSSProperty<Length> marginLeft = new CSSProperty<>();

    static final CSSProperty<Length> paddingTop = new CSSProperty<>();
    static final CSSProperty<Length> paddingRight = new CSSProperty<>();
    static final CSSProperty<Length> paddingBottom = new CSSProperty<>();
    static final CSSProperty<Length> paddingLeft = new CSSProperty<>();

    static final CSSProperty<Fill> background = new CSSProperty<>();
    static final CSSProperty<Length> cornerRadius = new CSSProperty<>();
    static final CSSProperty<Length> minWidth = new CSSProperty<>();
    static final CSSProperty<Length> minHeight = new CSSProperty<>();
    static final CSSProperty<BorderSpec> border = new CSSProperty<>();
}
