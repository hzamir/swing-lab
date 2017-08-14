package com.gfigroup.ts.ui.controls.tablex;

import java.util.ArrayList;


/**
 *  Serves as a holder for template for table columns that will be reused for in multiple humps
 */
public class TableColumnRepository
{
    protected ArrayList<TableColumnFactory> columnFactories_ = new ArrayList<TableColumnFactory>();


    public void addColumnFactory( TableColumnFactory tcf)
    {
        columnFactories_.add( tcf);
    }

    public void rebuildVisible()
    {

    }

    protected void clear()
    {
        columnFactories_.clear();
    }

    public int size()
    {
        return columnFactories_.size();
    }




    /**
     * does moving the column from -> to make any sense?
     * @param from
     * @param to
     * @return
     */
    public boolean isMoveable(int from, int to)
    {
       if(from == to)
           return false;
       if(from < 0 || to < 0)
           return false;
        return !((from >= size()) || (to >= size()));

    }

    /**
     *  move the table column factory position (and hence the column position
     * @param from
     * @param to
     * @return success moving the factory between the two indices
     */
    public boolean move(int from, int to)
    {
        if(!isMoveable(from,to))
            return false;

        if(from < to)
            --to;           // to will become smaller after the move

        TableColumnFactory tcf = columnFactories_.get(from);

        columnFactories_.remove(from);
        columnFactories_.add(to, tcf);
        return true;
    }

    public ArrayList<TableColumnFactory> getColumnFactories() {
        return columnFactories_;
    }

    public TableColumnFactory getColumnFactory(int index)
    {
        return columnFactories_.get(index);
    }
}
