package ui10.input.keyboard;

public sealed interface KeySymbol {

    record TextSymbol(String text) implements KeySymbol {
    }

    non-sealed interface FunctionSymbol extends KeySymbol {
    }

    enum StandardFunctionSymbol implements FunctionSymbol {
        LEFT, RIGHT, UP, DOWN, BACKSPACE, DELETE
    }

}
