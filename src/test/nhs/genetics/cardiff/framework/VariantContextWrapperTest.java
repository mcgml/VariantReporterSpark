package nhs.genetics.cardiff.framework;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 19/07/2017.
 */
public class VariantContextWrapperTest {

    private final static VariantContextBuilder variantContextBuilder1 = new VariantContextBuilder(
            "test", "1", 10, 10, Arrays.asList(Allele.create("A", true), Allele.create("T", false))
    );

    private final static VariantContextBuilder variantContextBuilder2 = new VariantContextBuilder(
            "test", "1", 11, 11, Arrays.asList(Allele.create("A", true), Allele.create("G", false))
    );

    @Test
    public void hashCodeTest() throws Exception {
        assertEquals(new VariantContextWrapper(variantContextBuilder1.make()).hashCode(), new VariantContextWrapper(variantContextBuilder1.make()).hashCode());
        assertNotEquals(new VariantContextWrapper(variantContextBuilder1.make()).hashCode(), new VariantContextWrapper(variantContextBuilder2.make()).hashCode());
    }

    @Test
    public void equalsTest() throws Exception {
        assertTrue(new VariantContextWrapper(variantContextBuilder1.make()).equals(new VariantContextWrapper(variantContextBuilder1.make())));
        assertFalse(new VariantContextWrapper(variantContextBuilder1.make()).equals(new VariantContextWrapper(variantContextBuilder2.make())));
    }

}