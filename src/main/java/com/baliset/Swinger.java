package com.baliset;

import com.baliset.data.DataGenerator;
import com.baliset.data.Item;
import com.baliset.tabledemo.*;
import com.baliset.ui.controls.snake.SnakeTable;
import com.baliset.ui.controls.snake.SnakeTableModel;
import com.baliset.ui.controls.tablex.*;
import com.jidesoft.grid.TableSelectionModel;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Swinger extends JFrame
{
    private final NavTable table_;
    private CMTableColumnRepository tcr_;

    private final boolean optionSnaking_;
    private final boolean optionDepth_;
    private final boolean optionMulti_;
    private final boolean optionHeadersForMulti_;
    private final JTextArea tableLogArea_;

    private ArrayList<MyTableModel> btms_;

    static {
        com.jidesoft.utils.Lm.verifyLicense("GFI Group", "GFI Group Development", "UClzjlwjp46BiC1m1b0Pfc7x4LIqdTY1");
    }

    public Swinger(int initialRows,
                   int sectors,
                   boolean optionSnaking,              // do we incorporate snaking?
                   boolean optionDepth,                // do we expand depth records?
                   boolean optionMulti,                // do we show multiple table models?
                   boolean optionHeadersForMulti       // if multitables, do we show headers?
    )
    {
        //----- very basic Swing App initialization -----
        super("Swinger");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        int kWidth = 800;
        int kHeight = 400;
        setSize(kWidth, kHeight);

        optionSnaking_ = optionSnaking;
        optionDepth_ = optionDepth;
        optionMulti_ = optionMulti;
        optionHeadersForMulti_ = optionHeadersForMulti;

        //----- create a snake table and its accompanying scrollPane -----
        NestingTableModel tableModel = createTableEssentials(initialRows, sectors);

        table_ = createTopTable(tcr_, tableModel);

        JScrollPane stmScrollPane = createTableScrollPane(table_, true);

      // JTable simpleTable  = JideUtils.createJideTable(columnRepository_);
     //   JScrollPane simpleScrollPane = createTableScrollPane(simpleTable, false);

        //----- create some extra controls ----
        JButton btnShowHide         = createShowHideButton();
        JButton btnAdd              = createAddButton();
        JButton btnDeleteSelected   = createDeleteSelectedButton();
        JButton btnDeleteLast       = createDeleteLastButton();


        JTextField txtSearch        = createTextSearch();
        JButton btnSearch           = createSearchButton(txtSearch);

        createExpandButton();
        createCollapseButton();
        createExpandCollapseHeaderButton();

        tableLogArea_ = createTableLogArea();

        //---layout the components
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.add(txtSearch);
        buttonsPanel.add(btnSearch);
        buttonsPanel.add(btnShowHide);
        buttonsPanel.add(btnDeleteSelected);
        buttonsPanel.add(btnDeleteLast);


        Container contentPane = getContentPane();


        JSplitPane splitTB = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stmScrollPane, new JScrollPane(tableLogArea_));
        splitTB.setOneTouchExpandable(true);
        splitTB.setDividerLocation((kHeight / 3) * 2);
        splitTB.setResizeWeight(1.0);


        contentPane.add(buttonsPanel, BorderLayout.NORTH);
        contentPane.add(splitTB, BorderLayout.CENTER);
   //      contentPane.add(stmScrollPane, BorderLayout.CENTER);

        setLocationByPlatform(true);
        setVisible(true);

         logMsg("Hello, just created a window");

    }


    private TableColumnFactory columnToHide = null;

    private NestingTableModel createTableEssentials(int initialRows, int sectors)
    {
        NestingTableModel tableModel;
        int numModels = sectors > 0? sectors: 1;
        btms_ = new ArrayList<MyTableModel>();

        ArrayList<DepthExpandingTableModel<Item>> dxtms = new ArrayList<>();


        TableCellRendererFactory tcrf = new TableCellRendererFactory()
        {
            @Override
            public TableCellRenderer createRenderer(final TableCellRenderer nestedRenderer)
            {
                return new DepthProxyRenderer(nestedRenderer);  //To change body of implemented methods use File | Settings | File Templates.
            }
        };


        for(int i = 0; i <numModels; ++i )
        {
            MyTableModel<Item> btm = new MyTableModel<Item>("Sector " + i, null);
            btms_.add(btm);

            for (int j = 0; j < initialRows; ++j)
            {
                Item item = DataGenerator.generateItem(j + (i * initialRows));
                btm.addItem(item);
            }

            if(optionDepth_)  {
                dxtms.add(new DepthExpandingTableModel<Item>(btm, tcrf));
            }

        }

        TableHeaderRowAdapter hra = optionHeadersForMulti_ && sectors > 0? new MyHeaderRowAdapter(): null;

        if(optionMulti_)
            tableModel = new MultiTableModel((optionDepth_? dxtms: btms_),hra);
        else
            tableModel = (optionDepth_? dxtms.get(0): btms_.get(0));

        String[] colnames = {"First", "Last", "Num", "DInfo"};

        TableCellRenderer[] tcrs = {
                new MyCellRendererFirstName(),
                new MyCellRendererLastName(),
                new MyCellRendererEmployeeNo(),
                new MyCellRendererInfo()
        };

        TableColumnFactory tcf;


        JTableHeader th = new JTableHeader();
        TableCellRenderer hr = th.getDefaultRenderer();


        tcr_ = new CMTableColumnRepository();


        // iterate through our columns
        for (int i = 0; i < tcrs.length; ++i) {
            tcf = new TableColumnFactory(colnames[i]);
            tcf.setHeaderRenderer(hr);    // must be set to something for now
            tcf.setCellRenderer(tcrs[i]);
            tcf.setIdentifier(tcf);
            tcr_.addColumnFactory(tcf);

            if(i == 2)
                columnToHide = (TableColumnFactory)tcf.getIdentifier();

        }

         return tableModel;
    }


    private NavTable createTopTable(TableColumnRepository tcr, NestingTableModel tableModel)
    {
       if(optionSnaking_) {

        SnakeTableModel stm = new SnakeTableModel(tableModel);

        SnakeTable snakeTable = new SnakeTable(stm,  tcr);
        snakeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        snakeTable.setRowSelectionAllowed(false);
        snakeTable.setCellSelectionEnabled(true);

        snakeTable.setNonContiguousCellSelection(true);

        snakeTable.setPrepareRenderer(true);
        stm.setTable(snakeTable); //??? circular reference used to recomputeSnake from stm catching events from btm

        snakeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        return snakeTable;

       } else {

           NestingModelTable table = new NestingModelTable(tableModel, tcr_);
           table.setRowSelectionAllowed(false);
           table.setCellSelectionEnabled(true);
           table.setNonContiguousCellSelection(true);
           table.setPrepareRenderer(true);



           return table;
       }
    }

    private void assignAction(int key, int modifier, SwingerAction sa, Action action)
    {
          JPanel jc = (JPanel)this.getContentPane();
          ActionMap am = jc.getActionMap();

          am.put(sa.name(), action);

           KeyStroke ks;

          jc.registerKeyboardAction(action, sa.name(),
                  KeyStroke.getKeyStroke(key, modifier), JComponent.WHEN_IN_FOCUSED_WINDOW  );

    }

    private JScrollPane createTableScrollPane(final JTable table, boolean nestedResize)
    {
        JScrollPane scrollPane = new JScrollPane(table);

        if (nestedResize) {

//            scrollPane.addComponentListener(new ComponentAdapter()
//            {
//                @Override
//                public void componentResized(ComponentEvent e)
//                {
//                    if(table instanceof SnakeTable)
//                        ((SnakeTable) table).recomputeSnake();
//                    // todo: is there a more direct natural way to accomplish this?
////                    for (ComponentListener cl : table.getComponentListeners()) {
////                        cl.componentResized(e);
////                    }
//                }
//            });

        }

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }



    private JTextArea createTableLogArea()
      {
          final JTextArea textArea = new JTextArea(20, 120);

          table_.addMouseListener(new MouseAdapter()
          {
              @Override
              public void mouseClicked(MouseEvent e)
              {
                  Point point = e.getPoint();
                  JTable t = (JTable) e.getSource();
                  NestingTableModel ntm = (NestingTableModel) t.getModel();

                  int sc = t.columnAtPoint(point);
                  int sr = t.rowAtPoint(point);
                  TableModelCoordinates tmc = new TableModelCoordinates(ntm, sr, sc);

                  TableModelCoordinates tmc2 = ntm.convertToMostNested(tmc);

                  String prefix = String.format("(Table %d,%d) -> ('%s' %d,%d)", sr, sc,
                          tmc2.model instanceof  MyTableModel? ((MyTableModel)tmc2.model).getName() : "*N/A*",
                          tmc2.row, tmc2.col);
                  String msg;

                  if (tmc2.row >= 0)
                  {
                      Object o = tmc2.model.getValueAt(tmc2.row, tmc2.col);


                      if (o instanceof Item)
                      {
                          Item item = (Item) o;

                          msg = String.format("%s -> %s %s %d %d\n", prefix, item.getFirst(), item.getLast(), item.getNumber(), item.getDepth());

                          //  System.out.print(msg);
                      } else {
                          msg = String.format("%s -> UNKNOWN !! \n", prefix);
                      }


                  } else {
                      msg = String.format("%s -> HEADER\n", prefix);
                  }

                  logMsg(msg);
              }
          });

          return textArea;
      }

    private static SimpleDateFormat sTimeFormat =  new SimpleDateFormat("HH:mm:ss.SSS");

    private void logMsg(String msg)
    {
        String prefix = sTimeFormat.format(new Date());

        tableLogArea_.setText(prefix + "> " + msg + tableLogArea_.getText());
        tableLogArea_.setCaretPosition(0);
    }

    private JButton createExpandCollapseHeaderButton()
    {
        SwingerAction sa = SwingerAction.ExpandCollapseHeader;

        AbstractAction headerAction = new AbstractAction(sa.getLabel())
        {
            public void actionPerformed(ActionEvent e)
            {
                TableModelCoordinates topTmc = getSelectedPoint(table_);
                NestingTableModel ntm = (NestingTableModel)topTmc.model;

                if(topTmc.row >= 0 && topTmc.col >= 0)
                {
                    TableModelCoordinates btmPoint = ntm.convertToMostNested(topTmc);
                    TableModelCoordinates dtmPoint = ntm.convertToNested(topTmc);

                    MyTableModel<Item> btm = (MyTableModel<Item>) btmPoint.model;

                    if(btmPoint.row >= 0)
                    {
                        btm.toggleHeader();

                        btm.sendUpdate(0, Integer.MAX_VALUE);
                        preserveSelection(topTmc, dtmPoint);
                    }
                }
                table_.invalidateCellSpanCache();
                table_.invalidateCellRendererCache();

            }

        };

        assignAction(KeyEvent.VK_H, KeyEvent.CTRL_MASK, sa, headerAction);

        return new JButton(headerAction);
    }

    private boolean showHideButtonPerformsShowAction_ = false;
    private JButton showHideButton_ = null;
    private JButton createShowHideButton()
    {
        SwingerAction sa = SwingerAction.ShowHideColumns;

        AbstractAction action = new AbstractAction(sa.getLabel())
        {
            public void actionPerformed(ActionEvent e)
            {
               if(showHideButtonPerformsShowAction_)
                   tcr_.unhideColumn(columnToHide);
               else
                   tcr_.hideColumn(columnToHide);

               table_.modifyColumns();

               showHideButtonPerformsShowAction_ = !showHideButtonPerformsShowAction_;
               showHideButton_.setText(showHideButtonPerformsShowAction_ ? "Show Col": "Hide Col");

            }
        };

        assignAction(KeyEvent.VK_C, KeyEvent.CTRL_MASK, sa, action);
        showHideButton_ =   new JButton(action);
        return showHideButton_;
    }



    private JButton createAddButton()
      {
          SwingerAction sa = SwingerAction.Append;

          AbstractAction appendItemAction = new AbstractAction(sa.getLabel())
              {
                  public void actionPerformed(ActionEvent e)
                  {
                    NestingTableModel ntm = (NestingTableModel) table_.getModel();
                    int lastRow = ntm.getRowCount() - 1;

                    if(lastRow >= 0) {
                        TableModelCoordinates tmc = ntm.convertToMostNested(new TableModelCoordinates(ntm, lastRow, 0));
                        ((MyTableModel) tmc.model).addItem(DataGenerator.generateItem(tmc.model.getRowCount()), true);

                    }
                      table_.invalidateCellSpanCache();
                      table_.invalidateCellRendererCache();

                  }
              };

          assignAction(KeyEvent.VK_A, KeyEvent.CTRL_MASK, sa, appendItemAction);

          return new JButton(appendItemAction);
      }



//    private void preserveSelection2(TableModelCoordinates btmc)
//    {
//
//        TableModelCoordinates ttmc = ((NestingTableModel)btmc.model).convertToLeastNested(btmc);
//
//
//        if(optionSnaking_) {
//
//            int lastRow = dtmPoint.model.getRowCount() - 1;
//            int mrow = Math.min(dtmPoint.row, lastRow);
//            int mcol = dtmPoint.col;
//            SnakeTableModel stm = (SnakeTableModel)topTmc.model;
//            if(lastRow >= 0) {
//                selRow = stm.modelToSnakeRow(mrow);
//                selCol = stm.modelToSnakeColumn(mrow, mcol);
//            } // end if anything can be left selected after delete
//
//        } else {
//            selRow = Math.min(selRow, topTmc.model.getRowCount() - 1);
//        }
//        if(selRow >= 0)
//        {
//            table_.setRowSelectionInterval(selRow, selRow);
//            table_.setColumnSelectionInterval(selCol, selCol);
//        }
//    }



    private void preserveSelection(TableModelCoordinates topTmc,  TableModelCoordinates dtmPoint)
    {
        int selRow = topTmc.row;
        int selCol = topTmc.col;
        
        if(optionSnaking_) {
            int lastRow = dtmPoint.model.getRowCount() - 1;
            int mrow = Math.min(dtmPoint.row, lastRow);
            int mcol = dtmPoint.col;
            SnakeTableModel stm = (SnakeTableModel)topTmc.model;
            if(lastRow >= 0) {
                selRow = stm.modelToSnakeRow(mrow);
                selCol = stm.modelToSnakeColumn(mrow, mcol);
            } // end if anything can be left selected after delete

       } else {
            selRow = Math.min(selRow, topTmc.model.getRowCount() - 1);
       }
        if(selRow >= 0)
        {
            table_.setRowSelectionInterval(selRow, selRow);
            table_.setColumnSelectionInterval(selCol, selCol);
        }
    }


    public TableModelCoordinates getSelectedPoint(JTable table)
    {
        return new TableModelCoordinates(table.getModel(), table.getSelectedRow(), table.getSelectedColumn());
    }


    private JButton createDeleteSelectedButton()
    {
        SwingerAction sa = SwingerAction.DeleteSelected;

        AbstractAction delSelectedItemAction = new AbstractAction(sa.getLabel())
            {
                public void actionPerformed(ActionEvent e)
                {
                    TableModelCoordinates topTmc = getSelectedPoint(table_);   // stmPoint is guaranteed good
                    NestingTableModel topModel = (NestingTableModel)topTmc.model;

                    if(topTmc.row >= 0 && topTmc.col >= 0) {

                        //TableModelPoint return values (as opposed to simple Points) are not exploited here
                        // partly because we need to cast them anyway


                        TableModelCoordinates btmPoint = topModel.convertToMostNested(topTmc);
                        if(btmPoint.row >= 0)
                        {
                            TableModelCoordinates dtmPoint = ((NestingTableModel)topTmc.model).convertToNested(topTmc);
                            ((MyTableModel<Item>) btmPoint.model).deleteItemAt(btmPoint.row, true);
                            preserveSelection(topTmc, dtmPoint);
                        }

                        table_.invalidateCellSpanCache();
                        table_.invalidateCellRendererCache();
                    } // end if anything is selected at time of operation
                } // end action performed
        };
        assignAction(KeyEvent.VK_DELETE, KeyEvent.CTRL_MASK, sa, delSelectedItemAction);
        assignAction(KeyEvent.VK_X, KeyEvent.CTRL_MASK, sa, delSelectedItemAction);

        return new JButton(delSelectedItemAction); // end new JButton

    }

    private JTextField createTextSearch()
    {
        return new JTextField("Madison", 20);
    }

    private JButton createSearchButton(final JTextField txtSearch)
    {

        SwingerAction sa = SwingerAction.Search;

        AbstractAction searchAction =  new AbstractAction(sa.getLabel())
        {
            public void actionPerformed(ActionEvent e)
            {
                String text = txtSearch.getText();

                if(text.isEmpty())
                    return;

                for(DepthTableModel<Item> ctm: btms_)
                {
                    int rowCount =  ctm.getRowCount();
                    for(int i = 0; i < rowCount; ++i)
                    {
                        Item item = (Item)ctm.getValueAt(i,0);

                        //matches only whole field, but case insensitive, at least
                        if(text.compareToIgnoreCase(item.getLast()) == 0)
                        {
                            TableSelectionModel tsm = table_.getTableSelectionModel();
                            tsm.clearSelection();
                            TableModelCoordinates tmc = new TableModelCoordinates(ctm, i, 0);
                            tmc = ctm.convertToLeastNested(tmc);
                            tsm.addSelection(tmc.row,tmc.col);
                            return;
                        }

                    }
                }

            }
        };


        return new JButton(searchAction);

    }


    private JButton createDeleteLastButton()
    {
        SwingerAction sa = SwingerAction.DeleteLast;

         AbstractAction deleteLastItemAction =  new AbstractAction(sa.getLabel())
            {
                public void actionPerformed(ActionEvent e)
                {
                    NestingTableModel ntm = (NestingTableModel)table_.getModel();  
                     
                    int rowIndex = ntm.getRowCount()-1;
                    if(rowIndex >= 0) {
                        
                        TableModelCoordinates tmc = ntm.convertToMostNested(new TableModelCoordinates(ntm,rowIndex, 0));
                        if (tmc.row >= 0)
                        {
                            ((MyTableModel<Item>) tmc.model).deleteItemAt(tmc.row, true);
                        }
                    }

                    table_.invalidateCellSpanCache();
                    table_.invalidateCellRendererCache();

                }
            };
        assignAction(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_MASK /*InputEvent.SHIFT_DOWN_MASK*/, sa, deleteLastItemAction);

        return new JButton(deleteLastItemAction);
    }

    private JButton createCollapseButton()
    {
        SwingerAction sa = SwingerAction.Collapse;

        AbstractAction collapseItemDepthAction =  new AbstractAction(sa.getLabel())
            {
                public void actionPerformed(ActionEvent e)
                {
                    TableModelCoordinates topTmc = getSelectedPoint(table_);
                    NestingTableModel ntm = (NestingTableModel)topTmc.model;
                    
                    if(topTmc.row >= 0 && topTmc.col >= 0)
                    {
                        TableModelCoordinates btmPoint = ntm.convertToMostNested(topTmc);
                        TableModelCoordinates dtmPoint = ntm.convertToNested(topTmc);

                        MyTableModel<Item> btm = (MyTableModel<Item>) btmPoint.model;

                        if(btmPoint.row >= 0)
                        {
                            Item item = (Item) btm.getRowAt(btmPoint.row);
                            item.collapse();

                        btm.sendUpdate(btmPoint.row, btmPoint.row);
                        preserveSelection(topTmc, dtmPoint);
                        }
                    }
                    table_.invalidateCellSpanCache();
                    table_.invalidateCellRendererCache();

                }
            };
        assignAction(KeyEvent.VK_C, KeyEvent.CTRL_MASK /*InputEvent.SHIFT_DOWN_MASK*/, sa, collapseItemDepthAction);

        return new JButton(collapseItemDepthAction);

    }

       private JButton createExpandButton() {
           SwingerAction sa = SwingerAction.Expand;

           AbstractAction expandDepthAction = new AbstractAction(sa.getLabel()) {
               public void actionPerformed(ActionEvent e) {
                   TableModelCoordinates topTmc = getSelectedPoint(table_);
                   NestingTableModel ntm = (NestingTableModel)topTmc.model;
                   if (topTmc.col >= 0 && topTmc.row >= 0) {
                       TableModelCoordinates btmPoint = ntm.convertToMostNested(topTmc);
                       TableModelCoordinates dtmPoint = ntm.convertToNested(topTmc);

                       MyTableModel<Item> btm = (MyTableModel<Item>) btmPoint.model;
                       Item item = (Item) btm.getRowAt(btmPoint.row);
                       item.expand();
                       btm.sendUpdate(btmPoint.row, btmPoint.row);
                       preserveSelection(topTmc, dtmPoint);
                   }
                   table_.invalidateCellSpanCache();
                   table_.invalidateCellRendererCache();

               }
           };
           assignAction(KeyEvent.VK_E, KeyEvent.CTRL_MASK /*InputEvent.SHIFT_DOWN_MASK*/, sa, expandDepthAction);

           return new JButton(expandDepthAction);

       }


}
