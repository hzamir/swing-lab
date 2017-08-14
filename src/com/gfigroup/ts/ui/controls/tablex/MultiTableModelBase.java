package com.gfigroup.ts.ui.controls.tablex;


import com.jidesoft.grid.CellSpan;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public abstract class MultiTableModelBase implements NestingTableModel
{
    private class HeaderInterceptingRenderer extends DefaultTableCellRenderer
    {
        TableCellRenderer standardRenderer_;

        public HeaderInterceptingRenderer(TableCellRenderer renderer)
        {
            standardRenderer_ = renderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if(value == NullCell.sNullObject) {
                return super.getTableCellRendererComponent(table,  value,  isSelected, hasFocus, row, column);
            }

            TableCellRenderer r = isHeaderRowCell(value)?
                                             headerRowAdapter_.getHeaderRenderer():
                                             standardRenderer_;

            return r.getTableCellRendererComponent(table,  value,  isSelected, hasFocus, row, column);
        }
    } // end inner class HeaderInterceptingRenderer


    public MultiTableModelBase(Collection<? extends TableModel> tableModels, TableHeaderRowAdapter hra)
    {
        headerRowAdapter_ = hra;
        tableModels_ =  tableModels;
        rowCounts_ = new RowCounts();
        parent_ = null;

        rebuild(tableModels, hra);
    }


    /**
     *  builds a version of the table that doesn't take snaking into account
     *  a recompute snake from the snaketable will fix this up for snaking later, when a number
     *  of physical table rows is known
     * @param tableModels
     * @param hra
     */
    protected void rebuild(Collection<? extends TableModel> tableModels, TableHeaderRowAdapter hra)
    {
        int modelRowCtr = 0;
        aggregate_.clear();

        for (TableModel tm : tableModels)
        {
            // compute offsets here, add some sort of row decorator I can use to have my own rows
            int headerHeightInRows = (hra == null)? 0: hra.getHeaderHeightInRows(tm);
            MiscTMInfo mi = new MiscTMInfo(tm, modelRowCtr + headerHeightInRows, headerHeightInRows, 0, tm.getRowCount());
            ((ChildTableModel)tm).setParent(this);
            // add in any header rows (multi row headers will be cell merged)
            if ((hra != null)) {
                for (int hrctr = 0; hrctr < headerHeightInRows; ++hrctr) {
                    aggregate_.add(mi);
                }
                modelRowCtr += headerHeightInRows;
            }

            // add in all of the regular rows for the table model
            for (int i = 0; i < tm.getRowCount(); ++i) {
                aggregate_.add(mi);
            }
            modelRowCtr += tm.getRowCount();
        }



    }


    protected class RowCounts {
        protected int modelRows;          // how many model rows will fit in the current page  (could go negative)
        protected int physicalRows;       // how many physical rows does that correspond to (but never negative)
        protected int rowsLeftInModel;    // how many rows are left in the model?
        protected boolean doesntFit;

        final protected int widowRowCount;
        final protected int orphanRowCount;

        public  RowCounts()
        {
            widowRowCount = headerRowAdapter_ != null? headerRowAdapter_.getWidowRowCount():0;
            orphanRowCount = headerRowAdapter_ != null? headerRowAdapter_.getOrphanRowCount():0;

        }


    }
    
    protected RowCounts rowCounts_;

    protected void countPhysicalRows(TableModel tm, int startIndex, int remainingTableRows, RowCounts rc, int headerHeightInRows)
    {
        remainingTableRows -=  headerHeightInRows;
        rc.rowsLeftInModel  = tm.getRowCount() - startIndex;
        rc.modelRows        = Math.min(rc.rowsLeftInModel, remainingTableRows);
        rc.doesntFit        = rc.modelRows < 0;
        rc.physicalRows     = rc.modelRows;      // expecting (barely, weakly) to use this with depth
    }


   /**
    * 
    *  how many rows from this model starting at index n can fit a given remaining space,
    *  given the height of a header which must fit, without violating the orphan control, and without pushing a widow
    *  to the next spot
    *  sometimes it returns a value, but says that value will not fit (rc.doesntFit).

    * What happens when one makes the table so narrow, that at least a header and one row will not fit?
    * That should factor into some sort of a minimum height for tables so that cannot happen!
    */

    private void countRows(TableModel tm, int startIndex, int headerHeightInRows, int remainingTableRows, RowCounts rc)
    {
        countPhysicalRows(tm, startIndex, remainingTableRows, rc, headerHeightInRows);

        if(headerRowAdapter_ == null)
            return;


        // no point in putting in a header if it can't have at least one row under it
        // !!! however, this must work with first headers that are empty! and it should, since -0 = 0 ???
//        if(rc.modelRows < 1 && startIndex > 0) {
//            rc.doesntFit = true;
//            return;
//        }
        // but neither can we put in a header with some rows, if the number of rows would be considered widowed
//        if(rc.modelRows <= rc.widowRowCount && rc.modelRows < rc.rowsLeftInModel ) {
//            rc.doesntFit = true;
//            return;
//        }

        // check how many rows we are pushing to the next "page" is it a small number we'd consider orphaned?
        // if so we'd rather move the whole header over to the next page to prevent that
//        int deferredRows = rc.rowsLeftInModel - rc.modelRows;
//        if(deferredRows > 0 && deferredRows < rc.orphanRowCount) {
//            rc.doesntFit = true;
//        }

    }


    // suppress header Rows where the table is too narrow to support them

    protected boolean shouldUseHeaders(int tableRows, TableHeaderRowAdapter hra)
    {
        return tableRows >= 3 && hra != null;

    }
    
    public void rebuild(Collection<? extends TableModel> tableModels, TableHeaderRowAdapter hra, int tableRows)
    {
        lastTableRows_ = tableRows;
        aggregate_.clear();

        int multiModelRowCtr = 0;
        int rowsLeftThisPage = tableRows;     // physical rows in the table, counts down, then resets for each page


        boolean useHeaders = shouldUseHeaders(tableRows, hra);

        for (TableModel tm : tableModels)
        {
            // compute offsets here, add some sort of row decorator I can use to have my own rows
            int headerHeightInRows = 0;        // how tall is the header (zero till proven taller)
            int totalSubModelRows = tm.getRowCount();     // how many rows are there in this tablemodel **** vs depth rows
            int subModelRowCtr = 0;            // what row of the table model are we currently working on?

            if(useHeaders) {
                headerHeightInRows = hra.getHeaderHeightInRows(tm);
            }

            do {


               countRows(tm, subModelRowCtr, headerHeightInRows, rowsLeftThisPage, rowCounts_);

                if(rowCounts_.doesntFit) {
                    //.... put in dummy rows? Or no need?
                    // move to next page
                    multiModelRowCtr += rowsLeftThisPage;
                    rowsLeftThisPage = tableRows;
                    continue;
                } else {
                      // put in a header and the allocated rows
                    MiscTMInfo mi = new MiscTMInfo(tm, multiModelRowCtr + headerHeightInRows, headerHeightInRows, subModelRowCtr, rowCounts_.modelRows);

                    // add in any header rows (multi row headers will be cell merged)
                    if (useHeaders) {
                        for (int hrctr = 0; hrctr < headerHeightInRows; ++hrctr) {
                            aggregate_.add(mi);
                        }
                        multiModelRowCtr += headerHeightInRows;
                        rowsLeftThisPage -= headerHeightInRows;
                    }

                    // add in all of the regular rows for the table model
                    for (int i = 0; i < rowCounts_.physicalRows; ++i) {
                        aggregate_.add(mi);
                    }
                    multiModelRowCtr += rowCounts_.modelRows;
                    subModelRowCtr += rowCounts_.modelRows;
                    headerHeightInRows = headerHeightInRows >= 1? 1: 0; // only the first and not continued header should grow %%% kludge? todo: fix this
                    rowsLeftThisPage -= rowCounts_.physicalRows;
                    if(rowsLeftThisPage == 0) {
                        rowsLeftThisPage = tableRows;
                        continue;
                    }

                }
            }  while(subModelRowCtr < totalSubModelRows); // end while (looping through all rows in the table model, across pages)

        } // end for each tablemodel

    } // end rebuild()




    public static class MiscTMInfo
    {
        public final TableModel model;
        public final int firstRow;
        final int spanRow;
        final int offset;
        final int count;             // number of rows under this header (remember, headers can repeat!)
        final int headerRows;        // how many rows of header are there

        MiscTMInfo(TableModel tm, int row, int headers, int first, int ct)
        {
             model = tm;
             spanRow = -(headers - row);
             firstRow = first;    // whether it is a 'continued' header or not (>0)
             offset = -row + firstRow;
             headerRows = headers;
             count = ct;
        }


        boolean contains(int row)
        {
            return row >= firstRow && (firstRow + count) > row;
        }

    }

    protected  Collection<? extends TableModel> tableModels_;
    protected TableHeaderRowAdapter headerRowAdapter_;
    protected final ArrayList<MiscTMInfo>
            aggregate_ = new ArrayList<MiscTMInfo>();
    protected int lastTableRows_ = -1;
    protected ChildTableModel parent_;

    protected MiscTMInfo nextInfo(int r)
    {

        return r < aggregate_.size() && r >= 0? aggregate_.get(r): null;
    }


    protected TableModel next(int r)
    {

        return r < aggregate_.size() && r >= 0? aggregate_.get(r).model: null;
    }

    @Override
    public TableCellRenderer decorateCellRenderer(TableCellRenderer renderer)
    {
        TableModel next = next(0);
        if (next instanceof NestingTableModel) {
            if(headerRowAdapter_ != null)
                return  new HeaderInterceptingRenderer(((NestingTableModel) next).decorateCellRenderer(renderer));
            return ((NestingTableModel) next).decorateCellRenderer(renderer);
        }

        return renderer;
    }

    @Override
    public TableCellRenderer decorateHeaderCellRenderer(TableCellRenderer renderer)
    {
        TableModel next = next(0);

        if (next instanceof NestingTableModel)
            return ((NestingTableModel) next).decorateHeaderCellRenderer(renderer);
        return renderer;
    }

   @Override
    public TableModelCoordinates convertToNested(int row, int col)
    {        
        MiscTMInfo info = nextInfo(row);
        
        if(info == null)
            return new TableModelCoordinates(this, row,col);
        return new TableModelCoordinates(info.model, info.offset + row, col);
    }

    @Override
    public TableModelCoordinates convertToNested(TableModelCoordinates tmRowCol)
    {
        MiscTMInfo info = nextInfo(tmRowCol.row);
        
        if(info == null)
            return tmRowCol;

        return new TableModelCoordinates(info.model, info.offset + tmRowCol.row, tmRowCol.col);
    }

    @Override
    public TableModelCoordinates convertToMostNested(TableModelCoordinates tmRowCol)
    {
        MiscTMInfo info = nextInfo(tmRowCol.row);
        TableModel ntm = info.model;
        
        if (ntm instanceof NestingTableModel) {
            if (isHeaderRowCell(getValueAt(tmRowCol.row, tmRowCol.col))) {
                return new TableModelCoordinates(info.model, info.offset + tmRowCol.row, tmRowCol.col);
            }
            return ((NestingTableModel) ntm).convertToMostNested(convertToNested(tmRowCol));
        } else {
            return convertToNested(tmRowCol);
        }
        
//        return (ntm instanceof NestingTableModel) ?
//                ((NestingTableModel) ntm).convertToMostNested(convertToNested(tmRowCol)) :
//                convertToNested(tmRowCol);
    }


    // default behavior
    @Override
    public int getRowCount()
    {
        return aggregate_.size();
    }

    @Override
    public int getColumnCount()
    {
        TableModel next = next(0);
        return next.getColumnCount();
    }

    @Override
    public String getColumnName(int i)
    {
        TableModel next = next(0);
        return next.getColumnName(i);
    }

    @Override
    public Class<?> getColumnClass(int i)
    {
        TableModel next = next(0);
        return next.getColumnClass(i);
    }


    @Override
    public Object getValueAt(int r, int c)
    {
        TableModelCoordinates tmc = convertToNested(r,c);
        MiscTMInfo info = nextInfo(r);
        TableModel next = tmc.model;
        if(tmc == null || tmc.row >= next.getRowCount())
            return NullCell.sNullObject;

        if(tmc.row < info.firstRow) {
            if(c == 0)
                return headerRowAdapter_.getHeader(info, info.headerRows + info.firstRow - tmc.row);
            else return null;
        }

        return next.getValueAt(tmc.row, tmc.col);
    }

    private int columnCount_ = -1;    // arbitrary number     todo: fix this!!! ???

    @Override
    public void setModelColumnCount(int c)
    {
         columnCount_ = c;
    }

    @Override
    public int getModelColumnCount()
    {
        return columnCount_;
    }


    @Override
    public void setValueAt(Object v, int r, int c)
    {
        TableModelCoordinates tmc = convertToNested(r,c);
        tmc.model.setValueAt(v, tmc.row, tmc.col);
    }

    @Override
    public boolean isCellEditable(int r, int c)
    {
        if (isHeaderRowCell(getValueAt(r, c))) 
        {
            return false;
        }
        TableModelCoordinates tmc = convertToNested(r,c);
        return tmc.model.isCellEditable(tmc.row, tmc.col);
    }

    public int indexOf(TableModel tableModel) 
    {
        int i = 0;
        for (TableModel currentModel : tableModels_) {
            if (currentModel == tableModel) {
                return i;
            }
            i++;
        }
        
        return -1;
    }

    // Jide CellSpan support for headers
    private final NestingCellSpan span = new NestingCellSpan(0,0,1,1,0,0);


    @Override
    public NestingCellSpan getNestedCellSpanAt(TableModelCoordinates tmc)
    {
        return (NestingCellSpan)getCellSpanAt(tmc.row, tmc.col);
    }

    // cell span implementation
    @Override
    public CellSpan getCellSpanAt(int row, int col)
    {
        if(headerRowAdapter_ == null)
            return null;

        // todo: ??? big redundancies here
        MiscTMInfo infox = nextInfo(row);
        if(infox == null)
            return null; //??? why do I need this, if I do?
        int height = infox.headerRows;

        if(height==0)
            return null;

        TableModelCoordinates tmc2 = convertToNested(row, col);

        TableModel next = tmc2.model;
        if(next == null || tmc2.row >= next.getRowCount())
            return null;

        if(tmc2.row >= infox.firstRow)
            return null;

        
        span.setRow(infox.spanRow);  // the anchor row
        span.setColumn(0);
        span.setRowSpan(height);
        span.setColumnSpan(getModelColumnCount());
        span.setMy(row, col);
        
        return span;
    }

    @Override
    public boolean isCellSpanOn()
    {
        return headerRowAdapter_ != null;
    }

    @Override
    public void geometryChange(NestingTableModel originator, int tableRows)
    {
        rebuild(tableModels_, headerRowAdapter_ , tableRows);

    }
    
    public boolean isHeaderRowCell(Object value) 
    {
        return headerRowAdapter_ != null && headerRowAdapter_.isObjectAHeaderRowCell(value);
    }

    @Override
    public void setParent(ChildTableModel model)
    {
        parent_ = model;
    }

    @Override
    public ChildTableModel getParent()
    {
        return parent_;
    }

    @Override
    public TableModelCoordinates convertFromNested(TableModelCoordinates rowCol)
    {

        boolean found = false;
        ChildTableModel model = (ChildTableModel)rowCol.model;
        int row = rowCol.row;
        for(MiscTMInfo info: aggregate_)
        {
            if(model != info.model) {
                if(found)
                    throw new ArrayIndexOutOfBoundsException("bad coordinates: row not found in model");
                continue;
            }
            found = true;
            if(info.contains(row))
                return(new TableModelCoordinates(this, row - info.offset, rowCol.col));

        }
        throw new ArrayIndexOutOfBoundsException("bad coordinates: model not found");

    }

    @Override
    public TableModelCoordinates convertToLeastNested(TableModelCoordinates tmRowCol) {
        ChildTableModel ptm = getParent();

        return (ptm != null)?
                ptm.convertToLeastNested(convertFromNested(tmRowCol)):
                convertFromNested(tmRowCol);
    }

}
