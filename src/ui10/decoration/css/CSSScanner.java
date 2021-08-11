package ui10.decoration.css;

import java.io.IOException;
import java.io.Reader;

class CSSScanner {

    private final Reader reader;
    int next;

    public CSSScanner(Reader reader) {
        this.reader = reader;

        try {
            next = reader.read();
        } catch (IOException e) {
            throw new CSSParseException(e);
        }
    }

    public int take() {
        int c = next;
        if (c == -1)
            throw new CSSParseException("EOF");

        try {
            next = reader.read();
        } catch (IOException e) {
            throw new CSSParseException(e);
        }
        return c;
    }

    public void expectAndSkipWhitespaces() {
        if (!Character.isWhitespace(next))
            throw new CSSParseException("expected whitespace, but got '" + chToString(next) + "'");
        while (Character.isWhitespace(next))
            take();
    }

    public void skipWhitespaces() {
        while (Character.isWhitespace(next))
            take();
    }

    public void expect(String s) {
        for (int i = 0; i < s.length(); i++)
            expect(s.charAt(i));
    }

    public void expect(int e) {
        int a = take();
        if (a != e)
            throw new CSSParseException("expected '" + chToString(e) + "', but got " + chToString(a));
    }

    public String skipWhitespaceAndReadIdentifier() {
        skipWhitespaces();
        return readIdentifier();
    }

    public String readIdentifier() {
        var sb = new StringBuilder();
        while (Character.isJavaIdentifierPart(next) || next == '-')
            sb.append((char) take());
        if (sb.isEmpty())
            throw new CSSParseException("expected identifer, but got " + chToString(next));
        return sb.toString();
    }

    public void expectIdentifier(String e) {
        String a = readIdentifier();
        if (!a.equals(e))
            throw new CSSParseException("expected '" + e + "', but got '" + a + "'");
    }

    public String readPossibleChars(String possibleCharacters) {
        var sb = new StringBuilder();
        while (possibleCharacters.indexOf(next) != -1)
            sb.append((char) take());
        if (sb.isEmpty())
            throw new CSSParseException("expected one of '" + possibleCharacters + "' characters, but got " + chToString(next));
        return sb.toString();
    }

    public String readPossibleCharsOrEmpty(String possibleCharacters) {
        var sb = new StringBuilder();
        while (possibleCharacters.indexOf(next) != -1)
            sb.append((char) take());
        return sb.toString();
    }

    public static String chToString(int c) {
        return c == -1 ? "EOF" : "'" + ((char) c) + "' (" + c + ")";
    }


    public static class CSSParseException extends RuntimeException {
        public CSSParseException(String message) {
            super(message);
        }

        public CSSParseException(Throwable cause) {
            super(cause);
        }
    }
}