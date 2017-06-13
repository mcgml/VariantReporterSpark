package nhs.genetics.cardiff;

import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderVersion;
import nhs.genetics.cardiff.framework.vep.MissingVEPHeaderException;

import java.io.*;

/**
 * Class for extracting VCF headers
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class VCFHeaders implements Serializable {

    private File file;
    private VCFHeader vcfHeader;
    private Integer vepVersion;
    private String[] vepHeaders;
    private VCFHeaderVersion vcfHeaderVersion;

    public VCFHeaders(File file){
        this.file = file;
    }

    public void populateVCFHeaders(){
        try (VCFFileReader vcfFileReader = new VCFFileReader(file)){
            this.vcfHeader = vcfFileReader.getFileHeader();
        }
    }

    public void populateVEPHeaders() throws MissingVEPHeaderException {
        try {
            this.vepHeaders = vcfHeader.getInfoHeaderLine("CSQ").getDescription().split("Format:")[1].trim().split("\\|");
            this.vepVersion = Integer.parseInt(vcfHeader.getOtherHeaderLine("VEP").getValue().split(" ")[0].split("v")[1]);
        } catch (NullPointerException e){
            throw new MissingVEPHeaderException("Could not parse VEP headers: " + e.getMessage());
        }
    }

    public void populateVCFVersion() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
            String version = bufferedReader.readLine().split("=")[1];
            switch(version){
                case "VCFv4.0": this.vcfHeaderVersion = VCFHeaderVersion.VCF4_0; break;
                case "VCFv4.1": this.vcfHeaderVersion = VCFHeaderVersion.VCF4_1; break;
                case "VCFv4.2": this.vcfHeaderVersion = VCFHeaderVersion.VCF4_2; break;
                default: throw new IllegalArgumentException("Could not determine VCF version or not supported.");
            }
        }
    }

    public VCFHeader getVcfHeader() {
        return vcfHeader;
    }
    public Integer getVepVersion() {
        return vepVersion;
    }
    public VCFHeaderVersion getVcfHeaderVersion() {
        return vcfHeaderVersion;
    }
    public String[] getVepHeaders() {
        return vepHeaders;
    }
    public boolean hasVepAnnotations(){
        return  vepHeaders != null && vepVersion != null;
    }
}
