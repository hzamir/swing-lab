package com.baliset.data;


public class DataGenerator
{

  static String firstNames[] = {
      "George",
      "John",
      "Thomas",
      "James",
      "James",
      "John Quincy",
      "Andrew",
      "Martin",
      "William Henry",
      "John",
      "James K.",
      "Zachary",
      "Millard",
      "Franklin",
      "James",
      "Abraham",
      "Andrew",
      "Ulysses S.",
      "Rutherford B.",
      "James",
      "Chester A.",
      "Grover",
      "Benjamin",
      "Grover",
      "William",
      "Theodore",
      "William Howard",
      "Woodrow",
      "Warren G.",
      "Calvin",
      "Herbert",
      "Franklin D.",
      "Harry S.",
      "Dwight D.",
      "John F.",
      "Lyndon B.",
      "Richard",
      "Gerald",
      "Jimmy",
      "Ronald",
      "George H.W.",
      "Bill",
      "George W.",
      "Barack",
      "Ron"
  };


  static String lastNames[] = {
      "Washington",
      "Adams",
      "Jefferson",
      "Madison",
      "Monroe",
      "Adams",
      "Jackson",
      "Van Buren",
      "Harrison",
      "Tyler",
      "Polk",
      "Taylor",
      "Fillmore",
      "Pierce",
      "Buchanan",
      "Lincoln",
      "Johnson",
      "Grant",
      "Hayes",
      "Garfield",
      "Arthur",
      "Cleveland",
      "Harrison",
      "Cleveland",
      "McKinley",
      "Roosevelt",
      "Taft",
      "Wilson",
      "Harding",
      "Coolidge",
      "Hoover",
      "Roosevelt",
      "Truman",
      "Eisenhower",
      "Kennedy",
      "Johnson",
      "Nixon",
      "Ford",
      "Carter",
      "Reagan",
      "Bush",
      "Clinton",
      "Bush",
      "Obama",
      "Paul"
  };


  public static Item generateItem(int index)
  {
    Item item;

    if (index < firstNames.length) {
      item = new Item(firstNames[index], lastNames[index], 1 + index);
    } else {
      ++index; // since we use 1 indexing in the text
      item = new Item("First-" + index, "Last-" + index, index);
    }

    item.randomizeDepth();
    return item;

  }


}
