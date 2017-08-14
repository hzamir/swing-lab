package com.baliset.ui.controls.tablex;

import javax.swing.table.TableCellRenderer;
public interface TableCellRendererFactory
{
    TableCellRenderer createRenderer(TableCellRenderer nestedRenderer);
}
