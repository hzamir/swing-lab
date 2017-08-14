package com.baliset.tabledemo;

import com.baliset.ui.controls.tablex.DelegatingTableModel;
import com.baliset.ui.controls.tablex.DepthExpandingTableModel;
import com.baliset.ui.controls.tablex.MultiTableModelBase;
import com.baliset.ui.controls.tablex.TableHeaderRowAdapter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;


public class MyHeaderRowAdapter implements TableHeaderRowAdapter
{
    private static class TCRenderer extends DefaultTableCellRenderer
    {
        static final Color sHeaderBackground = new Color(0, 0, 210);
        static final Color sHeaderForeground = Color.white;
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            //todo: not necessarily a label
            JLabel jl = (JLabel) comp;
            jl.setBackground(sHeaderBackground);
            jl.setForeground(sHeaderForeground);

            MultiTableModelBase.MiscTMInfo info = (MultiTableModelBase.MiscTMInfo)value;


            MyTableModel myModel = info.model instanceof MyTableModel?
                    (MyTableModel)info.model:
                    (MyTableModel)((DelegatingTableModel)info.model).getNestedTableModel();


            jl.setText(info.firstRow == 0? myModel.getName(): myModel.getName() + " (Continued)");

            return comp;
        }

    }


    private TCRenderer renderer_ = new TCRenderer();


    @Override
    public Object getHeader(MultiTableModelBase.MiscTMInfo info,  int level)
    {
        return info;
    }

    @Override
    public TableCellRenderer getHeaderRenderer()
    {
        return renderer_;
    }

    @Override
    public boolean isObjectAHeaderRowCell(Object o)
    {
        return o instanceof MultiTableModelBase.MiscTMInfo;
    }

    @Override
    public int getHeaderHeightInRows(TableModel tm)
    {
      if(tm instanceof DepthExpandingTableModel)
           return ((MyTableModel)((DelegatingTableModel)tm).getNestedTableModel()).getHeaderSize();
       else
          return ((MyTableModel)tm).getHeaderSize();
    }

    @Override public int getWidowRowCount() { return 1; }
    @Override public int getOrphanRowCount(){ return 1; }

}
