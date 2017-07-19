package nhs.genetics.cardiff.framework;

import com.google.common.base.Objects;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Created by ml on 19/07/2017.
 */
public class VariantContextWrapper extends VariantContext {
    public VariantContextWrapper(VariantContext variantContext){
        super(variantContext);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(contig, start, stop, alleles);
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof VariantContextWrapper){
            final VariantContextWrapper other = (VariantContextWrapper) obj;
            return Objects.equal(contig, other.contig)
                    && Objects.equal(alleles, other.alleles)
                    && start == other.start
                    && stop == other.stop;
        } else {
            return false;
        }
    }
}
