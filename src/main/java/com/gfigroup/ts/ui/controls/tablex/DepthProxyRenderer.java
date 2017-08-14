package com.gfigroup.ts.ui.controls.tablex;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DepthProxyRenderer implements TableCellRenderer
{
        private TableCellRenderer nestedRenderer_;
        public DepthProxyRenderer(TableCellRenderer nested)
        {
            nestedRenderer_ = nested;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            if(v != null && v != NullCell.sNullObject) {
                DepthExpandingTableModel.DepthProxy dp = (DepthExpandingTableModel.DepthProxy)v;
                v = dp.getObject();
                int depth = dp.getIndex();
                if(nestedRenderer_ instanceof DepthAwareTableCellRenderer)
                    return ((DepthAwareTableCellRenderer)nestedRenderer_).getTableCellRendererComponent(
                                    t, v,
                                    isSelected, hasFocus,
                                    row, column, depth);


                Component temp =  nestedRenderer_.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, column);
                if(depth > 0 && temp instanceof JLabel) {
                    ((JLabel) temp).setText("");
                    return temp;
                }


            }
            return nestedRenderer_.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, column);
        }
}