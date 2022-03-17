package com.cinua.spacetrader.view;

import com.glabs.tables.SimpleTable;

public interface GameView{
    void msgOut(String msg);
    String msgIn(String prompt);

    void tableOut(SimpleTable table);
}
