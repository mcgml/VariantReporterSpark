package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import nhs.genetics.cardiff.framework.GenomeVariant;
import nhs.genetics.cardiff.framework.panelapp.ModeOfInheritance;
import nhs.genetics.cardiff.framework.panelapp.PanelAppRestClient;
import nhs.genetics.cardiff.framework.panelapp.Result;
import nhs.genetics.cardiff.framework.spark.filter.gel.GelFilterFramework;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class for writing List<VariantContext> to text file
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class WriteVariants {

    private static final Logger LOGGER = Logger.getLogger(WriteVariants.class.getName());
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyHH:mm:ss");

    public static void toTextFile(List<VariantContext> variants, String sample, String[] vepHeaders, GelFilterFramework.Workflow workflow, HashSet<String> preferredTranscripts, boolean onlyPrintKnownRefSeq) throws IOException {
        LOGGER.log(Level.INFO, "Writing " + sample + " from workflow " + workflow.toString() + " with " + variants.size() + " variants");

        //store panelapp results
        HashMap<String, Result[]> panelAppResults = new HashMap<>();

        try (PrintWriter printWriter = new PrintWriter(sample + "_" + workflow.toString() + "_VariantReport.txt")){

            //print headers
            printWriter.println("#" + Main.PROGRAM + " v" + Main.VERSION + " (" + dateFormat.format(new Date()) + ")");
            printWriter.println("#VariantId\tGenotype\tdbSNP\tCosmic\tHGMD\tGnomadExomePopMax\tGnomadGenomePopMax\tGene\tModeOfInheritance\tDiseaseGroup\tDiseaseSubGroup\tDiseaseName\tTranscript\tPreferredTranscript\tHGVSc\tHGVSp\tConsequences\tIntron\tExon\tSIFT\tPolyPhen");

            //loop over writable variants alleles for this patient
            for (VariantContext variantContext : variants){

                //get Genotype for patient
                Genotype genotype = variantContext.getGenotype(sample);

                //split site level annotations and pair with headers
                HashSet<VepAnnotationObject> annotations = VepAnnotationObject.getVepAnnotationObjects(vepHeaders, variantContext.getAttribute("CSQ"));

                //loop over alternative alleles
                for (Allele allele : genotype.getAlleles()){
                    if (allele.isNonReference() && !allele.getBaseString().equals("*")){

                        boolean printed = false;
                        int alleleNum = GelFilterFramework.getVepAlleleNumIndex(variantContext, allele);

                        GenomeVariant genomeVariant = new GenomeVariant(variantContext.getContig(), variantContext.getStart(), variantContext.getReference().getBaseString(), allele.getBaseString());
                        genomeVariant.convertToMinimalRepresentation();

                        for (VepAnnotationObject vepAnnotationObject : annotations){
                            if (vepAnnotationObject.getAlleleNum() == alleleNum){
                                if (!vepAnnotationObject.getFeature().startsWith("NM") && onlyPrintKnownRefSeq) continue;

                                //contact panelApp for annotations
                                if (!panelAppResults.containsKey(vepAnnotationObject.getSymbol())){
                                    try {
                                        LOGGER.info("Connecting to panelApp: " + vepAnnotationObject.getSymbol());
                                        panelAppResults.put(vepAnnotationObject.getSymbol(), PanelAppRestClient.searchByGene(vepAnnotationObject.getSymbol()).getResults());
                                    } catch (IOException e){
                                        LOGGER.warning("Could not connect to PanelApp: " + e.getMessage() + " continuing without annotations.");
                                    }
                                }

                                //print variant annotations
                                printWriter.print(genomeVariant);printWriter.print("\t");
                                printWriter.print(genotype.getType()); printWriter.print("\t");

                                //dbSNP, cosmic etc
                                printWriter.print(vepAnnotationObject.getDbSnpIds()); printWriter.print("\t");
                                printWriter.print(vepAnnotationObject.getCosmicIds()); printWriter.print("\t");
                                printWriter.print(vepAnnotationObject.getHGMDIds()); printWriter.print("\t");

                                //exome
                                if (variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX",".").size() > 0) printWriter.print(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX",".").get(variantContext.getAlleleIndex(allele) - 1));
                                printWriter.print("\t");

                                //genome
                                if (variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + variantContext.getContig() + ".AF_POPMAX",".").size() > 0) printWriter.print(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + variantContext.getContig() + ".AF_POPMAX",".").get(variantContext.getAlleleIndex(allele) - 1));
                                printWriter.print("\t");

                                //transcript level annotations
                                if (vepAnnotationObject.getSymbol() != null) printWriter.print(vepAnnotationObject.getSymbol()); printWriter.print("\t");

                                //print panelApp annotations
                                if (panelAppResults.containsKey(vepAnnotationObject.getSymbol())) {

                                    //print mode of inheritance for this gene
                                    printWriter.print(
                                            Arrays.stream(panelAppResults.get(vepAnnotationObject.getSymbol()))
                                                    .map(Result::getModeOfInheritance)
                                                    .filter(modeOfInheritance -> modeOfInheritance != ModeOfInheritance.UNKNOWN)
                                                    .filter(p -> p != null)
                                                    .map(ModeOfInheritance::toString)
                                                    .distinct()
                                                    .collect(Collectors.joining("|"))
                                    );
                                    printWriter.print("\t");

                                    //print disease group
                                    printWriter.print(
                                            Arrays.stream(panelAppResults.get(vepAnnotationObject.getSymbol()))
                                                    .map(Result::getSpecificDiseaseName)
                                                    .filter(p -> p != null)
                                                    .distinct()
                                                    .collect(Collectors.joining("|"))
                                    );
                                    printWriter.print("\t");

                                    //print disease subgroup
                                    printWriter.print(
                                            Arrays.stream(panelAppResults.get(vepAnnotationObject.getSymbol()))
                                                    .map(Result::getSpecificDiseaseName)
                                                    .filter(p -> p != null)
                                                    .distinct()
                                                    .collect(Collectors.joining("|"))
                                    );
                                    printWriter.print("\t");

                                    //print disease name
                                    printWriter.print(
                                            Arrays.stream(panelAppResults.get(vepAnnotationObject.getSymbol()))
                                                    .map(Result::getSpecificDiseaseName)
                                                    .filter(p -> p != null)
                                                    .distinct()
                                                    .collect(Collectors.joining("|"))
                                    );
                                    printWriter.print("\t");


                                } else {
                                    printWriter.print("\t");
                                    printWriter.print("\t");
                                    printWriter.print("\t");
                                    printWriter.print("\t");
                                }

                                if (vepAnnotationObject.getFeature() != null) printWriter.print(vepAnnotationObject.getFeature()); printWriter.print("\t");
                                if (preferredTranscripts != null && preferredTranscripts.contains(vepAnnotationObject.getFeature())) printWriter.print(true); else printWriter.print(false); printWriter.print("\t");
                                if (vepAnnotationObject.getHgvsc() != null) printWriter.print(vepAnnotationObject.getHgvsc()); printWriter.print("\t");
                                if (vepAnnotationObject.getHgvsp() != null) printWriter.print(vepAnnotationObject.getHgvsp()); printWriter.print("\t");
                                if (vepAnnotationObject.getConsequence() != null) printWriter.print(Arrays.stream(vepAnnotationObject.getConsequence()).collect(Collectors.joining(","))); printWriter.print("\t");
                                if (vepAnnotationObject.getIntron() != null) printWriter.print(vepAnnotationObject.getIntron()); printWriter.print("\t");
                                if (vepAnnotationObject.getExon() != null) printWriter.print(vepAnnotationObject.getExon()); printWriter.print("\t");
                                if (vepAnnotationObject.getSift() != null) printWriter.print(vepAnnotationObject.getSift()); printWriter.print("\t");
                                if (vepAnnotationObject.getPolyphen() != null) printWriter.print(vepAnnotationObject.getPolyphen());

                                printWriter.println();
                                printed = true;
                            }
                        }

                        if (!printed){
                            //print variant annotations
                            printWriter.print(genomeVariant);printWriter.print("\t");
                            printWriter.print(genotype.getType()); printWriter.print("\t");

                            //dbSNP, cosmic etc
                            printWriter.print("\t");
                            printWriter.print("\t");
                            printWriter.print("\t");

                            //exome
                            if (variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX",".").size() > 0) printWriter.print(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX",".").get(variantContext.getAlleleIndex(allele) - 1));
                            printWriter.print("\t");

                            //genome
                            if (variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + variantContext.getContig() + ".AF_POPMAX",".").size() > 0) printWriter.print(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + variantContext.getContig() + ".AF_POPMAX",".").get(variantContext.getAlleleIndex(allele) - 1));

                            printWriter.println();
                        }

                    }
                }

            }
        }

    }
}
