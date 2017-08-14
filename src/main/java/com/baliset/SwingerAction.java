package com.baliset;

public enum SwingerAction
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

  SwingerAction()
  {
    label_ = this.name();
  }

  SwingerAction(String l)
  {
    label_ = l;
  }

  public String getLabel()
  {
    return label_;
  }

}
