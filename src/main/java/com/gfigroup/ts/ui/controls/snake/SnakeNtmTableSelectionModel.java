package com.gfigroup.ts.ui.controls.snake;

import com.gfigroup.ts.ui.controls.tablex.NtmTableSelectionModel;
import com.gfigroup.ts.ui.controls.tablex.TableModelCoordinates;
import com.jidesoft.grid.JideTable;


public class SnakeNtmTableSelectionModel<T> extends NtmTableSelectionModel<T>
{
    public SnakeNtmTableSelectionModel(JideTable t)
    {
        super(t);
    }

    @Override
    public TableModelCoordinates normalize(int row, int column)
    {
        return topModel_.convertToNested(row, column);
    }

}
