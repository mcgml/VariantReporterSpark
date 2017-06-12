package nhs.genetics.cardiff;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderVersion;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);
    private static final String VERSION = "1.0.0";
    private static final String PROGRAM = "VariantReporterSpark";
    private static VCFHeader vcfHeader;
    private static VCFHeaderVersion vcfHeaderVersion;

    public static void main(String[] args) {

        //define I/O
        File file = new File("/data/db/human/gatk/2.8/b37/1000G_phase1.indels.b37.vcf");
        //File file = new File("/Users/ml/Documents/1000G_phase1.indels.b37.vcf");

        //start spark
        SparkConf sparkConf = new SparkConf().setAppName(Main.PROGRAM).setMaster("local[2]");
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        sparkConf.set("spark.kryo.registrationRequired", "true");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        //read vcf headers & configure codec
        VCFFileReader vcfFileReader = new VCFFileReader(file);
        vcfHeader =  vcfFileReader.getFileHeader();
        vcfHeaderVersion = VCFHeaderVersion.VCF4_2;
        vcfFileReader.close();

        //read VCF and map to RDD
        JavaRDD<VariantContext> variants = javaSparkContext.textFile(file.toString())
                .filter(line -> !line.startsWith("#"))
                .map(line -> {
                    final VCFCodec vcfCodec = new VCFCodec();
                    vcfCodec.setVCFHeader(vcfHeader, vcfHeaderVersion);
                    return vcfCodec.decode(line);
                })
                .filter(VariantContext::isNotFiltered);

        //retain variants for later...
        variants.persist(StorageLevel.MEMORY_ONLY());

        //count lengths of indels
        JavaPairRDD<Integer, Integer> counts = variants
                .flatMap(variantContext -> variantContext.getIndelLengths().iterator())
                .mapToPair(len -> new Tuple2<>(len, 1))
                .reduceByKey((a, b) -> a + b);

        List<Tuple2<Integer, Integer>> countscollected = counts.collect();

        try (PrintWriter printWriter = new PrintWriter("out.txt")){
            for (Tuple2<Integer, Integer> iter : countscollected){
                printWriter.print(iter._1);
                printWriter.print("\t");
                printWriter.print(iter._2);
                printWriter.println();
            }
        } catch (IOException e){
            logger.error("Could not write to output file: " + e.getMessage());
            System.exit(-1);
        }

        //stop spark
        javaSparkContext.close();

    }

}
