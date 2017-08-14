package com.baliset.ui.controls.tablex;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.swing.event.TableModelEvent.*;

public class MultiTableModel extends MultiTableModelBase implements TableModelListener
{

    public MultiTableModel(Collection<? extends TableModel> tableModels, TableHeaderRowAdapter hra)
    {
        super(tableModels, hra );
        listeners_ = new ArrayList<TableModelListener>();

        for (TableModel tm : tableModels)
        {
            tm.addTableModelListener(this);
        }
    }


    private final List<TableModelListener> listeners_;

    @Override public void addTableModelListener(TableModelListener l)
    {
        listeners_ .add(l);
    }

    @Override public void removeTableModelListener(TableModelListener l)
    {
        listeners_ .remove(l);
    }


    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    @Override
    public void tableChanged(TableModelEvent e)
    {
        int uRowFirst = e.getFirstRow();
        int uRowLast  = e.getLastRow();

        TableModelEvent newEvent = null;

        switch (e.getType()) {
            case INSERT:
                newEvent = insertRows(e, uRowFirst, uRowLast);
                break;
            case DELETE:
                newEvent = removeRows(e, uRowFirst, uRowLast);
                break;
            case UPDATE:
                newEvent =  (uRowLast == Integer.MAX_VALUE)?
                        globalUpdateRows(e):
                        updateRows(e, uRowFirst, uRowLast);
                break;
        } // end switch()

        if (newEvent != null) {
            for (TableModelListener listener : listeners_) {
                listener.tableChanged(newEvent);
            }
        }
    }

    private TableModelEvent globalUpdateRows(TableModelEvent e)
    {
        rebuild(tableModels_, headerRowAdapter_);
        return new TableModelEvent(this, 0,Integer.MAX_VALUE, e.getColumn(), UPDATE);
    }

    
    private MiscTMInfo convertToEnclosingRow(TableModel sourceModel, int sourceRow, MiscTMInfo startInfo)
    {
        int max = aggregate_.size();

        MiscTMInfo info = null;
        boolean foundModel = false;

        int startPoint = startInfo == null? 0: startInfo.firstRow;
        for(int i = 0; i < max; i += (info.headerRows + info.count)) {
            info = aggregate_.get(i);
            if(info.model == sourceModel) {
                foundModel = true;
                if(sourceRow < info.firstRow)                 // right model, wrong info
                    continue;
                if(sourceRow < info.firstRow + info.count)    // we found it, return info
                    break;
            } else if(foundModel) {
                    return null;
            }
        }

        return info;  // if we cannot find it at all

    }



    private TableModelEvent updateRows(TableModelEvent e, int uRowFirst, int uRowLast)
    {
        TableModel srcModel = (TableModel)e.getSource();

        if(uRowLast == Integer.MAX_VALUE)
            return new TableModelEvent(this, uRowFirst, uRowLast, ALL_COLUMNS, UPDATE);

        MiscTMInfo infoFirst = convertToEnclosingRow(srcModel, uRowFirst, null);
        MiscTMInfo infoLast  = convertToEnclosingRow(srcModel, uRowLast, infoFirst);

        if(infoFirst == null || infoLast == null)
            return new TableModelEvent(this, 0, Integer.MAX_VALUE, e.getColumn(), UPDATE);

        return new TableModelEvent(this, uRowFirst-infoFirst.offset, uRowLast-infoLast.offset, e.getColumn(), UPDATE);
    }

    private TableModelEvent removeRows(TableModelEvent e, int uRowFirst, int uRowLast)
    {
        rebuild(tableModels_, headerRowAdapter_);
        return new TableModelEvent(this, 0 , Integer.MAX_VALUE, ALL_COLUMNS, UPDATE);
    }

    private TableModelEvent insertRows(TableModelEvent e, int uRowFirst, int uRowLast)
    {
        rebuild(tableModels_, headerRowAdapter_);
        return new TableModelEvent(this, 0 , Integer.MAX_VALUE, ALL_COLUMNS, UPDATE);
    }

    /**
     * Returns if the cell at the given coordinates can be navigated or
     * not.
     *
     * @param rowIndex    The row index
     * @param columnIndex The column index
     * @return <code>true</code> if navigable, <code>false</code> otherwise
     */
    @Override
    public boolean isNavigableAt(int rowIndex, int columnIndex)
    {
        return !isHeaderRowCell(getValueAt(rowIndex, 0));
    }

    /**
     * Checks if the navigation is on. If off, {@link #isNavigableAt(int, int)}
     * should always return <code>true</code> for valid indexes.
     *
     * @return <code>true</code> if on, <code>false</code> otherwise
     */
    @Override
    public boolean isNavigationOn()
    {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
