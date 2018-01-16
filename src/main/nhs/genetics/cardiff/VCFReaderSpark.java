package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.filters.*;
import nhs.genetics.cardiff.mappers.*;
import nhs.genetics.cardiff.framework.VariantContextWrapper;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.broadinstitute.hellbender.utils.samples.Affection;
import org.broadinstitute.hellbender.utils.samples.Sample;

import java.io.File;
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

    public static ArrayList<HashMap<VariantContextWrapper, ArrayList<FrameworkSparkFilter.Workflow>>> stratifyCandidateVariants(File file, VCFHeaders vcfHeaders, List<Sample> samples, Integer threads) {

        SparkConf sparkConf = new SparkConf().setAppName(Main.PROGRAM).setMaster("local[" + threads + "]");
        //sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer"); // TODO production
        //sparkConf.set("spark.kryo.registrationRequired", "true"); // TODO production
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        ArrayList<HashMap<VariantContextWrapper, ArrayList<FrameworkSparkFilter.Workflow>>> stratifiedVariants = new ArrayList<>();

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

        //loop over samples and collect stratified variants
        for (int n = 0; n < samples.size(); ++n){
            Sample sample = samples.get(n);

            stratifiedVariants.add(new HashMap<>());

            if (sample.getAffection() == Affection.AFFECTED){

                LOGGER.info("Filtering " + sample.getID());

                JavaRDD<VariantContext> informativeGenotypes = informativeVariants
                        .filter(new NonVariantBySampleSparkFilter(sample.getID()));
                informativeGenotypes.persist(StorageLevel.MEMORY_ONLY());

                LOGGER.info("Found " + informativeGenotypes.count() + " informative genotypes.");

                //requires parental samples
                if (sample.getMaternalID() != null && sample.getPaternalID() != null){

                    //de novo
                    for (VariantContextWrapper variantContext : informativeGenotypes
                            .filter(new DeNovoSparkFilter(sample.getID(), sample.getPaternalID(), sample.getMaternalID()))
                            .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                            .map(VariantContextWrapper::new)
                            .collect()){

                        if (!stratifiedVariants.get(n).containsKey(variantContext)){
                            stratifiedVariants.get(n).put(variantContext, new ArrayList<>());
                        }

                        stratifiedVariants.get(n).get(variantContext).add(FrameworkSparkFilter.Workflow.DE_NOVO);
                    }

                    //UPD
                    for (VariantContextWrapper variantContext : informativeGenotypes
                            .filter(new UniparentalIsodisomySparkFilter(sample.getID(), sample.getSex(), sample.getPaternalID(), sample.getMaternalID()))
                            .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                            .map(VariantContextWrapper::new)
                            .collect()){

                        if (!stratifiedVariants.get(n).containsKey(variantContext)){
                            stratifiedVariants.get(n).put(variantContext, new ArrayList<>());
                        }

                        stratifiedVariants.get(n).get(variantContext).add(FrameworkSparkFilter.Workflow.UNIPARENTAL_ISODISOMY);
                    }

                }

                //dominant
                for (VariantContextWrapper variantContext : informativeGenotypes
                        .filter(new DominantSparkFilter(sample.getID(), sample.getSex()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                        .map(VariantContextWrapper::new)
                        .collect()) {

                    if (!stratifiedVariants.get(n).containsKey(variantContext)) {
                        stratifiedVariants.get(n).put(variantContext, new ArrayList<>());
                    }

                    stratifiedVariants.get(n).get(variantContext).add(FrameworkSparkFilter.Workflow.DOMINANT);
                }

                //homozygous
                for (VariantContextWrapper variantContext : informativeGenotypes
                        .filter(new HomozygousSparkFilter(sample.getID(), sample.getSex()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                        .map(VariantContextWrapper::new)
                        .collect()){

                    if (!stratifiedVariants.get(n).containsKey(variantContext)){
                        stratifiedVariants.get(n).put(variantContext, new ArrayList<>());
                    }

                    stratifiedVariants.get(n).get(variantContext).add(FrameworkSparkFilter.Workflow.HOMOZYGOUS);
                }

                //compound het candidates
                JavaRDD<VariantContext> candidateCompoundHets = informativeGenotypes
                        .filter(new CompoundHeterozygousSparkFilter(sample.getID(), sample.getSex()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()));
                candidateCompoundHets.persist(StorageLevel.MEMORY_ONLY());

                //compound hets
                for (VariantContextWrapper variantContext : candidateCompoundHets
                        .filter(new GeneSparkFilter(
                                FrameworkSparkFilter.getVariantsWithMultipleGeneHits(candidateCompoundHets.flatMap(new FlatMapVepToGeneList(vcfHeaders.getVepHeaders())).countByValue()),
                                vcfHeaders.getVepHeaders()))
                        .map(VariantContextWrapper::new)
                        .collect()) {

                    if (!stratifiedVariants.get(n).containsKey(variantContext)) {
                        stratifiedVariants.get(n).put(variantContext, new ArrayList<>());
                    }

                    stratifiedVariants.get(n).get(variantContext).add(FrameworkSparkFilter.Workflow.COMPOUND_HETEROZYGOUS);
                }

            }
        }

        javaSparkContext.close();

        return stratifiedVariants;
    }
}
