package com.baliset.ui.controls.tablex;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


/**
 * TableHeaderRowAdapter implementations are designed to work with specific TableModel
 */
public interface TableHeaderRowAdapter
{
     // level is typically zero for single row header, a double row header would return 1 & 2

      Object getHeader(MultiTableModelBase.MiscTMInfo tm, int level);
      TableCellRenderer getHeaderRenderer();
      boolean isObjectAHeaderRowCell(Object o);   // examines an object to determine if it looks like a header
      int getHeaderHeightInRows(TableModel tm);   // return number of rows needed to render the header
      int getWidowRowCount();                     // return number of lines considered widowed
      int getOrphanRowCount();                    // return number of lines considered orphaned
}
