package com.gfigroup.ts.ui.controls.tablex;

import javax.swing.*;
import java.awt.event.MouseEvent;

public interface ToolTipHelper {

    JToolTip createToolTip(NavTable navTable);
    String getToolTipText(NavTable navTable, MouseEvent event);
    
}
