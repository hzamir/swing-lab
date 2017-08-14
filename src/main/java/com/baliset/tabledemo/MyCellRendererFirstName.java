package com.baliset.tabledemo;

import com.baliset.data.Item;
import com.baliset.ui.controls.tablex.NullCell;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MyCellRendererFirstName extends DefaultTableCellRenderer
{
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    JLabel jl = (JLabel) comp;

    if (value != null && value != NullCell.sNullObject) {
      Item item = (Item) value;
      jl.setForeground(Color.red);
      jl.setBackground(new Color(0.7f, 1.0f, 0.8f));
      jl.setText(item.getFirst());
    }
    return comp;    //To change body of overridden methods use File | Settings | File Templates.
  }
}
