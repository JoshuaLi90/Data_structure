//package net.datastructures;

import net.datastructures.*;
import java.awt.*;
import java.util.Comparator;
import javax.swing.*;
import java.util.Iterator;

public class ExtendedAVLTree<K, V> extends AVLTree<K, V> {
    public ExtendedAVLTree(Comparator<K> c)  { super(c); }
    public ExtendedAVLTree() { super(); }
    /*
     * Merge Function
     * Time Complexity
     * n is the node of tree1, m for tree2
     * every floor it across by level 0 so 1 node in level 0 with run logn times
     * 2^0 * logn + 2^1 * (logn - 1) + 2^2 * (logn - 2) + ... + n * 1  =>
     * -2^0 * logn + 2^1 + ... + 2^(n - 1) + 2^n =>
     * (2^n) - 2 - logn
     *  so, (2^n) + (2^m) is the finial answer of merge function
     */
    public static <K, V> AVLTree<K, V> merge(AVLTree<K, V> tree1, AVLTree<K, V> tree2) {
        ExtendedAVLTree<K, V> EVALTree = new ExtendedAVLTree<K, V>();
        AVLTree<K, V> newTree = new AVLTree<K, V>();
        for (int i = 0; i <= tree1.height(tree1.root()); i++) {
            EVALTree.InsertTree(tree1.root(), newTree, i, 0);
        }
        for (int j = 0; j <= tree2.height(tree2.root()); j++) {
            EVALTree.InsertTree(tree2.root(), newTree, j, 0);
        }
        return newTree;
    }
    /*
     * Insert in Tree Function: Level for the floor of the tree and h is the count of current floor of the tree
     *
     */
    private void InsertTree(Position<Entry<K, V>> position, AVLTree<K, V> newTree, int level, int h){
        if (level == h) {
            newTree.insert(position.element().getKey(), position.element().getValue());
        }
        else { // no downer search
            if (hasLeft(position) && isInternal(left(position))) {
                InsertTree(left(position), newTree, level, h+1);
            }
            if (hasRight(position) && isInternal(right(position))) {
                InsertTree(right(position), newTree, level, h+1);
            }
        }
    }
    /*
     * Clone Function
     */
    public static <K, V> AVLTree<K, V> clone(AVLTree<K, V> tree) {
        ExtendedAVLTree<K, V> EVALTree = new ExtendedAVLTree<K, V>();
        AVLTree<K, V> newTree = new AVLTree<K, V>();

        for (int i = 0; i <= tree.height(tree.root()); i++) {
            EVALTree.InsertTree(tree.root(), newTree, i, 0);
        }
        return newTree;
        //Another method by using Queue:
		/*AVLTree<K, V> newTree = new AVLTree<K, V>();
		java.util.Queue<Position<Entry<K,V>>> q = new java.util.LinkedList<Position<Entry<K,V>>>();
		if (tree.isInternal(tree.root()))
			q.add(tree.root()); // input root to the list q
		while (!q.isEmpty()) {
			Position<Entry<K,V>> next = q.poll(); // output from the list q
			newTree.insert(next.element().getKey(), next.element().getValue());
			if (tree.isInternal(tree.left(next)))
				q.add(tree.left(next)); // input left branch
			if (tree.isInternal(tree.right(next)))
				q.add(tree.right(next));// input right branch
		}
		return newTree;*/
    }
    /*
     * print function
     */
    static <K, V> void print(AVLTree<K, V> tree) {
        JFrame frame = new JFrame("Assignment2");
        int k = (int) Math.pow(tree.size(), 0.5);
        PaintPanel<K, V> PanelLayer = new PaintPanel<K, V>(tree);
        PanelLayer.k = k * 95;// for the xgap set
        frame.getContentPane().add(PanelLayer, BorderLayout.CENTER);
        frame.setSize(k * 400, k * 200);
        frame.setVisible(true);
    }
    /*
     * main function
     */
    public static void main(String[] args) {
        String values1[] = { "Sydney", "Beijing", "Shanghai", "New York",
                "Tokyo", "Berlin", "Athens", "Paris", "London", "Cairo" };
        int keys1[] = { 20, 8, 5, 30, 22, 40, 12, 10, 3, 5 };
        String values2[] = { "Fox", "Lion", "Dog", "Sheep", "Rabbit", "Fish" };
        int keys2[] = { 40, 7, 5, 32, 20, 30 };

        AVLTree<Integer, String> tree1 = new AVLTree<Integer, String>();
        for (int i = 0; i < 10; i++)
            tree1.insert(keys1[i], values1[i]);
        AVLTree<Integer, String> tree2 = new AVLTree<Integer, String>();
        for (int i = 0; i < 6; i++)
            tree2.insert(keys2[i], values2[i]);

        ExtendedAVLTree.print(tree1);
        ExtendedAVLTree.print(tree2);
        ExtendedAVLTree.print(ExtendedAVLTree.clone(tree1));
        ExtendedAVLTree.print(ExtendedAVLTree.clone(tree2));
        ExtendedAVLTree.print(ExtendedAVLTree.merge(tree1, tree2));

    }
}
/*
 *  Panel
 */
class PaintPanel<K, V> extends JPanel {
    public int k;
    private static final long serialVersionUID = 1L;
    private AVLTree<K, V> tree;
    public PaintPanel(AVLTree<K, V> tree) { this.tree = tree; }

    void printRealAvlTree(Position<Entry<K, V>> node, int xgap, Graphics g, int x, int y) {
        int radius = 25;
        int ygap = 90;

        if (node.element() != null) {
            g.drawArc(x, y, radius, radius, 0, 360);
            String key = node.element().getKey().toString();
            if(key.length() > 1)
                g.drawString(key, x + (int) (radius / 4) - key.length() + 2, y + (int) (radius / 1.4));
            else
                g.drawString(key, x + (int) (radius / 4) + 4, y + (int) (radius / 1.4));

            if (tree.hasLeft(node)) {
                g.drawLine(x + radius / 2, y + radius, x - xgap + radius / 2, y + ygap);
                printRealAvlTree(tree.left(node), (int) (xgap / 2), g, x - xgap, y + ygap);
            }
            if (tree.hasRight(node)) {
                g.drawLine(x + radius / 2, y + radius, x + xgap + radius / 2, y + ygap);
                printRealAvlTree(tree.right(node), (int) (xgap / 2), g, x + xgap, y + ygap);
            }
        }
        else if (node.element() == null) {
            g.drawRect(x, y, radius-3, radius-3);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Position<Entry<K, V>> node = tree.root();
        if (node != null) { printRealAvlTree(node, k, g, (int) (this.getWidth() / 2), (int) (this.getHeight() / 15)); }
    }
}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

//begin#fragment AVLTree
/**  Implementation of an AVL tree. */
//end#fragment AVLTree
/**
 * AVLTree class - implements an AVL Tree by extending a binary
 * search tree.
 *
 * @author Michael Goodrich, Roberto Tamassia, Eric Zamore
 */

//begin#fragment AVLTree
class AVLTree<K,V>
        extends BinarySearchTree<K,V> implements Dictionary<K,V> {
    public AVLTree(Comparator<K> c)  { super(c); }
    public AVLTree() { super(); }
    /** Nested class for the nodes of an AVL tree. */
    protected static class AVLNode<K,V> extends BTNode<Entry<K,V>> {
        protected int height;  // we add a height field to a BTNode
        AVLNode() {/* default constructor */}
        /** Preferred constructor */
        AVLNode(Entry<K,V> element, BTPosition<Entry<K,V>> parent,
                BTPosition<Entry<K,V>> left, BTPosition<Entry<K,V>> right) {
            super(element, parent, left, right);
            height = 0;
            if (left != null)
                height = Math.max(height, 1 + ((AVLNode<K,V>) left).getHeight());
            if (right != null)
                height = Math.max(height, 1 + ((AVLNode<K,V>) right).getHeight());
        } // we assume that the parent will revise its height if needed
        public void setHeight(int h) { height = h; }
        public int getHeight() { return height; }
    }
    /** Creates a new binary search tree node (overrides super's version). */
    protected BTPosition<Entry<K,V>> createNode(Entry<K,V> element,
                                                BTPosition<Entry<K,V>> parent, BTPosition<Entry<K,V>> left,
                                                BTPosition<Entry<K,V>> right) {
        return new AVLNode<K,V>(element,parent,left,right);  // now use AVL nodes
    }
    /** Returns the height of a node (call back to an AVLNode). */
    protected int height(Position<Entry<K,V>> p)  {
        return ((AVLNode<K,V>) p).getHeight();
    }
    /** Sets the height of an internal node (call back to an AVLNode). */
    protected void setHeight(Position<Entry<K,V>> p) {
        ((AVLNode<K,V>) p).setHeight(1+Math.max(height(left(p)), height(right(p))));
    }
    /** Returns whether a node has balance factor between -1 and 1. */
    protected boolean isBalanced(Position<Entry<K,V>> p)  {
        int bf = height(left(p)) - height(right(p));
        return ((-1 <= bf) &&  (bf <= 1));
    }
//end#fragment AVLTree
//begin#fragment AVLTree2
    /** Returns a child of p with height no smaller than that of the other child */
//end#fragment AVLTree2
    /**
     * Return a child of p with height no smaller than that of the
     * other child.
     */
//begin#fragment AVLTree2
    protected Position<Entry<K,V>> tallerChild(Position<Entry<K,V>> p)  {
        if (height(left(p)) > height(right(p))) return left(p);
        else if (height(left(p)) < height(right(p))) return right(p);
        // equal height children - break tie using parent's type
        if (isRoot(p)) return left(p);
        if (p == left(parent(p))) return left(p);
        else return right(p);
    }
    /**
     * Rebalance method called by insert and remove.  Traverses the path from
     * zPos to the root. For each node encountered, we recompute its height
     * and perform a trinode restructuring if it's unbalanced.
     */
    protected void rebalance(Position<Entry<K,V>> zPos) {
        if(isInternal(zPos))
            setHeight(zPos);
        while (!isRoot(zPos)) {  // traverse up the tree towards the root
            zPos = parent(zPos);
            setHeight(zPos);
            if (!isBalanced(zPos)) {
                // perform a trinode restructuring at zPos's tallest grandchild
                Position<Entry<K,V>> xPos =  tallerChild(tallerChild(zPos));
                zPos = restructure(xPos); // tri-node restructure (from parent class)
                setHeight(left(zPos));  // recompute heights
                setHeight(right(zPos));
                setHeight(zPos);
            }
        }
    }
    // overridden methods of the dictionary ADT
//end#fragment AVLTree2
    /**
     * Inserts an item into the dictionary and returns the newly created
     * entry.
     */
//begin#fragment AVLTree2
    public Entry<K,V> insert(K k, V v) throws InvalidKeyException  {
        Entry<K,V> toReturn = super.insert(k, v); // calls our createNode method
        rebalance(actionPos); // rebalance up from the insertion position
        return toReturn;
    }
//end#fragment AVLTree2
    /** Removes and returns an entry from the dictionary. */
//begin#fragment AVLTree2
    public Entry<K,V> remove(Entry<K,V> ent) throws InvalidEntryException {
        Entry<K,V> toReturn = super.remove(ent);
        if (toReturn != null)   // we actually removed something
            rebalance(actionPos);  // rebalance up the tree
        return toReturn;
    }
} // end of AVLTree class
//end#fragment AVLTree2

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

//begin#fragment LinkedBinaryTree
/**
 * An implementation of the BinaryTree interface by means of a linked structure.
 //end#fragment LinkedBinaryTree
 * This class serves as a superclass for the BinarySearchTree
 * implementation.  This design decision was made to emphasize the
 * conceptual relationship that a BinarySearchTree is a specialized
 * LinkedBinaryTree.  An unwanted side-effect of this is that the
 * {@link #size() size} method returns the number of total nodes
 * whereas the {@link BinarySearchTree#size() size} method in the
 * {@link BinarySearchTree BinarySearchTree} class returns the number
 * of internal nodes only.  For this reason, the the {@link #size
 * size} variable instead of the {@link #size() size} method is used
 * within this class.
 *
 * @author Luca Vismara, Roberto Tamassia, Michael Goodrich, Eric
 * Zamore
 * @see BinaryTree
//begin#fragment LinkedBinaryTree
 */
class LinkedBinaryTree<E> implements BinaryTree<E> {
    protected BTPosition<E> root;	// reference to the root
    protected int size;		// number of nodes
    /**  Creates an empty binary tree. */
    public LinkedBinaryTree() {
        root = null;  // start with an empty tree
        size = 0;
    }
    /** Returns the number of nodes in the tree. */
    public int size() {
        return size;
    }
//end#fragment LinkedBinaryTree
    /** Returns whether the tree is empty. */
    public boolean isEmpty() {
        return (size == 0);
    }
//begin#fragment LinkedBinaryTree
    /** Returns whether a node is internal. */
    public boolean isInternal(Position<E> v) throws InvalidPositionException {
        checkPosition(v);		// auxiliary method
        return (hasLeft(v) || hasRight(v));
    }
//end#fragment LinkedBinaryTree
    /** Returns whether a node is external. */
    public boolean isExternal(Position<E> v) throws InvalidPositionException {
        return !isInternal(v);
    }
//begin#fragment LinkedBinaryTree
    /** Returns whether a node is the root. */
    public boolean isRoot(Position<E> v) throws InvalidPositionException {
        checkPosition(v);
        return (v == root());
    }
    /** Returns whether a node has a left child. */
    public boolean hasLeft(Position<E> v) throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        return (vv.getLeft() != null);
    }
//end#fragment LinkedBinaryTree
    /** Returns whether a node has a right child. */
    public boolean hasRight(Position<E> v) throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        return (vv.getRight() != null);
    }
//begin#fragment LinkedBinaryTree
    /** Returns the root of the tree. */
    public Position<E> root() throws EmptyTreeException {
        if (root == null)
            throw new EmptyTreeException("The tree is empty");
        return root;
    }
    /** Returns the left child of a node. */
    public Position<E> left(Position<E> v)
            throws InvalidPositionException, BoundaryViolationException {
        BTPosition<E> vv = checkPosition(v);
        Position<E> leftPos = vv.getLeft();
        if (leftPos == null)
            throw new BoundaryViolationException("No left child");
        return leftPos;
    }
//end#fragment LinkedBinaryTree
    /** Returns the right child of a node. */
    public Position<E> right(Position<E> v)
            throws InvalidPositionException, BoundaryViolationException {
        BTPosition<E> vv = checkPosition(v);
        Position<E> rightPos = vv.getRight();
        if (rightPos == null)
            throw new BoundaryViolationException("No right child");
        return rightPos;
    }
//begin#fragment LinkedBinaryTree2
    /** Returns the parent of a node. */
    public Position<E> parent(Position<E> v)
            throws InvalidPositionException, BoundaryViolationException {
        BTPosition<E> vv = checkPosition(v);
        Position<E> parentPos = vv.getParent();
        if (parentPos == null)
            throw new BoundaryViolationException("No parent");
        return parentPos;
    }
    /** Returns an iterable collection of the children of a node. */
    public Iterable<Position<E>> children(Position<E> v)
            throws InvalidPositionException {
        PositionList<Position<E>> children = new NodePositionList<Position<E>>();
        if (hasLeft(v))
            children.addLast(left(v));
        if (hasRight(v))
            children.addLast(right(v));
        return children;
    }
    /** Returns an iterable collection of the tree nodes. */
    public Iterable<Position<E>> positions() {
        PositionList<Position<E>> positions = new NodePositionList<Position<E>>();
        if(size != 0)
            preorderPositions(root(), positions);  // assign positions in preorder
        return positions;
    }
    /** Returns an iterator of the elements stored at the nodes */
    public Iterator<E> iterator() {
        Iterable<Position<E>> positions = positions();
        PositionList<E> elements = new NodePositionList<E>();
        for (Position<E> pos: positions)
            elements.addLast(pos.element());
        return elements.iterator();  // An iterator of elements
    }
    /** Replaces the element at a node. */
    public E replace(Position<E> v, E o)
            throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        E temp = v.element();
        vv.setElement(o);
        return temp;
    }
//end#fragment LinkedBinaryTree2
//begin#fragment LinkedBinaryTree3
    // Additional accessor method
    /** Return the sibling of a node */
    public Position<E> sibling(Position<E> v)
            throws InvalidPositionException, BoundaryViolationException {
        BTPosition<E> vv = checkPosition(v);
        BTPosition<E> parentPos = vv.getParent();
        if (parentPos != null) {
            BTPosition<E> sibPos;
            BTPosition<E> leftPos = parentPos.getLeft();
            if (leftPos == vv)
                sibPos = parentPos.getRight();
            else
                sibPos = parentPos.getLeft();
            if (sibPos != null)
                return sibPos;
        }
        throw new BoundaryViolationException("No sibling");
    }
    // Additional update methods
    /** Adds a root node to an empty tree */
    public Position<E> addRoot(E e) throws NonEmptyTreeException {
        if(!isEmpty())
            throw new NonEmptyTreeException("Tree already has a root");
        size = 1;
        root = createNode(e,null,null,null);
        return root;
    }
    /** Inserts a left child at a given node. */
    public Position<E>  insertLeft(Position<E> v, E e)
            throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        Position<E> leftPos = vv.getLeft();
        if (leftPos != null)
            throw new InvalidPositionException("Node already has a left child");
        BTPosition<E> ww = createNode(e, vv, null, null);
        vv.setLeft(ww);
        size++;
        return ww;
    }
//end#fragment LinkedBinaryTree3
    /** Inserts a right child at a given node. */
    public Position<E>  insertRight(Position<E> v, E e)
            throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        Position<E> rightPos = vv.getRight();
        if (rightPos != null)
            throw new InvalidPositionException("Node already has a right child");
        BTPosition<E> w = createNode(e, vv, null, null);
        vv.setRight(w);
        size++;
        return w;
    }
//begin#fragment LinkedBinaryTree4
    /** Removes a node with zero or one child. */
    public E remove(Position<E> v)
            throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        BTPosition<E> leftPos = vv.getLeft();
        BTPosition<E> rightPos = vv.getRight();
        if (leftPos != null && rightPos != null)
            throw new InvalidPositionException("Cannot remove node with two children");
        BTPosition<E> ww; 	// the only child of v, if any
        if (leftPos != null)
            ww = leftPos;
        else if (rightPos != null)
            ww = rightPos;
        else 		// v is a leaf
            ww = null;
        if (vv == root) { 	// v is the root
            if (ww != null)
                ww.setParent(null);
            root = ww;
        }
        else { 		// v is not the root
            BTPosition<E> uu = vv.getParent();
            if (vv == uu.getLeft())
                uu.setLeft(ww);
            else
                uu.setRight(ww);
            if(ww != null)
                ww.setParent(uu);
        }
        size--;
        return v.element();
    }

//end#fragment LinkedBinaryTree4
//begin#fragment LinkedBinaryTree5
    /** Attaches two trees to be subtrees of an external node. */
    public void attach(Position<E> v, BinaryTree<E> T1, BinaryTree<E> T2)
            throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        if (isInternal(v))
            throw new InvalidPositionException("Cannot attach from internal node");
        if (!T1.isEmpty()) {
            BTPosition<E> r1 = checkPosition(T1.root());
            vv.setLeft(r1);
            r1.setParent(vv);		// T1 should be invalidated
        }
        if (!T2.isEmpty()) {
            BTPosition<E> r2 = checkPosition(T2.root());
            vv.setRight(r2);
            r2.setParent(vv);		// T2 should be invalidated
        }
    }
//end#fragment LinkedBinaryTree5
    /** Swap the elements at two nodes */
    public void swapElements(Position<E> v, Position<E> w)
            throws InvalidPositionException {
        BTPosition<E> vv = checkPosition(v);
        BTPosition<E> ww = checkPosition(w);
        E temp = w.element();
        ww.setElement(v.element());
        vv.setElement(temp);
    }
    /** Expand an external node into an internal node with two external
     node children */
    public void expandExternal(Position<E> v, E l, E r)
            throws InvalidPositionException {
        if (!isExternal(v))
            throw new InvalidPositionException("Node is not external");
        insertLeft(v, l);
        insertRight(v, r);
    }
    /** Remove an external node v and replace its parent with v's
     sibling */
    public void removeAboveExternal(Position<E> v)
            throws InvalidPositionException {
        if (!isExternal(v))
            throw new InvalidPositionException("Node is not external");
        if (isRoot(v))
            remove(v);
        else {
            Position<E> u = parent(v);
            remove(v);
            remove(u);
        }
    }
    // Auxiliary methods
//begin#fragment LinkedBinaryTree5
    /** If v is a good binary tree node, cast to BTPosition, else throw exception */
    protected BTPosition<E> checkPosition(Position<E> v)
            throws InvalidPositionException {
        if (v == null || !(v instanceof BTPosition))
            throw new InvalidPositionException("The position is invalid");
        return (BTPosition<E>) v;
    }
    /** Creates a new binary tree node */
    protected BTPosition<E> createNode(E element, BTPosition<E> parent,
                                       BTPosition<E> left, BTPosition<E> right) {
        return new BTNode<E>(element,parent,left,right); }
    /** Creates a list storing the the nodes in the subtree of a node,
     * ordered according to the preorder traversal of the subtree. */
    protected void preorderPositions(Position<E> v, PositionList<Position<E>> pos)
            throws InvalidPositionException {
        pos.addLast(v);
        if (hasLeft(v))
            preorderPositions(left(v), pos);	// recurse on left child
        if (hasRight(v))
            preorderPositions(right(v), pos);	// recurse on right child
    }
//end#fragment LinkedBinaryTree5
    /** Creates a list storing the the nodes in the subtree of a node,
     * ordered according to the inorder traversal of the subtree. */
    protected void inorderPositions(Position<E> v, PositionList<Position<E>> pos)
            throws InvalidPositionException {
        if (hasLeft(v))
            inorderPositions(left(v), pos);  // recurse on left child
        pos.addLast(v);
        if (hasRight(v))
            inorderPositions(right(v), pos); // recurse on right child
    }
}


//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


/**
 * Realization of a dictionary by means of a binary search tree.
 * @author Michael Goodrich, Eric Zamore
 */

//begin#fragment BinarySearchTree
// Realization of a dictionary by means of a binary search tree
class BinarySearchTree<K,V>
        extends LinkedBinaryTree<Entry<K,V>> implements Dictionary<K,V> {
    //end#fragment BinarySearchTree
    // Instance variables:
//begin#fragment BinarySearchTree
    protected Comparator<K> C;	// comparator
    protected Position<Entry<K,V>>
            actionPos; // insert node or removed node's parent
    protected int numEntries = 0;	// number of entries
    /** Creates a BinarySearchTree with a default comparator. */
    public BinarySearchTree()  {
        C = new DefaultComparator<K>();
        addRoot(null);
    }
//end#fragment BinarySearchTree
    /** Creates a BinarySearchTree with the given comparator. */
//begin#fragment BinarySearchTree
    public BinarySearchTree(Comparator<K> c)  {
        C = c;
        addRoot(null);
    }
    /** Nested class for location-aware binary search tree entries */
    protected static class BSTEntry<K,V> implements Entry<K,V> {
        protected K key;
        protected V value;
        protected Position<Entry<K,V>> pos;
        BSTEntry() { /* default constructor */ }
        BSTEntry(K k, V v, Position<Entry<K,V>> p) {
            key = k; value = v; pos = p;
        }
        public K getKey() { return key; }
        public V getValue() { return value; }
        public Position<Entry<K,V>> position() { return pos; }
    }
//end#fragment BinarySearchTree
    // Auxiliary methods:
//begin#fragment BinarySearchTree
    /** Extracts the key of the entry at a given node of the tree. */
    protected K key(Position<Entry<K,V>> position)  {
        return position.element().getKey();
    }
    /** Extracts the value of the entry at a given node of the tree. */
    protected V value(Position<Entry<K,V>> position)  {
        return position.element().getValue();
    }
    /** Extracts the entry at a given node of the tree. */
    protected Entry<K,V> entry(Position<Entry<K,V>> position)  {
        return position.element();
    }
    /** Replaces an entry with a new entry (and reset the entry's location) */
    protected void replaceEntry(Position <Entry<K,V>> pos, Entry<K,V> ent) {
        ((BSTEntry<K,V>) ent).pos = pos;
        replace(pos, ent);
    }
    //end#fragment BinarySearchTree
//begin#fragment BinarySearchTree2
    /** Checks whether a given key is valid. */
    protected void checkKey(K key) throws InvalidKeyException {
        if(key == null)  // just a simple test for now
            throw new InvalidKeyException("null key");
    }
    /** Checks whether a given entry is valid. */
    protected void checkEntry(Entry<K,V> ent) throws InvalidEntryException {
        if(ent == null || !(ent instanceof BSTEntry))
            throw new InvalidEntryException("invalid entry");
    }
    /** Auxiliary method for inserting an entry at an external node */
    protected Entry<K,V> insertAtExternal(Position<Entry<K,V>> v, Entry<K,V> e) {
        expandExternal(v,null,null);
        replace(v, e);
        numEntries++;
        return e;
    }
    /** Auxiliary method for removing an external node and its parent */
    protected void removeExternal(Position<Entry<K,V>> v) {
        removeAboveExternal(v);
        numEntries--;
    }
    /** Auxiliary method used by find, insert, and remove. */
    protected Position<Entry<K,V>> treeSearch(K key, Position<Entry<K,V>> pos) {
        if (isExternal(pos)) return pos; // key not found; return external node
        else {
            K curKey = key(pos);
            int comp = C.compare(key, curKey);
            if (comp < 0)
                return treeSearch(key, left(pos));	// search left subtree
            else if (comp > 0)
                return treeSearch(key, right(pos));	// search right subtree
            return pos;		// return internal node where key is found
        }
    }
//end#fragment BinarySearchTree2
    /** Adds to L all entries in the subtree rooted at v having keys
     * equal to k. */
//begin#fragment BinarySearchTree2
// Adds to L all entries in the subtree rooted at v having keys equal to k
    protected void addAll(PositionList<Entry<K,V>> L,
                          Position<Entry<K,V>> v, K k) {
        if (isExternal(v)) return;
        Position<Entry<K,V>> pos = treeSearch(k, v);
        if (!isExternal(pos))  {  // we found an entry with key equal to k
            addAll(L, left(pos), k);
            L.addLast(pos.element()); 	// add entries in inorder
            addAll(L, right(pos), k);
        } // this recursive algorithm is simple, but it's not the fastest
    }
//end#fragment BinarySearchTree2
    //begin#fragment BinarySearchTree3
    // methods of the dictionary ADT
    //end#fragment BinarySearchTree3
    /** Returns the number of entries in the tree. */
    //begin#fragment BinarySearchTree3
    public int size() { return numEntries; }
    //end#fragment BinarySearchTree3
    /** Returns whether the tree is empty. */
    //begin#fragment BinarySearchTree3
    public boolean isEmpty() { return size() == 0; }
    //end#fragment BinarySearchTree3
    /** Returns an entry containing the given key.  Returns null if no
     * such entry exists. */
    //begin#fragment BinarySearchTree3
    public Entry<K,V> find(K key) throws InvalidKeyException {
        checkKey(key);		// may throw an InvalidKeyException
        Position<Entry<K,V>> curPos = treeSearch(key, root());
        actionPos = curPos;		// node where the search ended
        if (isInternal(curPos)) return entry(curPos);
        return null;
    }
    //end#fragment BinarySearchTree3
    /** Returns an iterable collection of all the entries containing the
     * given key. */
    //begin#fragment BinarySearchTree3
    public Iterable<Entry<K,V>> findAll(K key) throws InvalidKeyException {
        checkKey(key);		// may throw an InvalidKeyException
        PositionList<Entry<K,V>> L = new NodePositionList<Entry<K,V>>();
        addAll(L, root(), key);
        return L;
    }
    //end#fragment BinarySearchTree3
    /** Inserts an entry into the tree and returns the newly created entry. */
    //begin#fragment BinarySearchTree3
    public Entry<K,V> insert(K k, V x) throws InvalidKeyException {
        checkKey(k);	// may throw an InvalidKeyException
        Position<Entry<K,V>> insPos = treeSearch(k, root());
        while (!isExternal(insPos))  // iterative search for insertion position
            insPos = treeSearch(k, left(insPos));
        actionPos = insPos;	// node where the new entry is being inserted
        return insertAtExternal(insPos, new BSTEntry<K,V>(k, x, insPos));
    }
    //end#fragment BinarySearchTree3
    /** Removes and returns a given entry. */
    //begin#fragment BinarySearchTree3
    public Entry<K,V> remove(Entry<K,V> ent) throws InvalidEntryException  {
        checkEntry(ent);            // may throw an InvalidEntryException
        Position<Entry<K,V>> remPos = ((BSTEntry<K,V>) ent).position();
        Entry<K,V> toReturn = entry(remPos);	// entry to be returned
        if (isExternal(left(remPos))) remPos = left(remPos);  // left easy case
        else if (isExternal(right(remPos))) remPos = right(remPos); // right easy case
        else {			//  entry is at a node with internal children
            Position<Entry<K,V>> swapPos = remPos;	// find node for moving entry
            remPos = right(swapPos);
            do
                remPos = left(remPos);
            while (isInternal(remPos));
            replaceEntry(swapPos, (Entry<K,V>) parent(remPos).element());
        }
        actionPos = sibling(remPos);	// sibling of the leaf to be removed
        removeExternal(remPos);
        return toReturn;
    }
//end#fragment BinarySearchTree3
    /** Returns an iterator containing all entries in the tree. */
    public Iterable<Entry<K,V>> entries() {
        PositionList<Entry<K,V>> entries = new NodePositionList<Entry<K,V>>();
        Iterable<Position<Entry<K,V>>> positer = positions();
        for (Position<Entry<K,V>> cur: positer)
            if (isInternal(cur))
                entries.addLast(cur.element());
        return entries;
    }
    /**
     * Performs a tri-node restructuring.  Assumes the nodes are in one
     * of following configurations:
     *
     * <pre>
     *          z=c       z=c        z=a         z=a
     *         /  \      /  \       /  \        /  \
     *       y=b  t4   y=a  t4    t1  y=c     t1  y=b
     *      /  \      /  \           /  \         /  \
     *    x=a  t3    t1 x=b        x=b  t4       t2 x=c
     *   /  \          /  \       /  \             /  \
     *  t1  t2        t2  t3     t2  t3           t3  t4
     * </pre>
     * @return the new root of the restructured subtree
     */
    protected Position<Entry<K,V>> restructure(Position<Entry<K,V>> x) {
        BTPosition<Entry<K,V>> a, b, c, t1, t2, t3, t4;
        Position<Entry<K,V>> y = parent(x);	// assumes x has a parent
        Position<Entry<K,V>> z = parent(y);	// assumes y has a parent
        boolean xLeft = (x == left(y));
        boolean yLeft = (y == left(z));
        BTPosition<Entry<K,V>> xx = (BTPosition<Entry<K,V>>)x,
                yy = (BTPosition<Entry<K,V>>)y, zz = (BTPosition<Entry<K,V>>)z;
        if (xLeft && yLeft) {
            a = xx; b = yy; c = zz;
            t1 = a.getLeft(); t2 = a.getRight(); t3 = b.getRight(); t4 = c.getRight();
        }
        else if (!xLeft && yLeft) {
            a = yy; b = xx; c = zz;
            t1 = a.getLeft(); t2 = b.getLeft(); t3 = b.getRight(); t4 = c.getRight();
        }
        else if (xLeft && !yLeft) {
            a = zz; b = xx; c = yy;
            t1 = a.getLeft(); t2 = b.getLeft(); t3 = b.getRight(); t4 = c.getRight();
        }
        else { // right-right
            a = zz; b = yy; c = xx;
            t1 = a.getLeft(); t2 = b.getLeft(); t3 = c.getLeft(); t4 = c.getRight();
        }
        // put b at z's place
        if (isRoot(z)) {
            root = b;
            b.setParent(null);
        }
        else {
            BTPosition<Entry<K,V>> zParent = (BTPosition<Entry<K,V>>)parent(z);
            if (z == left(zParent)) {
                b.setParent(zParent);
                zParent.setLeft(b);
            }
            else { // z was a right child
                b.setParent(zParent);
                zParent.setRight(b);
            }
        }
        // place the rest of the nodes and their children
        b.setLeft(a);
        a.setParent(b);
        b.setRight(c);
        c.setParent(b);
        a.setLeft(t1);
        t1.setParent(a);
        a.setRight(t2);
        t2.setParent(a);
        c.setLeft(t3);
        t3.setParent(c);
        c.setRight(t4);
        t4.setParent(c);
        // Reset the location-aware entries
        ((BSTEntry<K,V>) a.element()).pos = a;
        ((BSTEntry<K,V>) b.element()).pos = b;
        ((BSTEntry<K,V>) c.element()).pos = c;
        return b; // the new root of this subtree
    }
//begin#fragment BinarySearchTree3
} 	// entries() method is omitted here
//end#fragment BinarySearchTree3
