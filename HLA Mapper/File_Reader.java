import java.io.*;
import java.util.*;

public class File_Reader { // read txt files in VCF format and create dictionaries of SNP objects that contain chromosome, location, reference, and alternate also create a blank 23 and me dictionary so you have all SNPS that 23andme looks for.
    public static SNP collecter(String[] values) {
            SNP new_SNP = new SNP(values[0], values[1],values[2],values[3]); //creates a SNP object from given values in a list which comes from a line in the txt files
            return new_SNP;
        }

    public static Map<String, String[]> me_23_blank_dictionary() { // creates a dictionary which has all locations 23andme looks for, cannot use SNP class above becuase it is a different format than VCF
        try {
            Map<String, String[]> blank = new HashMap<String, String[]>(); //creates a hashmap which has a String for the key and a List for the value
            
            try (Scanner input = new Scanner(System.in)) { //asks for the address of the blank template for the 23 and me file
                System.out.print("Enter the file address of your blank 23 and me file.\n");
                String blank_file_address = input.nextLine();
                
                BufferedReader br = new BufferedReader(
                        new FileReader(blank_file_address)); //where the location of the blank file!!
                String s;
                
                while((s = br.readLine()) != null ) { //reads and iterates through the lines in the blank 23 and me file
                    String[] values = s.split("\t"); // creates a list of the values between tabs

                    if (Character.isDigit(values[1].charAt(0))) { //This pattern is covered in the method below
                        
                        if (Integer.parseInt(values[1]) < 10) {
                            blank.put("chr0" + values[1] + values[2], values);
                        }
                        else if (Integer.parseInt(values[1]) >= 10) {
                            blank.put("chr" + values[1] + values[2], values);
                        }						
                    }
                    else if (values[1].equals("X") || values[1].equals("Y")) {
                        blank.put("chr" + values[1] + values[2], values);							
                    }
                }
                List<String> keys = new ArrayList<String>(blank.keySet()); //This pattern is covered in the method below. I tried making a single method to be used by both but there were too many differences between the formats of the original files to make it simple
                Collections.sort(keys);
                
                Map<String, String[]> sorted_blank = new LinkedHashMap<>();
                
                for (String key : keys) {
                    sorted_blank.put(key, blank.get(key));
                }
                br.close();
                return sorted_blank;
            }								
    }catch(Exception ex) {
        return null;
    }			
    }
    
    public static Linked_SNP_Dict reader(int function, String file_name, String[] range, Map<String, String[]> blank_dictionary) { // the reader method which utilizes above classes to return completed dictionary
        try {
            BufferedReader br = new BufferedReader( //reads through a VCF formatted gene to create a dictionary of SNP objects. A SNP object contains the chromosome, location, alternate, and reference information for a particular Single Nucleotide Polymorphism.
            new FileReader(file_name));
            String s = null; // variable for the lines to be read.
            String key = null; // variable for the key to be created for each SNP in the dictionary.
            SNP_Dict new_dict = new SNP_Dict();
            
            while((s = br.readLine()) != null ) { //iterates through the file by the number of lines in the txt file.
                
                String[] values = s.split(" "); //creates an array of the values separated by spaces in each line
                int position = Integer.parseInt(values[1]); //creates an integer of the position which is the second item in the line.
                String edit = values[0].substring(3); //removes chr before the chromosome number so I can see if it is 
                
                if (edit.length() <= 2) {   // So any chromosomes outside the normal 23 ( file-included bacterial dna for example) are left out
                    
                    if (Character.isDigit(edit.charAt(0))) { //asks whether the chromosome is a number, so below it doesn't try to parse X or Y as integers
                        
                        if (Integer.parseInt(edit) < 10) { //here I make the chromosomes that are single digits to have keys which add a zero after the chr. 
                                                            // I needed to make a universal key between the VCF files and the 23 and me file so the VCF could be converted.
                            edit = "chr0" + edit;			//So i figured the key would be the chromosome name and the position but those under 10 might have matches with ones in the double digits such as chr1134346, this could be chromosome 1 position 134346 or chromosome 11 34346, so adding a zero before the single digit chromosomes fixes this problem. 
                            key = edit + values[1];
                        }
                        else if (Integer.parseInt(edit) >= 10) {
                            edit = values[0];
                            key = values[0] + values[1];
                        }						
                    else if (edit.equals("X") || edit.equals("Y")) {
                        edit = values[0];
                        key = values[0] + values[1];
                    }
                    }
                    if (function == 0 && values[0].equals(range[0]) && position >= Integer.parseInt(range[1]) && position <= Integer.parseInt(range[2])) {
                        new_dict.populate(edit, File_Reader.collecter(values)); //creates a dictionary within the range desired. 
                    }
                    if (function == 1) {
                        
                        if (blank_dictionary.containsKey(key)) {
                            new_dict.populate(edit, File_Reader.collecter(values));	//creates dictionary if the SNPS are also in the blank 23 and me dictionary				
                        }
                    }				
                }
            }
            List<String> keys = new ArrayList<String>(new_dict.SNPDict.keySet()); // creates an array of all keys in the newly created dictionary
            
            Collections.sort(keys);  //sorts the keys numerically. THERE IS A BIT OF A BUG HERE THAT SHOULDN'T MATTER, the keys aren't sorted numerically perfectly. Because of the way the keys are made it will put 1000000000 before 11000 I tried several solutions but realized this was cosmetic, if I ever ran into an issue I would probably try to fix this. The issue arose from trying to sort by a value within an array inside of the values of a dictionary. I had trouble figuring that out, finding any solutions online either.
            
            Linked_SNP_Dict sorted_dict = new Linked_SNP_Dict(); //creates a linked dictionary to populate sortedly using the array above
            
            for (String k: keys) { //iterates through the sorted array
                String[] arr = {new_dict.SNPDict.get(k).chromosome, new_dict.SNPDict.get(k).position, new_dict.SNPDict.get(k).reference, new_dict.SNPDict.get(k).alternate};
                File_Reader.collecter(arr);
                sorted_dict.LinkedSNPDict.put(k, File_Reader.collecter(arr)); // uses the SNP dict methods to populate a dictionary. 
            }
            br.close();
            return sorted_dict;
          }
        catch(Exception ex) {
            return null;
        }
    }
}