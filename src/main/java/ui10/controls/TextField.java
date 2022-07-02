package ui10.controls;

public class TextField extends InputField<TextView, TextView.StringContentPoint> {

    public TextField() {
        super(new TextView());
    }

    public TextField(String initialValue) {
        super(new TextView(initialValue));
    }

    public String text() {
        return content.text();
    }

    public void text(String text) {
        content.text(text);
    }
}
