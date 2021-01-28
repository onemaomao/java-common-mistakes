AbstractCollection
Implements most of the Collection interface.

AbstractList
Extends AbstractCollection and implements most of the List interface.

AbstractSequentialList
Extends AbstractList for use by a collection that uses sequential rather than random access of its elements.

LinkedList
Implements a linked list by extending AbstractSequentialList.

ArrayList
Implements a dynamic array by extending AbstractList.

AbstractSet
Extends AbstractCollection and implements most of the Set interface.

HashSet
Extends AbstractSet for use with a hash table.

LinkedHashSet
Extends HashSet to allow insertion-order iterations.

TreeSet
Implements a set stored in a tree. Extends AbstractSet.


AbstractMap
Implements most of the Map interface.

HashMap
Extends AbstractMap to use a hash table.

TreeMap
Extends AbstractMap to use a tree.

WeakHashMap
Extends AbstractMap to use a hash table with weak keys.

LinkedHashMap
Extends HashMap to allow insertion-order iterations.

IdentityHashMap
Extends AbstractMap and uses reference equality when comparing documents.

Vector
This implements a dynamic array. It is similar to ArrayList, but with some differences.

Stack
Stack is a subclass of Vector that implements a standard last-in, first-out stack.

Dictionary
Dictionary is an abstract class that represents a key/value storage repository and operates much like Map.

Hashtable
Hashtable was part of the original java.util and is a concrete implementation of a Dictionary.

Properties
Properties is a subclass of Hashtable. It is used to maintain lists of values in which the key is a String and the value is also a String.

BitSet
A BitSet class creates a special type of array that holds bit values. This array can increase in size as needed.