package com.baliset.ui.controls.snake;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class GutterCellRenderer extends DefaultTableCellRenderer
{
    
    Color backgroundColor_;
    public GutterCellRenderer(Color color)
    {
        backgroundColor_ = color;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JLabel jl = (JLabel)comp;
        if(jl == null)
            return comp;
        jl.setBackground(backgroundColor_);

         return comp;
 
    }

}
