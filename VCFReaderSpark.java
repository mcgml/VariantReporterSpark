package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.framework.spark.filter.AutosomalDominantSparkFilter;
import nhs.genetics.cardiff.framework.spark.filter.FunctionalConsequenceSparkFilter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import java.io.File;
import java.io.Serializable;

/**
 * Class for reading and filtering variants
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class VCFReaderSpark implements Serializable {

    private File file;
    private VCFHeaders vcfHeaders;
    private Integer threads;

    public VCFReaderSpark(File file, VCFHeaders vcfHeaders, Integer threads){
        this.file = file;
        this.vcfHeaders = vcfHeaders;
        this.threads= threads;
    }

    public void filterVariants(){
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
        variants.persist(StorageLevel.MEMORY_ONLY_SER());

        //autosomal dominant variants
        JavaRDD<VariantContext> autosomalDominantVariants = variants
                .filter(new AutosomalDominantSparkFilter("14M01382"))
                .filter(new FunctionalConsequenceSparkFilter("14M01382"));

        //write to console
        autosomalDominantVariants.collect().forEach(System.out::println);

        javaSparkContext.close();
    }

}
