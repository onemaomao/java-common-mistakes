package org.geekbang.time.tutorialspoint.java.collections.classes;

import java.util.*;
public class HashSetDemo {

   public static void main(String args[]) {
      // create a hash set
      HashSet hs = new HashSet();
      
      // add elements to the hash set
      hs.add("B");
      hs.add("AAA");
      hs.add("D");
      hs.add("E");
      hs.add("C");
      hs.add("F");
      hs.add("A");
      System.out.println(hs);
   }
}