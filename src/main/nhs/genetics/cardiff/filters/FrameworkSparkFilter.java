package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * functions for assisting variant filtering
 */
public class FrameworkSparkFilter {

    public enum Workflow {
        DE_NOVO,
        UNIPARENTAL_ISODISOMY,
        DOMINANT,
        HOMOZYGOUS,
        COMPOUND_HETEROZYGOUS
    }

    /**
     * @param variantContext
     * @param allele
     * @return 0-based index for info fields from alt allele (AC)
     */
    public static int getAlternativeAlleleNumIndex(VariantContext variantContext, Allele allele){
        return variantContext.getAlleleIndex(allele) - 1;
    }

    /**
     * @param variantContext
     * @param allele
     * @return Used to get the 0-based index for info fields from all allele (AF)
     */
    public static int getAllAlleleNumIndex(VariantContext variantContext, Allele allele){
        return variantContext.getAlleleIndex(allele);
    }

    /**
     * @param variantContext
     * @param allele
     * @return allele num for vep field
     */
    public static int getVepAlleleNumIndex(VariantContext variantContext, Allele allele){
        return variantContext.getAlleleIndex(allele);
    }

    /**
     * @param variantContext
     * @return maximum allele count for all alternative alleles
     */
    public static int getMaxAlleleCount(VariantContext variantContext){
        return variantContext.getAttributeAsIntList("AC", 0).stream().reduce(Integer.MIN_VALUE, (a, b) -> Integer.max(a, b));
    }

    /**
     * checks if any alternative allele(s) are low population frequency
     * @param variantContext
     * @param maxAlleleFrequency
     * @return true = any low, false = all high
     */
    public static boolean areAnyAlternativeAllelesLowFrequency(VariantContext variantContext, double maxAlleleFrequency){
        for (Allele allele : variantContext.getAlternateAlleles()){
            if (getGnomadGenomeAlternativeAlleleFrequency(variantContext, allele) <= maxAlleleFrequency &&
                    getGnomadExomeAlternativeAlleleFrequency(variantContext, allele) <= maxAlleleFrequency){
                return true;
            }
        }
        return false;
    }

    /**
     * @param variantContext
     * @param alternativeAllele
     * @return variant allele frequency for an allele from Gnomad genome project
     */
    public static double getGnomadGenomeAlternativeAlleleFrequency(VariantContext variantContext, Allele alternativeAllele) {

        if (alternativeAllele.isReference()){
            return 0.0;
        }

        try {
            return Double.parseDouble(
                    variantContext
                            .getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + (isContigPrefixedChr(variantContext.getContig()) ? trimLeadingChrPrefix(variantContext.getContig()) : variantContext.getContig())  + ".AF_POPMAX",".")
                            .get(getAlternativeAlleleNumIndex(variantContext, alternativeAllele))
            );
        } catch (NumberFormatException|NullPointerException|IndexOutOfBoundsException e){
            return 0.0;
        }

    }

    /**
     * @param variantContext
     * @param alternativeAllele
     * @return variant allele frequency for an allele from Gnomad exome project
     */
    public static double getGnomadExomeAlternativeAlleleFrequency(VariantContext variantContext, Allele alternativeAllele) {

        if (alternativeAllele.isReference()){
            return 0.0;
        }

        try {
            return Double.parseDouble(
                    variantContext
                            .getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX",".")
                            .get(getAlternativeAlleleNumIndex(variantContext, alternativeAllele))
            );
        } catch (NumberFormatException|NullPointerException|IndexOutOfBoundsException e){
            return 0.0;
        }

    }

    /**
     * Returns allele count for alternative allele in cohort
     * @param variantContext
     * @param alternativeAllele
     * @return count
     */
    public static int getCohortAlternativeAlleleCount(VariantContext variantContext, Allele alternativeAllele) {

        if (alternativeAllele.isReference()){
            return 0;
        }

        try {
            return variantContext.getAttributeAsIntList("AC",0).get(getAlternativeAlleleNumIndex(variantContext, alternativeAllele));
        } catch (NullPointerException|IndexOutOfBoundsException e){
            return 0;
        }

    }

    /**
     * filters map<String, Long> for >1 gene occurrence
     * @param counts
     * @return unique hash of genes
     */
    public static HashSet<String> getVariantsWithMultipleGeneHits(Map<String, Long> counts){
        HashSet<String> hits = new HashSet<>();
        for (Map.Entry<String, Long> hit : counts.entrySet()){
            if (hit.getValue() > 1){
                hits.add(hit.getKey());
            }
        }
        return hits;
    }

    /**
     * checks if allele is spanning deletion
     * @param allele
     * @return bool
     */
    public static boolean isAlleleSpanningDeletion(Allele allele){
        return allele.equals(Allele.SPAN_DEL) || allele.toString().equals("<*:DEL>");
    }

    /**
     * checks if contig has chr prefix
     * @param contig
     * @return bool
     */
    public static boolean isContigPrefixedChr(String contig){
        return contig.startsWith("chr");
    }

    /**
     * trim leading chr prefix
     * @param contig
     * @return contig
     */
    public static String trimLeadingChrPrefix(String contig){
        return contig.replace("chr","");
    }

    /**
     * Functional coding impact
     */
    public static final Set<String> functionalCodingImpact = new HashSet<String>(){{
        //HIGH
        add("splice_donor_variant"); //0001575
        add("splice_acceptor_variant"); //0001574
        add("frameshift_variant"); //0001589
        add("transcript_ablation"); //0001893
        add("initiator_codon_variant"); //0001582
        add("stop_gained"); //0001587
        add("stop_lost"); //0001578

        //MODERATE
        add("transcript_amplification"); //0001889
        add("inframe_insertion"); //0001821 & 0001822
        add("missense_variant"); //0001583 & 0001630
        add("incomplete_terminal_codon_variant"); //0001626

        //LOW
        add("splice_region_variant");
        add("inframe_deletion");
        add("synonymous_variant");
    }};

    /**
     * Autosomal chromosomes
     */
    public static final Set<String> autosomes = new HashSet<String>() {{
        add("1"); add("chr1");
        add("2"); add("chr2");
        add("3"); add("chr3");
        add("4"); add("chr4");
        add("5"); add("chr5");
        add("6"); add("chr6");
        add("7"); add("chr7");
        add("8"); add("chr8");
        add("9"); add("chr9");
        add("10"); add("chr10");
        add("11"); add("chr11");
        add("12"); add("chr12");
        add("13"); add("chr13");
        add("14"); add("chr14");
        add("15"); add("chr15");
        add("16"); add("chr16");
        add("17"); add("chr17");
        add("18"); add("chr18");
        add("19"); add("chr19");
        add("20"); add("chr20");
        add("21"); add("chr21");
        add("22"); add("chr22");
    }};

    /**
     * X chromosome
     */
    public static final Set<String> x = new HashSet<String>() {{
        add("X"); add("chrX");
    }};

    /**
     * Y chromosome
     */
    public static final Set<String> y = new HashSet<String>() {{
        add("Y"); add("chrY");
    }};

}
