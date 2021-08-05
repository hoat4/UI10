package ui10.renderer.java2d;

import ui10.input.keyboard.KeyTypeEvent;
import ui10.input.keyboard.Keyboard;

import java.awt.event.KeyEvent;
import java.util.Optional;

import static java.awt.event.KeyEvent.*;

public class AWTKeyTypeEvent implements KeyTypeEvent {

    // private final ScalarProperty<Boolean> consumed = ScalarProperty.<Boolean>create().set(false);
    private final KeyEvent evt;

    public AWTKeyTypeEvent(KeyEvent keyEvent) {
        this.evt = keyEvent;
    }

    @Override
    public Keyboard.Symbol symbol() {
        if (evt.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
            return functionKeySymbol(evt.getExtendedKeyCode());
        else
            return new Keyboard.Symbol() {

                @Override
                public Optional<Keyboard.StandardSymbol> standardSymbol() {
                    return Optional.of(new Keyboard.StandardTextSymbol(new String(new char[]{evt.getKeyChar()})));
                }
            };
    }

    private Keyboard.Symbol functionKeySymbol(int code) {
        Keyboard.StandardFunctionSymbol s = switch(code) {
            case VK_LEFT -> Keyboard.StandardFunctionSymbol.LEFT;
            case VK_RIGHT -> Keyboard.StandardFunctionSymbol.RIGHT;
            case VK_DOWN -> Keyboard.StandardFunctionSymbol.DOWN;
            case VK_UP -> Keyboard.StandardFunctionSymbol.UP;
            default -> null;
        };
        return new Keyboard.Symbol() {
            @Override
            public Optional<Keyboard.StandardSymbol> standardSymbol() {
                return Optional.ofNullable(s);
            }
        };
    }
}
