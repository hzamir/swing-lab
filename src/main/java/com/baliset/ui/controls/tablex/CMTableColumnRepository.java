package com.baliset.ui.controls.tablex;


import com.baliset.ui.controls.basic.ColumnIdentifier;
import com.baliset.ui.controls.basic.ShowHideColumnModel;

import java.util.HashMap;

public class CMTableColumnRepository extends TableColumnRepository implements ShowHideColumnModel
{
    HashMap<Object, TableColumnFactory> map_;
    
    public CMTableColumnRepository()
    {
      map_ = new HashMap<Object, TableColumnFactory>();
    }


   @Override
   public void addColumnFactory(TableColumnFactory tcf)
   {
       //assert map_.get(tcf.getIdentifier()) == null;
       if (map_.containsKey(tcf.getIdentifier())) {
           return;
       }

       super.addColumnFactory(tcf);
       map_.put(tcf.getIdentifier(), tcf);
   }

    @Override
    protected void clear()
    {
        super.clear();
        map_.clear();
    }


    //--- ShowHideColumn implementation
    @Override
    public boolean isHidden(final ColumnIdentifier identifier)
    {
        TableColumnFactory tcf = map_.get(identifier);
        return !tcf.getVisible();
    }

    @Override
    public int getWidth(final ColumnIdentifier identifier)
    {
        TableColumnFactory tcf = map_.get(identifier);
        if(tcf != null )
        {
            return tcf.prefWidth_;
        }
        return -1; //^^^
    }

    @Override
    public void setWidths(final ColumnIdentifier identifier, final int prefWidth, final int minWidth)
    {
        TableColumnFactory tcf = map_.get(identifier);
        if(tcf != null )
        {
            tcf.setMinWidth(minWidth);      // don't like conflation of this pair, but seems to be needed for CM
            tcf.setPreferredWidth(prefWidth);
        }

    }

    @Override
    public void hideColumn(final ColumnIdentifier identifier)
    {
        TableColumnFactory tcf = map_.get(identifier);
        if(tcf.getVisible()) {
            tcf.setVisible(false);
        }
    }

    @Override
    public void unhideColumn(final ColumnIdentifier identifier)
    {
        TableColumnFactory tcf = map_.get(identifier);
        if(!tcf.getVisible()) {
            tcf.setVisible(true);
        }

    }

    @Override
    public void hideColumns(final ColumnIdentifier... identifiers)
    {
        for(ColumnIdentifier id: identifiers)
        {
           hideColumn(id);
        }
    }



    @Override
    public void unhideColumns(final ColumnIdentifier... identifiers)
    {
        for(ColumnIdentifier id: identifiers)
        {
            unhideColumn(id);
        }
    }


}
