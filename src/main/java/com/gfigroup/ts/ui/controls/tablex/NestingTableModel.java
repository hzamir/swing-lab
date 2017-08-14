package com.gfigroup.ts.ui.controls.tablex;

import com.jidesoft.grid.NavigableTableModel;

import javax.swing.table.TableCellRenderer;

public interface NestingTableModel extends NestingSpanModel, NavigableTableModel, ChildTableModel
{
    TableCellRenderer decorateCellRenderer(TableCellRenderer renderer);
    TableCellRenderer decorateHeaderCellRenderer(TableCellRenderer renderer);

    TableModelCoordinates convertToNested(int row, int col);
    TableModelCoordinates convertToNested(TableModelCoordinates rowCol);
    TableModelCoordinates convertToMostNested(TableModelCoordinates rowCol);

    /**
     *
     * broadcast down chain intention to change the number of physical table rows
     * this gives each model in the chain (on the way back up) the opportunity to add
     * rows
     * @param tableRows
     * @return
     */
    void geometryChange(NestingTableModel originator, int tableRows);


}
