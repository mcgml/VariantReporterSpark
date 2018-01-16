package nhs.genetics.cardiff.framework;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ml on 16/01/2018.
 */
public class GenomeVariantTest {
    @Test
    public void convertToMinimalRepresentationTestShouldPass() throws Exception {
        GenomeVariant genomeVariant = new GenomeVariant("chr1", 100, "ACCCC", "ACCCG");
        genomeVariant.convertToMinimalRepresentation();

        assertTrue(genomeVariant.toString().equals("chr1:104C>G"));
    }
    @Test
    public void convertToMinimalRepresentationTestShouldFail() throws Exception {
        GenomeVariant genomeVariant = new GenomeVariant("chr1", 100, "ACCCC", "ACCCG");
        genomeVariant.convertToMinimalRepresentation();

        assertFalse(genomeVariant.toString().equals("chr1:100C>G"));
    }
    @Test(expected = RuntimeException.class)
    public void convertToMinimalRepresentationTestShouldRaiseException() throws Exception {
        GenomeVariant genomeVariant = new GenomeVariant("chr1", 100, "ACCCC", "*");
        genomeVariant.convertToMinimalRepresentation();
    }
}