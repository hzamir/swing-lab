package com.baliset.util;

import java.util.*;

public class CrossIndexedArrayList<E> extends AbstractList<E>
{
    private final List<E>       elements_;
    private final Map<E, int[]> indexes_;

    //------
    public CrossIndexedArrayList()
    {
        elements_      = new ArrayList<>();
        indexes_ = new HashMap<>();
    }

     public CrossIndexedArrayList(int initialCapacity)
    {
       elements_      = new ArrayList<>(initialCapacity);
       indexes_ = new HashMap<>(initialCapacity);
    }

    @Override public       E get(int index)        { return elements_.get(index);          }
    @Override public     int size()                { return elements_.size();              }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override public boolean contains(Object o)    { return indexes_.containsKey(o);       }

    @Override public    void clear()               { elements_.clear(); indexes_.clear();  }

    @Override
    public int indexOf(Object o)
    {
        int[] idx  = indexes_.get(o);
        return idx != null ? idx[0] : -1;
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return indexOf(o);
    }


    @Override
    public E set(int index, E element)
    {
       //todo: change exception, or implement everywhere or put in a policy regarding checking this stuff
        if(contains(element))
            throw new IllegalArgumentException("Non Unique element"+ element);

        E oldItem = elements_.set(index, element);
        indexes_.remove(oldItem);
        indexes_.put(element, new int[]{index});
        return oldItem;
    }

    @Override
    public void add(int index, E element)
    {
       elements_.add(index, element);
       indexes_.put(element, new int[]{index});
       _updateOffsets(index + 1, 1);
    }

    @Override
    public boolean add(E e) {
	   add(size(), e);
	   return true;
    }

    @Override
    public E remove(int index)
    {
        E item = get(index);
        removeRows(index, 1);
        return item;
    }

    //--- nonstandard methods----

    public void removeRows(int index, int numRows)
    {
        for (int i = 0; i < numRows; i++) {
            E removed = elements_.remove(index);
            indexes_.remove(removed);
        }

        _updateOffsets(index, -numRows);
    }

    @SafeVarargs
    public final void insertRows(int index, E... elements)
    {
        int length = elements.length;

        for (int i = 0; i < length; i++) {
            E element = elements[i];
            int currIndex = index + i;
            elements_.add(currIndex, element);
            indexes_.put(element, new int[]{currIndex});
        }

        _updateOffsets(index + length, length);
    }



    private void _updateOffsets(int start, int increment)
    {
        assert elements_.size() == indexes_.size();

        for (int i = start; i < elements_.size(); i++) {
            E t = elements_.get(i);
            indexes_.get(t)[0] += increment;
        }
    }

}