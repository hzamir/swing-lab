package com.baliset;

public enum SwingLabAction
{
  Append("Add Item"),
  InsertBefore("InsertBefore"),
  InsertAfter,
  DeleteSelected("Del Selected"),
  DeleteLast,
  Search,
  Filter("Filter (broken)"),
  ColumnResize,
  Reset,
  Expand,
  Collapse,
  Launch("Create Table"),
  ExpandCollapseHeader,
  ShowHideColumns("Hide Col"),
  OSnake,
  ODepth,
  OMulti,
  OHMulti;

  private String label_;

  SwingLabAction()
  {
    label_ = this.name();
  }

  SwingLabAction(String l)
  {
    label_ = l;
  }

  public String getLabel()
  {
    return label_;
  }

}
