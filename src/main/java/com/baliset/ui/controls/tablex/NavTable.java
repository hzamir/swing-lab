package com.baliset.ui.controls.tablex;

import com.jidesoft.grid.CellSpanTable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public abstract class NavTable extends CellSpanTable
{

    protected TableColumnRepository tcr_;
    private final java.util.List<TablexColumnListener> tablexColumnListeners_ = new ArrayList<TablexColumnListener>();
    private boolean changeSelectionOnRightClick;
    private ToolTipHelper toolTipHelper;

    public NavTable(TableModel tm, TableColumnRepository tcr, TableColumnModel cm)
    {
        super(tm, cm);
        tcr_ = tcr;



    }
    
    public void setToolTipHelper(ToolTipHelper toolTipHelper) {
        this.toolTipHelper = toolTipHelper;
    }
    
    @Override
    public JToolTip createToolTip() {
        return toolTipHelper == null 
                    ? super.createToolTip() 
                    : toolTipHelper.createToolTip(this);
    }
    
    @Override
    public String getToolTipText(MouseEvent event) {
        return toolTipHelper == null
                    ? super.getToolTipText(event)
                    : toolTipHelper.getToolTipText(this, event);
    }
    
    public void setChangeSelectionOnRightClick(boolean changeSelection) {
		this.changeSelectionOnRightClick = changeSelection;
	}
    
    public boolean isChangeSelectionOnRightClick() {
		return changeSelectionOnRightClick;
	}
    
    @Override
    protected void processMouseEvent(MouseEvent e) {
    	if (isChangeSelectionOnRightClick()
    			&& e.getID() == MouseEvent.MOUSE_PRESSED 
    			&& SwingUtilities.isRightMouseButton(e)) {
    		int row = rowAtPoint(e.getPoint());
    		int column = columnAtPoint(e.getPoint());
    		changeSelection(row, column, false, false);
    	}
    	super.processMouseEvent(e);
    }

    public void addColumnListener(TablexColumnListener tcl){
        tablexColumnListeners_.add(tcl);
    }
    public void removeColumnListener(TablexColumnListener tcl)
    {
        tablexColumnListeners_.remove(tcl);
    }

    public TableColumnRepository  getTableColumnRepository() { return tcr_;}



    protected boolean customPrepareRenderer_ = false;
    public void setPrepareRenderer(boolean prepare)
    {
        customPrepareRenderer_ = prepare;
    }

    static protected Color darkenBy(Color c, double factor)
    {
        if (c == null) {
            return null;
        }
        
        return new Color(
                Math.max((int)(c.getRed()  *factor), 0),
                Math.max((int)(c.getGreen()*factor), 0),
                Math.max((int)(c.getBlue() *factor), 0)
        );
    }

    static protected Color brightenBy(Color c, double factor)
    {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                Math.min((int)(g/factor), 255),
                Math.min((int)(b/factor), 255));
    }




    @Override
    protected boolean isNavigationKey(KeyStroke ks)
    {
        if (ks == null)
        {
            return false;
        }

        switch (ks.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                return true;
            default:
                return super.isNavigationKey(ks);
        }
    }

    public boolean shouldCellBeHighlighted(final int row, final int column)
    {
        return getTableSelectionModel().isSelected(row, column);
    }

    protected void tandemMoveColumn(int from, int to)
    {
    }

    protected int tandemResizeColumn(int resizeIndex)
    {
        TableColumnModel cm = getColumnModel();
        TableColumn col = cm.getColumn(resizeIndex);

        int width = col.getPreferredWidth();
        int modIndex =  resizeIndex %  tcr_.size();

        // change the master version of the column width
        TableColumnFactory tcf = tcr_.getColumnFactory(modIndex);
        tcf.setPreferredWidth(width);

        for(TablexColumnListener tcl: tablexColumnListeners_)
        {
            tcl.columnResized(modIndex, width);
        }

        return modIndex;

    }

    public void modifyColumns()
    {
        TableColumnModel cm = getColumnModel();

        int count = cm.getColumnCount();
        for(int i = 0; i < count; ++i )
        {
            TableColumn col = cm.getColumn(i);
            int modIndex =  i %  tcr_.size();
            TableColumnFactory tcf = tcr_.getColumnFactory(modIndex);

            if(tcf.visible_) {
                  if(col.getPreferredWidth() == 0) {  // evident that it was "hidden"
                      col.setMinWidth(tcf.minWidth_);
                      col.setPreferredWidth(tcf.prefWidth_);
                      col.setWidth(tcf.prefWidth_);
                  }
            } else {
                  if(col.getPreferredWidth() != 0) {  // evidence that it was not "hidden"

                      // in cases where tcf widths were defaulted to -1, steal the new values
                      // in the columns for successful unhiding the columns later
                      tcf.setPreferredWidth(col.getPreferredWidth());
                      tcf.setMinWidth(col.getMinWidth());

                      col.setMinWidth(0);
                      col.setPreferredWidth(0);
                      col.setWidth(0);
                  }
            }
        }

    }

}
