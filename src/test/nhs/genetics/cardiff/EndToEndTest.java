package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.filters.DeNovoSparkFilter;
import nhs.genetics.cardiff.filters.NonVariantBySampleSparkFilter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.storage.StorageLevel;
import org.broadinstitute.hellbender.utils.samples.Affection;
import org.broadinstitute.hellbender.utils.samples.Sample;
import org.broadinstitute.hellbender.utils.samples.Sex;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ml on 31/01/2018.
 */
public class EndToEndTest {

    public static File getTestDataDir() {
        return new File("src/test/resources");
    }

    @Test
    public void deNovoWorkflow() throws Exception {

        //find resource
        File file = new File(getTestDataDir() + "/vcf/CEUTrio.HiSeq.WGS.b37.bestPractices.b37.genotypes_refined_filtered.denovo.validated.vep.gnomad.vcf");

        //parse VCF headers
        VCFHeaders vcfHeaders = new VCFHeaders(file);
        vcfHeaders.populateVCFHeaders();
        vcfHeaders.populateVCFVersion();
        vcfHeaders.populateVEPHeaders();

        Sample sample = new Sample("NA12878", "FAM1463", "NA12891", "NA12892", Sex.FEMALE, Affection.AFFECTED);

        //launch spark
        SparkConf sparkConf = new SparkConf().setAppName(Main.PROGRAM).setMaster("local[" + 1 + "]");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        //load variants and persist informative variants
        JavaRDD<VariantContext> informativeVariants = javaSparkContext.textFile(file.toString())
                .filter(line -> !line.startsWith("#"))
                .map(line -> {
                    final VCFCodec vcfCodec = new VCFCodec();
                    vcfCodec.setVCFHeader(vcfHeaders.getVcfHeader(), vcfHeaders.getVcfHeaderVersion());
                    return vcfCodec.decode(line);
                });
        informativeVariants.persist(StorageLevel.MEMORY_ONLY());

        JavaRDD<VariantContext> informativeGenotypes = informativeVariants
                .filter(new NonVariantBySampleSparkFilter(sample.getID()));
        informativeGenotypes.persist(StorageLevel.MEMORY_ONLY());

        List<VariantContext> deNovoVariants = informativeGenotypes.filter(new DeNovoSparkFilter(sample.getID(), sample.getPaternalID(), sample.getMaternalID())).collect();

        javaSparkContext.close();

        assertEquals(41, deNovoVariants.size());
    }

}
