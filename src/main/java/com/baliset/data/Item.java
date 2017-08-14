package com.baliset.data;

import java.util.Random;

public class Item
{
    private String first_;
    private String last_;
    private Integer number_;
    private String[] infos_;
    private static final String sEmpty = "n/a";


    public Item(String first, String last, Integer num)
    {
        this.first_ = first;
        this.last_ = last;
        this.number_ = num;
    }

    public Item(String first, String last, Integer num, String... infos)
    {
        this(first, last, num);
        infos_ = infos;
    }


    public String getFirst()
    {
        return first_;
    }

    public String getLast()
    {
        return last_;
    }


    public Integer getNumber()
    {
        return number_;
    }

    public String getInfo(int index)
    {
        return infos_ == null? sEmpty : infos_[index];
    }


    public int getDepth()
    {
        return Math.max(1,infos_ == null?0:infos_.length);
    }

    public void collapse()
    {
        infos_ = null;
    }

    public void randomizeDepth()
    {
        if(infos_ == null)
        {
              infos_ = generateRandomDepth();
        }

    }

     public void expand()
    {
        while(infos_ == null)
        {
              infos_ = generateRandomDepth();
        }

    }


    private static Random randomGenerator_  = new Random();

    private static String[] depthStrings_ = {"d1", "d2", "d3", "d4", "d5"};

    private  static String[] generateRandomDepth()
    {
           //note a single Random object is reused here
      int depth = randomGenerator_.nextInt(depthStrings_.length);

      if(depth <= 1)
            return null;
        String[] strings = new String[depth];

      for(int i = 0; i < depth; ++i )
          strings[i] = depthStrings_[i];
      return strings;
    }


}
