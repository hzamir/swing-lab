package com.baliset.ui.controls.tablex;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;


public class NestingModelTable extends NavTable
{
    public NestingModelTable(NestingTableModel tm, TableColumnRepository tcr)
    {
        super(tm, tcr, new DefaultTableColumnModel());
        tm.setModelColumnCount(tcr.size());
        TableColumnModel columnModel = getColumnModel();

        //----- Populate the column model from the column repository
        // we do it this way because SnakeTables use column repositories to dynamically build the column model
        int i = 0;
        for (TableColumnFactory cf : tcr.getColumnFactories())
        {
            TableColumn tc = cf.createColumn(i++);
            tc.setCellRenderer(tm.decorateCellRenderer(tc.getCellRenderer()));
            tc.setHeaderRenderer(tm.decorateHeaderCellRenderer(tc.getHeaderRenderer()));
            tc.setResizable(true);
            columnModel.addColumn(tc);
        }
    }


    @Override
    public void createDefaultTableSelectionModel()
    {
        setTableSelectionModel(new NtmTableSelectionModel(this));
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col)
    {
        Component comp = super.prepareRenderer(renderer, row, col);

        if(customPrepareRenderer_) {

            if (shouldCellBeHighlighted(row, col)) {
                comp.setBackground(Color.yellow);
                comp.setForeground(Color.black);
            } else {
                //even index, selected or not selected
                Color bg = comp.getBackground();
                comp.setBackground((row & 1) == 0 ? bg : darkenBy(bg, 0.9));
            }
        }
        return comp;
    }

    

}
