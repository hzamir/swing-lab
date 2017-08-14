package com.baliset.ui.controls.tablex;


import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import static javax.swing.event.TableModelEvent.*;

public class DepthExpandingTableModel<T> extends DelegatingTableModel implements TableModelListener
{

    static public class DepthProxy<R>
    {
        public DepthProxy(R o, int index)
        {
            obj_ = o;
            index_ = index;
        }

        public Object getObject() { return obj_; }
        public int getIndex()     { return index_; }

        @Override public boolean equals(Object o)
        {
            DepthProxy<R> dp = (DepthProxy<R>) o;
            return (this == dp) || (obj_.equals(dp.obj_)) && index_.equals(dp.index_);
        }

        @Override public int hashCode()
        {
            return 31 * obj_.hashCode() + index_.hashCode();
        }

        protected R obj_;  // cannot be made final for sake of search subclass
        final private Integer index_;
    }
    static private class SearchProxy<S> extends DepthProxy<S>
    {
        SearchProxy(S o, int index)          { super(o,index);   }
        DepthProxy<S> mutate(S newObject)    { obj_ = newObject; return this; }
    }


    CrossIndexedArrayList<T>          shallow_;
    CrossIndexedArrayList<DepthProxy<T>> deep_;
    final TableCellRendererFactory rendererFactory_;


    public DepthExpandingTableModel(DepthTableModel<T> nestedModel, TableCellRendererFactory rendererFactory)
    {
        super(nestedModel);
        rendererFactory_ = rendererFactory;
        rebuildDeepAndShallow();
        nestedModel.addTableModelListener(this);
    }

    // difference between rebuild and reset is that rebuild starts from underlying model
    // and reset is using localData
    private void rebuildDeepAndShallow()
    {
        DepthTableModel<T> nestedModel = (DepthTableModel<T>)getNestedTableModel();
        shallow_   = new CrossIndexedArrayList<T>(nestedModel.getRowCount());
        deep_      = new CrossIndexedArrayList<DepthProxy<T>>(nestedModel.getRowCount());

        int count = nestedModel.getRowCount();
        for(int i = 0; i < count; ++i) {
            T item = nestedModel.getRowAt(i);
            int depth = nestedModel.getDepthByIndexOrObject(i, item);
            shallow_.add(item);
            for(int j = 0; j < depth; ++j)
            {
                deep_.add(new DepthProxy<T>(item,j));     // note: not slow since it adding to the end
            }
        }
    }

    @Override
    public int getRowCount()
    {
        return deep_.size();
    }

    @Override
    public Object getValueAt(int r, int c)
    {
       if(r >= deep_.size())
            return NullCell.sNullObject;

        DepthProxy<T> dp = deep_.get(r);
        r = shallow_.indexOf(dp.getObject());
           // todo: we are creating too many objects here, find an alternative to wrapping the cells,
           // one possibility is to have a special api by the depth cell renderer to handle this
        return new DepthProxy<Object>(getNestedTableModel().getValueAt(r,c), dp.getIndex());
    }

    @Override
    public void setValueAt(Object v, int r, int c)
    {
        // TODO: propagate depth index?
        TableModelCoordinates nestedCoordinates = convertToNested(r, c);
        nestedCoordinates.model.setValueAt(v, nestedCoordinates.row, nestedCoordinates.col);
    }

    @Override
    public boolean isCellEditable(int r, int c)
    {
        // TODO: propagate depth index?
        TableModelCoordinates nestedCoordinates = convertToNested(r, c);
        return nestedCoordinates.model.isCellEditable(nestedCoordinates.row, nestedCoordinates.col);
    }


    private int compressedToExpandedRow(int row)
    {
        // these special cases are used for inserts
        if(row == 0)               return 0;
        if(row == shallow_.size()) return deep_.size();

        return deep_.indexOf(new DepthProxy<T>(shallow_.get(row),0));
    }

    private int compressedToLastExpandedRow(int row)
    {
        int firstExpandedRow =  compressedToExpandedRow(row);
        return expandedToLastExpandedRow(firstExpandedRow);
    }


    private int expandedToLastExpandedRow(int expandedIndex)
    {
        int size = deep_.size();

        if(expandedIndex == size-1) // if it is the last one there cannot be more
            return expandedIndex;

        DepthProxy<T> first = deep_.get(expandedIndex);
        DepthProxy<T> temp =  deep_.get(expandedIndex+1);

        if(first.getObject() != temp.getObject())
            return expandedIndex;

        int i = expandedIndex + 1;

        // this loop could be eliminated by reusing depthProxy like object (or depthproxy itself)
        // in the shallow as well as the deep structure. In shallow, the 'index' would actually be
        // a count, so we don't have to loop to get our result, since the loop is usually
        // pretty short, we don't care for now.
        while((i < size) && (temp = deep_.get(i)).getObject() == first.getObject())
        {
            ++i;
        }

        return i - 1;

    }



    public int expandedToCompressedRow(int row)
    {
        return deep_.size() <= row? -1: shallow_.indexOf(deep_.get(row).getObject());
    }



    @Override
    public void tableChanged(TableModelEvent e)
    {
        int uRowFirst = e.getFirstRow();
        int uRowLast  = e.getLastRow();

        TableModelEvent newEvent = null;

        switch (e.getType()) {
            case INSERT:
                newEvent = insertRows(uRowFirst, uRowLast);
                break;
            case DELETE:
                newEvent = removeRows(uRowFirst, uRowLast);
                break;
            case UPDATE:
                newEvent = (uRowLast == Integer.MAX_VALUE)?
                        globalUpdateRows(e):
                        updateRows(e, uRowFirst, uRowLast);
                break;
        } // end switch()

        if (newEvent != null) {
            for (TableModelListener listener : listeners_) {
                listener.tableChanged(newEvent);
            }
        }

    } // end tableChanged

    private TableModelEvent globalUpdateRows(TableModelEvent e)
    {
        rebuildDeepAndShallow();
        return new TableModelEvent(this, 0,Integer.MAX_VALUE, e.getColumn(), UPDATE);
    }


    private T[] generateArrayOfNestedItems(int first, int last)
    {
        int count = last - first + 1;
        T[] result = (T[])new Object[count];

        DepthTableModel<T> nestedModel = (DepthTableModel<T>)getNestedTableModel();

        for(int i = 0; i < count; ++i) {
             result[i] = nestedModel.getRowAt(first + i);
        }
        return result;
    }

    private DepthProxy<T>[] generateProxies(T item, int start, int count)
    {
        DepthProxy<T>[] result = new DepthProxy[count];

        for(int i = 0; i < count; ++i) {
            result[i] = new DepthProxy<T>(item, i+start);
        }
        return result;
    }

    private DepthProxy<T>[] generateMultipleProxies(int offset, T[] items)
    {
        // determine total depth count first
        DepthTableModel<T> nestedModel = (DepthTableModel<T>)getNestedTableModel();
        int deepCount = 0;
        int i = 0;
        for(T item: items) {
            deepCount += nestedModel.getDepthByIndexOrObject(offset + i, item);
            ++i;
        }

        // allocate an array to hold all the deep items, so they can be inserted in one shot
        // (the reason for this is to avoid multiple index updating intthe deep array
        DepthProxy<T>[] result = new DepthProxy[deepCount];

        int deepCounter = 0;
        for(T item: items) {
            i = 0;
            int depth = nestedModel.getDepthByIndexOrObject(offset + i, item);
            for(i = 0; i < depth; ++i) {
                result[deepCounter++] = new DepthProxy<T>(item, i);
            }
        }

        return result;
    }



    private void replaceNDepthWithN(T nItem, int nDepth, int oStart, int oStop)
    {
        int oDepth = oStop - oStart + 1;
        int diff = nDepth - oDepth;

        if(diff <= 0) {
            // change the subset of rows in place
            for(int d = 0; d < nDepth; ++d) {
                deep_.set(oStart + d, new DepthProxy<T>(nItem, d));    // replace item, no reindexing
            }
            // remove excess rows (causes reindexing)
            if(diff < 0) {
                deep_.removeRows(oStop - diff, -diff);
            }
        } else if(oDepth < nDepth) {
            // change the subset of rows in place
            for(int d = 0; d < oDepth; ++d) {
                deep_.set(oStart + d, new DepthProxy<T>(nItem, d));    // replace item, no reindexing
            }
            deep_.insertRows(oStart + oDepth, generateProxies((T) nItem, oDepth, diff));
        }

    }

    private TableModelEvent updateRows( TableModelEvent e, int uRowFirst, int uRowLast)
    {
        DepthTableModel<T> nestedModel = (DepthTableModel<T>)getNestedTableModel();

        // reusable search object it is a DepthProxy with a mutating method
        // to avoid generating throw away search objects in a loop
        SearchProxy<T> searchProxy = new SearchProxy<T>(null,0);

        int newLastDepthRow = -1, oldLastDepthRow = -1;
        if(uRowLast == Integer.MAX_VALUE)
            uRowLast = shallow_.size() - 1;
        for(int i = uRowFirst; i <= uRowLast; ++i) {

            //---- first collect information about one row of nested mode for the update
            T nestedItem = nestedModel.getRowAt(i);
            T shallowItem = shallow_.get(i);

            int oDeepStart = deep_.indexOf(searchProxy.mutate(shallowItem));
            int oDeepStop  = expandedToLastExpandedRow(oDeepStart);
            int nDepth     = nestedModel.getDepthByIndexOrObject(i, nestedItem);

            //--- Does it a) point to a different object than our copy? Or b) point to the same one?
            if(shallowItem != nestedItem) {    // a) not a match, remove shallow and deep item, and replace them
                shallow_.set(i, nestedItem);
                replaceNDepthWithN(nestedItem, nDepth, oDeepStart, oDeepStop);
            } else {                           // b) it is a match, just fix up the deep array
                int oDepth = oDeepStop - oDeepStart + 1;
                int diff = nDepth - oDepth;
                if(diff == 0) { 
                    // do nothing if the depth is unchanged shallow/deep are in sync 
                } else if(diff < 0) {
                    deep_.removeRows(oDeepStop + diff + 1, -diff);
                } else {
                    deep_.insertRows(oDeepStop + 1, generateProxies(nestedItem, oDepth, diff));
                }
            }
            
            // these two are to track event translation from update to insert/delete which event do we make?
            oldLastDepthRow = oDeepStop;
            newLastDepthRow = oDeepStart + nDepth - 1;
        } // end for each row in update

        int newFirstDepthRow = compressedToExpandedRow(uRowFirst);


        if(oldLastDepthRow == newLastDepthRow) {
            return new TableModelEvent(this, newFirstDepthRow, newLastDepthRow, e.getColumn());
        } else if(oldLastDepthRow < newLastDepthRow) {
            // we need to both update the rows that are the same,
            evtupdate(e, newFirstDepthRow, oldLastDepthRow);
            return new TableModelEvent(this, oldLastDepthRow+1, newLastDepthRow,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        } else {
            evtupdate(e, newFirstDepthRow, newLastDepthRow);
            return new TableModelEvent(this, newLastDepthRow+1, oldLastDepthRow,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        }

    }

    private void evtupdate(TableModelEvent e, int first, int last)
    {
        TableModelEvent newEvent = new TableModelEvent(this, first, last,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE );
         for (TableModelListener listener : listeners_) {
                listener.tableChanged(newEvent);
            }
    }

    private TableModelEvent removeRows(int uRowFirst, int uRowLast)
    {
        int nRowFirst = compressedToExpandedRow(uRowFirst);
        int nRowLast  = compressedToLastExpandedRow(uRowLast);

        shallow_.removeRows(uRowFirst, uRowLast - uRowFirst + 1);
        deep_.removeRows(nRowFirst, nRowLast - nRowFirst + 1);
        return new TableModelEvent(this, nRowFirst, nRowLast, ALL_COLUMNS, DELETE);
    }

    private TableModelEvent insertRows(int uRowFirst, int uRowLast)
    {
        T[] items = generateArrayOfNestedItems(uRowFirst, uRowLast);

        int nRowFirst = compressedToExpandedRow(uRowFirst);

        shallow_.insertRows(uRowFirst, items);
        deep_.insertRows(nRowFirst, generateMultipleProxies(uRowFirst, items));

        int nRowLast  = compressedToLastExpandedRow(uRowLast);

        return new TableModelEvent(this, nRowFirst, nRowLast, ALL_COLUMNS, INSERT);
    }

    @Override
    public TableCellRenderer decorateCellRenderer(TableCellRenderer renderer)
    {

        TableModel nested = getNestedTableModel();
        if(nested instanceof NestingTableModel) {
            renderer =  ((NestingTableModel)nested).decorateCellRenderer(renderer);
        }

        return rendererFactory_ != null? rendererFactory_.createRenderer(renderer): renderer;
    }

    @Override
    public TableModelCoordinates convertToNested(int r, int c)
    {
        return(new TableModelCoordinates(getNestedTableModel(), expandedToCompressedRow(r),c));
    }


    @Override
    public TableModelCoordinates convertToNested(TableModelCoordinates rowCol)
    {
        return(new TableModelCoordinates(getNestedTableModel(), expandedToCompressedRow(rowCol.row),rowCol.col));
    }

    @Override
    public TableModelCoordinates convertFromNested(TableModelCoordinates childTmc)
    {
        assert childTmc.model == getNestedTableModel(): "not a nested model";

        int myRow = compressedToExpandedRow(childTmc.row);

        return new TableModelCoordinates(this, myRow, childTmc.col);
    }


}
