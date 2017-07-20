package nhs.genetics.cardiff.mappers;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Test
 */
public class FlatMapVepToGeneListTest {
    private final static String[] headers = "Allele|Consequence|IMPACT|SYMBOL|Gene|Feature_type|Feature|BIOTYPE|EXON|INTRON|HGVSc|HGVSp|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|ALLELE_NUM|DISTANCE|STRAND|FLAGS|VARIANT_CLASS|SYMBOL_SOURCE|HGNC_ID|CANONICAL|TSL|APPRIS|CCDS|ENSP|SWISSPROT|TREMBL|UNIPARC|REFSEQ_MATCH|GENE_PHENO|SIFT|PolyPhen|DOMAINS|HGVS_OFFSET|GMAF|AFR_MAF|AMR_MAF|EAS_MAF|EUR_MAF|SAS_MAF|AA_MAF|EA_MAF|ExAC_MAF|ExAC_Adj_MAF|ExAC_AFR_MAF|ExAC_AMR_MAF|ExAC_EAS_MAF|ExAC_FIN_MAF|ExAC_NFE_MAF|ExAC_OTH_MAF|ExAC_SAS_MAF|CLIN_SIG|SOMATIC|PHENO|PUBMED|MOTIF_NAME|MOTIF_POS|HIGH_INF_POS|MOTIF_SCORE_CHANGE".trim().split("\\|");
    private static String record = "G|intron_variant|MODIFIER|MCPH1|79648|Transcript|NM_024596.3|protein_coding||12/13|NM_024596.3:c.2215-19A>G|||||||rs2936531|1||1||SNV|||||||NP_078872.2||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||,G|intron_variant|MODIFIER|TEST1|79648|Transcript|NM_024596.3|protein_coding||12/13|NM_024596.3:c.2215-19A>G|||||||rs2936531|1||1||SNV|||||||NP_078872.2||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||,G|intron_variant|MODIFIER|TEST2|79648|Transcript|XM_005266034.1|protein_coding||12/13|XM_005266034.1:c.2215-19A>G|||||||rs2936531|1||1||SNV|||YES||||XP_005266091.1||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||,G|intron_variant|MODIFIER|TEST2|79648|Transcript|XM_005266034.1|protein_coding||12/13|XM_005266034.1:c.2215-19A>G|||||||rs2936531|1||1||SNV|||YES||||XP_005266091.1||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||,G|intron_variant|MODIFIER|MCPH1|79648|Transcript|XM_005266035.1|protein_coding||11/12|XM_005266035.1:c.1060-19A>G|||||||rs2936531|1||1||SNV|||||||XP_005266092.1||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||,G|intron_variant|MODIFIER|MCPH1|79648|Transcript|XM_005266035.1|protein_coding||11/12|XM_005266035.1:c.1060-19A>G|||||||rs2936531|1||1||SNV|||||||XP_005266092.1||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||,G|upstream_gene_variant|MODIFIER||100507530|Transcript|XR_132725.2|misc_RNA||||||||||rs2936531|1|4243|-1||SNV|||YES||||||||||||||G:0.4904|G:0.4554|G:0.366|G:0.6835|G:0.4235|G:0.4959|G:0.4542|G:0.4058|G:0.448|G:0.4492|G:0.4748|G:0.4031|G:0.6766|G:0.4479|G:0.4107|G:0.4531|G:0.5032|||1|||||";

    @Test
    public void pass() throws Exception {
        GenotypeBuilder genotypeBuilder = new GenotypeBuilder();
        HashSet<String> genes = new HashSet<String>() {{
            add("MCPH1");
            add("TEST1");
            add("TEST2");
        }};

        VariantContextBuilder variantContextBuilder = new VariantContextBuilder("test", "8", 6478956, 6478956, Arrays.asList(Allele.create("A", true), Allele.create("G", false)));
        variantContextBuilder.attribute("AC", new int[]{1});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Genome_chr1.AF_POPMAX", new double[]{0.0001});
        variantContextBuilder.attribute("GNOMAD_2.0.1_Exome.AF_POPMAX", new double[]{0.00001});
        variantContextBuilder.attribute("CSQ", new ArrayList(Arrays.asList(record.split(","))));
        variantContextBuilder.unfiltered();

        variantContextBuilder.genotypes(
                genotypeBuilder.name("sample")
                        .alleles(Arrays.asList(Allele.create("A", true), Allele.create("G", false)))
                        .unfiltered()
                        .make()
        );

        assertEquals(convertIteratorToStringList(genes.iterator()), convertIteratorToStringList(new FlatMapVepToGeneList(headers).call(variantContextBuilder.make())));
    }

    private static String convertIteratorToStringList(Iterator<String> iter){
        List<String> copy = new ArrayList<>();
        while (iter.hasNext())
            copy.add(iter.next());
        return copy.stream()
                .sorted()
                .collect(Collectors.joining(";"));
    }
}