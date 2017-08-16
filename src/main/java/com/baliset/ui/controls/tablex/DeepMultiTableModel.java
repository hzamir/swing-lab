package com.baliset.ui.controls.tablex;


import java.util.*;

public class DeepMultiTableModel extends MultiTableModel implements DepthTableModel
{
   public DeepMultiTableModel(Collection<? extends DepthTableModel> collection,
                              TableHeaderRowAdapter hra

                              )
   {
       super(collection, hra);
   }


    @Override
    public int getDepthByObject(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDepthByIndex(int index)
    {
        MiscTMInfo info = super.nextInfo(index);

        int newIndex = index + info.offset;
        return (newIndex < 0 && info.headerRows > 0)? 1: ((DepthTableModel)info.model).getDepthByIndex(newIndex);
    }


    @Override
    public int getDepthByIndexOrObject(int index, Object o)
    {
        MiscTMInfo info = super.nextInfo(index);

        int newIndex = index + info.offset;
        return (newIndex < 0 && info.headerRows > 0)?
                1:
               ((DepthTableModel)info.model).getDepthByIndexOrObject(newIndex, o);
    }

    @Override
    public Object getRowAt(int index)
    {
        MiscTMInfo info = super.nextInfo(index);

        int newIndex = index + info.offset;
        if(newIndex < 0) {
            return headerRowAdapter_.getHeader(info, info.headerRows + newIndex);
        }

        return ((DepthTableModel)info.model).getRowAt(newIndex);
    }
    
  /*
    @Override
    protected int countRows(TableModel tm, int startIndex, int headerHeightInRows, int remainingTableRows)
    {

        DepthTableModel dtm = (DepthTableModel)tm;

        int availableRows = 0;

        int rowsLeftInModel = tm.getRowCount() - startIndex;
        return Math.min()
        for(int i = headerHeightInRows; i < remainingTableRows; ++i) {
            ++availableRows;


        }


    }
    */
}
