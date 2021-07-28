import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
    		  
    		  // Split keys and values between new nodes
              BTreeNode updatedNode = addKeyValToNode(root,student.studentId,student.recordId,true);
              long[] oversizedKeyArray = updatedNode.keys;
    		  splitKeyValsBetweenNodes(updatedNode, leftChild, rightChild);
              root.keys = new long[2 * t - 1]; // get fresh array for root
              root.keys[0] = oversizedKeyArray[oversizedKeyArray.length / 2]; // copy up middle key
              root.n = 1;
    		  
    		  // TODO Split values between new nodes: DONE addKeyValToNode & splitKeyValBetweenNodes
              
              // Make the right child to sibling of the left child
              leftChild.next = rightChild;
              
    		  // Update children on root
    		  root.children[0] = leftChild;
    		  root.children[1] = rightChild;
    		  
    		  // Clear values array on root
    		  root.values = new long[2 * t - 1];
    		  
    		  // Update leaf status
    		  root.leaf = false;
    		  
    		}
    	}
    	// root is not a leaf, need to traverse tree
    	else {
    	  visitChild(root, student);
    	  // TODO need to handle splitting the root if we pushed a key into it and it's too big
    	  if(root.keys.length > (2* t -1)) {
    		  // Create new root node
    		  BTreeNode newRoot = new BTreeNode(this.t,false);
    		  split(root,newRoot,student,false,1);
    		  this.root=newRoot;
    	  }
    	}
    	
    	
    	// Sync the Student.csv with the BTree
    	
    	// read csv to check for duplicates
    	List<Long> studentList = new ArrayList<>();
        Scanner readStudents=null;
        
        try {
			readStudents = new Scanner(new File("src/Student.csv"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Student file not found here.");
		}
        
		while(readStudents.hasNextLine()) {
			String[] studentLine = readStudents.nextLine().split(",");
			long studentId= Long.parseLong(studentLine[0]);
        	studentList.add(studentId);
		}
		readStudents.close();
		
		// check for duplicates. If no duplicate, add new student to Student.csv
		FileWriter studentFile=null;
		try {
			studentFile = new FileWriter("src/Student.csv",true);
		} catch (IOException e) {
			System.out.println("Student file not found or here.");
		}
		
		if(!studentList.contains(student.studentId)) {
			String newStudent = student.studentId + "," + student.studentName + "," + student.major + "," + student.level + "," + student.age + "," + student.recordId + "\n";
				try {
					studentFile.write(newStudent);
					studentFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
        return this;
    }
    
    /**
     * Recursive helper to handle traversing the tree for inserting
     * This will find the right child to visit
     * If the child is a leaf, it will handle inserting the key/value and splitting if needed
     * If the child is not a leaf, it will call itself recursively
     * 
     * @param current - BTreeNode to start with and find it's child
     * @param student - student being inserted to the BTree
     */
    private void visitChild(BTreeNode current, Student student) {
      
      int childIndex = -1;
      long key = student.studentId;
      long value = student.recordId;
      
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
          child = addKeyValToNode(child, key, value,false);
          child.n++;
          // TODO insert value in right position in values array: DONE via addKeyValToNode
        }
      } else { // Child is not a leaf
        visitChild(child, student);
        //TODO split child if too big
        if (child.keys.length > (2 * t - 1)) {
          split(child, current, student, false, childIndex + 1);
        }
      }
      
    }
    
    
    /**
     * Split the child node if it is too large
     * @param child -- child BTreeNode
     * @param parent -- parent BTreeNode to the child
     * @param student -- student to be inserted to the child
     * @param isLeaf -- is the childNode a leaf?
     * @param index -- index in the parent's children array where the new student is being inserted
     */
    private void split(BTreeNode child, BTreeNode parent, Student student, Boolean isLeaf, int index) {

      // Create the new node
      BTreeNode newNode = new BTreeNode(this.t, isLeaf);
      //isLeaf = child.leaf;
      // Call the right method based on leaf status
      if (isLeaf) {
        splitLeaf(child, parent, student, newNode, index);
      } else {
        splitNonLeaf(child, parent, newNode, index);
      }
      
      return;
    }
    
    /**
     * Add to and split the a leaf node and update the parent
     * @param child -- leaf node
     * @param parent -- parent to the leaf
     * @param student -- student to add to the BTree
     * @param newNode -- node to hold the over-fill from the child
     * @param index -- index in the parent's children array where the new node is
     */
    private void splitLeaf(BTreeNode child, BTreeNode parent, Student student, BTreeNode newNode, int index) {

      // Add the key-value pair to the child node 
      BTreeNode updatedChildNode = addKeyValToNode(child, student.studentId, student.recordId, true);
      
      // Now we have an array with the old and new keys
      // Split this between the two child nodes
      splitKeyValsBetweenNodes(updatedChildNode, child, newNode);

      // Update next pointers for both nodes
      newNode.next = child.next;
      child.next = newNode;
        
      //TODO split values between nodes: done via addKeyValToNode & splitKeyValsBetweenNodes
      
      //TODO Update list of children on parent
      // This needs to be updated to temporarily expand the children array if full
      boolean changedSize = false;
      if(parent.children[parent.children.length-1] != null) { // children array is full 
    	  int newSize = (parent.children.length);
    	  parent.children= Arrays.copyOf(parent.children, newSize+1);
    	  changedSize = true;
      }
      
      for (int i = parent.children.length - 1; i > index; i--) {
        parent.children[i] = parent.children[i - 1];
      }
      parent.children[index] = newNode;
      
      // Copy up middle key - expand parent's keys array if needed, add new key in the right spot
      boolean expand = false;
      if(changedSize) {  
      	expand = true;
      }
      
      parent = addKeyValToNode(parent, newNode.keys[0], newNode.values[0], expand);
      parent.n++;
    }
    
    /**
     * Private helper method to add a key value pair to an existing node.
     * Add the pair to the node even if it means the node being overfilled.
     * The overfilled node is passed to splitKeyValsBetweenNodes for splitting.
     * @param existingNode -- existing node to add the key value pair
     * @param key -- new key to add to the node
     * @param value -- value associated with the key
     * @param expand -- true if the size of the key/value arrays need to become larger to accomodate the new key/value pair
     * @return
     */
    private BTreeNode addKeyValToNode(BTreeNode existingNode, long key, long value, boolean expand) {
      
    	long[] keys = existingNode.keys;
    	long[] vals = existingNode.values;
      // Create a temporary array we can add the new key to
      int newLength = keys.length;
      if (expand) {
        newLength++;
      }
      long[] combinedKeys = Arrays.copyOf(keys, newLength);
      long[] combinedVals = Arrays.copyOf(vals, newLength);
      
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
        combinedVals[combinedKeys.length - 1] = value;
      } else {
        for (int j = combinedKeys.length - 1; j > index; j--) {
          combinedKeys[j] = combinedKeys[j - 1];
          combinedVals[j] = combinedKeys[j - 1];
        }
        combinedKeys[index] = key;
        combinedVals[index] = value;
      }
      
      existingNode.keys = combinedKeys;
      if(existingNode.leaf) { // only add values to a leaf node
    	  existingNode.values = combinedVals;
      }
      return existingNode;
    }
    
    /**
     * Private helper method to split key value pairs from 1 parent node into 2 child nodes
     * @param nodeToSplit -- parent node to split
     * @param left -- left child node of the parent
     * @param right -- right child node of the parent
     */
    private void splitKeyValsBetweenNodes(BTreeNode nodeToSplit, BTreeNode left, BTreeNode right) {
      
      long[] keyArray = nodeToSplit.keys;
      long[] valArray = nodeToSplit.values;
      
      // Create fresh arrays with the correct length (minus the temporary expansion)
      long[] leftKeys = new long[left.keys.length];
      long[] leftVals = new long[left.values.length];
      long[] rightKeys = new long[right.keys.length];
      long[] rightVals = new long[right.values.length];
      
      // Existing (left) child
      int mid = keyArray.length / 2;
      for (int k = 0; k < mid; k++) {
        leftKeys[k] = keyArray[k];
        leftVals[k] = valArray[k];
      }
      
      // New (right) child
      int newKeysIndex = 0;
      for (int k = mid; k < keyArray.length; k++) {
    	  rightKeys[newKeysIndex] = keyArray[k];
        rightVals[newKeysIndex++] = valArray[k];
        
      }
      
      // Update the key arrays for each child node
      left.keys = leftKeys;
      left.values = leftVals;
      right.keys = rightKeys;
      right.values = rightVals;
      
      // Update key count
      left.n = mid;
      right.n = (keyArray.length - mid);
      
      return;
    }
    
    /**
     * Split an internal (non-leaf) node in the tree
     * @param child -- internal node to be split
     * @param parent -- parent of the internal node that is being split
     * @param newNode -- new node that gets overfill from the child
     * @param index -- location in the parent's children array where newNode is placed
     */
    private void splitNonLeaf(BTreeNode child, BTreeNode parent, BTreeNode newNode, int index) {
    	//TODO split keys
    	long midKey = child.keys[child.keys.length/2];
    	long midIndex = child.keys.length/2;
    	
    	long[] leftKeys = new long[2 * t - 1];
    	for(int i = 0; i < midIndex; i++) {
    		leftKeys[i] = child.keys[i];
    	}
    	
    	long[] rightKeys = new long[2 * t - 1];
    	int counter = 0;
    	for(int i = (int) (midIndex+1); i < child.keys.length; i++) {
    		rightKeys[counter++]=child.keys[i];
    	}
    	child.keys = leftKeys;
    	child.n = (int) midIndex;
    	newNode.keys = rightKeys;
    	newNode.n = (int) (child.keys.length-midIndex);
    	
      //TODO split children
    	int midChild = child.children.length/2;
    	BTreeNode[] leftChildren = new BTreeNode[2*t];
    	for(int i = 0; i <= midChild; i++) {
    		leftChildren[i] = child.children[i];
    	}
    	
    	BTreeNode[] rightChildren = new BTreeNode[2*t];
    	counter=0;
    	for(int i = midChild+1; i < child.children.length; i++) {
    		rightChildren[counter++]=child.children[i];
    	}
    	
    	child.children=leftChildren;
    	newNode.children=rightChildren;
    	
    	if(index == parent.children.length) {
    		int newSize = parent.children.length;
    		parent.keys = Arrays.copyOf(parent.keys, newSize);
    		parent.children = Arrays.copyOf(parent.children,newSize+1);   
    	}
    	
    	parent.children[index-1]=child;
    	parent.children[index]=newNode;
      
      //TODO push up middle key - expand parent's keys array and add new key in the right spot
    	parent.keys[index-1] = midKey;
    	parent.n++;
    	
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
        if (k == node.keys.length-1) {
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
        if (k == node.values.length-1) {
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
