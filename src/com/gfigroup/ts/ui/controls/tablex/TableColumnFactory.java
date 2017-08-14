package com.gfigroup.ts.ui.controls.tablex;

import com.gfigroup.ts.ui.controls.basic.ColumnIdentifier;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TableColumnFactory implements ColumnIdentifier
{
    TableCellRenderer cellRenderer_;
    TableCellRenderer headerRenderer_;
    String columnName_;
    Object identifier_;
    int minWidth_ = -1;
    int prefWidth_ = -1;
    boolean visible_ = true;

    public TableColumnFactory(String colName)
    {
       columnName_ = colName;
    }

    public void setCellRenderer(TableCellRenderer cr)
    {
      cellRenderer_ = cr;
    }

    public void setHeaderRenderer(TableCellRenderer hr)
    {
      headerRenderer_ = hr;
    }

    public void setMinWidth(int minWidth)
    {
        minWidth_ =  minWidth;
    }


    public void setPreferredWidth(int width)
    {
         prefWidth_ = width;
    }
    
    public void setIdentifier(Object identifier) 
    {
        identifier_ = identifier;
    }

    public Object getIdentifier()
    {
        return identifier_;
    }

    void setVisible(boolean visible)
    {
       visible_ = visible;
    }

    boolean getVisible()
    {
        return visible_;
    }


    public TableColumn createColumn(int modelIndex)
    {
        TableColumn tc = new TableColumn(modelIndex);

        if(cellRenderer_ != null)
            tc.setCellRenderer(cellRenderer_);
        if(headerRenderer_ != null)
            tc.setHeaderRenderer(headerRenderer_);

        if(getVisible()) {
            if(minWidth_ >= 0)
                tc.setMinWidth(minWidth_);
            if(prefWidth_ >= 0)
                tc.setPreferredWidth(prefWidth_);
        } else {
            tc.setMinWidth(0);
            tc.setPreferredWidth(0);
            tc.setWidth(0);
        }

        tc.setHeaderValue(columnName_);
        tc.setIdentifier(identifier_);
        return tc;
    }

}
