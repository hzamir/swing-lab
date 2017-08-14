package com.gfigroup.ts.ui.controls.tablex;

import javax.swing.table.TableModel;

public interface DepthTableModel<R> extends TableModel, ChildTableModel
{
    //  to how many records should item at index expand?
    // used to wrap calls to getSpan()
    int getDepthByIndex(int index);
    int getDepthByObject(Object o);
    int getDepthByIndexOrObject(int index, Object o);

    R getRowAt(int index);

    //... needed for th following

    //.... enumerate all the items in the collection in an efficient way to make them

}
