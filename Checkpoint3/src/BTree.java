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
      if (root == null) {
        return -1;
      }
      
      return searchHelper(root, studentId);
    }
      
    long searchHelper(BTreeNode current, long key) {
      // If the current node is a leaf, search the keys
      // If found, return the value at the same index where the key was found
      if (current.leaf) {
        for (int i = 0; i < current.keys.length; i++) {
          if (key == current.keys[i]) {
            return current.values[i];
          }
        }
        
        // Key was not found
        return -1;
        
      } else { // Find the right child to visit
        int childIndex = -1;
        for (int j = 0; j < current.keys.length; j++) {
          if ((key < current.keys[j]) || (current.keys[j] == 0)) {
            childIndex = j;
            break; // Stop looping once we found the right child
          }
        }
        
        // If the student key is greater than all in current, visit the last child
        if (childIndex == -1) {
          childIndex = current.children.length - 1;
        }
        
        // Visit the child node
        return searchHelper(current.children[childIndex], key);
      }
    }

    BTree insert(Student student) {
        /**
         * TODO:
         * Implement this function to insert in the B+Tree.
         * Also, insert in student.csv after inserting in B+Tree.
         */
    	
    	// Tree needs created
    	if(this.root==null) {
    		// create a new B Tree Node for the root
    		BTreeNode newNode = new BTreeNode(this.t,true);
    		this.root=newNode;
    		root.keys[0]=student.studentId;
    		root.values[0]=student.recordId;
    		root.n++;
    		return this; // insertion is complete for this student.
    	}
    	// Tree has been is created already
    	// No children
    	if(this.root.leaf == true) {
    		if (this.root.n < (this.root.keys.length)) { //there is room in the root
	    		root.keys[root.n]=student.studentId;
	    		root.values[root.n]=student.recordId;
	    		
	    		// Need to check that keys are in sorted order & make changes to values if needed
	    		boolean sorted = false;
	    		int i=0;
	    		while(!sorted) {
	    			if(root.keys[i] > root.keys[root.n]) { // need to change the order
	    				long tempKey = root.keys[i];
	    				long tempVal = root.values[i];
	    				root.keys[i]=root.keys[root.n]; // move the new key to where it should be
	    				root.values[i]=root.values[root.n];
	    				for(int j=i+1; j<=root.n ; j++) { // move all other elements to be in order
	    					long nextTempKey = root.keys[j];
	    					long nextTempVal = root.values[j];
	    					root.keys[j]=tempKey;
	    					root.values[j]=tempVal;
	    					tempKey=nextTempKey;
	    					tempVal=nextTempVal;
	    				}
	    				sorted=true;
	    			}
	    			i++;
	    			if(i > root.n) {
	    				sorted = true;
	    			}
	    		}
	    		root.n++;
	    		return this; // insertion to the root is complete.
    		} else { // no room in the root - split
    		  
    		  // Create two new nodes
    		  BTreeNode leftChild = new BTreeNode(this.t, true);
    		  BTreeNode rightChild = new BTreeNode(this.t, true);
    		  
    		  // Split keys between new nodes
    		  long[] combinedKeys = addKeyToArray(root.keys, student.studentId, true);
              splitKeysBetweenNodes(combinedKeys, leftChild, rightChild);
              root.keys = new long[2 * t - 1]; // get fresh array for root
              root.keys[0] = combinedKeys[combinedKeys.length / 2]; // copy up middle key
              root.n = 1;
    		  
    		  // TODO Split values between new nodes

    		  
    		  // Update children on root
    		  root.children[0] = leftChild;
    		  root.children[1] = rightChild;
    		  
    		  // Clear values array on root
    		  root.values = new long[2 * t - 1];
    		  
    		  // Update leaf status
    		  root.leaf = false;
    		  
    		}
    	}
    	
    	//BTreeNode x = root;
    	//BTreeNode newNode = null;
    	
    	//if root is empty, create a leaf node with key from student and make that node the root
    	/* causing null pointer exception
    	if (x == null) {
    		newNode.keys[0] = student.studentId;
    		newNode.values[0] = student.recordId;
    		newNode.n++;
    		newNode.leaf = true;
    		newNode.children = null;
    		this.root = newNode;
    	}
    	*/
    	
    	// root is not a leaf, need to traverse tree
    	else {
    	  visitChild(root, student);
    	  // TODO need to handle splitting the root if we pushed a key into it and it's too big
    	}
    	
    	
    	
    	// Original code
    	//if the root exists, check to see if it has children. If not, check to see if there is space to store another value. If no space, split
//    	else if (this.root.children != null) {
//    		if (this.root.keys.length < t-1) {//checks to see if root is full. If not, add new value to root
//    			this.root.keys[this.root.keys.length] = student.studentId;
//    			Arrays.sort(this.root.keys);
//    			//this.root.n++; not sure if we need this
//    			this.root.values[this.root.keys.length] = student.recordId; 
//    		}
//    		else {//root will be full after adding, will need to split
//    			this.root.keys[this.root.keys.length] = student.studentId;
//    			this.root.n++;
//    			this.root.values[this.root.keys.length] = student.recordId; 
//    			split(root);
//    		}
//    	}
//    	//root exists and has children, search for where to insert the new values
//    	else {
//    		BTreeNode current = this.root;
//    		while (current.children != null)  {//continue through nodes until we find the correct leaf    			
//    			for (int i = 0; i < current.keys.length; i++) { //go through values in node
//    				if (current.keys[i] < student.studentId) {}
//    				else {
//    					current = current.children[i];
//    				}
//    			}
//    		}//while loop	
//    		if (current.keys.length == t) { //the leaf node does not have enough space, add the key and split
//    			current.keys[current.keys.length] = student.studentId;
//    			Arrays.sort(current.keys);
//    			current.values[this.root.keys.length] = student.recordId;
//    			split(current);
//    		}
//    		else { // leaf node has enough space, add key and sort
//    			current.keys[current.keys.length] = student.studentId;
//    			Arrays.sort(current.keys);
//    			current.values[this.root.keys.length] = student.recordId;
//    		}
//    	}
        return this;
    }
    
    /**
     * Recursive helper to handle traversing the tree for inserting
     * This will find the right child to visit
     * If the child is a leaf, it will handle inserting the key/value and splitting if needed
     * If the child is not a leaf, it will call itself recursively
     * 
     * @param current
     * @param student
     */
    private void visitChild(BTreeNode current, Student student) {

      int childIndex = -1;
      long key = student.studentId;
      
      // Find the right child to visit
      for (int i = 0; i < current.keys.length; i++) {
        if ((key < current.keys[i]) || (current.keys[i] == 0)) {
          childIndex = i;
          break; // Stop looping once we found the right child
        }
      }
      
      // If the student key is greater than all in current, visit the last child
      if (childIndex == -1) {
        childIndex = current.children.length - 1;
      }
      
      // If the next child is a leaf, try to insert into it
      // If not, then visit the child recursively
      BTreeNode child = current.children[childIndex];
      
      if (child != null && child.leaf) { // Child is a leaf
        if (child.n == child.keys.length) { // Child is full
          // TODO Split
          split(child, current, student, true, childIndex + 1);
        } else { // Add key/value to child
//          child.keys[child.keys.length] = student.studentId;
//          Arrays.sort(child.keys);
//          child.values[child.keys.length] = student.recordId;
          child.keys = addKeyToArray(child.keys, student.studentId, false);
          child.n++;
          // TODO insert value in right position in values array
        }
      } else { // Child is not a leaf
        visitChild(child, student);
        //TODO split child if too big
        if (child.keys.length > (2 * t - 1)) {
          split(child, current, student, false, childIndex + 1);
        }
      }
      
    }
    
    /*
     * helper method for insert. Splits both Root and leaf nodes
     * if leaf does not have space to insert in node, split into 2 nodes, redistribute keys, copy up middle key, add pointer to new node 
     * if non-leaf does not have space, split into 2 nodes, redistribute keys, move up middle key
     */
//    private void split(BTreeNode node) {
//    	BTreeNode parentNode = node;
//    	BTreeNode originalNode = node;
//    	BTreeNode newNode = null;
//    	long midKey = originalNode.keys[t/2];
//    	int a = 0;
//    	
//    	//Split for non-leaf node
//    	if (node.children != null) {    
//        	for (int i = t/2; i < originalNode.keys.length; i++) {
//        		newNode.keys[a] = originalNode.keys[i];
//       			a++;
//       		}
//    	}
//    	//split for leaf node
//    	else { 
//    		for (int i = t/2; i < originalNode.keys.length; i++) {
//    			newNode.keys[a] = originalNode.keys[i];
//    			newNode.leaf = true;
//    			originalNode.next = newNode;
//    			a++;
//    		}
//    	}
//      
//      
//      
//    }
    
    // Index parameter should be the index in the parent's children array where the new node
    // should be inserted
    private void split(BTreeNode child, BTreeNode parent, Student student, Boolean isLeaf, int index) {

      // Create the new node
      BTreeNode newNode = new BTreeNode(this.t, isLeaf);
      
      // Call the right method based on leaf status
      if (isLeaf) {
        splitLeaf(child, parent, student, newNode, index);
      } else {
        splitNonLeaf(child, parent, newNode, index);
      }
      
      return;
    }
      
    private void splitLeaf(BTreeNode child, BTreeNode parent, Student student, BTreeNode newNode, int index) {

      // Create a temporary array we can add the new key to
      long[] combinedKeys = addKeyToArray(child.keys, student.studentId, true);
      
      // Now we have an array with the old and new keys
      // Split this between the two child nodes
      splitKeysBetweenNodes(combinedKeys, child, newNode);

      // Update next pointers for both nodes
      newNode.next = child.next;
      child.next = newNode;
        
      //TODO split values between nodes
      
      //TODO Update list of children on parent
      // This needs to be updated to temporarily expand the children array if full
      for (int i = parent.children.length - 1; i > index; i--) {
        parent.children[i] = parent.children[i - 1];
      }
      parent.children[index] = newNode;
      
      // Copy up middle key - expand parent's keys array if needed, add new key in the right spot
      boolean expand = false;
      if (parent.n == parent.keys.length) {
        expand = true;
      }
      
      parent.keys = addKeyToArray(parent.keys, newNode.keys[0], expand);
      parent.n++;
    }
    
    private long[] addKeyToArray(long[] keys, long key, boolean expand) {
      
      // Create a temporary array we can add the new key to
      int newLength = keys.length;
      if (expand) {
        newLength++;
      }
      long[] combinedKeys = Arrays.copyOf(keys, newLength);
      
      // Find the index to insert the new value
      int index = -1;
      for (int i = 0; i < combinedKeys.length; i++) {
        if ((key < combinedKeys[i]) || (combinedKeys[i] == 0)) {
          index = i;
          break;
        }
      }
      
      // If the new key is greater than all the keys, insert at the end
      // If not, then slide all the existing values toward the end
      if (index == -1) {
        combinedKeys[combinedKeys.length - 1] = key;
      } else {
        for (int j = combinedKeys.length - 1; j > index; j--) {
          combinedKeys[j] = combinedKeys[j - 1];
        }
        combinedKeys[index] = key;
      }
      
      return combinedKeys;
    }
    
    private void splitKeysBetweenNodes(long[] array, BTreeNode left, BTreeNode right) {
      
      // Create fresh arrays with the correct length (minus the temporary expansion)
      long[] leftKeys = new long[left.keys.length];
      long[] rightKeys = new long[right.keys.length];
      
      // Existing (left) child
      int mid = array.length / 2;
      for (int k = 0; k < mid; k++) {
        leftKeys[k] = array[k];
      }
      
      // New (right) child
      int newKeysIndex = 0;
      for (int k = mid; k < array.length; k++) {
        rightKeys[newKeysIndex++] = array[k];
      }
      
      // Update the key arrays for each child node
      left.keys = leftKeys;
      right.keys = rightKeys;
      
      // Update key count
      left.n = mid;
      right.n = (array.length - mid);
      
      return;
    }
    
    private void splitNonLeaf(BTreeNode child, BTreeNode parent, BTreeNode newNode, int index) {
      //TODO split keys
      
      //TODO split children
      
      //TODO push up middle key - expand parent's keys array and add new key in the right spot
      
      return;
    }
    
    boolean delete(long studentId) {
        /**
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
      boolean result = false;
      
      // Root doesn't exist
      if (root == null) {
        return false;
      }
      
      // Root is a leaf - delete student from root
      if (root.leaf == true) {
        result = deleteFromNode(root, studentId);
      } else { // Root is not a leaf - find the right node to delete from
        result = visitChildDelete(root, studentId);
      }
      
      // TODO
      if (result) {
        // delete from CSV file
      }
      
      return result;
    }
    
    private boolean visitChildDelete(BTreeNode current, long key) {

      int childIndex = -1;
      boolean result = false;
      
      // Find the right child to visit
      for (int i = 0; i < current.keys.length; i++) {
        if ((key < current.keys[i]) || (current.keys[i] == 0)) {
          childIndex = i;
          break; // Stop looping once we found the right child
        }
      }
      
      // If the student key is greater than all in current, visit the last child
      if (childIndex == -1) {
        childIndex = current.children.length - 1;
      }
      
      // If the next child is a leaf, try to delete from it
      // If not, then visit the child recursively
      BTreeNode child = current.children[childIndex];
      
      if (child != null && child.leaf) { // Child is a leaf
        result = deleteFromNode(child, key);
        
        // If the key wasn't found, return
        if (!result) {
          return false;
        }
        
        // Key was found - check if child has enough keys left
        int minKeys = (((2 * t - 1) / 2) + 1);
        if (child.n < minKeys) { // Child needs more keys
          
          // Try to redistribute from sibling
          if (child.next != null && child.next.n > minKeys) { // Next leaf has available keys
            
            // Move first key/value in next to child
            child.keys[child.n] = child.next.keys[0];
            child.values[child.n] = child.next.values[0];
            
            // Remove key from next's keys array
            for (int i = 0; i < child.next.keys.length - 1; i++) {
              child.next.keys[i] = child.next.keys[i + 1];
            }
            // Clear the last index in the array
            child.next.keys[child.next.keys.length - 1] = 0;
            
            // Replace key in parent with new 1st key in next
            current.keys[childIndex - 1] = child.next.keys[0];
            
            // Remove value from next's keys array
            for (int i = 0; i < child.next.values.length - 1; i++) {
              child.next.values[i] = child.next.values[i + 1];
            }
            // Clear the last index in the array
            child.next.values[child.next.values.length - 1] = 0;
            
            // Update the key counts
            child.n++;
            child.next.n--;
            
          } else { // Next leaf does not have available keys - need to merge child nodes
            //TODO handle removing key from parent
            
            // Move keys from next -> child
            int nextIndex = 0;
            for (int i = child.n; i < child.keys.length; i++) {
              child.keys[i] = child.next.keys[nextIndex];
              nextIndex++;
            }
            
            // Move values from next -> child
            nextIndex = 0;
            for (int i = child.n; i < child.values.length; i++) {
              child.values[i] = child.next.values[nextIndex];
              nextIndex++;
            }
            
            // Update key count
            child.n += child.next.n;
            
            // Update child's next pointer
            if (child.next != null) {
              child.next = child.next.next;
            } else {
              child.next = null;
            }
          }
        }
      } else { // Child is not a leaf
        result = visitChildDelete(child, key);
        
        if (!result) {
          return false;
        }

        //TODO check if child nodes need to be merged
        
      }
      return result;
    }
    
    private boolean deleteFromNode(BTreeNode node, long key) {
      int index = -1;
      
      // Find the key in the key array
      for (int i = 0; i < node.keys.length; i++) {
        if (node.keys[i] == key) {
          index = i;
          break;
        }
      }
      
      // Key not found
      if (index == -1) {
        return false;
      }
      
      // Key found - remove it from the array
      for (int k = index; k < node.keys.length; k++) {
        if (node.keys[k] == node.keys.length) {
          node.keys[k] = 0;
        } else {
          node.keys[k] = node.keys[k + 1];
        }
      }
      
      // Update key count
      node.n--;
      
      // Return if this isn't a leaf
      if (!node.leaf) {
        return true;
      }
      
      // Remove the same index from the values in the leaf
      for (int k = index; k < node.values.length; k++) {
        if (node.values[k] == node.values.length) {
          node.values[k] = 0;
        } else {
          node.values[k] = node.values[k + 1];
        }
      }

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
        BTreeNode current = root;
        
        if (root == null) {
          return listOfRecordID;
        }
        
        // Starting from the root, traverse the tree using the left-most child of each non-leaf node
        while (current != null && !current.leaf) {
          current = current.children[0];
        }
        
        // Current is now the left-most leaf node
        // For each leaf, pull the record IDs from the values array
        while (current != null) {
          for (int i = 0; i < current.keys.length; i++) {
            if (current.keys[i] != 0) {
              listOfRecordID.add(current.values[i]);
            }
          }
          current = current.next;
        }
        
        return listOfRecordID;
    }
}
