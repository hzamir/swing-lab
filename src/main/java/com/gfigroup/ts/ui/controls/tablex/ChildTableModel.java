package com.gfigroup.ts.ui.controls.tablex;


public interface ChildTableModel
{
    void setParent(ChildTableModel model);
    ChildTableModel getParent();
    TableModelCoordinates convertFromNested(TableModelCoordinates rowCol);
    TableModelCoordinates convertToLeastNested(TableModelCoordinates rowCol);
}
