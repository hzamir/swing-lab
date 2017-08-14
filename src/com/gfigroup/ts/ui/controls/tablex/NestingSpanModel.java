package com.gfigroup.ts.ui.controls.tablex;

import com.jidesoft.grid.SpanModel;


public interface NestingSpanModel extends SpanModel
{
    NestingCellSpan getNestedCellSpanAt(TableModelCoordinates tmc);
    void setModelColumnCount(int c);
    int getModelColumnCount();
}
