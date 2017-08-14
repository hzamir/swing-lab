package com.baliset.tabledemo;

import com.baliset.data.Item;
import com.baliset.ui.controls.tablex.NullCell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyCellRendererLastName extends DefaultTableCellRenderer
{
    private static final Color sColor = new Color(0,128,0);
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JLabel jl = (JLabel)comp;

        if (value != null && value != NullCell.sNullObject) {
            Item item = (Item) value;
            jl.setForeground(sColor);
            jl.setBackground(Color.white);
            jl.setText(item.getLast());
        }
        return comp;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
