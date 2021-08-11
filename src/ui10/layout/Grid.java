package ui10.layout;

import ui10.binding.ObservableList;
import ui10.binding.ObservableListImpl;
import ui10.binding.ObservableScalar;
import ui10.geom.Point;
import ui10.geom.Rectangle;
import ui10.geom.Size;
import ui10.nodes.Layout;
import ui10.nodes.Node;
import ui10.nodes.Pane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Grid extends Pane {

    private final List<List<Cell>> rows;
    private final ObservableList<Node> children;
    private int cols;

    public Grid(List<List<Cell>> rows, List<Node> children, int cols) {
        this.rows = rows;
        this.children = new ObservableListImpl<>(children);
        this.cols = cols;
    }

    @Override
    protected ObservableScalar<? extends Node> paneContent() {
        return new Layout(children) {

            record GridLayout(int[] colWidths, int[] rowHeights, List<Size> sizes) {
            }

            @Override
            protected Size determineSize(BoxConstraints constraints) {
                GridLayout gl = computeLayout(constraints);

                int w = 0;
                for (int colWidth : gl.colWidths)
                    w += colWidth;

                int h = 0;
                for (int rowHeight : gl.rowHeights)
                    h += rowHeight;

                return new Size(w, h);
            }

            private GridLayout computeLayout(BoxConstraints constraints) {
                // TODO unbounded constraints
                List<Size> sizes = new ArrayList<>();
                for (Node n : children)
                    sizes.add(n.determineSize(constraints.withMinimum(Size.ZERO)));

                int[] colWidths = new int[cols];
                for (int i = 0; i < cols; i++) {
                    for (int j = 0; j < rows.size(); j++) {
                        List<Cell> row = rows.get(j);
                        if (row.size() < i + 1)
                            continue;
                        Size s = sizes.get(children.indexOf(row.get(i).node));
                        colWidths[i] = Math.max(colWidths[i], s.width());
                    }
                }

                int[] rowHeights = new int[rows.size()];
                for (int i = 0; i < rows.size(); i++) {
                    List<Cell> row = rows.get(i);
                    for (Cell cell : row) {
                        Size s = sizes.get(children.indexOf(cell.node));
                        rowHeights[i] = Math.max(rowHeights[i], s.height());
                    }
                }

                return new GridLayout(colWidths, rowHeights, sizes);
            }

            @Override
            protected void layout(Collection<?> updated) {
                GridLayout l = computeLayout(BoxConstraints.fixed(bounds.get().size()));

                int x, y = 0;
                for (int i = 0; i < rows.size(); i++) {
                    List<Cell> row = rows.get(i);
                    x = 0;
                    for (int j = 0; j < row.size(); j++) {
                         Node n = row.get(j).node;
                        n.bounds.set(new Rectangle(new Point(x, y), new Size(l.colWidths[j], l.rowHeights[i])));
                        x += l.colWidths[j];
                    }
                    y += l.rowHeights[i];
                }
            }
        }.asNodeObservable();
    }

    private static record Cell(Node node, int x, int y, int spanX, int spanY) {
    }

    public static class GridBuilder {

        private final List<List<Cell>> rows = new ArrayList<>();
        private final List<Node> children = new ArrayList<>();
        private int x, y, cols;

        public GridBuilder add(Node node, CellOption... options) {
            int spanX = 1, spanY = 1;
            for (CellOption o : options) {
                if (o instanceof Span span) {
                    if (spanX == 1)
                        spanX = span.spanX;
                    else if (span.spanX != 1)
                        throw new IllegalArgumentException("different horizontal spans: " + spanX + ", " + span.spanX);

                    if (spanY == 1)
                        spanY = span.spanY;
                    else if (span.spanY != 1)
                        throw new IllegalArgumentException("different vertical spans: " + spanY + ", " + span.spanY);
                } else
                    throw new UnsupportedOperationException(o.toString());
            }
            Cell cell = new Cell(node, x, y, spanX, spanY);
            for (int i = 0; i < spanY; i++) {
                List<Cell> row = row(y + i);
                while (row.size() < x + spanX)
                    row.add(null);
                for (int j = 0; j < spanX; j++)
                    if (row.set(x + j, cell) != null)
                        throw new IllegalArgumentException(rows + ", " + cell);
            }
            children.add(node);

            x += spanX;
            cols = Math.max(x, cols);

            return this;
        }

        public GridBuilder wrap() {
            x = 0;
            y++;
            return this;
        }

        private List<Cell> row(int row) {
            while (row >= rows.size())
                rows.add(new ArrayList<>());
            return rows.get(row);
        }

        public Grid build() {
            return new Grid(rows, children, cols);
        }
    }

    public interface CellOption {

        static CellOption span(int spanX, int spanY) {
            return new Span(spanX, spanY);
        }

        static CellOption spanX(int spanX) {
            return span(spanX, 1);
        }

        static CellOption spanY(int spanY) {
            return span(1, spanY);
        }
    }

    private record Span(int spanX, int spanY) implements CellOption {
    }
}
