package nhs.genetics.cardiff;

import com.fasterxml.jackson.databind.ObjectMapper;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.framework.spark.filter.AutosomalDominantSparkFilter;
import nhs.genetics.cardiff.framework.spark.filter.FunctionalConsequenceSparkFilter;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import java.io.File;
import java.util.HashMap;

/**
 * Class for reading and filtering variants
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class VCFReaderSpark {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static VepAnnotationObject deserialiseVepAnnotation(String[] vepHeaders, String vepFields){
        HashMap<String, String> hashMap = new HashMap<String, String>();

        //split annotation fields
        String[] annotations = vepFields.split("\\|");

        //pair headers with fields
        for (int i=0 ; i < annotations.length; i++) {
            hashMap.put(vepHeaders[i].trim(), annotations[i].trim());
        }

        return objectMapper.convertValue(hashMap, VepAnnotationObject.class);
    }

    public static void filterVariants(File file, VCFHeaders vcfHeaders, Integer threads){

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
                .filter(new AutosomalDominantSparkFilter("14M01382"));

        //functional filtering
        JavaRDD<VariantContext> functionallySignificantVariants = autosomalDominantVariants
                .filter(new FunctionalConsequenceSparkFilter("14M01382", vcfHeaders.getVepHeaders()));

        //write variants
        WriteVariants.toTextFile(functionallySignificantVariants.collect(), "14M01382", vcfHeaders);

        javaSparkContext.close();
    }

}
