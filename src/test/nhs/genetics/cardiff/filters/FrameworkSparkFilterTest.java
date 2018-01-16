package nhs.genetics.cardiff.filters;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ml on 16/06/2017.
 */
public class FrameworkSparkFilterTest {

    private final static String[] headers = "Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|ALLELE_NUM|DISTANCE|STRAND|FLAGS|VARIANT_CLASS|SYMBOL_SOURCE|HGNC_ID|CANONICAL|TSL|APPRIS|CCDS|ENSP|SWISSPROT|TREMBL|UNIPARC|REFSEQ_MATCH|GENE_PHENO|SIFT|PolyPhen|DOMAINS|HGVS_OFFSET|GMAF|AFR_MAF|AMR_MAF|EAS_MAF|EUR_MAF|SAS_MAF|AA_MAF|EA_MAF|ExAC_MAF|ExAC_Adj_MAF|ExAC_AFR_MAF|ExAC_AMR_MAF|ExAC_EAS_MAF|ExAC_FIN_MAF|ExAC_NFE_MAF|ExAC_OTH_MAF|ExAC_SAS_MAF|CLIN_SIG|SOMATIC|PHENO|PUBMED|MOTIF_NAME|MOTIF_POS|HIGH_INF_POS|MOTIF_SCORE_CHANGE".trim().split("\\|");
    private final static String keep = "-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204184.1|protein_coding||3/12|NM_001204184.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191113.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204184.1|protein_coding||3/12|NM_001204184.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191113.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204184.1|protein_coding||3/12|NM_001204184.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191113.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204184.1|protein_coding||3/12|NM_001204184.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191113.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204185.1|protein_coding||3/12|NM_001204185.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191114.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204185.1|protein_coding||3/12|NM_001204185.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191114.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204185.1|protein_coding||3/12|NM_001204185.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191114.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204185.1|protein_coding||3/12|NM_001204185.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191114.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204186.1|protein_coding||3/10|NM_001204186.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191115.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204186.1|protein_coding||3/10|NM_001204186.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191115.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204186.1|protein_coding||3/10|NM_001204186.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191115.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204186.1|protein_coding||3/10|NM_001204186.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191115.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204187.1|protein_coding||3/11|NM_001204187.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191116.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204187.1|protein_coding||3/11|NM_001204187.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191116.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204187.1|protein_coding||3/11|NM_001204187.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191116.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204187.1|protein_coding||3/11|NM_001204187.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191116.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204188.1|protein_coding||3/11|NM_001204188.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191117.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204188.1|protein_coding||3/11|NM_001204188.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191117.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204188.1|protein_coding||3/11|NM_001204188.1:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||NP_001191117.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_001204188.1|protein_coding||3/11|NM_001204188.1:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||NP_001191117.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_005427.3|protein_coding||3/13|NM_005427.3:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||YES||||NP_005418.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_005427.3|protein_coding||3/13|NM_005427.3:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||YES||||NP_005418.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_005427.3|protein_coding||3/13|NM_005427.3:c.186+7delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||YES||||NP_005418.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|splice_region_variant&intron_variant|LOW|TP73|7161|Transcript|NM_005427.3|protein_coding||3/13|NM_005427.3:c.186+7dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||YES||||NP_005418.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|intron_variant|MODIFIER|TP73|7161|Transcript|XM_005244779.1|protein_coding||2/12|XM_005244779.1:c.180+13delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||XP_005244836.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|intron_variant|MODIFIER|TP73|7161|Transcript|XM_005244779.1|protein_coding||2/12|XM_005244779.1:c.180+13dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||XP_005244836.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,-|intron_variant|MODIFIER|TP73|7161|Transcript|XM_005244779.1|protein_coding||2/12|XM_005244779.1:c.180+13delG|||||||rs57492244&TMP_ESP_1_3599751_3599751|1||1||sequence_alteration|||||||XP_005244836.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||,GG|intron_variant|MODIFIER|TP73|7161|Transcript|XM_005244779.1|protein_coding||2/12|XM_005244779.1:c.180+13dupG|||||||rs57492244&TMP_ESP_1_3599751_3599751|2||1||sequence_alteration|||||||XP_005244836.1||||||||||-:0.1677||||||||-:0.100&GG:6.838e-04&-:0.100&GG:6.838e-04|-:0.1007&GG:0.0006714&-:0.1007&GG:0.0006714|-:0.3014&GG:9.684e-05&-:0.3014&GG:9.684e-05|-:0.08756&GG:0.0005197&-:0.08756&GG:0.0005197|-:0.1539&GG:0&-:0.1539&GG:0|-:0.1176&GG:0&-:0.1176&GG:0|-:0.05697&GG:0.001082&-:0.05697&GG:0.001082|-:0.0871&GG:0&-:0.0871&GG:0|-:0.1259&GG:0.0001287&-:0.1259&GG:0.0001287||||||||";
    private GenotypeBuilder genotypeBuilder = new GenotypeBuilder();
    private VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "1", 3599750, 3599751, Arrays.asList(Allele.create("TG", true), Allele.create("T", false), Allele.create("TGG", false), Allele.create("TC", false)));

    public FrameworkSparkFilterTest(){
        variantContextBuilder.attribute("AC", new int[]{1,2,3});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.001, 0.5, 0.5});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.002, 0.5, 0.5});
        variantContextBuilder.attribute("CSQ", new ArrayList(Arrays.asList(keep.split(","))));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("TG", true), Allele.create("T", false)))
                        .unfiltered()
                        .make()
        );
    }

    @Test
    public void getAlternativeAlleleNumIndexTest() throws Exception {
        assertEquals(1, FrameworkSparkFilter.getAlternativeAlleleNumIndex(variantContextBuilder.make(), Allele.create("TGG", false)));
    }

    @Test
    public void getAllAlleleNumIndexTest() throws Exception{
        assertEquals(2, FrameworkSparkFilter.getAllAlleleNumIndex(variantContextBuilder.make(), Allele.create("TGG", false)));
    }

    @Test
    public void getVepAlleleNumIndexTest() throws Exception{
        assertEquals(2, FrameworkSparkFilter.getVepAlleleNumIndex(variantContextBuilder.make(), Allele.create("TGG", false)));
    }

    @Test
    public void getMaxAlleleCountTest() throws  Exception {
        assertEquals(3, FrameworkSparkFilter.getMaxAlleleCount(variantContextBuilder.make()));
    }

    @Test
    public void areAnyAlternativeAllelesLowFrequencyTest() throws Exception {
        assertEquals(true, FrameworkSparkFilter.areAnyAlternativeAllelesLowFrequency(variantContextBuilder.make(), 0.01));
    }

    @Test
    public void areAnyAlternativeAllelesLowFrequencyChrTest() throws Exception {
        VariantContextBuilder variantContextBuilderChr = new VariantContextBuilder(variantContextBuilder);
        variantContextBuilderChr.chr("chr1");
        assertEquals(true, FrameworkSparkFilter.areAnyAlternativeAllelesLowFrequency(variantContextBuilderChr.make(), 0.01));
    }

    @Test
    public void getGnomadGenomeAlternativeAlleleFrequencyTest() throws Exception{
        assertEquals(0.001, FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContextBuilder.make(), Allele.create("T", false)), 0);
    }

    @Test
    public void getGnomadExomeAlternativeAlleleFrequencyTest() throws Exception{
        assertEquals(0.002, FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContextBuilder.make(), Allele.create("T", false)), 0);
    }

    @Test
    public void getGnomadGenomeAlternativeAlleleFrequencyChrTest() throws Exception{
        VariantContextBuilder variantContextBuilderChr = new VariantContextBuilder(variantContextBuilder);
        variantContextBuilderChr.chr("chr1");
        assertEquals(0.001, FrameworkSparkFilter.getGnomadGenomeAlternativeAlleleFrequency(variantContextBuilderChr.make(), Allele.create("T", false)), 0);
    }

    @Test
    public void getGnomadExomeAlternativeAlleleFrequencyChrTest() throws Exception{
        VariantContextBuilder variantContextBuilderChr = new VariantContextBuilder(variantContextBuilder);
        variantContextBuilderChr.chr("chr1");
        assertEquals(0.002, FrameworkSparkFilter.getGnomadExomeAlternativeAlleleFrequency(variantContextBuilderChr.make(), Allele.create("T", false)), 0);
    }

    @Test
    public void getCohortAlternativeAlleleCountTest() throws Exception{
        assertEquals(2, FrameworkSparkFilter.getCohortAlternativeAlleleCount(variantContextBuilder.make(), Allele.create("TGG")));
    }

    @Test
    public void isAlleleSpanningDeletionTest() throws Exception {
        assertTrue(FrameworkSparkFilter.isAlleleSpanningDeletion(Allele.create("*")));
        assertFalse(FrameworkSparkFilter.isAlleleSpanningDeletion(Allele.create("A")));
        assertFalse(FrameworkSparkFilter.isAlleleSpanningDeletion(Allele.create("T")));
        assertFalse(FrameworkSparkFilter.isAlleleSpanningDeletion(Allele.create("G")));
        assertFalse(FrameworkSparkFilter.isAlleleSpanningDeletion(Allele.create("C")));
    }

    @Test
    public void isContigPrefixedChrTest() throws Exception {
        assertTrue(FrameworkSparkFilter.isContigPrefixedChr("chr1"));
        assertFalse(FrameworkSparkFilter.isContigPrefixedChr("1"));
    }

    @Test
    public void trimLeadingChrPrefixTest() throws Exception {
        assertTrue(FrameworkSparkFilter.trimLeadingChrPrefix("chr1").equals("1"));
        assertTrue(FrameworkSparkFilter.trimLeadingChrPrefix("1").equals("1"));
    }

}
