package com.gfigroup.ts.ui.controls.tablex;

import javax.swing.table.TableModel;


// used in coordinate conversions to include identity of the matching table
// this is needed for coordinate conversions to aggegrated tables, so is included everywhere
public class TableModelCoordinates
{
    public TableModelCoordinates(TableModel tm, int r,  int c)
    {
        col = c;
        row = r;
        model = tm;
    }

    final public TableModel model;
    final public int row;
    final public int col;

}
