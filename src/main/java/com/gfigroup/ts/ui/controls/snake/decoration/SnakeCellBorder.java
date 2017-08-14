package com.gfigroup.ts.ui.controls.snake.decoration;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class SnakeCellBorder extends AbstractBorder
{
 private Color wallColor = Color.gray;

  private int sinkLevel = 10;

  public SnakeCellBorder() {
  }

  public SnakeCellBorder(int sinkLevel) {
    this.sinkLevel = sinkLevel;
  }

  public SnakeCellBorder(Color wall) {
    this.wallColor = wall;
  }

  public SnakeCellBorder(int sinkLevel, Color wall) {
    this.sinkLevel = sinkLevel;
    this.wallColor = wall;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
    g.setColor(getWallColor());


      g.drawRoundRect(x, y, w - 1, h - 1, sinkLevel, sinkLevel);

  }

  public Insets getBorderInsets(Component c) {
    return new Insets(sinkLevel, sinkLevel, sinkLevel, sinkLevel);
  }

  public Insets getBorderInsets(Component c, Insets i) {
    i.left = i.right = i.bottom = i.top = sinkLevel;
    return i;
  }

  public boolean isBorderOpaque() {
    return true;
  }

  public int getSinkLevel() {
    return sinkLevel;
  }

  public Color getWallColor() {
    return wallColor;
  }
}
