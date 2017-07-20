package nhs.genetics.cardiff.framework.hgmd;

import nhs.genetics.cardiff.framework.GenomeVariant;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.logging.Logger;

/**
 * Web-scrape API for working with HGMD annotations
 */
public class HGMDClient {
    private static final Logger LOGGER = Logger.getLogger(HGMDClient.class.getName());
    private String sessionId;

    public ArrayList<HGMDBatchSearchResult> batchSearchByHg19Vcf(List<GenomeVariant> genomeVariantList) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        //loop over genome variants and build query list
        for (int i = 0; i < genomeVariantList.size(); ++i){
            stringBuilder.append(genomeVariantList.get(i).getContig());
            stringBuilder.append(" ");
            stringBuilder.append(genomeVariantList.get(i).getPos());
            stringBuilder.append(" ");
            stringBuilder.append("ID");
            stringBuilder.append(i + 1);
            stringBuilder.append(" ");
            stringBuilder.append(genomeVariantList.get(i).getRef());
            stringBuilder.append(" ");
            stringBuilder.append(genomeVariantList.get(i).getAlt());
            stringBuilder.append("\n");
        }

        //get hgmd document
        LOGGER.info("HGMD Payload: " + stringBuilder.toString());
        return parseDocument(Jsoup
                .connect("https://portal.biobase-international.com/hgmd/pro/batsearch.php")
                .data("search4", "hg19VCF", "input", stringBuilder.toString(), "DM", "Y", "DP", "Y")
                .cookie("sid", sessionId)
                .get());

    }

    public void setCookie(String username, String password) throws IOException {
        Connection.Response response = Jsoup
                .connect("https://portal.biobase-international.com/cgi-bin/portal/login.cgi")
                .data("login", username, "password", password)
                .method(Connection.Method.POST)
                .execute();
        sessionId = response.cookie("sid");
    }

    protected ArrayList<HGMDBatchSearchResult> parseDocument(Document document){
        ArrayList<HGMDBatchSearchResult> results = new ArrayList<>();

        //loop over annotation rows and map to obj
        for (Element row : document.select("tbody").get(1).select("tr")){
            results.add(
                    new HGMDBatchSearchResult(
                            Integer.parseInt(row.select("td").get(0).text()),
                            row.select("td").get(1).text(),
                            row.select("td").get(2).text(),
                            row.select("td").get(3).text(),
                            row.select("td").get(4).text(),
                            row.select("td").get(5).text(),
                            convertClassificationToEnum(row.select("td").get(6).text()),
                            row.select("td").get(7).text().equals("N/A") ? null : row.select("td").get(7).text(),
                            row.select("form").select("input").attr("value")
                    )
            );
        }

        return results;
    }

    protected HGMDVariantClass convertClassificationToEnum(String classification){
        switch (classification){
            case "DM": return HGMDVariantClass.DISEASE_CAUSING_MUTATION;
            case "DM?" : return HGMDVariantClass.DISEASE_CAUSING_MUTATION_QUERY;
            case "DP": return HGMDVariantClass.DISEASE_ASSOCIATED_POLYMORPHISM;
            default: throw new UnknownFormatConversionException("Cannot recognise: " + classification);
        }
    }

    public boolean isCookieSet(){
        return sessionId != null;
    }

}
