package ui10.decoration.css;

abstract  class PropertyParser<T> {

    CSSScanner p;

    protected abstract T parse();
}
