package com.gfigroup.ts.ui.controls.tablex;

import java.util.EventListener;


public interface TablexColumnListener extends EventListener
{
     void columnResized(int columnIndex, int size);
}
