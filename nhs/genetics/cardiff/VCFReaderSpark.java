package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.filters.*;
import nhs.genetics.cardiff.framework.VariantContextWrapper;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.broadinstitute.gatk.engine.samples.Affection;
import org.broadinstitute.gatk.engine.samples.Sample;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class for reading and filtering variants
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class VCFReaderSpark {
    private static final Logger LOGGER = Logger.getLogger(VCFReaderSpark.class.getName());

    public static void reportVariants(File file, VCFHeaders vcfHeaders, List<Sample> samples, Integer threads, HashSet<String> preferredTranscripts, boolean onlyPrintKnownRefSeq) throws IOException {

        SparkConf sparkConf = new SparkConf().setAppName(Main.PROGRAM).setMaster("local[" + threads + "]");
        //sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer"); // TODO production
        //sparkConf.set("spark.kryo.registrationRequired", "true"); // TODO production
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        //load variants and persist informative variants
        JavaRDD<VariantContext> informativeVariants = javaSparkContext.textFile(file.toString())
                .filter(line -> !line.startsWith("#"))
                .map(line -> {
                    final VCFCodec vcfCodec = new VCFCodec();
                    vcfCodec.setVCFHeader(vcfHeaders.getVcfHeader(), vcfHeaders.getVcfHeaderVersion());
                    return vcfCodec.decode(line);
                })
                .filter(new NonInformativeSiteSparkFilter());
        informativeVariants.persist(StorageLevel.MEMORY_ONLY());

        LOGGER.info("Identified " + informativeVariants.count() + " informative sites.");

        for (Sample sample : samples){
            if (sample.getAffection() == Affection.AFFECTED){

                LOGGER.info("Filtering " + sample.getID());

                HashMap<VariantContextWrapper, ArrayList<FrameworkSparkFilter.Workflow>> results = new HashMap<>();

                JavaRDD<VariantContext> informativeGenotypes = informativeVariants
                        .filter(new NonVariantBySampleSparkFilter(sample.getID()));
                informativeGenotypes.persist(StorageLevel.MEMORY_ONLY());

                LOGGER.info("Found " + informativeGenotypes.count() + " informative genotypes.");

                //requires parental samples
                if (sample.getMother() != null && sample.getFather() != null){

                    //de novo
                    for (VariantContextWrapper variantContext : informativeGenotypes
                            .filter(new DeNovoSparkFilter(sample.getID(), sample.getFather().getID(), sample.getMother().getID()))
                            .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                            .map(VariantContextWrapper::new)
                            .collect()){

                        if (!results.containsKey(variantContext)){
                            results.put(variantContext, new ArrayList<>());
                        }

                        results.get(variantContext).add(FrameworkSparkFilter.Workflow.DE_NOVO);
                    }

                    //UPD
                    for (VariantContextWrapper variantContext : informativeGenotypes
                            .filter(new UniparentalIsodisomySparkFilter(sample.getID(), sample.getGender(), sample.getFather().getID(), sample.getMother().getID()))
                            .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                            .map(VariantContextWrapper::new)
                            .collect()){

                        if (!results.containsKey(variantContext)){
                            results.put(variantContext, new ArrayList<>());
                        }

                        results.get(variantContext).add(FrameworkSparkFilter.Workflow.UNIPARENTAL_ISODISOMY);
                    }

                }

                //dominant
                for (VariantContextWrapper variantContext : informativeGenotypes
                        .filter(new DominantSparkFilter(sample.getID(), sample.getGender()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                        .map(VariantContextWrapper::new)
                        .collect()) {

                    if (!results.containsKey(variantContext)) {
                        results.put(variantContext, new ArrayList<>());
                    }

                    results.get(variantContext).add(FrameworkSparkFilter.Workflow.DOMINANT);
                }

                //homozygous
                for (VariantContextWrapper variantContext : informativeGenotypes
                        .filter(new HomozygousSparkFilter(sample.getID(), sample.getGender()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                        .map(VariantContextWrapper::new)
                        .collect()){

                    if (!results.containsKey(variantContext)){
                        results.put(variantContext, new ArrayList<>());
                    }

                    results.get(variantContext).add(FrameworkSparkFilter.Workflow.HOMOZYGOUS);
                }

                //compound het candidates
                JavaRDD<VariantContext> candidateCompoundHets = informativeGenotypes
                        .filter(new CompoundHeterozygousSparkFilter(sample.getID(), sample.getGender()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()));
                candidateCompoundHets.persist(StorageLevel.MEMORY_ONLY());

                //compound hets
                for (VariantContextWrapper variantContext : candidateCompoundHets
                        .filter(new GeneSparkFilter(
                                FrameworkSparkFilter.getVariantsWithMultipleGeneHits(candidateCompoundHets.flatMap(new FlatMapVepToGeneList(vcfHeaders.getVepHeaders())).countByValue()),
                                vcfHeaders.getVepHeaders()))
                        .map(VariantContextWrapper::new)
                        .collect()) {

                    if (!results.containsKey(variantContext)) {
                        results.put(variantContext, new ArrayList<>());
                    }

                    results.get(variantContext).add(FrameworkSparkFilter.Workflow.COMPOUND_HETEROZYGOUS);
                }

                //write variant report
                WriteVariants.toTextFile(results, sample, vcfHeaders.getVepHeaders(), preferredTranscripts, onlyPrintKnownRefSeq);
            }
        }

        javaSparkContext.close();
    }
}
