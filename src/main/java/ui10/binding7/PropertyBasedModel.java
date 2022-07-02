package ui10.binding7;

import ui10.base.ElementModel;
import ui10.binding5.Parameterization;
import ui10.binding5.ReflectionUtil;

import java.util.Arrays;
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

    protected void invalidate(P... properties) {
        boolean notify = dirty.isEmpty();
        dirty.addAll(Arrays.asList(properties));
        if (notify)
            listener().modelInvalidated();
    }

    @SuppressWarnings("unchecked")
    private Class<P> propertyEnumClass() {
        return (Class<P>) PROPERTY_ENUM_CLASS_CV.get(getClass());
    }

    public interface PropertyBasedModelListener extends ElementModelListener {

        void modelInvalidated();
    }

    private static final ClassValue<Class<? extends Enum<?>>> PROPERTY_ENUM_CLASS_CV = new ClassValue<>() {
        @SuppressWarnings("unchecked")
        @Override
        protected Class<? extends Enum<?>> computeValue(Class<?> type) {
            return (Class<? extends Enum<?>>) ReflectionUtil.rawType(
                    Parameterization.ofRawType(type).resolve(PropertyBasedModel.class.getTypeParameters()[0]));
        }
    };
}
