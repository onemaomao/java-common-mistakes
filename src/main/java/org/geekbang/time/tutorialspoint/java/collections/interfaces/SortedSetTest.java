package org.geekbang.time.tutorialspoint.java.collections.interfaces;

import java.util.*;
public class SortedSetTest {

   public static void main(String[] args) {
      // Create the sorted set
      SortedSet set = new TreeSet(); 

      // Add elements to the set
      set.add("b");
      set.add("c");
      set.add("a");

      // Iterating over the elements in the set
      Iterator it = set.iterator();

      while (it.hasNext()) {
         // Get element
         Object element = it.next();
         System.out.println(element.toString());
      }

      SortedSet c = set.headSet("c");
      System.out.println(c);
      c.remove("a");
      System.out.println(c);
      System.out.println(set);
   }
}