package com.baliset.tabledemo;

import com.baliset.data.Item;
import com.baliset.ui.controls.tablex.DepthAwareTableCellRenderer;
import com.baliset.ui.controls.tablex.NullCell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyCellRendererInfo extends DefaultTableCellRenderer implements DepthAwareTableCellRenderer
{
    private static final Color sColor = Color.black;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column)
    {
        return getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column, 0);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,  Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column, int depth)
    {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JLabel jl = (JLabel)comp;

        if (value != null && value != NullCell.sNullObject) {
            Item item = (Item) value;
            jl.setForeground(sColor);
            jl.setBackground(Color.white);
            jl.setText(item.getInfo(depth));
        }
        return comp;
    }
}
