package ui10.geom.shape;

import ui10.geom.Insets;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;

public interface Shape {

    Shape NULL = Rectangle.of(Size.ZERO);

    Rectangle bounds();

    Path outline();

    Shape translate(Point point);

    Shape withInnerInsets(Insets insets); // inkább szög->hossz függvény kéne (Function<Fraction, Integer> ?)

    Shape withOuterInsets(Insets insets);

    Shape unionWith(Shape other);

    Shape intersectionWith(Shape other);


}
