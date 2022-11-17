import java.util.*;

public class Menu { // IN FUTURE ADD OPTION TO COMPARE ENTIRE SNP LIST AND GIVE MATCH PERCENT AND TOTAL SNP MATCH COUNT
    public static void menu() {
        try (Scanner input = new Scanner(System.in)) { // Menu to choose how to interact with the VCF formatted gene files.
            System.out.print("Please choose an option.\n0: Compare the SNPS of two genomes in a range(s) of your choosing.\n1: Convert your VCF file to 23 and me format.\n");
            int choice = Integer.parseInt(input.nextLine());
            int count = 0;
            ArrayList<String[]> ranges = new ArrayList<String[]>();
            
            if (choice == 0) {
                System.out.print("Please provide the file address of the first genome.\n"); // here and below you enter the file address of the txt file.
                String file_name0 = input.nextLine();

                System.out.print("Please provide the file address of the second genome.\n");
                String file_name1 = input.nextLine();
                
                ranges = range_menu(ranges);	
                                
                for (String[] range : ranges) {  // This iterates through the array which is created by "range_menu" and creates dictionaries containing the SNP objects from the class at the top of this page.
                    Linked_SNP_Dict SNPDict0 = File_Reader.reader(0, file_name0, range, null);
                    Linked_SNP_Dict SNPDict1 = File_Reader.reader(0, file_name1, range, null);
                    
                    String file_name = "HLA_match_results" + count + ".txt";  //This creates new file names using a counter for each range in the array created above.
                    count += 1;
                    
                    File_Writer.compare_HLA(SNPDict0, SNPDict1, file_name); 
                }
            }				
            else if (choice == 1) {
                System.out.print("Please provide the file name of the file you would like to convert.\n");
                String file_name = input.nextLine();
                Map<String, String[]> SNP_Dict0 = File_Reader.me_23_blank_dictionary();
                Linked_SNP_Dict SNPDict1 = File_Reader.reader(1, file_name, null , SNP_Dict0);
                File_Writer.meand23format(SNP_Dict0, SNPDict1);
                
            }				
            else {
                System.out.print("Invalid entry please try again.\n");
                menu();
            }
        } 
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<String[]> range_menu(ArrayList<String[]> ranges) { // This is a recursive portion of the menu which adds different range requests for comparison between the two chosen VCF formmatted txt files.
        
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Please provide the chromosome you would like to look at. \nType chr before whichever chromosome you want to choose.\n");  //use format chr__
            String chromosome = input.nextLine();
            
            System.out.print("Please provide the starting position in the range.\n");
            String start_pos = input.nextLine();
            
            System.out.print("Please provide the ending position in the range.\n");
            String end_pos = input.nextLine();
            
            String[] range = {chromosome, start_pos, end_pos};
            
            System.out.print("Would you like to compare another area? \nYes or No?\n");
            String yes_no = input.nextLine();
            
            if (yes_no.equals("Yes")) { // if you choose yes it calls range_menu again and sets up a recursion to add as many ranges to the array as "yes" is chosen.
                ranges = range_menu(ranges);  //recursion babyyyy
                ranges.add(range);
                return ranges;
            }			
            else if (yes_no.equals("No")) {
                ranges.add(range);
                return ranges;
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return null;	
    }
}