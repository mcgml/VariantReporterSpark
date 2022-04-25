# VariantReporterSpark

Scalable application for prioritising clinically relevant variants from WGS/WES/Panel studies built using [Apache Spark](https://spark.apache.org/).

This software will extract annotated variants to text file that fall into these categories:

- Dominant inheritance
- Recessive inheritance
- Compound Heterozygous inheritance
- De Novo (trio only)
- Uniparental disomy (trio only)

## Usage

Perform variant calling, filtering and annotation using the [GermlineEnrichment](https://github.com/mcgml/germlineenrichment) pipeline.

Run variant prioritise workflow

```sh
/share/apps/jre-distros/jre1.8.0_131/bin/java \
-Djava.io.tmpdir=/state/partition1/tmpdir \
-Xmx48g \
-jar /data/diagnostics/apps/VariantReporterSpark/VariantReporterSpark-1.3.2/VariantReporterSpark.jar \
-V calls.vcf \
-P pedigree.ped \
-T <threads> \
-N
```

## Theory of operation

Variants are passed through a series of filters as shown in the flowchart and described below.

<p align="center">
  <img src="https://github.com/mcgml/VariantReporterSpark/blob/master/flow.jpg">
</p>

### Non Informative Site Filter

Identifies variant sites for downstream analysis

- Pass all context filters (Quality, QD, FS, MQ, MQRankSum, ReadPosRankSum, InbreedingCoeff)
- Has functional annotation
- Non reference in one or more samples
- Less than 1% population allele frequency (Gnomad) for one or more alternative alleles

### Non Variant By Sample Filter

Identifies genotypes for downstream analysis

- Must pass genotype filters (DP >10, GQ > 20)
- Cannot be homozygous for the reference allele 
- Cannot be no call (insufficient data to call genotype)

### Dominant Filter

- Autosomal
  - Heterozygous
  - Cohort allele count < 4
  - gnomAD (Exome) allele frequency < 0.1%
  - gnomAD (Genome) allele frequency < 0.75%
- X-Female
  - As autosomal
- X/Y-Male
  - Homozygous (hemizygous)
  - Cohort allele count < 6
  - gnomAD (Exome) allele frequency < 0.1%
  - gnomAD (Genome) allele frequency < 0.75%

### Homozygous Filter

- Autosomal
  - Homozygous for alternative allele
  - Cohort allele count < 4
  - gnomAD (Exome) allele frequency < 1%
  - gnomAD (Genome) allele frequency < 1%
 - X-Female
    - Homozygous for alternative allele
    - Cohort allele count < 8
    - gnomAD (Exome) allele frequency < 1%
    - gnomAD (Genome) allele frequency < 1%

### Compound Heterozygous Filter

- Autosomal or X-Female
  - Heterozygous
  - gnomAD (Exome) allele frequency < 1%
  - gnomAD (Genome) allele frequency < 1%
  - Two or more variants per gene

### De Novo Filter

- Any chromosome
  - Both parents are homozygous reference
  - Cohort allele count < 4
  - gnomAD (Exome) allele frequency < 0.1%
  - gnomAD (Genome) allele frequency < 0.75%


### Uniparental Isodisomy Filter

- Autosomal
  - Homozygous for alternative allele
  - Mother is heterozygous and father is homozygous reference OR mother is homozygous reference and father is heterozygous
  - gnomAD (Exome) allele frequency < 1%
  - gnomAD (Genome) allele frequency < 1%
- X-Female
  - Homozygous for alternative allele
  - Mother is heterozygous and father is homozygous reference OR mother is homozygous reference and father is homozygous (hemizygous) alternative
  - gnomAD (Exome) allele frequency < 1%
  - gnomAD (Genome) allele frequency < 1%

### Functional Consequence Filter

Variant has consequence on one or more transcripts
  - frameshift_variant
  - incomplete_terminal_codon_variant
  - inframe_deletion
  - inframe_insertion
  - initiator_codon_variant
  - missense_variant
  - splice_acceptor_variant
  - splice_donor_variant
  - splice_region_variant
  - stop_gained
  - stop_lost
  - synonymous_variant
  - transcript_ablation
  - transcript_amplification
