package com.gfigroup.ts.ui.controls.snake;

import com.gfigroup.ts.ui.controls.tablex.NavTable;
import com.gfigroup.ts.ui.controls.tablex.TableColumnFactory;
import com.gfigroup.ts.ui.controls.tablex.TableColumnRepository;
import com.gfigroup.ts.ui.controls.tablex.TableModelCoordinates;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class SnakeTable extends NavTable
{
    private SnakeTableModel stm_;
    private int humps_;
    private final ColumnTracker ct_;
    private Color rowOddColor_, rowEvenColor_, rowEmptyColor_;

    class ColumnTracker {
        private int from_ = -1;
        private int to_ = -1;

        private int resizeIndex = -1;

        private boolean moving_ = false;
        private boolean resizing_ = true;

        void openForMove()        { moving_ = true;}
        void openForResize()      { resizing_ = true;}
        boolean isOpenForMove()   { return moving_; }
        boolean isOpenForResize() { return resizing_; }
        void close() { moving_ = false; resizing_ = false; }

        public ColumnTracker() { close(); }

        public void move(int from, int to) { from_ = from; to_ = to;}
        public void resize(int col) { resizeIndex = col; }
    }



    static private TableColumnModel generateColumnModel()
    {
       return new DefaultTableColumnModel();
    }

    private void customizeColumnOps()
    {
       TableColumnModel cm = this.getColumnModel();


       cm.addColumnModelListener(new NullTableColumnModelListener()
       {
           public void columnMoved(TableColumnModelEvent e)
           {
               int from = e.getFromIndex();
               int to = e.getToIndex();

               if(from != to) {
                   ct_.openForMove();
                   ct_.move(from, to);
               }
           }

           public void columnMarginChanged(ChangeEvent e)
           {
               ct_.openForResize();
           }
       });



        final SnakeTable table = this;

        JTableHeader header =  getTableHeader();

        header.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (ct_.isOpenForMove()) {
                    table.tandemMoveColumn(ct_.from_, ct_.to_);
                } else if (ct_.isOpenForResize()) {
                    table.tandemResizeColumn(ct_.resizeIndex);
                }
                ct_.close();
            }

            @Override
            public void mousePressed(MouseEvent evt)
            {
                JTable table = ((JTableHeader) evt.getSource()).getTable();
                TableColumnModel colModel = table.getColumnModel();

                // The index of the column whose header was clicked
                int vColIndex = colModel.getColumnIndexAtX(evt.getX());
                //int mColIndex = table.convertColumnIndexToModel(vColIndex);

                // Return if not clicked on any column header
                if (vColIndex == -1) {
                    return;
                }

                // Determine if mouse was clicked between column heads
                Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
                if (vColIndex == 0) {
                    headerRect.width -= 3;    // Hard-coded constant
                } else {
                    headerRect.grow(-3, 0);   // Hard-coded constant
                }
                if (!headerRect.contains(evt.getX(), evt.getY())) {
                    // Mouse was clicked between column heads
                    // vColIndex is the column head closest to the click

                    // vLeftColIndex is the column head to the left of the click
                    int vLeftColIndex = vColIndex;
                    if (evt.getX() < headerRect.x) {
                        vLeftColIndex--;
                    }
                    ct_.resize(vLeftColIndex); // not sure which is the right one here
                }
            }

        });


    }

    public SnakeTable(SnakeTableModel tm, TableColumnRepository columnRepository)
    {
        super(tm, columnRepository, generateColumnModel());

        addGutterColumn(columnRepository);
        
        tm.setModelColumnCount(columnRepository.size() - 1);

        // end column augmentation

        setRowColorScheme(Color.white, new Color(235, 235, 235), Color.white);

        ct_ = new ColumnTracker();

        customizeColumnOps();

        setShowGrid(true);

        stm_ = tm;

        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                SnakeTable.this.recomputeSnake();
            }
        });



    }

    public void addGutterColumn(TableColumnRepository columnRepository)
    {
        //----- create a new column specific to snaking
        TableColumnFactory tcf = new TableColumnFactory(" ");
        tcf.setHeaderRenderer(new GutterCellRenderer(Color.white));
        tcf.setCellRenderer(new GutterCellRenderer(Color.white));
        tcf.setPreferredWidth(8);
        tcf.setIdentifier(this);
        tcf.setMinWidth(2);

        columnRepository.addColumnFactory(tcf);
    }


    public void createDefaultTableSelectionModel() {
        setTableSelectionModel(new SnakeNtmTableSelectionModel(this));
    }


    public void setRowColorScheme(Color odd, Color even, Color empty)
    {
       rowOddColor_ = odd; rowEvenColor_ = even; rowEmptyColor_ = empty;
    }


    /**
     * Returns true if the specified index is in the valid range of rows,
     * and the row at that index is selected.
     *
     * @return true if <code>row</code> is a valid index and the row at
     *         that index is selected (where 0 is the first row)
     */
    @Override
    public boolean isRowSelected(final int row)
    {
        return selectionModel.isSelectedIndex(row);
    }

    @Override
    public boolean isCellSelected(final int row, final int column)
    {
        if (!getRowSelectionAllowed() && !getColumnSelectionAllowed()) {
            return false;
        }
        return (!getRowSelectionAllowed() ||
                isRowSelected(row)) &&
                   (!getColumnSelectionAllowed() || isColumnSelected(column));
    }




    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col)
    {
        Component comp = super.prepareRenderer(renderer, row, col);

         if(customPrepareRenderer_) {
             int mcol = stm_.snakeToModelColumn(col);

             if(mcol == stm_.trueModelColumnCount_) {
                 return comp;
             }

             int mrow = stm_.snakeToModelRow(row, col);

             if (mrow >= stm_.getNestedTableModel().getRowCount()) {
                 comp.setBackground(rowEmptyColor_);
             } else if (shouldCellBeHighlighted(row, col)) {
                 comp.setBackground(Color.yellow);
                 comp.setForeground(Color.black);
             } else if (shouldCellBeDarkened(row, col)) {
                 int hump = stm_.snakeColumnToHump(col) & 1;

                 //even index, selected or not selected
                 Color bg = comp.getBackground();
                  if ((row & 1) == 0) {
                     comp.setBackground((hump & 1) == 0 ? darkenBy(bg,0.9) : darkenBy(darkenBy(bg,0.9),0.8));
                 } else {
                     comp.setBackground((hump & 1) == 0 ? bg : darkenBy(bg, 0.9));
                 }

             }
         }
        return comp;
    }


    int getPreferredWidth()
    {
        int newColumnWidths = 0;

        TableColumnModel cm = this.getColumnModel();
        for(int i = 0; i < cm.getColumnCount(); ++i)
        {
            TableColumn tc = cm.getColumn(i);
            newColumnWidths += tc.getPreferredWidth();
        }
        return newColumnWidths;
    }

    int getRowCountThatFits()
    {
        int tableheight =  this.getParent().getHeight();     // how tall is the table space
        int rowheight = this.getRowHeight();    // how big is one row?
        int room = (tableheight / rowheight);  // how many rows (excluding header) can I have?

        return Math.max(room, 1);                // minimum one row
    }

    int getPreferredHeight()
    {
       return getRowCountThatFits() * this.getRowHeight();
    }
    
    protected boolean shouldCellBeDarkened(final int row, final int column)
    {
        return true;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(getPreferredWidth(), getPreferredHeight());
    }

    public void recomputeSnake()
    {
        SnakeTableModel stm = (SnakeTableModel)this.getModel();

        int room = getRowCountThatFits();

        int oldRowCount = stm.getSnakeRowCount();
        stm.setSnakeRowCount(room);

        resetHumps(stm.requiredHumpCount() );
        invalidateCellSpanCache();
        invalidateCellRendererCache();
        
        if (oldRowCount != stm.getSnakeRowCount()) {
            repaint();
        }
    }


    private void addHump(int humpIndex)
     {
         int columnsPerHump = tcr_.size();
         int humpFactor = humpIndex * columnsPerHump;

         int i = 0;

         for(TableColumnFactory cf: tcr_.getColumnFactories())
         {
             TableColumn tc = cf.createColumn(i++ + humpFactor);
             tc.setCellRenderer(stm_.decorateCellRenderer(tc.getCellRenderer()));
             tc.setHeaderRenderer(stm_.decorateHeaderCellRenderer(tc.getHeaderRenderer()));
             tc.setResizable(true);
             this.getColumnModel().addColumn(tc);
         }

     }

     private void removeHump(int humpIndex)
     {
         int columnsPerHump = tcr_.size();
         int humpFactor = humpIndex * columnsPerHump;

         try {
             // for now assumes that all the columns in the columnFactories are used
             // in future column editors will operate on them, hide some, etc.

             TableColumnModel cm = this.getColumnModel();
             for( int cindex = tcr_.size() - 1; cindex >= 0; --cindex ) {
                 TableColumn tc = cm.getColumn(cindex + humpFactor);
                 cm.removeColumn(tc);
             }

         } catch(Exception e) {
             System.out.println("exception removing columns");
         }
     }



     private void resetHumps(int humpCount)
     {
        if(humpCount == humps_){
            return;
        }


         while(humps_ > humpCount) {
             removeHump(humps_-1);
             --humps_;
         }

         while(humps_ < humpCount ) {
             addHump(humps_);
             ++humps_;
         }
     }

    // brute force version
     public void tandemMoveColumn(int from, int to)
     {
         // kill all the columns
         while(humps_ > 0) {
            removeHump(humps_-1);
            --humps_;
         }


         // normalize the operation
         from %=  tcr_.size();
         to   %= tcr_.size();

         //----- move everything around in the column repository? (not if this is shared, bud)
         tcr_.move(from, to);

         // rebuild all the columns
         recomputeSnake();

     }

    @Override
    protected int tandemResizeColumn(int resizeIndex)
    {
        int modIndex = super.tandemResizeColumn(resizeIndex);
        TableColumnModel cm = getColumnModel();
        TableColumn col = cm.getColumn(resizeIndex);

        int width = col.getPreferredWidth();

        // set all the current columns to avoid regenerating
        for(int i = modIndex; i < cm.getColumnCount(); i += tcr_.size()) {
            cm.getColumn(i).setPreferredWidth(width);
        }
        return modIndex;
    }

    private int _recurse = 0;
    protected int[] findNextNavigableCellInColumn(int row, int column, int rowCount) { // down, enter
        try
        {
            ++_recurse;

            do
            {
                //   if (row < rowCount)
                //   { // at
                if (_recurse > 1 && isCellNavigable(row, column))
                {
                    return new int[]{row, column};
                }
                //   }

                row++;
                if (row >= rowCount)
                { // at the end

                    return findNextNavigableCellInColumn(0, column + tcr_.size(), rowCount);
                }
                if (isCellNavigable(row, column))
                {
                    return new int[]{row, column};
                }
            }

            while (true);
        } finally
        {
            --_recurse;
        }

    }

    protected int[] findPreviousNavigableCellInColumn(int row, int column) { // up

        try
        {
            ++_recurse;
            do
            {
                if (_recurse > 1 && row >= 0)
                { // at
                    if (isCellNavigable(row, column))
                    {
                        return new int[]{row, column};
                    }
                }

                row--;
                if (row < 0)
                { // at the first cell
                    return findPreviousNavigableCellInColumn(stm_.getRowCount() - 1, column - tcr_.size());
                }
                if (isCellNavigable(row, column))
                {
                    return new int[]{row, column};
                }
            }
            while (true);
        } finally {
            --_recurse;
        }
    }





    private KeyStroke _navigationKeyStroke2;

    @Override
    public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {

        try {
            if (pressed) {
                if (isNavigationKey(ks)) {
                    _navigationKeyStroke2 = ks;
                }
            }
            return super.processKeyBinding(ks, e, condition, pressed);
        }
        finally {
            _navigationKeyStroke2 = null;
        }


    }

   private boolean equalsOneOf(String a, String... against)
   {
       for(String s: against) {
           if(a.equals(s))
               return true;
       }
       return false;
   }

   private int prevRow = -1, prevCol = -1;
   private int curRow = -1, curCol = -1;

   private boolean modInput(int row, int column, KeyStroke ks, Object action)
   {
        prevRow = curRow;
        prevCol = curCol;
        curRow = row;
        curCol = column;

        int rows = stm_.getRowCount();
        if(action == null)
           return false;

       if (prevRow == curRow)
       {
           String as = action.toString();
           if (curRow == rows - 1 && equalsOneOf(as, "selectNextRow", "selectNextRowCell"))
           {
               TableModelCoordinates tmc = stm_.convertToNested(row, column);
               if (tmc.row < tmc.model.getRowCount())
               {
                   return true;
               }


           }
           else if (curRow == 0 && equalsOneOf(as, "selectPreviousRow", "selectPreviousRowCell"))
           {
               TableModelCoordinates tmc = stm_.convertToNested(row, column);
               if (tmc.row >= rows)
               {
                   return true;
               }

           }

       }

       return false;
   }

   @Override
    public void changeSelection(int row, int column, boolean toggle, boolean expand) {
        // This method is called when the user tries to move to a different cell.
        // If the cell they're trying to move to is not navigable, we look for
        // then next cell in the proper direction that is editable.


        Object action = null;

        if (_navigationKeyStroke2 != null && isNavigationKey(_navigationKeyStroke2)) {
            InputMap map = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            action = map.get(_navigationKeyStroke2);

//            System.out.printf("changeSelection(r:%d,c:%d,t:%s,x:%s) where keystroke:%s, action:%s\n",
//                    row,column, toggle, expand,_navigationKeyStroke2, action);
        }



       if (modInput( row,  column, _navigationKeyStroke2, action) || !isCellNavigable(row, column)) {

            // still scroll it visible first.
            scrollRectToVisible(getCellRect(row, column, true));

            // Find the row and column we're coming from.
            int currentRow = getSelectedRow();
            int currentColumn = getSelectedColumn();

            // If we can't find a cell to move to, we'll stay here.
            int nextRow = row;
            int nextCol = column;

            int rowCount = getRowCount();
            int columnCount = getColumnCount();

            int[] newCell = null;
            

            if (action != null) {

                if ("selectNextColumnCell".equals(action)) { // tab
                    newCell = findNextNavigableCell(row, column, currentRow, currentColumn, rowCount, columnCount);
                }
                else if ("selectPreviousColumnCell".equals(action)) { // shift-tab
                    newCell = findPreviousNavigableCell(row, column, currentRow, currentColumn, rowCount, columnCount);
                }
                else if ("selectNextRowCell".equals(action)) { // enter
                    newCell = findNextNavigableCellVertically(row, column, currentRow, currentColumn, rowCount, columnCount);
                }
                else if ("selectPreviousRowCell".equals(action)) { // shift-enter
                    newCell = findPreviousNavigableCellVertically(row, column, currentRow, currentColumn, rowCount, columnCount);
                }
                else if ("selectNextColumn".equals(action) || "selectNextColumnExtendSelection".equals(action)) { // left or shift+left
                    if (expand) {
                        nextCol--; // in case there is no navigable columns after this column, keep selections no change
                    }
                    newCell = findNextNavigableCellInRow(row, column, columnCount);
                }
                else if ("selectPreviousColumn".equals(action) || "selectPreviousColumnExtendSelection".equals(action)) { // right or shift+right
                    if (expand) {
                        nextCol++; // in case there is no navigable columns before this column, keep selections no change
                    }
                    newCell = findPreviousNavigableCellInRow(row, column);
                }
                else if ("selectNextRow".equals(action) || "selectNextRowExtendSelection".equals(action)) { // down or shift+down
                    if (expand) {
                        nextRow--; // in case there is no navigable rows after this row, keep selections no change
                    }
                    newCell = findNextNavigableCellInColumn(row, column, rowCount);
                }
                else if ("selectPreviousRow".equals(action) || "selectPreviousRowExtendSelection".equals(action)) { // up or shift+up
                    if (expand) {
                        nextRow++; // in case there is no navigable rows before this row, keep selections no change
                    }
                    newCell = findPreviousNavigableCellInColumn(row, column);
                }
                else if ("selectFirstColumn".equals(action)) { // home
                    newCell = findNextNavigableCellInRow(row, column, columnCount);
                }
                else if ("selectFirstRow".equals(action)) { // ctrl-home
                    newCell = findNextNavigableCellInColumn(row, column, rowCount);
                }
                else if ("selectLastColumn".equals(action)) { // end
                    newCell = findPreviousNavigableCellInRow(row, column);
                }
                else if ("selectLastRow".equals(action)) { // ctrl-end
                    newCell = findPreviousNavigableCellInColumn(row, column);
                }
                else if ("scrollUpChangeSelection".equals(action)) { // page up
                    newCell = findPreviousNavigableCellInColumn(row, column);
                }
                else if ("scrollLeftChangeSelection".equals(action)) { // ctrl-page up
                    newCell = findNextNavigableCellInRow(row, column, columnCount);
                }
                else if ("scrollDownChangeSelection".equals(action)) { // page down
                    newCell = findNextNavigableCellInColumn(row, column, rowCount);
                }
                else if ("scrollRightChangeSelection".equals(action)) { // ctrl-page up
                    newCell = findPreviousNavigableCellInRow(row, column);
                }

                if (newCell == null && !expand) {
                    newCell = new int[]{currentRow, currentColumn};
                }
            }

            if (newCell != null) {
                nextRow = newCell[0];
                nextCol = newCell[1];
            }
            // Go to the cell we found. If newCell is null, we keep the current selection
            super.changeSelection(nextRow, nextCol, toggle, expand);

        }
        else {
            // It's an editable cell, so leave the selection here.
            super.changeSelection(row, column, toggle, expand);
        }
    }




}
