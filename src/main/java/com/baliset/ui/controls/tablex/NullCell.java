package com.baliset.ui.controls.tablex;

public class NullCell
{
    public static final NullObject sNullObject = new NullObject();

    static class NullObject
    {
        @Override
        public String toString()
        {
            return "";
        }
    }
}