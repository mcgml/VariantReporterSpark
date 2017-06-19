package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.framework.spark.filter.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.broadinstitute.gatk.engine.samples.Affection;
import org.broadinstitute.gatk.engine.samples.Gender;
import org.broadinstitute.gatk.engine.samples.Sample;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Class for reading and filtering variants
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class VCFReaderSpark {

    public static void reportVariants(File file, VCFHeaders vcfHeaders, List<Sample> samples, Integer threads, HashSet<String> preferredTranscripts, boolean onlyPrintKnownRefSeq) throws IOException {

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
        for (Sample sample : samples){
            if (sample.getAffection().equals(Affection.AFFECTED)) {

                //Autosomal dominant
                WriteVariants.toTextFile(
                        variants
                                .filter(new NonVariantBySampleSparkFilter(sample.getID()))
                                .filter(new AutosomalDominantSparkFilter(sample.getID()))
                                .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                                .collect(),
                        sample.getID(),
                        vcfHeaders.getVepHeaders(),
                        FrameworkSparkFilter.Workflow.AUTOSOMAL_DOMINANT,
                        preferredTranscripts,
                        onlyPrintKnownRefSeq
                );

                //X-linked Male
                if (sample.getGender().equals(Gender.MALE)){
                    WriteVariants.toTextFile(
                            variants
                                    .filter(new NonVariantBySampleSparkFilter(sample.getID()))
                                    .filter(new MaleXDominantSparkFilter(sample.getID()))
                                    .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                                    .collect(),
                            sample.getID(),
                            vcfHeaders.getVepHeaders(),
                            FrameworkSparkFilter.Workflow.MALE_X,
                            preferredTranscripts,
                            onlyPrintKnownRefSeq
                    );
                }

                //X-linked female
                if (sample.getGender().equals(Gender.FEMALE)){
                    WriteVariants.toTextFile(
                            variants
                                    .filter(new NonVariantBySampleSparkFilter(sample.getID()))
                                    .filter(new FemaleXDominantSparkFilter(sample.getID()))
                                    .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()))
                                    .collect(),
                            sample.getID(),
                            vcfHeaders.getVepHeaders(),
                            FrameworkSparkFilter.Workflow.FEMALE_X,
                            preferredTranscripts,
                            onlyPrintKnownRefSeq
                    );
                }

                //autosomal recessive
                //TODO count by gene
                JavaRDD<VariantContext> autosomalRecessiveCandidates = variants
                        .filter(new NonVariantBySampleSparkFilter(sample.getID()))
                        .filter(new AutosomalRecessiveSparkFilter(sample.getID()))
                        .filter(new FunctionalConsequenceSparkFilter(sample.getID(), vcfHeaders.getVepHeaders()));

                WriteVariants.toTextFile(
                        autosomalRecessiveCandidates.collect(),
                        sample.getID(),
                        vcfHeaders.getVepHeaders(),
                        FrameworkSparkFilter.Workflow.AUTOSOMAL_RECESSIVE,
                        preferredTranscripts,
                        onlyPrintKnownRefSeq
                );
            }
        }

        javaSparkContext.close();
    }

}
