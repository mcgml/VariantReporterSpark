package nhs.genetics.cardiff;

import com.fasterxml.jackson.databind.ObjectMapper;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import nhs.genetics.cardiff.framework.spark.filter.*;
import nhs.genetics.cardiff.framework.vep.VepAnnotationObject;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

    public static void filterVariants(File file, VCFHeaders vcfHeaders, Integer threads) throws IOException {

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


        List<VariantContext> autosomalDomiantVariants = variants
                .filter(new AutosomalDominantSparkFilter("14M01382"))
                .filter(new FunctionalConsequenceSparkFilter("14M01382", vcfHeaders.getVepHeaders()))
                .collect();

        /*List<VariantContext> autosomalRecessive = variants
                .filter(new AutosomalRecessiveSparkFilter("14M01382"))
                .filter(new FunctionalConsequenceSparkFilter("14M01382", vcfHeaders.getVepHeaders()))
                .collect();*/

        List<VariantContext> maleX = variants
                .filter(new MaleXDominantSparkFilter("14M01382"))
                .filter(new FunctionalConsequenceSparkFilter("14M01382", vcfHeaders.getVepHeaders()))
                .collect();

        List<VariantContext> femaleX = variants
                .filter(new FemaleXDominantSparkFilter("14M01382"))
                .filter(new FunctionalConsequenceSparkFilter("14M01382", vcfHeaders.getVepHeaders()))
                .collect();

        //write variants
        WriteVariants.toTextFile(autosomalDomiantVariants, "14M01382", vcfHeaders.getVepHeaders(), FrameworkSparkFilter.Workflow.AUTOSOMAL_DOMINANNT);
        //WriteVariants.toTextFile(autosomalRecessive, "14M01382", vcfHeaders.getVepHeaders(), FrameworkSparkFilter.Workflow.AUTOSOMAL_RECESSIVE);
        WriteVariants.toTextFile(maleX, "14M01382", vcfHeaders.getVepHeaders(), FrameworkSparkFilter.Workflow.MALE_X);
        WriteVariants.toTextFile(femaleX, "14M01382", vcfHeaders.getVepHeaders(), FrameworkSparkFilter.Workflow.FEMALE_X);

        javaSparkContext.close();
    }

}
