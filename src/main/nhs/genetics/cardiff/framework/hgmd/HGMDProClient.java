package nhs.genetics.cardiff.framework.hgmd;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.broadinstitute.gatk.utils.variant.GATKVariantContextUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.logging.Logger;

/**
 * Web-scrape API for working with HGMD annotations
 */
public class HGMDProClient {
    private static final Logger LOGGER = Logger.getLogger(HGMDProClient.class.getName());
    private static final Integer MAX_CONCURRENT_QUERIES = 500;
    private String sessionId;

    public List<VariantContext> annotateVariantContexts(List<VariantContext> variantContexts, HGMDProGenomeBuild build) throws IOException {

        int alleleNo, n;
        ArrayList<VariantContext> annotatedVariantContexts = new ArrayList<>();
        ArrayList<String> queries = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        HGMDProBatchSearchResult[] results = null;

        //check we have set the cookie
        if (sessionId == null){
            throw new NullPointerException("HGMD session cookie not set");
        }

        //decompose variant contexts, trim alternative allele(s) and build query list
        alleleNo = 0;
        for (VariantContext variantContext : variantContexts){
            for (Allele allele : variantContext.getAlternateAlleles()){
                if (!allele.isSymbolic() && allele.isNonReference() && !allele.equals(Allele.SPAN_DEL)){

                    //decompose variant context
                    VariantContextBuilder variantContextBuilder = new VariantContextBuilder(
                            "vc", variantContext.getContig(), variantContext.getStart(), variantContext.getEnd(), Arrays.asList(variantContext.getReference(), allele)
                    );

                    VariantContext decomposedVariantContext = GATKVariantContextUtils.trimAlleles(variantContextBuilder.make(), true, true);

                    String query = decomposedVariantContext.getContig() +
                            " " +
                            decomposedVariantContext.getStart() +
                            " " +
                            "ID" +
                            alleleNo +
                            " " +
                            decomposedVariantContext.getReference().getBaseString() +
                            " " +
                            decomposedVariantContext.getAlternateAlleles().get(0).getBaseString();

                    queries.add(query);
                    alleleNo++;
                }
            }
        }

        //query HGMD in chunks within the max concurrent query size
        n = 0;
        for (String query : queries){
            n++;

            if (n < MAX_CONCURRENT_QUERIES){
                stringBuilder.append(query);
                stringBuilder.append("\n");
            } else {
                stringBuilder.append(query);
                stringBuilder.append("\n");

                results = (results == null ? parseDocument(searchByVcf(stringBuilder.toString(), build)) :
                        ArrayUtils.addAll(results, parseDocument(searchByVcf(stringBuilder.toString(), build))));

                n = 0;
                stringBuilder = new StringBuilder();
            }

            results = (results == null ? parseDocument(searchByVcf(stringBuilder.toString(), build)) :
                    ArrayUtils.addAll(results, parseDocument(searchByVcf(stringBuilder.toString(), build))));
        }

        //annotate variant contexts with HGMD
        if (results != null) {

            alleleNo = 0;
            for (VariantContext variantContext : variantContexts){

                //save all contexts
                VariantContextBuilder variantContextBuilder = new VariantContextBuilder(variantContext);

                ArrayList<String> accessions = new ArrayList<>();
                ArrayList<String> classifications = new ArrayList<>();

                for (Allele allele : variantContext.getAlternateAlleles()){
                    if (!allele.isSymbolic() && allele.isNonReference() && !allele.equals(Allele.SPAN_DEL)){

                        //build hgmd arrays
                        if (results[alleleNo] != null){
                            accessions.add(results[alleleNo].getHgmdAccession());
                            classifications.add(results[alleleNo].getHgmdVariantClass().name());
                        } else {
                            accessions.add(null);
                            classifications.add(null);
                        }

                        alleleNo++;
                    }
                }

                //add HGMD annotation to vc
                variantContextBuilder.attribute("HGMDAccessions", accessions.toArray());
                variantContextBuilder.attribute("HGMDClassifications", classifications.toArray());

                //save annotated vc
                annotatedVariantContexts.add(variantContextBuilder.make());
            }

        } else {
            LOGGER.info("HGMD Pro had no annotations for the queries submitted.");
            return variantContexts;
        }

        return annotatedVariantContexts;
    }

    Document searchByVcf(String vcfLines, HGMDProGenomeBuild build) throws IOException {
        LOGGER.info("Connecting to HGMD with payload: " + vcfLines);
        return Jsoup.connect("https://portal.biobase-international.com/hgmd/pro/batsearch.php")
                .data("search4", build.name() + "VCF", "input", vcfLines, "DM", "Y", "DP", "Y")
                .cookie("sid", sessionId)
                .get();
    }

    static HGMDProBatchSearchResult[] parseDocument(Document document){
        HGMDProBatchSearchResult[] results = new HGMDProBatchSearchResult[document.select("tbody").get(1).select("tr").size()];

        //loop over annotation rows and map to obj
        for (Element row : document.select("tbody").get(1).select("tr")){

            //skip null records
            if (!row.select("td").get(3).text().contains("not found in HGMD using")){

                HGMDProBatchSearchResult hgmdProBatchSearchResult = new HGMDProBatchSearchResult(
                        Integer.parseInt(row.select("td").get(0).text()),
                        row.select("td").get(1).text(),
                        row.select("td").get(2).text(),
                        row.select("td").get(3).text(),
                        row.select("td").get(4).text(),
                        row.select("td").get(5).text(),
                        convertClassificationToEnum(row.select("td").get(6).text()),
                        row.select("td").get(7).text().equals("N/A") ? null : row.select("td").get(7).text(),
                        row.select("form").select("input").attr("value")
                );

                int id = Integer.parseInt(hgmdProBatchSearchResult.getSearchTerm().split(" ")[2].split("ID")[1]);
                results[id] = hgmdProBatchSearchResult;
            }

        }

        return results;
    }

    static HGMDProVariantClass convertClassificationToEnum(String classification){
        switch (classification){
            case "DM": return HGMDProVariantClass.DISEASE_CAUSING_MUTATION;
            case "DM?" : return HGMDProVariantClass.DISEASE_CAUSING_MUTATION_QUERY;
            case "DP": return HGMDProVariantClass.DISEASE_ASSOCIATED_POLYMORPHISM;
            default: throw new UnknownFormatConversionException("Cannot recognise: " + classification);
        }
    }

    public void setCookie(String username, String password) throws IOException {
        Connection.Response response = Jsoup
                .connect("https://portal.biobase-international.com/cgi-bin/portal/login.cgi")
                .data("login", username, "password", password)
                .method(Connection.Method.POST)
                .execute();
        sessionId = response.cookie("sid");
    }

    public boolean isCookieSet(){
        return sessionId != null;
    }

}
