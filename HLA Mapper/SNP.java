public class SNP { // Object class that contains all four data points 
    public String chromosome = null;
    public String position = null;
    public String reference = null;
    public String alternate = null;
    
    public SNP(String chrom, String pos, String ref, String alt) { //Object which becomes the value in the VCF formatted text files. These values are filled by the information in each line of those files.
        chromosome = chrom;
        position = pos;
        reference = ref;
        alternate = alt;
    }		
}