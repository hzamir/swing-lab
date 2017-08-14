package com.baliset.ui.controls.tablex;

import com.jidesoft.grid.CellSpan;
import com.jidesoft.grid.NavigableTableModel;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 *  DelegatingTableModel exists solely to provide default implementations for a concrete
 *  TableModelClass that deals with a nested TableModel.
 *
 *  It's abstract because: if you used all the default methods, you might as well just
 *  directly consume the nested model.
 */

public abstract class DelegatingTableModel implements NestingTableModel
{

    public DelegatingTableModel(TableModel model)         {
        listeners_ = new ArrayList<TableModelListener>();
        next = model;
        parent = null;

        if(next != null)
            ((ChildTableModel)model).setParent(this);
    }
    public TableModel getNestedTableModel()               { return next;  }

    public TableCellRenderer decorateCellRenderer(TableCellRenderer renderer)
    {
        if(next instanceof NestingTableModel)
            return ((NestingTableModel) next).decorateCellRenderer(renderer);
        return renderer;
    }

    public TableCellRenderer decorateHeaderCellRenderer(TableCellRenderer renderer)
    {
        if(next instanceof NestingTableModel)
            return ((NestingTableModel) next).decorateHeaderCellRenderer(renderer);
        return renderer;
    }

    public abstract TableModelCoordinates convertToNested(TableModelCoordinates tmRowCol);
    
     public TableModelCoordinates convertToMostNested(TableModelCoordinates tmRowCol) {
        TableModel ntm = getNestedTableModel();
         
            return (ntm instanceof NestingTableModel)?
                    ((NestingTableModel) ntm).convertToMostNested(convertToNested(tmRowCol)):
                    convertToNested(tmRowCol);
    }
    
    
    // default behavior
    @Override public int      getRowCount   (     )       { return next.getRowCount();      }
    @Override public int      getColumnCount(     )       { return next.getColumnCount();   }
    @Override public String   getColumnName (int i)       { return next.getColumnName(i);   }
    @Override public Class<?> getColumnClass(int i)       { return next.getColumnClass(i);  }

    @Override public Object   getValueAt    (int r, int c)
    {
        if(r >= next.getRowCount()) {
            return NullCell.sNullObject;
        }

        return next.getValueAt(r,c);
    }
    @Override public void     setValueAt    (Object v, int r, int c)    { next.setValueAt(v, r, c);          }
    @Override public boolean  isCellEditable(int r, int c)              { return next.isCellEditable(r, c);  }


    protected final List<TableModelListener> listeners_;

    @Override public void addTableModelListener(TableModelListener l)
    {
        listeners_ .add(l);
    }

    @Override public void removeTableModelListener(TableModelListener l)
    {
        listeners_ .remove(l);
    }

    private ChildTableModel parent;
    final private TableModel next;
    private int columnCount_ = 0;    // arbitrary number

    @Override
    public void setModelColumnCount(int c)
    {
         columnCount_ = c;
         TableModel ntm = getNestedTableModel();
         if(ntm != null && ntm instanceof NestingTableModel)
             ((NestingTableModel) ntm).setModelColumnCount(c);
    }

    @Override
    public int getModelColumnCount()
    {
        return columnCount_;
    }

    @Override
   public NestingCellSpan getNestedCellSpanAt(TableModelCoordinates tmc)
   {
       TableModelCoordinates ntm = convertToNested(tmc);

       if((ntm.model != this) && (ntm.model instanceof NestingTableModel)) {
           NestingCellSpan ncs = (NestingCellSpan)((NestingTableModel)ntm.model).getCellSpanAt(ntm.row, ntm.col);
           // adjust coordinates from nested to this
           if(ncs != null) {
            int rowOffset = (tmc.row-ntm.row);
            int colOffset = (tmc.col-ntm.col);
            ncs.setRow(ncs.getRow() + rowOffset);
            ncs.setColumn(ncs.getColumn() + colOffset);
            ncs.setMy(ncs.getMyRow() + rowOffset, ncs.getMyCol() + colOffset );
           }
           return ncs;
       } else {
           return null;
       }
  
   }

    @Override
    public CellSpan getCellSpanAt(int row, int col)
    {
        TableModelCoordinates tmc = new TableModelCoordinates(this,row,col);
        return getNestedCellSpanAt(tmc);
    }

    @Override
    public boolean isCellSpanOn()
    {
        TableModel ntm = getNestedTableModel();
        return (ntm instanceof NestingTableModel) && ((NestingTableModel) ntm).isCellSpanOn();
    }
    
    @Override 
    public void geometryChange(NestingTableModel originator, int tableRows)
    {
        TableModel ntm = getNestedTableModel();
        if(ntm instanceof NestingTableModel)
             ((NestingTableModel) ntm).geometryChange(originator, tableRows);
    }

     // NavigableModel methods (making us a navigable table model
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
        TableModel tm = getNestedTableModel();
        if(tm instanceof NavigableTableModel) {
            TableModelCoordinates tmc = convertToNested(rowIndex, columnIndex);
            return ((NavigableTableModel)tmc.model).isNavigableAt(tmc.row, tmc.col);
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
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
        return true;
    }

    @Override
    public void setParent(ChildTableModel p)
    {
        parent = p;
    }

    @Override
    public ChildTableModel getParent()
    {
        return parent;
    }



    @Override
    public TableModelCoordinates convertFromNested(TableModelCoordinates childTmc)
    {
        // only a bottom tablemodel to kickstart conversion process should use this inherited method
        // it is a "null" conversion
        assert childTmc.model == this: "default convertFromNested only intended for null conversions";
        return childTmc;
    }


    @Override
    public TableModelCoordinates convertToLeastNested(TableModelCoordinates tmRowCol) {
        ChildTableModel ptm = getParent();

        return (ptm != null)?
                ptm.convertToLeastNested(convertFromNested(tmRowCol)):
                convertFromNested(tmRowCol);
    }

}
