package nhs.genetics.cardiff;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Programme for filtering and writing variants using Apache Spark
 *
 * @author  Matt Lyon
 * @since   2017-06-12
 */

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static final String VERSION = "1.0.0";
    static final String PROGRAM = "VariantReporterSpark";

    public static void main(String[] args) {

        boolean onlyPrintKnownRefSeq;
        File variantCallFormatFile = null, preferredTranscriptsFile = null, output = null;
        int threads = 1;
        HashSet<String> preferredTranscripts;

        //parse command line
        CommandLineParser commandLineParser = new BasicParser();
        CommandLine commandLine = null;
        HelpFormatter formatter = new HelpFormatter();
        Options options = new Options();

        options.addOption("V", "Variant", true, "Path to input VCF file");
        options.addOption("P", "PreferredTranscript", true, "Path to preferred transcript list");
        options.addOption("K", "Known", false, "Report only known RefSeq transcripts (NM)");
        options.addOption("O", "Output", true, "Output filename");
        options.addOption("T", "Threads", true, "Execution threads [" + threads + "]");

        try {
            commandLine = commandLineParser.parse(options, args);

            variantCallFormatFile = commandLine.hasOption("V") ? new File(commandLine.getOptionValue("V")) : null;
            preferredTranscriptsFile = commandLine.hasOption("P") ? new File(commandLine.getOptionValue("P")) : null;
            onlyPrintKnownRefSeq = commandLine.hasOption("K");
            output = commandLine.hasOption("O") ? new File(commandLine.getOptionValue("O")) : null;
            variantCallFormatFile = commandLine.hasOption("V") ? new File(commandLine.getOptionValue("V")) : null;
            if (commandLine.hasOption("T")) threads = Integer.parseInt(commandLine.getOptionValue("T"));

        } catch (ParseException | NullPointerException e){
            formatter.printHelp(PROGRAM + " " + VERSION, options);
            LOGGER.log(Level.SEVERE, "Check args: " + e.getMessage());
            System.exit(-1);
        }

        //parse preferred transcripts list
        if (preferredTranscriptsFile != null){
            try (Stream<String> stream = Files.lines(Paths.get(commandLine.getOptionValue("T")))) {
                preferredTranscripts = stream.collect(Collectors.toCollection(HashSet::new));
            } catch (IOException e){
                LOGGER.log(Level.SEVERE,"Could not parse preferred transcript list: " + e.getMessage());
                System.exit(-1);
            }
        }

        //parse VCF headers
        VCFHeaders vcfHeaders = new VCFHeaders(variantCallFormatFile);
        try {
            vcfHeaders.setVCFHeaders();
            vcfHeaders.setVCFVersion();
            vcfHeaders.setVEPVersion();
        } catch (NullPointerException e){
            LOGGER.log(Level.WARNING, "Could not read VEP header. Assuming no annotations, continuing without it.");
        } catch (IOException|IllegalArgumentException e){
            LOGGER.log(Level.SEVERE, "Could not read VCF version or not supported: " + e.getMessage());
            System.exit(-1);
        }

        //report variants
        VCFReaderSpark vcfReaderSpark = new VCFReaderSpark(variantCallFormatFile, vcfHeaders, 2);
        vcfReaderSpark.loadVariants();

    }

}
