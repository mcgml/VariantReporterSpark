package nhs.genetics.cardiff;

import nhs.genetics.cardiff.framework.hgmd.HGMDClient;
import nhs.genetics.cardiff.framework.vep.MissingVEPHeaderException;
import org.apache.commons.cli.*;
import org.broadinstitute.gatk.engine.samples.PedReader;
import org.broadinstitute.gatk.engine.samples.Sample;
import org.broadinstitute.gatk.engine.samples.SampleDB;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Program for filtering and writing variants using Apache Spark
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static final String VERSION = "1.0.0";
    static final String PROGRAM = "VariantReporterSpark";

    public static void main(String[] args) {

        Integer threads = null;
        Boolean onlyPrintKnownRefSeq = null;
        File variantCallFormatFile = null, preferredTranscriptsFile = null, pedFile = null;
        HashSet<String> preferredTranscripts = null;
        List<Sample> samples = null;
        String hgmdUsername = null, hgmdPassword = null;
        HGMDClient hgmdClient = new HGMDClient();

        //parse command line
        CommandLineParser commandLineParser = new BasicParser();
        CommandLine commandLine = null;
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();

        options.addOption("V", "Variant", true, "Path to input VCF file");
        options.addOption("F", "Features", true, "Path to preferred features list");
        options.addOption("P", "Ped", true, "Path to PED file");
        options.addOption("N", "NM", false, "Report only known RefSeq transcripts (NM)");
        options.addOption("T", "Threads", true, "Execution threads");
        options.addOption("Hu", "HGMDUser", true, "HGMD credentials");
        options.addOption("Hp", "HGMDPass", true, "HGMD credentials");

        try {
            commandLine = commandLineParser.parse(options, args);

            variantCallFormatFile = commandLine.hasOption("V") ? new File(commandLine.getOptionValue("V")) : null;
            preferredTranscriptsFile = commandLine.hasOption("F") ? new File(commandLine.getOptionValue("F")) : null;
            pedFile = commandLine.hasOption("P") ? new File(commandLine.getOptionValue("P")) : null;
            onlyPrintKnownRefSeq = commandLine.hasOption("N");
            threads = commandLine.hasOption("T") ? Integer.parseInt(commandLine.getOptionValue("T")) : 1;
            hgmdUsername = commandLine.hasOption("Hu") ? commandLine.getOptionValue("Hu") : null;
            hgmdPassword = commandLine.hasOption("Hp") ? commandLine.getOptionValue("Hp") : null;

            if (variantCallFormatFile == null){
                throw new NullPointerException("Need to specify VCF input");
            }

            if (pedFile == null){
                throw new NullPointerException("Need to specify PED input");
            }

        } catch (ParseException | NullPointerException e){
            formatter.printHelp(PROGRAM + " " + VERSION, options);
            LOGGER.log(Level.SEVERE, "Check arguments: " + e.getMessage());
            System.exit(-1);
        }

        //connect to HGMD
        if (hgmdUsername != null && hgmdPassword != null){
            try {
                hgmdClient.setCookie(commandLine.getOptionValue("Hu"), commandLine.getOptionValue("Hp"));
            } catch (IOException e){
                LOGGER.log(Level.SEVERE,"Could not connect to HGMD: " + e.getMessage());
                System.exit(1);
            }
        }

        //parse preferred transcripts list
        if (preferredTranscriptsFile != null){
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(commandLine.getOptionValue("T")))) {

                String line;
                preferredTranscripts = new HashSet<>();

                while ((line = bufferedReader.readLine()) != null) {
                    preferredTranscripts.add(line);
                }

            } catch (IOException e){
                LOGGER.log(Level.SEVERE,"Could not parse preferred transcript list: " + e.getMessage());
                System.exit(-1);
            }
        }

        //parse VCF headers
        VCFHeaders vcfHeaders = new VCFHeaders(variantCallFormatFile);
        try {

            vcfHeaders.populateVCFHeaders();
            vcfHeaders.populateVCFVersion();

            try {
                vcfHeaders.populateVEPHeaders();
            } catch (MissingVEPHeaderException e){
                LOGGER.log(Level.WARNING, e.getMessage());
            }

        } catch (IOException|IllegalArgumentException e){
            LOGGER.log(Level.SEVERE, "Could not read VCF version or not supported: " + e.getMessage());
            System.exit(-1);
        }

        //parse PED file
        try {
            samples = new PedReader().parse(pedFile, EnumSet.noneOf(PedReader.MissingPedField.class), new SampleDB());
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Could not write variant report: " + e.getMessage());
            System.exit(-1);
        }

        //report variants
        try {
            WriteVariants.toTextFile(
                    VCFReaderSpark.stratifyCandidateVariants(variantCallFormatFile, vcfHeaders, samples, threads),
                    samples,
                    vcfHeaders.getVepHeaders(),
                    preferredTranscripts,
                    onlyPrintKnownRefSeq,
                    hgmdClient
            );
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Could not write variant report: " + e.getMessage());
            System.exit(-1);
        }

    }

}
