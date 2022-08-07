import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class NutraGenie {
	
	public static class SNP_Dict { //object class that creates a dictionary you can populate with SNPS
		
		 private Map<String, SNP> SNPDict = new HashMap<String, SNP>(); //initiates a hashmap which is filled with SNPS
		
		 public void populate(String edit, SNP input) { //populate dictionary with SNPS
				SNPDict.put(edit + String.valueOf(input.position), input);	//You will see these if statements below as well, I am creating unique keys for each position on the genome because the VCF files don't use rsid so I am just making a universal key, examples: chr013425134 or chr1351848318
	}
	}
	
	public static class Linked_SNP_Dict {
		Map<String, SNP> LinkedSNPDict = new LinkedHashMap<String, SNP>(); //This is a Linked dictionary for SNPs
		
	}
	public static class SNP { // Object class that contains all four data points 
		private String chromosome = null;
		private String position = null;
		private String reference = null;
		private String alternate = null;
		
		public SNP(String chrom, String pos, String ref, String alt) { //Object which becomes the value in the VCF formatted text files. These values are filled by the information in each line of those files.
			chromosome = chrom;
			position = pos;
			reference = ref;
			alternate = alt;
		}		
	}
	
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
	
	public class File_Writer {//Compare the blank dictionary to new_dict and create a text file in 23 and me format that is populate with all present SNPS, Compare two different new_dicts and output a percentage of similarity.
			public static void compare_HLA(NutraGenie.Linked_SNP_Dict SNPDict0, NutraGenie.Linked_SNP_Dict SNPDict1, String file_name) {
				double count = 0; //counter of how many keys are shared between the two SNP dictionaries created from the two chosen VCF formatted gene files. 
				double totalCount = 0; //counter of total keys in the first SNP dictionary

				for (String key : SNPDict0.LinkedSNPDict.keySet()) {
					totalCount +=1;
					
					if(SNPDict1.LinkedSNPDict.containsKey(key)) { //if the key is the same AND the reference and alternate genes match the counter goes up.
						if (SNPDict0.LinkedSNPDict.get(key).reference.equals(SNPDict1.LinkedSNPDict.get(key).reference) && SNPDict0.LinkedSNPDict.get(key).alternate.equals(SNPDict1.LinkedSNPDict.get(key).alternate)) {
							count += 1;
						}
					}	
				}
				double matchPercent = (count/totalCount) * 100; //calculates the percent match.
				
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(file_name));
					bw.write("Percent Match:" + matchPercent + "%" + "\n\n"); //writes in the file_name created in the method which calls this method the percent match and total count of shared SNPs.
					bw.write("Number of matching SNPs:" + count + "\n\n");
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			public static void meand23format(Map<String, String[]> input_dict1, NutraGenie.Linked_SNP_Dict input_dict2) { //converts the VCF formatted gene files into 23 and me formatted files.
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("23andme.txt")); //writes results into a txt file named 23andme.txt
										
					for (String name : input_dict1.keySet()) {   //iterates through each key in the blank 23 and me dictionary created using the method above. 
						
						if (input_dict2.LinkedSNPDict.containsKey(name)) { //if the SNP dictionary of the VCF formatted file contains the same key as the blank dictionary it adds the reference and alternate nucleotides into that location else it adds two dashes, which is what completed 23 and me files have
							bw.write(input_dict1.get(name)[0] + "\t" + input_dict1.get(name)[1] + "\t" +input_dict1.get(name)[2] + "\t" + input_dict2.LinkedSNPDict.get(name).reference + input_dict2.LinkedSNPDict.get(name).alternate + "\n");
						}
						else {
							bw.write(input_dict1.get(name)[0] + "\t" + input_dict1.get(name)[1] + "\t" +input_dict1.get(name)[2] + "\t" + "--\n");
						}
					}
					bw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	public static class Menu { // IN FUTURE ADD OPTION TO COMPARE ENTIRE SNP LIST AND GIVE MATCH PERCENT AND TOTAL SNP MATCH COUNT
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
						NutraGenie.Linked_SNP_Dict SNPDict0 = File_Reader.reader(0, file_name0, range, null);
						NutraGenie.Linked_SNP_Dict SNPDict1 = File_Reader.reader(0, file_name1, range, null);
						
						String file_name = "HLA_match_results" + count + ".txt";  //This creates new file names using a counter for each range in the array created above.
						count += 1;
						
						File_Writer.compare_HLA(SNPDict0, SNPDict1, file_name); 
					}
				}				
				else if (choice == 1) {
					System.out.print("Please provide the file name of the file you would like to convert.\n");
					String file_name = input.nextLine();
					Map<String, String[]> SNP_Dict0 = File_Reader.me_23_blank_dictionary();
					NutraGenie.Linked_SNP_Dict SNPDict1 = File_Reader.reader(1, file_name, null , SNP_Dict0);
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
		
	public static void main(String[] args) {
		Menu.menu();
		}
	}



