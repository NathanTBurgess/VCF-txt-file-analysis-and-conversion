import java.util.*;

public class SNP_Dict { //object class that creates a dictionary you can populate with SNPS
		
    public Map<String, SNP> SNPDict = new HashMap<String, SNP>(); //initiates a hashmap which is filled with SNPS
   
    public void populate(String edit, SNP input) { //populate dictionary with SNPS
           SNPDict.put(edit + String.valueOf(input.position), input);	//You will see these if statements below as well, I am creating unique keys for each position on the genome because the VCF files don't use rsid so I am just making a universal key, examples: chr013425134 or chr1351848318
}
}