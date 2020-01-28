package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;
import structures.Vertex.Neighbor;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {

	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		/* COMPLETE THIS METHOD */
		
		PartialTreeList L = new PartialTreeList();
		
		
		for(int v = 0; v<graph.vertices.length;v++) {
			
			PartialTree T = new PartialTree(graph.vertices[v]);
			
			
			MinHeap<Arc> P = new MinHeap<Arc>();
			Neighbor nbr = T.getRoot().neighbors;
			while(nbr != null) {
				
				Arc arcs = new Arc(T.getRoot(), nbr.vertex, nbr.weight);
				P.insert(arcs);
				nbr = nbr.next;
			}
			T.getArcs().merge(P);			
			
			L.append(T);
		}
		return L;
		
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		
		/* COMPLETE THIS METHOD */
		ArrayList<Arc> MSTList = new ArrayList<Arc>();
		
		while(ptlist.size()>1) {
			PartialTree PTX = ptlist.remove();
			MinHeap<Arc>PQX = new MinHeap<Arc>(PTX.getArcs());
			if(PQX.isEmpty())continue;
			Arc alpha = PQX.deleteMin();
			
			while(alpha.getv2().getRoot() == PTX.getRoot()) {
				alpha = PQX.deleteMin();
			}
			MSTList.add(alpha);
			PartialTree PTY = ptlist.removeTreeContaining(alpha.getv2());
			PTX.merge(PTY);
			ptlist.append(PTX);
		}
		return MSTList;	
	}

	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	/* COMPLETE THIS METHOD */
    	PartialTree output = null;
    	boolean found = false;
    	
        if(rear == null) {
            throw new NoSuchElementException();
        }
        
        Node ptr = rear.next;
        Node prev = rear;
        int i = 0;
        
        output = removeTree(i, vertex, ptr, found, output, prev);

        return output;
    }
    private PartialTree removeTree(int i,Vertex vertex,Node ptr, boolean found,PartialTree output,Node prev) {
    	do {
    		if(removeTreeTraverse(vertex, ptr.tree,found) == true) {
    			if(ptr == rear) {
    				output = ptr.tree;
    				prev.next = rear.next;
    				rear = prev;
    				size--;
    				return output;
    			}
    			else {
    				output = ptr.tree;
    				prev.next = ptr.next;
    				size--;
    				return output;
    			}
    		}
    		prev = ptr;
    		ptr = ptr.next;
    		i++;
    	}while(i < size);
    	return output;
    }
   
    private boolean removeTreeTraverse(Vertex vertex, PartialTree Tree, boolean found) {
    	while(vertex != null) {
    		if(vertex == Tree.getRoot()) {
    			found = true;
    			return true;
    		}
    		if(vertex.equals(vertex.parent)) {
    			return false;
    		}
    		vertex = vertex.parent;
    	}
    	return false;
    }

    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
    
}