package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.GenomeVariant;
import nhs.genetics.cardiff.framework.spark.filter.FrameworkSparkFilter;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class WriteVariants {

    public static void toTextFile(List<VariantContext> variants, String sample, String[] vepHeaders, FrameworkSparkFilter.Workflow workflow) throws IOException {

        try (PrintWriter printWriter = new PrintWriter(sample + "_" + workflow.toString() + "_VariantReport.txt")){

            //print headers
            printWriter.println("VariantId\tdbSNP\tCosmic\tHGMD\tGnomadExomePopMax\tGnomadGenomePopMax\tGene\tTranscript\tHGVSc\tHGVSp\tConsequences\tIntron\tExon\tSIFT\tPolyPhen");

            //loop over writable variants alleles for this patient
            for (VariantContext variantContext : variants){

                //get Genotype for patient
                Genotype genotype = variantContext.getGenotype(sample);

                //split site level annotations and pair with headers
                HashSet<VepAnnotationObject> annotations = new HashSet<>();
                if (variantContext.hasAttribute("CSQ")) {

                    try {
                        annotations.add(VCFReaderSpark.deserialiseVepAnnotation(vepHeaders, (String) variantContext.getAttribute("CSQ")));
                    } catch (ClassCastException e) {
                        for (String field : (ArrayList<String>) variantContext.getAttribute("CSQ")) {
                            annotations.add(VCFReaderSpark.deserialiseVepAnnotation(vepHeaders, field));
                        }
                    }

                }

                //loop over alternative alleles
                for (Allele allele : genotype.getAlleles()){
                    if (allele.isNonReference() && !allele.getBaseString().equals("*")){

                        int alleleNum = variantContext.getAlleleIndex(allele) - 1;

                        GenomeVariant genomeVariant = new GenomeVariant(variantContext.getContig(), variantContext.getStart(), variantContext.getReference().getBaseString(), allele.getBaseString());
                        genomeVariant.convertToMinimalRepresentation();

                        for (VepAnnotationObject vepAnnotationObject : annotations){
                            if (vepAnnotationObject.getAlleleNum() == alleleNum && vepAnnotationObject.getFeature().startsWith("NM")){

                                //print variant annotations
                                printWriter.print(genomeVariant);printWriter.print("\t");

                                //dbSNP, cosmic etc
                                printWriter.print(vepAnnotationObject.getDbSnpIds()); printWriter.print("\t");
                                printWriter.print(vepAnnotationObject.getCosmicIds()); printWriter.print("\t");
                                printWriter.print(vepAnnotationObject.getHGMDIds()); printWriter.print("\t");

                                //exome
                                printWriter.print(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX",".").get(variantContext.getAlleleIndex(allele) - 1)); printWriter.print("\t");

                                //genome
                                printWriter.print(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + variantContext.getContig() + ".AF_POPMAX",".").get(variantContext.getAlleleIndex(allele) - 1)); printWriter.print("\t");

                                //transcript level annotations
                                if (vepAnnotationObject.getSymbol() != null) printWriter.print(vepAnnotationObject.getSymbol()); printWriter.print("\t");
                                if (vepAnnotationObject.getFeature() != null) printWriter.print(vepAnnotationObject.getFeature()); printWriter.print("\t");
                                if (vepAnnotationObject.getHgvsc() != null) printWriter.print(vepAnnotationObject.getHgvsc()); printWriter.print("\t");
                                if (vepAnnotationObject.getHgvsc() != null) printWriter.print(vepAnnotationObject.getHgvsc()); printWriter.print("\t");
                                if (vepAnnotationObject.getConsequence() != null) printWriter.print(Arrays.stream(vepAnnotationObject.getConsequence()).collect(Collectors.joining(","))); printWriter.print("\t");
                                if (vepAnnotationObject.getIntron() != null) printWriter.print(vepAnnotationObject.getIntron()); printWriter.print("\t");
                                if (vepAnnotationObject.getExon() != null) printWriter.print(vepAnnotationObject.getExon()); printWriter.print("\t");
                                if (vepAnnotationObject.getSift() != null) printWriter.print(vepAnnotationObject.getSift()); printWriter.print("\t");
                                if (vepAnnotationObject.getPolyphen() != null) printWriter.print(vepAnnotationObject.getPolyphen());

                                printWriter.println();

                            }
                        }

                    }
                }

            }
        }

    }
}
