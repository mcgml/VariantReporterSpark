package nhs.genetics.cardiff.framework.hgmd;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enum for HGMD variant class
 */

public enum HGMDVariantClass {
    DISEASE_CAUSING_MUTATION,
    DISEASE_CAUSING_MUTATION_QUERY,
    DISEASE_ASSOCIATED_POLYMORPHISM;
}
