import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
/**
 * B+Tree Structure
 * Key - StudentId
 * Leaf Node should contain [ key,recordId ]
 */
class BTree {

    /**
     * Pointer to the root node.
     */
    private BTreeNode root;
    /**
     * Number of key-value pairs allowed in the tree/the minimum degree of B+Tree
     **/
    private int t;

    BTree(int t) {
        this.root = null;
        this.t = t;
    }

    long search(long studentId) {
        /**
         * TODO:
         * Implement this function to search in the B+Tree.
         * Return recordID for the given StudentID.
         * Otherwise, print out a message that the given studentId has not been found in the table and return -1.
         */
        return -1;
    }

    BTree insert(Student student) {
        /**
         * TODO:
         * Implement this function to insert in the B+Tree.
         * Also, insert in student.csv after inserting in B+Tree.
         */
    	BTreeNode x = root;
    	BTreeNode newNode = null;
    	
    	//if root is empty, create a leaf node with key from student and make that node the root
    	if (x == null) {
    		newNode.keys[0] = student.studentId;
    		newNode.values[0] = student.recordId;
    		newNode.n++;
    		newNode.leaf = true;
    		newNode.children = null;
    		this.root = newNode;
    	}
    	//if the root exists, check to see if it has children. If not, check to see if there is space to store another value. If no space, split
    	else if (this.root.children != null) {
    		if (this.root.keys.length < t-1) {//checks to see if root is full. If not, add new value to root
    			this.root.keys[this.root.keys.length] = student.studentId;
    			Arrays.sort(this.root.keys);
    			//this.root.n++; not sure if we need this
    			this.root.values[this.root.keys.length] = student.recordId; 
    		}
    		else {//root will be full after adding, will need to split
    			this.root.keys[this.root.keys.length] = student.studentId;
    			this.root.n++;
    			this.root.values[this.root.keys.length] = student.recordId; 
    			split(root);
    		}
    	}
    	//root exists and has children, search for where to insert the new values
    	else {
    		BTreeNode current = this.root;
    		while (current.children != null)  {//continue through nodes until we find the correct leaf    			
    			for (int i = 0; i < current.keys.length; i++) { //go through values in node
    				if (current.keys[i] < student.studentId) {}
    				else {
    					current = current.children[i];
    				}
    			}
    		}//while loop	
    		if (current.keys.length == t) { //the leaf node does not have enough space, add the key and split
    			current.keys[current.keys.length] = student.studentId;
    			Arrays.sort(current.keys);
    			current.values[this.root.keys.length] = student.recordId;
    			split(current);
    		}
    		else { // leaf node has enough space, add key and sort
    			current.keys[current.keys.length] = student.studentId;
    			Arrays.sort(current.keys);
    			current.values[this.root.keys.length] = student.recordId;
    		}
    	}
        return this;
    }
    
    /*
     * helper method for insert. Splits both Root and leaf nodes
     * if leaf does not have space to insert in node, split into 2 nodes, redistribute keys, copy up middle key, add pointer to new node 
     * if non-leaf does not have space, split into 2 nodes, redistribute keys, move up middle key
     */
    private void split(BTreeNode node) {
    	BTreeNode parentNode = node;
    	BTreeNode originalNode = node;
    	BTreeNode newNode = null;
    	long midKey = originalNode.keys[t/2];
    	int a = 0;
    	
    	//Split for non-leaf node
    	if (node.children != null) {    
        	for (int i = t/2; i < originalNode.keys.length; i++) {
        		newNode.keys[a] = originalNode.keys[i];
       			a++;
       		}
    	}
    	//split for leaf node
    	else { 
    		for (int i = t/2; i < originalNode.keys.length; i++) {
    			newNode.keys[a] = originalNode.keys[i];
    			newNode.leaf = true;
    			originalNode.next = newNode;
    			a++;
    		}
    	}
    }
    
    
    
    
    
    
    
    boolean delete(long studentId) {
        /**
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        return true;
    }

    List<Long> print() {

        List<Long> listOfRecordID = new ArrayList<>();

        /**
         * TODO:
         * Implement this function to print the B+Tree.
         * Return a list of recordIDs from left to right of leaf nodes.
         *
         */
        return listOfRecordID;
    }
}
