package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.framework.spark.filter.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * Class for reading and filtering variants
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class VCFReaderSpark {

    public static void reportVariants(File file, VCFHeaders vcfHeaders, Integer threads, HashSet<String> preferredTranscripts, boolean onlyPrintKnownRefSeq) throws IOException {

        SparkConf sparkConf = new SparkConf().setAppName(Main.PROGRAM).setMaster("local[" + threads + "]");
        //sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer"); // TODO production
        //sparkConf.set("spark.kryo.registrationRequired", "true"); // TODO production
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        //load variants and persist
        JavaRDD<VariantContext> variants = javaSparkContext.textFile(file.toString())
                .filter(line -> !line.startsWith("#"))
                .map(line -> {
                    final VCFCodec vcfCodec = new VCFCodec();
                    vcfCodec.setVCFHeader(vcfHeaders.getVcfHeader(), vcfHeaders.getVcfHeaderVersion());
                    return vcfCodec.decode(line);
                })
                .filter(VariantContext::isNotFiltered);
        variants.persist(StorageLevel.MEMORY_ONLY());

        //filter variants for each sample
        for (String sample : vcfHeaders.getVcfHeader().getSampleNamesInOrder()){

            //test
            variants
                    .filter(new NonVariantBySampleSparkFilter(sample))
                    .flatMap(new MapToGenomeVariants(sample))
                    .collect()
                    .forEach(System.out::println);

            //Autosomal dominant
            WriteVariants.toTextFile(
                    variants
                    .filter(new NonVariantBySampleSparkFilter(sample))
                    .filter(new AutosomalDominantSparkFilter(sample))
                    .filter(new FunctionalConsequenceSparkFilter(sample, vcfHeaders.getVepHeaders()))
                    .collect(),
                    sample,
                    vcfHeaders.getVepHeaders(),
                    FrameworkSparkFilter.Workflow.AUTOSOMAL_DOMINANT,
                    preferredTranscripts,
                    onlyPrintKnownRefSeq
            );

            //X-linked Male
            WriteVariants.toTextFile(
                    variants
                            .filter(new NonVariantBySampleSparkFilter(sample))
                            .filter(new MaleXDominantSparkFilter(sample))
                            .filter(new FunctionalConsequenceSparkFilter(sample, vcfHeaders.getVepHeaders()))
                            .collect(),
                    sample,
                    vcfHeaders.getVepHeaders(),
                    FrameworkSparkFilter.Workflow.MALE_X,
                    preferredTranscripts,
                    onlyPrintKnownRefSeq
            );

            //X-linked female
            WriteVariants.toTextFile(
                    variants
                            .filter(new NonVariantBySampleSparkFilter(sample))
                            .filter(new FemaleXDominantSparkFilter(sample))
                            .filter(new FunctionalConsequenceSparkFilter(sample, vcfHeaders.getVepHeaders()))
                            .collect(),
                    sample,
                    vcfHeaders.getVepHeaders(),
                    FrameworkSparkFilter.Workflow.FEMALE_X,
                    preferredTranscripts,
                    onlyPrintKnownRefSeq
            );

            //autosomal recessive
            JavaRDD<VariantContext> autosomalRecessiveCandidates = variants
                    .filter(new NonVariantBySampleSparkFilter(sample))
                    .filter(new AutosomalRecessiveSparkFilter(sample))
                    .filter(new FunctionalConsequenceSparkFilter(sample, vcfHeaders.getVepHeaders()));

            //TODO count by gene

            WriteVariants.toTextFile(
                    autosomalRecessiveCandidates.collect(),
                    sample,
                    vcfHeaders.getVepHeaders(),
                    FrameworkSparkFilter.Workflow.AUTOSOMAL_RECESSIVE,
                    preferredTranscripts,
                    onlyPrintKnownRefSeq
            );

        }

        javaSparkContext.close();
    }

}
