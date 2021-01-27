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