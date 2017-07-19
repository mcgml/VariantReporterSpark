package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import nhs.genetics.cardiff.filters.FrameworkSparkFilter;
import nhs.genetics.cardiff.framework.GenomeVariant;
import nhs.genetics.cardiff.framework.VariantContextWrapper;
import nhs.genetics.cardiff.framework.panelapp.ModeOfInheritance;
import nhs.genetics.cardiff.framework.panelapp.PanelAppRestClient;
import nhs.genetics.cardiff.framework.panelapp.Result;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.broadinstitute.gatk.engine.samples.Sample;

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
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    public static void toTextFile(HashMap<VariantContextWrapper, ArrayList<FrameworkSparkFilter.Workflow>> variants, Sample sample, String[] vepHeaders, HashSet<String> preferredTranscripts, boolean onlyPrintKnownRefSeq) throws IOException {
        LOGGER.log(Level.INFO, "Writing " + sample.getID() + " with " + variants.size() + " variants");

        //store panelapp results
        HashMap<String, Result[]> panelAppResults = new HashMap<>();

        try (PrintWriter printWriter = new PrintWriter(sample.getFamilyID() + "_" + sample.getID() + "_VariantReport.txt")){

            //print headers
            printWriter.println("#" + Main.PROGRAM + " v" + Main.VERSION + " (" + dateFormat.format(new Date()) + ")");
            printWriter.println("#SampleId\tWorkflow\tVariantId\tGenotype\tProband\tFather\tMother\tdbSNP\tCosmic\tHGMD\tGnomadExomePopMax\tGnomadGenomePopMax\tGene\tModeOfInheritance\tDiseaseName\tTranscript\tPreferredTranscript\tHGVSc\tHGVSp\tConsequences\tIntron\tExon\tSIFT\tPolyPhen");

            //loop over writable variants alleles for this patient
            for (Map.Entry<VariantContextWrapper, ArrayList<FrameworkSparkFilter.Workflow>> iter : variants.entrySet()){
                VariantContextWrapper variantContext = iter.getKey();

                //get Genotype for patient
                Genotype genotype = variantContext.getGenotype(sample.getID());

                //split site level annotations and pair with headers
                HashSet<VepAnnotationObject> annotations = VepAnnotationObject.getVepAnnotationObjects(vepHeaders, variantContext.getAttribute("CSQ"));

                //loop over alternative alleles
                for (Allele allele : genotype.getAlleles()){
                    if (allele.isNonReference() && !allele.getBaseString().equals("*")){

                        int vepIndex = FrameworkSparkFilter.getVepAlleleNumIndex(variantContext, allele);

                        GenomeVariant genomeVariant = new GenomeVariant(variantContext.getContig(), variantContext.getStart(), variantContext.getReference().getBaseString(), allele.getBaseString());
                        genomeVariant.convertToMinimalRepresentation();

                        for (VepAnnotationObject vepAnnotationObject : annotations){
                            if (vepAnnotationObject.getAlleleNum() == vepIndex){
                                if (!vepAnnotationObject.getFeature().startsWith("NM") && onlyPrintKnownRefSeq) continue;

                                //contact panelApp for annotations
                                if (!panelAppResults.containsKey(vepAnnotationObject.getSymbol())){
                                    try {
                                        LOGGER.info("Connecting to panelApp: " + vepAnnotationObject.getSymbol());
                                        panelAppResults.put(vepAnnotationObject.getSymbol(), PanelAppRestClient.searchByGene(vepAnnotationObject.getSymbol()).getResults());
                                    } catch (IOException e){
                                        LOGGER.severe("Could not connect to PanelApp: " + e.getMessage());
                                        throw e;
                                    }
                                }

                                //print variant annotations
                                printWriter.print(sample.getID());printWriter.print("\t");
                                printWriter.print(iter.getValue().stream().map(Enum::name).collect(Collectors.joining(",")));printWriter.print("\t");
                                printWriter.print(genomeVariant);printWriter.print("\t");
                                printWriter.print(genotype.getType()); printWriter.print("\t");

                                //proband genotype
                                printWriter.print(genotype.getGenotypeString()); printWriter.print("\t");

                                //paternal genotype
                                printWriter.print(sample.getFather() != null ? variantContext.getGenotype(sample.getFather().getID()).getGenotypeString() : null); printWriter.print("\t");

                                //maternal genotype
                                printWriter.print(sample.getMother() != null ? variantContext.getGenotype(sample.getMother().getID()).getGenotypeString() : null); printWriter.print("\t");

                                //dbSNP, cosmic etc
                                printWriter.print(vepAnnotationObject.getDbSnpIds()); printWriter.print("\t");
                                printWriter.print(vepAnnotationObject.getCosmicIds()); printWriter.print("\t");
                                printWriter.print(vepAnnotationObject.getHGMDIds()); printWriter.print("\t");

                                //exome
                                printWriter.print(FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContext, allele));
                                printWriter.print("\t");

                                //genome
                                printWriter.print(FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele));
                                printWriter.print("\t");

                                //transcript level annotations
                                if (vepAnnotationObject.getSymbol() != null) printWriter.print(vepAnnotationObject.getSymbol()); printWriter.print("\t");

                                //print mode of inheritance for this gene
                                if (panelAppResults.containsKey(vepAnnotationObject.getSymbol())) {
                                    printWriter.print(
                                            Arrays.stream(panelAppResults.get(vepAnnotationObject.getSymbol()))
                                                    .map(Result::getModeOfInheritance)
                                                    .filter(modeOfInheritance -> modeOfInheritance != ModeOfInheritance.UNKNOWN)
                                                    .filter(p -> p != null)
                                                    .map(ModeOfInheritance::toString)
                                                    .distinct()
                                                    .collect(Collectors.joining("|"))
                                    );
                                }
                                printWriter.print("\t");

                                //print disease  name
                                if (panelAppResults.containsKey(vepAnnotationObject.getSymbol())) {
                                    printWriter.print(
                                            Arrays.stream(panelAppResults.get(vepAnnotationObject.getSymbol()))
                                                    .map(Result::getSpecificDiseaseName)
                                                    .filter(p -> p != null)
                                                    .distinct()
                                                    .collect(Collectors.joining("|"))
                                    );
                                }
                                printWriter.print("\t");

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
                            }
                        }

                    }
                }

            }
        }

    }
}
