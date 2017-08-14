package com.baliset.ui.controls.tablex;

import javax.swing.*;
import java.awt.*;

public interface DepthAwareTableCellRenderer
{
    Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column, int depth);

}
