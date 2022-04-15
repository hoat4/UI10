package ui10.decoration.css;

import ui10.decoration.DecorationContext;

public interface Expression<T> {

    T value(CSSContext context);

    record Constant<T>(T value) implements Expression<T> {
        @Override
        public T value(CSSContext context) {
            return value;
        }
    }

    record VarRef<T>(String name) implements Expression<T> {
        @Override
        public T value(CSSContext context) {
            return (T) context.variables.get(name);
        }
    }

    record LengthExpression(double value, Unit unit) implements Expression<Integer> {

        @Override
        public Integer value(CSSContext context) {
            if (unit == Unit.PX)
                return (int)Math.round(value);
            else
                throw new UnsupportedOperationException("unknown unit: "+unit);
        }
    }
}
