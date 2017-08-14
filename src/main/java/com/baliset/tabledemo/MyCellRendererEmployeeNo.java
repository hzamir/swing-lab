package com.baliset.tabledemo;

import com.baliset.data.Item;
import com.baliset.ui.controls.tablex.NullCell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyCellRendererEmployeeNo extends DefaultTableCellRenderer
{
    private static final Color background_ = new Color(255,255,200);
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JLabel jl = (JLabel)comp;

        if (value != null && value != NullCell.sNullObject) {
            Item item = (Item) value;
            jl.setForeground(Color.blue);
            jl.setBackground(background_);
            jl.setText(item.getNumber().toString());
        }
        return comp;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
