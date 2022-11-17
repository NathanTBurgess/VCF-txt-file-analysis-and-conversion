import java.util.*;
import java.io.*;

public class File_Writer {//Compare the blank dictionary to new_dict and create a text file in 23 and me format that is populate with all present SNPS, Compare two different new_dicts and output a percentage of similarity.
    public static void compare_HLA(Linked_SNP_Dict SNPDict0, Linked_SNP_Dict SNPDict1, String file_name) {
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
    
    public static void meand23format(Map<String, String[]> input_dict1, Linked_SNP_Dict input_dict2) { //converts the VCF formatted gene files into 23 and me formatted files.
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