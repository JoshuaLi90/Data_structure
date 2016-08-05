import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.*;

/** Node of a doubly linked list of strings */
class DNode {
    protected String element;	// String element stored by a node
    protected DNode next, prev;	// Pointers to next and previous nodes
    /** Constructor that creates a node with given fields */
    public DNode(String e, DNode p, DNode n) {
        element = e;
        prev = p;
        next = n;
    }
    /** Returns the element of this node */
    public String getElement() { return element; }
    /** Returns the previous node of this node */
    public DNode getPrev() { return prev; }
    /** Returns the next node of this node */
    public DNode getNext() { return next; }
    /** Sets the element of this node */
    public void setElement(String newElem) { element = newElem; }
    /** Sets the previous node of this node */
    public void setPrev(DNode newPrev) { prev = newPrev; }
    /** Sets the next node of this node */
    public void setNext(DNode newNext) { next = newNext; }
}

// *****************************************************************************

class Dlist {
    protected int size;			// number of elements
    protected DNode header, trailer;	// sentinels

    /** Constructor that creates an empty list */
    public Dlist() {
        size = 0;
        header = new DNode(null, null, null);	// create header
        trailer = new DNode(null, header, null);	// create trailer
        header.setNext(trailer);	// make header and trailer point to each other
    }

    /** Returns the number of elements in the list */
    public int size() { return size; }

    /** Returns whether the list is empty */
    public boolean isEmpty() { return (size == 0); }

    /** Returns the first node of the list */
    public DNode getFirst() throws IllegalStateException {
        if (isEmpty()) throw new IllegalStateException("List is empty");
        return header.getNext();
    }

    /** Returns the last node of the list */
    public DNode getLast() throws IllegalStateException {
        if (isEmpty()) throw new IllegalStateException("List is empty");
        return trailer.getPrev();
    }

    /** Returns the node before the given node v. An error occurs if v
     * is the header */
    public DNode getPrev(DNode v) throws IllegalArgumentException {
        if (v == header) throw new IllegalArgumentException
                ("Cannot move back past the header of the list");
        return v.getPrev();
    }

    /** Returns the node after the given node v. An error occurs if v
     * is the trailer */
    public DNode getNext(DNode v) throws IllegalArgumentException {
        if (v == trailer) throw new IllegalArgumentException
                ("Cannot move forward past the trailer of the list");
        return v.getNext();
    }

    /** Inserts the given node z before the given node v. An error
     * occurs if v is the header */
    public void addBefore(DNode v, DNode z) throws IllegalArgumentException {
        DNode u = getPrev(v);	// may throw an IllegalArgumentException
        z.setPrev(u);
        z.setNext(v);
        v.setPrev(z);
        u.setNext(z);
        size++;
    }

    /** Inserts the given node z after the given node v. An error occurs
     * if v is the trailer */
    public void addAfter(DNode v, DNode z) {
        DNode w = getNext(v);	// may throw an IllegalArgumentException
        z.setPrev(v);
        z.setNext(w);
        w.setPrev(z);
        v.setNext(z);
        size++;
    }

    /** Inserts the given node at the head of the list */
    public void addFirst(DNode v) {
        addAfter(header, v);
    }

    /** Inserts the given node at the tail of the list */
    public void addLast(DNode v) {
        addBefore(trailer, v);
    }

    /** Removes the given node v from the list. An error occurs if v is
     * the header or trailer */
    public void remove(DNode v) {
        DNode u = getPrev(v);	// may throw an IllegalArgumentException
        DNode w = getNext(v);	// may throw an IllegalArgumentException
        // unlink the node from the list
        w.setPrev(u);
        u.setNext(w);
        v.setPrev(null);
        v.setNext(null);
        size--;
    }

    /** Returns whether a given node has a previous node */
    public boolean hasPrev(DNode v) { return v != header; }

    /** Returns whether a given node has a next node */
    public boolean hasNext(DNode v) { return v != trailer; }

    /** Returns a string representation of the list */
    public String toString() {
        String s = "[";
        DNode v = header.getNext();
        while (v != trailer) {
            s += v.getElement();
            v = v.getNext();
            if (v != trailer)
                s += ",";
        }
        s += "]";
        return s;
    }
}

//******************************* 2 *******************************************

public class MyDlist extends Dlist {

    /* This constructor creates an empty doubly
    /  linked list.                                 */

    public MyDlist() {
        //super();
        size = 0;
        header = new DNode(null, null, null);	// create header
        trailer = new DNode(null, header, null);	// create trailer
        header.setNext(trailer);	// make header and trailer point to each other
    }

    /* This constructor creates a doubly linked list
    /  by reading all strings from a file named f.
    /  Assume that adjacent strings in the file f
    /  are separated by one or more white space
    /  characters. If f is "stdin", MyDlist("stdin")
    /  creates a doubly linked list by reading all
    /  strings from the standard input. Assume that
    /  each input line is a string and an empty line
    /  denotes end of input.                        */
    public MyDlist(String f) throws Exception { // as need to read files
        if ("stdin".equals(f)) { // get input from standard input
            Scanner s1 = new Scanner(System.in);
            while (s1.hasNextLine()) {
                String s = s1.nextLine();
                if (s.isEmpty()) {
                    break;
                }
                DNode newNode = new DNode(s, null, null);
                addLast(newNode);
            }
            s1.close();
        }
        else {
            // method1  *********************************************
            URL url = MyDlist.class.getResource(f);
            File fl = new File(url.getPath());// toURI()
            Scanner s2 = new Scanner(fl);
            while (s2.hasNext()) {
                DNode newNode = new DNode(s2.next(), null, null);
                addLast(newNode);
            }
            s2.close();
            /********************************************   method2*//*
            FileInputStream fis = new FileInputStream(f);
            InputStream is = new BufferedInputStream(fis);*/
        }
    }
    /* This instance method prints all elements of a
    /  list on the standard output, one element per
    /  line.                                        */
    public void printList() {
        DNode dn = getFirst();
        while (dn != trailer) {
            System.out.println(dn.element);
            dn = dn.getNext();
        }
        dn = null;
    }
    /* This class method creates an identical copy
    /  of a doubly linked list u and returns a
    /  reference to the resulting doubly linked
    /  list.                                        */
    public static MyDlist cloneList(MyDlist u) {
        MyDlist copy = new MyDlist();
        DNode recent = new DNode(u.getFirst().getElement(), null, u.getFirst().getNext());
        while (recent != u.trailer) {
            DNode newNode = new DNode(recent.element, null, null);
            copy.addLast(newNode);
            recent = recent.getNext();
        }
        return copy;
    }
    /* This class method concatenates two doubly linked
    /  lists u and v into a single doubly linked list
    /  and returns a reference to the resulting doubly
    /  linked list. In the resulting doubly linked list,
    /  the linked list u precedes the linked list v.    */
    public static MyDlist concatenateList(MyDlist u, MyDlist v) {
        u.getLast().setNext(v.getFirst());
        v.getFirst().setPrev(u.getLast());

        v.header = null;
        u.trailer = v.trailer;

        u.size = u.size + v.size;
        return u;
    }
    /* This instance method removes all the nodes whose
    /  elements are equal to e. If such a node does not
    /  exist, this method will print " no node contains
    /  e!" on the standard output.                     */
    public void removeNode(String e) {
        int flag = size();
        DNode tem = getFirst();
        while (tem.next != trailer) {
            tem = tem.getNext();          // move to the next node
            if (tem.getPrev().element == e) {
                remove(tem.prev);    // remove last e node
            }
        }
        if (flag == size()) {
            System.out.println("no node contains e!");
        }
    }
    // main function
    public static void main(String[] args) throws Exception {
        System.out.println("please type some strings, one string each line and an empty line for the end of input:");
        /** Create the first doubly linked list
         by reading all the strings from the standard input. */
        MyDlist firstList = new MyDlist("stdin");

        /** Print all elememts in firstList */
        firstList.printList();

        /** Create the second doubly linked list
         by reading all the strings from the file myfile that contains some strings. */
        MyDlist secondList = new MyDlist("myfile");

        /** Print all elememts in secondList */
        secondList.printList();

        /** Innsert "data" into firstList */
        firstList.addFirst(new DNode("data", null, null));

        /** insert "structures" into firstList */
        firstList.addFirst(new DNode("structures", null, null));

        /** Print all elements in firstList. */
        firstList.printList();

        /** Innsert "data" into secondtList */
        secondList.addFirst(new DNode("data", null, null));

        /** insert "structures" into secondList */
        secondList.addFirst(new DNode("structures", null, null));

        /** Print all elements in secondList. */
        secondList.printList();

        /** Concatenate firstList and secondList into thirdList */
        MyDlist thirdList = concatenateList(firstList, secondList);

        /** Print all elements in thirdList. */
        thirdList.printList();

        /** Remove all the nodes in thirdList that contains "data". */
        firstList.removeNode("data");

        /** Print thirdList. */
        thirdList.printList();

        /** Remove all the nodes in thirdList that contains "structures". */
        thirdList.removeNode("structures");

        /** Print thirdList. */
        thirdList.printList();

        /** Clone thirdList */
        MyDlist fourthList = cloneList(thirdList);

        /** Print all elements in fourthList. */
        fourthList.printList();
    }
}
