package nhs.genetics.cardiff.mappers;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.hellbender.utils.samples.PedigreeValidationType;
import org.broadinstitute.hellbender.utils.samples.SampleDBBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by ml on 20/07/2017.
 */
public class SubcontextVariantBySampleTest {

    private GenotypeBuilder genotypeBuilder = new GenotypeBuilder();
    private VariantContextBuilder variantContextBuilder = new VariantContextBuilder(
            "test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false), Allele.create("G", false))
    );

    @Test
    public void call() throws Exception {

        variantContextBuilder.genotypes(
                genotypeBuilder.name("s1")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("s2")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("A", true)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("s3")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("G", false)))
                        .unfiltered()
                        .make(),
                genotypeBuilder.name("s4")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("G", false)))
                        .unfiltered()
                        .make()
        );
        variantContextBuilder.attribute("AC",Arrays.asList(1,2));

        VariantContext cohort = variantContextBuilder.make();
        cohort.validateAlternateAlleles();
        cohort.validateChromosomeCounts();

        SampleDBBuilder sampleDBBuilder = new SampleDBBuilder(PedigreeValidationType.STRICT);
        sampleDBBuilder.addSamplesFromPedigreeStrings(Arrays.asList("f1\ts1\t0\t0\t1\t1"));
        sampleDBBuilder.getFinalSampleDB();

        VariantContext s1 = new SubcontextVariantBySample(sampleDBBuilder.getFinalSampleDB().getSample("s1")).call(cohort);

        assertEquals(cohort.getAttribute("AC"), s1.getAttribute("AC"));
        assertEquals(cohort.getGenotype("s1"), s1.getGenotype("s1"));
        assertNotEquals(cohort.getAlleles(), s1.getAlleles());
    }

}