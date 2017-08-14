package com.baliset.ui.controls.snake.decoration;

import javax.swing.border.LineBorder;
import java.awt.*;


public class SnakeLineBorder extends LineBorder

{
  public SnakeLineBorder(Color color)
  {
    super(color);
  }

  public SnakeLineBorder(Color color, int thickness)
  {
    super(color, thickness);
  }

  public SnakeLineBorder(Color color, int thickness, boolean roundedCorners)
  {
    super(color, thickness, roundedCorners);
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
  {
    Color oldColor = g.getColor();
    int i;

    g.setColor(lineColor);
    if (!roundedCorners) {
      g.drawRect(x, y, width - 1, height - 1);
    } else {
      Shape oldclip = g.getClip();

      g.setClip(x + thickness - 1, y - 1, width + 1, height + 1);
      g.drawRect(x, y, width, height);
      g.setClip(x, y, thickness, thickness);

      g.drawRoundRect(x, y, width, height, thickness, thickness);
      g.setClip(oldclip);
    }
    g.setColor(oldColor);
  }
}
