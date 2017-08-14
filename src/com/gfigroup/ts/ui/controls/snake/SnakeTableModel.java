package com.gfigroup.ts.ui.controls.snake;

import com.gfigroup.ts.ui.controls.tablex.NullCell;
import com.jidesoft.grid.CellSpan;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


public class SnakeTableModel extends SnakeComputer implements TableModelListener
{
    SnakeTable table_;
    CoordCache cache_;

    public SnakeTableModel(TableModel businessTableModel) {
        super(businessTableModel);
        cache_ = new CoordCache();
        businessTableModel.addTableModelListener(this);
    }

    public void setTable(SnakeTable table)
    {
        table_ = table;
    }
    //------Table Model Methods-----
    public int getRowCount()
    {
        return snakeRowCount_;
    }

    public int getColumnCount()
    {
        return modelColumnCount_ * requiredHumpCount();
    }

    @Override
    public int getModelColumnCount()
    {
         return modelColumnCount_;
    }

    public String getColumnName(int snakeColumnIndex)
    {
        int modelColumnIndex = snakeToModelColumn(snakeColumnIndex);

        return getNestedTableModel().getColumnName(modelColumnIndex);
    }

    public Class<?> getColumnClass(int snakeColumnIndex)
    {
        int modelColumnIndex = snakeToModelColumn(snakeColumnIndex);
        return super.getColumnClass(modelColumnIndex);
    }

    public boolean isCellEditable(int snakeRowIndex, int snakeColumnIndex)
    {
        int modelRowIndex = snakeToModelRow(snakeRowIndex, snakeColumnIndex);
        if(modelRowIndex >= getNestedTableModel().getRowCount())
            return false;

        int modelColumnIndex = snakeToModelColumn(snakeColumnIndex);
        return getNestedTableModel().isCellEditable(modelRowIndex, modelColumnIndex);
    }

    public Object getValueAt(int snakeRowIndex, int snakeColumnIndex)
    {
        int modelRowIndex = snakeToModelRow(snakeRowIndex, snakeColumnIndex);
        int modelColumnIndex = snakeToModelColumn(snakeColumnIndex);
        if(modelColumnIndex == trueModelColumnCount_)
            return NullCell.sNullObject;

        return getNestedTableModel().getValueAt(modelRowIndex, modelColumnIndex);
    }

    public void setValueAt(Object aValue, int snakeRowIndex, int snakeColumnIndex)
    {
        int modelRowIndex = snakeToModelRow(snakeRowIndex, snakeColumnIndex);
        int modelColumnIndex = snakeToModelColumn(snakeColumnIndex);

        getNestedTableModel().setValueAt(aValue, modelRowIndex, modelColumnIndex);
    }



    public int requiredHumpCount()
    {
        return modelRowToSnakeHump(Math.max(getNestedTableModel().getRowCount()-1,0)) + 1;
    }


    private void broadcastEvent(TableModelEvent e)
    {
        if(e == null)
            return;

        for (TableModelListener listener : listeners_) {
            listener.tableChanged(e);
        }
    }

    //---- implementation as a tablemodellistener ----

    /**
     *   Translate the tablemodel events that bubble up to us from the exposed table model to the SnakeTable itself
     * @param e
     */
    public void tableChanged(TableModelEvent e)
    {
        //---stage one, keep our tableModel in sync by recomputing it's rows and columns
        int oRowCount = this.getRowCount();
        table_.recomputeSnake();
        int nRowCount = this.getRowCount();
        int firstModelRow = e.getFirstRow();
        int lastModelRow = e.getLastRow();
        int rowDiff = nRowCount - oRowCount;

       //---stage two, translate events according to type and differences
        switch (e.getType()) {
            case TableModelEvent.INSERT:
                assert rowDiff >= 0: rowDiff;
                broadcastEvent(
                        new TableModelEvent(this, 0, oRowCount-1,
                                TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE)
                );
                if (rowDiff > 0) {
                    broadcastEvent(
                            new TableModelEvent(this, oRowCount, nRowCount-1,
                                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT)
                    );
                }
                break;
            case TableModelEvent.DELETE:
                assert rowDiff <= 0 : rowDiff;
                broadcastEvent(
                        new TableModelEvent(this, 0, nRowCount-1,
                                TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE)
                );

                if (rowDiff < 0) {
                    broadcastEvent(
                            new TableModelEvent(this, nRowCount, oRowCount-1,
                                    TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
                }
                break;

            case TableModelEvent.UPDATE:

                //todo: optimize for single column changes, sorely needed
                assert rowDiff == 0: rowDiff;
                int modelCol = e.getColumn();

                int updCol = TableModelEvent.ALL_COLUMNS; // assume all columns will be updated until proven otherwise

                if(modelCol != TableModelEvent.ALL_COLUMNS) {
                    int firstModifiedSnakeCol = modelToSnakeColumn(firstModelRow, modelCol);
                    int lastModifiedSnakeCol = modelToSnakeColumn(lastModelRow, modelCol);
                    if(firstModifiedSnakeCol == lastModifiedSnakeCol) {
                        updCol = firstModifiedSnakeCol;       // changes restricted to single column in snake table
                    }
                }

                broadcastEvent(new TableModelEvent(this,
                        modelToSnakeRow(firstModelRow),
                        lastModelRow == Integer.MAX_VALUE ? Integer.MAX_VALUE: modelToSnakeRow(lastModelRow),
                        updCol, TableModelEvent.UPDATE)
                );
                break;
        } // end switch

    } // end tableChanged


    CellSpan stmSpan = new CellSpan(0,0,1,1);
    @Override
    public CellSpan getCellSpanAt(int row, int col)
    {
        int modelColumn = snakeToModelColumn(col);
        if(modelColumn == trueModelColumnCount_)
        {
           // stmSpan.setRow(0); //would be zero anyway
            // stmSpan.setColumnSpan(1); defaults to this anyway
            stmSpan.setColumn(col);
            stmSpan.setRowSpan(snakeRowCount_);
            return stmSpan;
        }

        return super.getCellSpanAt(row,col);

    }

    @Override
    public boolean isCellSpanOn() { return true; }
    
  /*

    disable these as special renderers are not expected participate in snake table gutter rendering
    @Override
    public TableCellRenderer decorateCellRenderer(TableCellRenderer renderer)
    {
        return new SnakeCellRenderer(super.decorateCellRenderer(renderer), this);
    }
    */

    @Override
    public TableCellRenderer decorateHeaderCellRenderer(TableCellRenderer renderer)
    {
        return new SnakeHeaderCellRenderer(this, super.decorateHeaderCellRenderer(renderer));
    }


    private boolean isGutterColumn(int columnIndex)
    {
        return (columnIndex % modelColumnCount_) == (modelColumnCount_ - 1);
    }

    // Jide Navigable enhancement we don't key navigate to gutter columns
    @Override
    public boolean isNavigableAt(int rowIndex, int columnIndex)
    {

       boolean  gutter = isGutterColumn(columnIndex);
       return !gutter && super.isNavigableAt(rowIndex, columnIndex);

    }

} // end SnakeTableModel
