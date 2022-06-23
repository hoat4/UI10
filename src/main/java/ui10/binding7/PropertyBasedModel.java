package ui10.binding7;

import ui10.base.ElementModel;
import ui10.binding5.Parameterization;
import ui10.binding5.ReflectionUtil;

import java.util.EnumSet;
import java.util.Set;

public class PropertyBasedModel<P extends Enum<P>> extends ElementModel<PropertyBasedModel.PropertyBasedModelListener> {

    private final Set<P> dirty = EnumSet.noneOf(propertyEnumClass());

    public Set<P> dirtyProperties() {
        return dirty;
    }

    protected void invalidate(P property) {
        boolean notify = dirty.isEmpty();
        dirty.add(property);
        if (notify)
            listener().modelInvalidated();
    }

    @SuppressWarnings("unchecked")
    private Class<P> propertyEnumClass() {
        return (Class<P>) ReflectionUtil.rawType(
                Parameterization.ofRawType(getClass()).resolve(PropertyBasedModel.class.getTypeParameters()[0]));
    }

    public interface PropertyBasedModelListener extends ElementModelListener {

        void modelInvalidated();
    }
}
