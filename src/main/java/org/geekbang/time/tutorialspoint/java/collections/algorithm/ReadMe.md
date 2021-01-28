static int binarySearch(List list, Object value, Comparator c)
Searches for value in the list ordered according to c. Returns the position of value in list, or -1 if value is not found.

static int binarySearch(List list, Object value)
Searches for value in the list. The list must be sorted. Returns the position of value in list, or -1 if value is not found.

static void copy(List list1, List list2)
Copies the elements of list2 to list1.

static Enumeration enumeration(Collection c)
Returns an enumeration over c.

static void fill(List list, Object obj)
Assigns obj to each element of the list.

static int indexOfSubList(List list, List subList)
Searches list for the first occurrence of subList. Returns the index of the first match, or .1 if no match is found.


static int lastIndexOfSubList(List list, List subList)
Searches list for the last occurrence of subList. Returns the index of the last match, or .1 if no match is found.

static ArrayList list(Enumeration enum)
Returns an ArrayList that contains the elements of enum.

static Object max(Collection c, Comparator comp)
Returns the maximum element in c as determined by comp.

static Object max(Collection c)
Returns the maximum element in c as determined by natural ordering. The collection need not be sorted.

static Object min(Collection c, Comparator comp)
Returns the minimum element in c as determined by comp. The collection need not be sorted.

static Object min(Collection c)
Returns the minimum element in c as determined by natural ordering.

static List nCopies(int num, Object obj)
Returns num copies of obj contained in an immutable list. num must be greater than or equal to zero.

static boolean replaceAll(List list, Object old, Object new)
Replaces all occurrences of old with new in the list. Returns true if at least one replacement occurred. Returns false, otherwise.

static void reverse(List list)
Reverses the sequence in list.

static Comparator reverseOrder( )
Returns a reverse comparator.

static void rotate(List list, int n)
Rotates list by n places to the right. To rotate left, use a negative value for n.

static void shuffle(List list, Random r)
Shuffles (i.e., randomizes) the elements in the list by using r as a source of random numbers.

static void shuffle(List list)
Shuffles (i.e., randomizes) the elements in list.

static Set singleton(Object obj)
Returns obj as an immutable set. This is an easy way to convert a single object into a set.

static List singletonList(Object obj)
Returns obj as an immutable list. This is an easy way to convert a single object into a list.

static Map singletonMap(Object k, Object v)
Returns the key/value pair k/v as an immutable map. This is an easy way to convert a single key/value pair into a map.

static void sort(List list, Comparator comp)
Sorts the elements of list as determined by comp.

static void sort(List list)
Sorts the elements of the list as determined by their natural ordering.

static void swap(List list, int idx1, int idx2)
Exchanges the elements in the list at the indices specified by idx1 and idx2.

static Collection synchronizedCollection(Collection c)
Returns a thread-safe collection backed by c.

static List synchronizedList(List list)
Returns a thread-safe list backed by list.

static Map synchronizedMap(Map m)
Returns a thread-safe map backed by m.

static Set synchronizedSet(Set s)
Returns a thread-safe set backed by s.

static SortedMap synchronizedSortedMap(SortedMap sm)
Returns a thread-safe sorted set backed by sm.

static SortedSet synchronizedSortedSet(SortedSet ss)
Returns a thread-safe set backed by ss.

static Collection unmodifiableCollection(Collection c)
Returns an unmodifiable collection backed by c.

static List unmodifiableList(List list)
Returns an unmodifiable list backed by the list.

static Map unmodifiableMap(Map m)
Returns an unmodifiable map backed by m.

static Set unmodifiableSet(Set s)
Returns an unmodifiable set backed by s.

static SortedMap unmodifiableSortedMap(SortedMap sm)
Returns an unmodifiable sorted map backed by sm.

static SortedSet unmodifiableSortedSet(SortedSet ss)
Returns an unmodifiable sorted set backed by ss.