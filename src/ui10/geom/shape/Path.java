package ui10.geom.shape;

import ui10.geom.Point;
import ui10.geom.Transformation;

public interface Path {

    void iterate(PathConsumer consumer);

    abstract class PathConsumer {

        private Point first, last;
        private final Transformation transformation;
        private final Shape clip;

        public PathConsumer(Transformation transformation, Shape clip) {
            this.transformation = transformation;
            this.clip = clip;
        }

        public Transformation transformation() {
            return transformation;
        }

        /**
         * ezt nem kötelező figyelembe venni, csak ajánlás. Lehet null is.
         */
        public Shape clip() {
            return clip;
        }

        // returns transformed point (or null)
        public Point first() {
            return first;
        }

        // returns transformed point (or null)
        public Point last() {
            return last;
        }

        public void addPoint(Point p) {
            addTransformedPoint(transformation.transform(p));
        }

        public void addTransformedPoint(Point p) {
            addPointImpl(p);
            if (first == null)
                first = p;
            last = p;
        }

        protected abstract void addPointImpl(Point p);

        public void addSubpath(Path path) {
            path.iterate(this);
        }
    }
}
