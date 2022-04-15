package ui10.base;

import ui10.binding.ReadTransaction;

public class UIThread extends Thread {

    public ReadTransaction currentReadTransaction;

    public UIThread(Runnable r) {
        super(r, "UI Event Loop");
    }
}
