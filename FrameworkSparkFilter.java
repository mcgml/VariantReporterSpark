package nhs.genetics.cardiff.framework.spark;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ml on 12/06/2017.
 */
public class FrameworkSparkFilter {

    public static final Set<String> autosomes = new HashSet<String>() {{
        add("1");add("2");add("3");add("4");add("5");add("6");add("7");add("8");add("9");add("10");
        add("11");add("12");add("13");add("14");add("15");add("16");add("17");add("18");add("19");
        add("20");add("21");add("22");
    }};
    public static final Set<String> x = new HashSet<String>() {{
        add("X");
    }};

    public static boolean areAnyAlternativeAlleleCountsLow(VariantContext variantContext, String sample, int maxAlleleCount){
        List<Integer> alleleCounts = variantContext.getAttributeAsIntList("AC",0);

        for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
            if (allele.isNonReference()){
                if (alleleCounts.get(variantContext.getAlleleIndex(allele) - 1) <= maxAlleleCount){
                    return true;
                }
            }
        }

        return false;
    }
    public static boolean areAnyAlternativeAlleleFrequencyLow(VariantContext variantContext, String sample, double maxAlleleFrequency){
        List<Double> alleleFrequency = variantContext.getAttributeAsDoubleList("AF",0);

        for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
            if (allele.isNonReference()){
                if (alleleFrequency.get(variantContext.getAlleleIndex(allele) - 1) <= maxAlleleFrequency){
                    return true;
                }
            }
        }

        return false;
    }
    public static boolean areAnyAlternativeAllelesHighGnomadExomeFrequency(VariantContext variantContext, String sample, double maxAlleleFrequency){
        List<Double> alleleFrequencies = stringListToDoubleList(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Exome.AF_POPMAX","."));

        for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
            if (allele.isNonReference()){
                try {
                    if (alleleFrequencies.get(variantContext.getAlleleIndex(allele) - 1) > maxAlleleFrequency){
                        return true;
                    }
                } catch (IndexOutOfBoundsException |NumberFormatException e){

                }
            }
        }
        return false;
    }
    public static boolean areAnyAlternativeAllelesHighGnomadGenomeFrequency(VariantContext variantContext, String sample, double maxAlleleFrequency){
        List<Double> alleleFrequencies = stringListToDoubleList(variantContext.getAttributeAsStringList("GNOMAD_2.0.1_Genome_chr" + variantContext.getContig() + ".AF_POPMAX","."));

        for (Allele allele : variantContext.getGenotype(sample).getAlleles()){
            if (allele.isNonReference()){
                try {
                    if (alleleFrequencies.get(variantContext.getAlleleIndex(allele) - 1) > maxAlleleFrequency){
                        return true;
                    }
                } catch (IndexOutOfBoundsException | NumberFormatException e){

                }
            }
        }
        return false;
    }
    private static List<Double> stringListToDoubleList(List<String> strings){
        ArrayList<Double> doubles = new ArrayList<>();
        for (String string : strings){
            try {
                doubles.add(Double.parseDouble(string));
            } catch (NumberFormatException e){
                doubles.add(0.00);
            }
        }
        return doubles;
    }
}
