package nhs.genetics.cardiff.framework.clinvar;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 12/07/2017.
 */
public class ClinvarTest {

    private VariantContext knownToClinvarVc = new VariantContextBuilder("test", "1", 957568, 957568, Arrays.asList(Allele.create("A", true), Allele.create("G", false))).make();
    private VariantContext unknownToClinvarVc = new VariantContextBuilder("test", "1", 957600, 957600, Arrays.asList(Allele.create("A", true), Allele.create("G", false))).make();
    private Long[] knownIds = {(long) 263166};
    private Long[] unknownIds = {(long) -1};

    @Test
    public void getIdListFromGRCh37KnownCoordinatesTest() throws Exception {
        assertArrayEquals(knownIds, Clinvar.getIdListFromGRCh37Coordinates(knownToClinvarVc));
    }

    @Test
    public void getIdListFromGRCh37UnknownCoordinatesTest() throws Exception {
        assertArrayEquals(new Long[0], Clinvar.getIdListFromGRCh37Coordinates(unknownToClinvarVc));
    }

    @Test
    public void getClinvarRecordsFromKnownIdListTest() throws Exception {
        assertEquals((long) knownIds[0], Clinvar.getClinvarRecordsFromIdList(knownIds).get(0).getUid());
    }

    @Test
    public void getClinvarRecordsFromUnknownIdListTest() throws Exception {
        assertEquals(new ArrayList<ClinvarRecord>() {{add(null);}}, Clinvar.getClinvarRecordsFromIdList(unknownIds));
    }

}