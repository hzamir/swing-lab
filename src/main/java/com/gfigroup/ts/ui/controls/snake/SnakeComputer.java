package com.gfigroup.ts.ui.controls.snake;

import com.gfigroup.ts.ui.controls.tablex.DelegatingTableModel;
import com.gfigroup.ts.ui.controls.tablex.TableModelCoordinates;

import javax.swing.table.TableModel;

public  class SnakeComputer extends DelegatingTableModel
{
    int snakeRowCount_;
    int modelColumnCount_;
    int trueModelColumnCount_;

    public SnakeComputer(TableModel model)
    {
        super(model);
    }

    public void setSnakeRowCount(int src) {
        snakeRowCount_ = src;

        geometryChange(this, src);
    }
    
    public int getSnakeRowCount() {
        return snakeRowCount_;
    }

    public void setModelColumnCount(int mcc) {
        super.setModelColumnCount(mcc);
        modelColumnCount_ = mcc + 1;
        trueModelColumnCount_ = mcc; // (for gutters)
    }



    public int modelRowToSnakeHump(int modelRow)
    {
       return  (modelRow + snakeRowCount_)/snakeRowCount_ - 1;

    }


    public int snakeColumnToHump(int snakeColumn)
    {
        return   snakeColumn / modelColumnCount_;
    }

    public int modelToSnakeRow(int modelRow)
    {
        return modelRow % snakeRowCount_;
    }

    public int modelToSnakeColumn(int modelRow, int modelColumn)
    {
       return modelRowToSnakeHump(modelRow) * modelColumnCount_ + modelColumn;

    }


    public int snakeToModelRow(int snakeRow, int snakeColumn)
    {
        return snakeRowCount_ *   snakeColumnToHump(snakeColumn) + snakeRow;
    }


    public int snakeToModelColumn(int snakeColumn)
    {
        return  snakeColumn % modelColumnCount_;
    }


    @Override
    public TableModelCoordinates convertToNested(int row, int col) {

        int r = snakeToModelRow(row, col);
        int c = snakeToModelColumn(col);
        return new TableModelCoordinates(getNestedTableModel(), r, c);
    }


    @Override
    public TableModelCoordinates convertToNested(TableModelCoordinates rowCol) {

        int row = snakeToModelRow(rowCol.row, rowCol.col);
        int col = snakeToModelColumn(rowCol.col);
        return new TableModelCoordinates(getNestedTableModel(), row, col);
    }

    @Override
    public TableModelCoordinates convertFromNested(TableModelCoordinates childTmc)
    {
        assert childTmc.model == getNestedTableModel(): "not a nested model";
        return new TableModelCoordinates(this, modelToSnakeRow(childTmc.row), modelToSnakeColumn(childTmc.row, childTmc.col));
    }




}
