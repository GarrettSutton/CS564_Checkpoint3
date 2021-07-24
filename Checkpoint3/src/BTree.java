import java.util.ArrayList;
import java.util.List;

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
        return this;
    }

    boolean delete(long studentId) {
        /**
         * TODO:
         * Implement this function to delete in the B+Tree.
         * Also, delete in student.csv after deleting in B+Tree, if it exists.
         * Return true if the student is deleted successfully otherwise, return false.
         */
        if (root == null) {
          return false;
        }
        
        if (root.leaf == true) {
          return deleteFromNode(root, studentId);
        }
        
        return true;
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
        return listOfRecordID;
    }
}
