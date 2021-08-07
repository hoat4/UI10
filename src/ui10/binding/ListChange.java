package ui10.binding;

import java.util.List;

public record ListChange<E>(int index, List<E> oldElements, List<E> newElements) {
}
