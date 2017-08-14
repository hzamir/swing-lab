package com.baliset.tabledemo;

import com.baliset.data.Item;
import com.baliset.ui.controls.tablex.DelegatingTableModel;
import com.baliset.ui.controls.tablex.DepthTableModel;
import com.baliset.ui.controls.tablex.TableModelCoordinates;
import com.jidesoft.grid.CellSpan;
import com.jidesoft.grid.SpanModel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;



public class MyTableModel<T> extends DelegatingTableModel implements DepthTableModel, SpanModel
{
    private final List<T> itemsList = new ArrayList<T>();
    private String name_;
    private boolean expanded_ = false;

    public MyTableModel(String name, TableModel tm) {
       super(tm);
       name_ = name;
       expanded_ = false;
    }

    
    public void toggleHeader()
    {
        expanded_ = !expanded_;
    }

    public int getHeaderSize()
    {
         return expanded_? 2: 1;
    }

    public String getName() { return name_;}

    public void addItem(T item)
    {
        addItem(item, false);
    }

    public void deleteItemAt(int row)
    {
        deleteItemAt(row, false);
    }

    public void deleteItemAt(int row, boolean refresh)
    {
        itemsList.remove(row);

        if(refresh) {
            TableModelEvent e = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
            this.broadcastChange(e);
        }
    }

    public void sendUpdate(int first, int last)
    {
        TableModelEvent e = new TableModelEvent(this, first, last, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
        this.broadcastChange(e);

    }

    public void addItem(T item, boolean refresh) {
        itemsList.add(item);

        if(refresh) {
            int row = itemsList.size()-1;
            TableModelEvent e = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
            this.broadcastChange(e);
        }
    }



    public void broadcastChange(TableModelEvent e)
    {
        // this doesn't seem right
        for (TableModelListener listener : listeners_) {
            listener.tableChanged(e);
        }
    }


    @Override
    public TableModelCoordinates convertToNested(int row, int col)
    {
        return new TableModelCoordinates(this, row, col);
    }

    @Override
    public TableModelCoordinates convertToNested(TableModelCoordinates tmRowCol)
    {
        return tmRowCol;
    }

    //------Table Model Methods-----
    public int getRowCount()
    {
        return itemsList.size();
    }

    public int getColumnCount()
    {
        return 3;
    }

    public String getColumnName(int columnIndex)
    {


        return null;
    }

    public Class<?> getColumnClass(int columnIndex)
    {
        return Object.class;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    // could be reimplemented with a separate renderer which knows what to do, we would return the whole item instead
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if(rowIndex >= itemsList.size())
            return null;
        return itemsList.get(rowIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        throw new UnsupportedOperationException();
    }



    // Depth oriented methods
    @Override
    public int getDepthByIndex(int rowIndex)
    {
        return ((Item)itemsList.get(rowIndex)).getDepth();
    }

    @Override
    public int getDepthByObject(Object o)
    {
        return ((Item)o).getDepth();
    }

    @Override
    public int getDepthByIndexOrObject(int rowIndex, Object o)
    {
        return ((Item)o).getDepth();
    }


    @Override
    public Object getRowAt(int index)
    {
        return itemsList.get(index);
    }



    private final CellSpan span = new CellSpan(0,0,1,1);

    // cell span implementation
    @Override
    public CellSpan getCellSpanAt(int row, int col)
    {
        return null;

    }

    @Override
    public boolean isCellSpanOn()
    {
         return false;
    }
}
