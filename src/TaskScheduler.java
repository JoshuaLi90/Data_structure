//package TaskScheduler;
import net.datastructures.*;
import net.datastructures.PriorityQueue;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
/*
* time complexity is nlgn because the priority queue using heap for insert and removeMin complexity is logn
* and the for loop is n time, so the whole time complexity is 2*nlogn(2 for loops) which is nlogn.
*/
public class TaskScheduler {
    static void scheduler(String file1, String file2, Integer m) throws IOException {
        URL url = TaskScheduler.class.getResource(file1);
        File fl = new File(url.getPath());// toURI() is also acceptable
        Scanner s = new Scanner(fl);
        int count = 0;
        String key = "";
        int temp = 0;
        HeapPriorityQueue<Integer,String> pq1 = new HeapPriorityQueue();
        for (int mark = 3; s.hasNext(); mark++) {
            if (mark == 3) { // the key String
                key = s.next();
                mark = 0;
            }
            else if (mark == 1 && s.hasNextInt()) { // the 1th value
                temp = s.nextInt();
            }
            else if (mark == 2 && s.hasNextInt()) { // the 2ed value
                pq1.insert(temp = temp * 10000 + s.nextInt(), key);
                //System.out.println(key + ": " + temp); // Just for test
                count++;
            }
            else { //3.
                System.out.println("file1 is not following the format");
                break;
            }
        }

        s.close();

        //1. file1 does not exist.
        if (true) {}
        //2. file2 already exists.
        File checkFile = new File(file2 + ".txt");
        if (checkFile.exists()) {
            System.out.println(file2 + " already exists");
        }
        //3. The task attributes (task name, release time and deadline) of file1 do not follow the format as shown next.
        if (count == 0) {
            System.out.println(file1 + " is not following the format");
        }

        int timeNow = 0; // mark the time now
        int coreUsed = 0; // mark the used core a one time
        FileWriter out = new FileWriter(file2 + ".txt");
        for (int i = 0; !pq1.isEmpty(); i++) {
            int t1 = pq1.min().getKey() / 10000; // get release time
            int t2 = pq1.min().getKey() - ((pq1.min().getKey() / 10000) * 10000); // get deadline
            if (t1 <= timeNow && t2 > timeNow && coreUsed < m) {
                //System.out.println(pq1.removeMin().getValue() + " " + timeNow + " ");
                out.write(pq1.removeMin().getValue() + " " + timeNow + " "); //logn
                coreUsed++;
            }
            else if (t1 > timeNow && t2 > timeNow) { // no test to assign
                timeNow++;
                coreUsed = 0;
            }
            else if (t2 > timeNow && coreUsed == m) { // no core to assign
                timeNow++;
                coreUsed = 0;
            }
            else { //4. The feasible schedule can not be found.
                System.out.println("No feasible schedule exists");
                out.close();
                File f2 = new File(file2 + ".txt");
                //System.out.println(f2.getPath());// Just for test
                try {
                    f2.delete();
                }
                catch(Exception e) {

                }
                break;
            }
        }
        out.close();
        //FileWriter fw = new FileWriter("c:\\" + file2 + ".txt");
        //fw.write(pq1.removeMin().getKey().toString() + "\n");
    }


    /*
    * @author Hui Wu
    */
    public static void main(String[] args) throws Exception {
        TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule1", 4);
        /** There is a feasible schedule on 4 cores */
        TaskScheduler.scheduler("samplefile1.txt", "feasibleschedule2", 3);
        /** There is no feasible schedule on 3 cores */
        TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule3", 5);
        /** There is a feasible scheduler on 5 cores */
        TaskScheduler.scheduler("samplefile2.txt", "feasibleschedule4", 4);
        /** There is no feasible schedule on 4 cores */
    }
}

class HeapPriorityQueue<K,V> implements PriorityQueue<K,V> {
    public CompleteBinaryTree<Entry<K,V>> heap;	// underlying heap
    public Comparator<K> comp;	// comparator for the keys
    /** Inner class for heap entries. */
    protected static class  MyEntry<K,V> implements Entry<K,V> {
        protected K key;
        protected V value;
        public MyEntry(K k, V v) { key = k; value = v; }
        public K getKey() { return key; }
        public V getValue() { return value; }
        public String toString() { return "(" + key  + "," + value + ")"; }
    }
    /** Creates an empty heap with the default comparator */
    public HeapPriorityQueue() {
        heap = new ArrayListCompleteBinaryTree<Entry<K,V>>(); // use an array list
        comp = new DefaultComparator<K>();     // use the default comparator
    }
    /** Creates an empty heap with the given comparator */
    public HeapPriorityQueue(Comparator<K> c) {
        heap = new ArrayListCompleteBinaryTree<Entry<K,V>>();
        comp = c;
    }
    //end#fragment HeapPriorityQueue
    /** Sets the comparator used for comparing items in the heap.
     * @throws IllegalStateException if priority queue is not empty */
    public void setComparator(Comparator<K> c) throws IllegalStateException {
        if(!isEmpty())  // this is only allowed if the priority queue is empty
            throw new IllegalStateException("Priority queue is not empty");
        comp = c;
    }
    //begin#fragment HeapPriorityQueue
    /** Returns the size of the heap */
    public int size() { return heap.size(); }
    /** Returns whether the heap is empty */
    public boolean isEmpty() { return heap.size() == 0; }
    //end#fragment HeapPriorityQueue
    //begin#fragment mainMethods
    /** Returns but does not remove an entry with minimum key */
    public Entry<K,V> min() throws EmptyPriorityQueueException {
        if (isEmpty())
            throw new EmptyPriorityQueueException("Priority queue is empty");
        return heap.root().element();
    }
    /** Inserts a key-value pair and returns the entry created */
    public Entry<K,V> insert(K k, V x) throws InvalidKeyException {
        checkKey(k);  // may throw an InvalidKeyException
        Entry<K,V> entry = new MyEntry<K,V>(k,x);
        upHeap(heap.add(entry));
        return entry;
    }
    /** Removes and returns an entry with minimum key */
    public Entry<K,V> removeMin() throws EmptyPriorityQueueException {
        if (isEmpty())
            throw new EmptyPriorityQueueException("Priority queue is empty");
        Entry<K,V> min = heap.root().element();
        if (size() == 1)
            heap.remove();
        else {
            heap.replace(heap.root(), heap.remove());
            downHeap(heap.root());
        }
        return min;
    }
    /** Determines whether a given key is valid */
    protected void checkKey(K key) throws InvalidKeyException {
        try {
            comp.compare(key,key);
        }
        catch(Exception e) {
            throw new InvalidKeyException("Invalid key");
        }
    }
    //end#fragment mainMethods
    //begin#fragment auxiliary
    /** Performs up-heap bubbling */
    protected void upHeap(Position<Entry<K,V>> v) {
        Position<Entry<K,V>> u;
        while (!heap.isRoot(v)) {
            u = heap.parent(v);
            if (comp.compare(u.element().getKey(), v.element().getKey()) <= 0) break;
            swap(u, v);
            v = u;
        }
    }
    /** Performs down-heap bubbling */
    protected void downHeap(Position<Entry<K,V>> r) {
        while (heap.isInternal(r)) {
            Position<Entry<K,V>> s;		// the position of the smaller child
            if (!heap.hasRight(r))
                s = heap.left(r);
            else if (comp.compare(heap.left(r).element().getKey(),
                    heap.right(r).element().getKey()) <=0)
                s = heap.left(r);
            else
                s = heap.right(r);
            if (comp.compare(s.element().getKey(), r.element().getKey()) < 0) {
                swap(r, s);
                r = s;
            }
            else
                break;
        }
    }
    /** Swaps the entries of the two given positions */
    protected void swap(Position<Entry<K,V>> x, Position<Entry<K,V>> y) {
        Entry<K,V> temp = x.element();
        heap.replace(x, y.element());
        heap.replace(y, temp);
    }
    /** Text visualisation for debugging purposes */
    public String toString() {
        return heap.toString();
    }
    //end#fragment auxiliary
}
