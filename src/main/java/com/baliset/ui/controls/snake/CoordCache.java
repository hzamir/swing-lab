package com.baliset.ui.controls.snake;


/**
 * Created by hzamir on 2/14/14.
 */
public class CoordCache
{


  protected Object[][] cache_;

  public void reset(int rows, int columns)
  {
    cache_ = new Object[rows][columns];
  }

  public Object get(int row, int column)
  {
    return cache_[row][column];
  }

  public void put(int row, int column, Object o)
  {
    cache_[row][column] = o;
  }


  // todo: add special cases that maintain values in the cache for certain basic geometry changes such as inserts and deletes

}
