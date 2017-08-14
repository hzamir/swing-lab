package com.gfigroup.ts.ui.controls.basic;

public interface ShowHideColumnModel
{
     void hideColumn(ColumnIdentifier identifier);
     void hideColumns(ColumnIdentifier... identifiers);
     void unhideColumn(ColumnIdentifier identifier);
     void unhideColumns(ColumnIdentifier... identifiers);
     boolean isHidden(ColumnIdentifier identifier);

     int getWidth(ColumnIdentifier identifier);
     void setWidths(ColumnIdentifier identifier, int preferredWidth, int minimumWidth);

}
