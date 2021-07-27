import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application.
 */
public class BTreeMain {

    public static void main(String[] args) {

        /** Read the input file -- input.txt */
        Scanner scan = null;
        try {
            scan = new Scanner(new File("src/input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        /** Read the minimum degree of B+Tree first */

        int degree = scan.nextInt();

        BTree bTree = new BTree(degree);

        /** Reading the database student.csv into B+Tree Node*/
        List<Student> studentsDB = getStudents();

        for (Student s : studentsDB) {
            bTree.insert(s);
        }
        
        long size = (long) studentsDB.size();
        /** Start reading the operations now from input file*/
        try {
            while (scan.hasNextLine()) {
                Scanner s2 = new Scanner(scan.nextLine());

                while (s2.hasNext()) {

                    String operation = s2.next();

                    switch (operation) {
                        case "insert": {

                            long studentId = Long.parseLong(s2.next());
                            String studentName = s2.next() + " " + s2.next();
                            String major = s2.next();
                            String level = s2.next();
                            int age = Integer.parseInt(s2.next());
                            /** TODO: Write a logic to generate recordID*/

                            long recordID = ++size;

                            Student s = new Student(studentId, age, studentName, major, level, recordID);
                            bTree.insert(s);

                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else
                                System.out.println("Student deletion failed.");

                            break;
                        }
                        case "search": {
                            long studentId = Long.parseLong(s2.next());
                            long recordID = bTree.search(studentId);
                            if (recordID != -1)
                                System.out.println("Student exists in the database at " + recordID);
                            else
                                System.out.println("Student does not exist.");
                            break;
                        }
                        case "print": {
                            List<Long> listOfRecordID = new ArrayList<>();
                            listOfRecordID = bTree.print();
                            System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
                            break;
                        }
                        default:
                            System.out.println("Wrong Operation");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Student> getStudents() {

        /** TODO:
         * Extract the students information from "Students.csv"
         * return the list<Students>
         */
    	
        List<Student> studentList = new ArrayList<>();
        Scanner readStudents=null;
        try {
			readStudents = new Scanner(new File("src/Student.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Student file not found.");
		}
		while(readStudents.hasNextLine()) {
			String[] studentLine = readStudents.nextLine().split(",");
			long studentId= Long.parseLong(studentLine[0]);
			int age = Integer.parseInt(studentLine[4]);
			long recordId = Long.parseLong(studentLine[5]);
        	Student newStudent = new Student(studentId,age,studentLine[1],studentLine[2],studentLine[3],recordId);
        	studentList.add(newStudent);
		}
        
        readStudents.close();
        return studentList;
    }
}
