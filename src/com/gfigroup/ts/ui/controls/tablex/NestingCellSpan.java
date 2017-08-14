package com.gfigroup.ts.ui.controls.tablex;

import com.jidesoft.grid.CellSpan;

public class NestingCellSpan extends CellSpan
{

    private int myRow_;
    private int myCol_;

    public NestingCellSpan(int anchorRow, int anchorCol, int rowSpan, int columnSpan, int myRow, int myCol)
    {
        super(anchorRow, anchorCol, rowSpan, columnSpan);
        myRow_ = myRow;
        myCol_ = myCol;
    }

    // how is the coordinate querying the cellspan from the anchor point
    public void setMy(int myRow, int myCol)
    {
        myRow_ = myRow;
        myCol_ = myCol;
    }

    public int getMyRow()     { return myRow_;    }
    public int getMyCol()     { return myCol_;    }



}
