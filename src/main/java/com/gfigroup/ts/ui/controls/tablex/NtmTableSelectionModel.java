package com.gfigroup.ts.ui.controls.tablex;

import com.jidesoft.grid.DefaultTableSelectionModel;
import com.jidesoft.grid.JideTable;

import java.util.Comparator;
import java.util.TreeSet;


public class NtmTableSelectionModel<T> extends DefaultTableSelectionModel
{
    protected int selectedCol_;
    protected TreeSet<TableModelCoordinates> selectedRows_;
    protected NestingTableModel topModel_;

    class MyComparator implements Comparator<TableModelCoordinates>
    {
        public int compare(TableModelCoordinates a, TableModelCoordinates b) {
            return b.row-a.row;
        }
    }

    
    public NtmTableSelectionModel(JideTable t)
    {
        topModel_= (NestingTableModel)t.getModel();
        selectedCol_ = -1;
        selectedRows_ = new TreeSet<TableModelCoordinates>(new MyComparator());
    }

    public void clearSelection() {
        selectedCol_= -1;
        selectedRows_.clear();
        super.clearSelection();
    }


    public TableModelCoordinates normalize(int row, int column)
    {
        return new TableModelCoordinates(topModel_, row, column);
    }

    
    public void addSelection(int row, int column)
    {
        if(topModel_.isNavigableAt(row,column)) {
            TableModelCoordinates tmc = normalize(row, column);
            selectedRows_.add(normalize(row,column));
            selectedCol_ = tmc.col;
            super.addSelection(row,column);
        }
    }

    public void removeSelection(int row, int column)
    {
        TableModelCoordinates tmc = normalize(row, column);
        selectedRows_.remove(tmc);
        if(selectedRows_.isEmpty())
            selectedCol_ = -1;
        super.removeSelection(row, column);
    }

    public void setSelection(int row, int column) {
        TableModelCoordinates tmc = normalize(row, column);
        selectedRows_.clear();
        selectedRows_.add(normalize(row,column));
        selectedCol_ = tmc.col;
        super.setSelection(row, column);
    }

    public boolean isSelected(int row, int column)
    {
        // optimize for nothing selected case
        if(selectedCol_ == -1)
            return false;
        TableModelCoordinates tmc = normalize(row, column);
        return selectedRows_.contains(tmc);
    }




}
