package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.GenomeVariant;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WriteVariants {

    public static void toTextFile(List<VariantContext> variants, String sample, VCFHeaders vcfHeaders){

        //print headers
        //TODO

        //loop over writable variants alleles for this patient
        for (VariantContext variantContext : variants){

            //split site level annotations and pair with headers
            HashSet<VepAnnotationObject> annotations = new HashSet<>();
            if (variantContext.hasAttribute("CSQ")) {

                try {
                    annotations.add(VCFReaderSpark.deserialiseVepAnnotation(vcfHeaders.getVepHeaders(), (String) variantContext.getAttribute("CSQ")));
                } catch (ClassCastException e) {
                    for (String field : (ArrayList<String>) variantContext.getAttribute("CSQ")) {
                        annotations.add(VCFReaderSpark.deserialiseVepAnnotation(vcfHeaders.getVepHeaders(), field));
                    }
                }

            }

            //get Genotype for patient
            Genotype genotype = variantContext.getGenotype(sample);

            //loop over alternative alleles
            for (Allele allele : genotype.getAlleles()){
                if (allele.isNonReference()){
                    int alleleNum = variantContext.getAlleleIndex(allele) - 1;

                    GenomeVariant genomeVariant = new GenomeVariant(variantContext.getContig(), variantContext.getStart(), variantContext.getReference().getBaseString(), allele.getBaseString());
                    genomeVariant.convertToMinimalRepresentation();

                    for (VepAnnotationObject vepAnnotationObject : annotations){
                        if (vepAnnotationObject.getAlleleNum() == alleleNum){

                        }
                    }

                }
            }

        }
    }
}
