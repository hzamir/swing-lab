package com.baliset.ui.controls.snake;

import com.baliset.ui.controls.tablex.NtmTableSelectionModel;
import com.baliset.ui.controls.tablex.TableModelCoordinates;
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
