package com.baliset.ui.controls.snake;

import com.baliset.ui.controls.snake.decoration.SnakeGenericCellBorder;
import com.baliset.ui.controls.tablex.NullCell;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SnakeCellRenderer extends DefaultTableCellRenderer
{
  private TableCellRenderer businessRenderer_;
  private SnakeComputer sc_;
  private Color gridColor_ = new Color(210, 210, 255);

  public SnakeCellRenderer(TableCellRenderer renderer, SnakeComputer sc)
  {
    businessRenderer_ = renderer;
    sc_ = sc;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    if (value == NullCell.sNullObject) {
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    Component comp = businessRenderer_.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    //todo: not necessarily a lable
    JLabel jl = (JLabel) comp;

    //int modelColumn = sc_.snakeToModelColumn(column);

    Border border = new SnakeGenericCellBorder(gridColor_, table);
    jl.setBorder(border);
    return comp;
  }
}
