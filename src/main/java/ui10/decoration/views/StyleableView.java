package ui10.decoration.views;

import ui10.base.Element;
import ui10.binding5.ReflectionUtil;
import ui10.decoration.Style;
import ui10.decoration.StyleableContainer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public abstract class StyleableView<M extends Element, D extends Style>
        extends StyleableContainer<D> {

    // ennek lehet hogy inkább protectednek kéne lennie, de DecoratorElementnek kell egyelőre
    public final M model;

    // a model most így duplikáltan van tárolva, mert parent is tartalmazza

    public StyleableView(M model) {
        this.model = model;
    }

    @Override
    public void initParent(Element parent) {
        if (parent != model)
            throw new IllegalArgumentException();
        super.initParent(model);
    }
}
