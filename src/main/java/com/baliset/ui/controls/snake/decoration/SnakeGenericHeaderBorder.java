package com.baliset.ui.controls.snake.decoration;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;


public class SnakeGenericHeaderBorder extends LineBorder

{
    private JTableHeader tableHeader_;

    public SnakeGenericHeaderBorder(Color color, JTableHeader tableHeader)
    {
        super(color);
        tableHeader_ = tableHeader;

    }


    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        //---- save old graphics settings
        Color oldColor = g.getColor();
        Shape oldclip = g.getClip();

        //----- set color and clip ----
        g.setColor(lineColor);

        Rectangle trex = SwingUtilities.convertRectangle(tableHeader_, tableHeader_.getVisibleRect(), c);
        g.setClip(x-1, y-1, width+1, height+1);    //intersect with cell
        g.clipRect(trex.x, trex.y, trex.width, trex.height);                            //replace with visible table clip

        //----- draw full cell outline -----
        g.drawRect(x-1, y-1, width+1, height+1);  // draw the cell outline

        //----- restore graphics settings -----
        g.setClip(oldclip);
        g.setColor(oldColor);


    }
}
