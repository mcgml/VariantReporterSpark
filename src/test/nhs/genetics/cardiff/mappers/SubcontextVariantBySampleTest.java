package nhs.genetics.cardiff.mappers;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.gatk.engine.samples.Sample;
import org.broadinstitute.gatk.engine.samples.SampleDB;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

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

        VariantContext s1 = new SubcontextVariantBySample(new Sample("s1", new SampleDB())).call(cohort);

        assertEquals(cohort.getAttribute("AC"), s1.getAttribute("AC"));
        assertEquals(cohort.getGenotype("s1"), s1.getGenotype("s1"));
        assertNotEquals(cohort.getAlleles(), s1.getAlleles());
    }

}