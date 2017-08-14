package com.gfigroup.ts.ui.controls.snake;

import com.gfigroup.ts.ui.controls.snake.decoration.SnakeGenericHeaderBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class SnakeHeaderCellRenderer extends DefaultTableCellRenderer
{
     private SnakeComputer snakeComputer_;
     TableCellRenderer businessRenderer_;
     public SnakeHeaderCellRenderer(SnakeComputer snakeComputer, TableCellRenderer headerRenderer)
     {
         super();
         snakeComputer_ = snakeComputer;
         businessRenderer_ = headerRenderer;
     }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component comp;
        if(businessRenderer_ != null) {
          comp = businessRenderer_.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else {
          comp = null;
        }

        JLabel jl = (JLabel)comp;
        if(jl == null)
            return comp;

        /*
        int modelColumn = snakeComputer_.snakeToModelColumn(column);

        if(modelColumn == 0) {
            jl.setBackground(Color.gray);
        }
        */

        Border border = new SnakeGenericHeaderBorder(Color.black, table.getTableHeader());
        jl.setBorder(border);

        return comp;    //To change body of overridden methods use File | Settings | File Templates.
    }


}
