package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ml on 20/06/2017.
 */
public enum ModeOfInheritance {
    @JsonProperty("monoallelic_not_imprinted")
    MONOALLELIC_NOT_IMPRINTED,
    @JsonProperty("monoallelic_maternally_imprinted")
    MONOALLELIC_MATERNALLY_IMPRINTED,
    @JsonProperty("monoallelic_paternally_imprinted")
    MONOALLELIC_PATERNALLY_IMPRINTED,
    @JsonProperty("monoallelic")
    MONOALLELIC,
    @JsonProperty("biallelic")
    BIALLELIC,
    @JsonProperty("monoallelic_and_biallelic")
    MONOALLELIC_AND_BIALLELIC,
    @JsonProperty("monoallelic_and_more_severe_biallelic")
    MONOALLELIC_AND_MORE_SEVERE_BIALLELIC,
    @JsonProperty("xlinked_biallelic")
    XLINKED_BIALLELIC,
    @JsonProperty("xlinked_monoallelic")
    XLINKED_MONOALLELIC,
    @JsonProperty("mitochondrial")
    MITOCHONDRIAL,
    @JsonProperty("unknown")
    UNKNOWN
}
