package nhs.genetics.cardiff.framework.clinvar;

import com.fasterxml.jackson.databind.ObjectMapper;
import htsjdk.variant.variantcontext.VariantContext;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ml on 12/07/2017.
 */
public class Clinvar {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static Long[] getIdListFromGRCh37Coordinates(VariantContext variantContext) throws IOException {
        URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=clinvar&term=" + URLEncoder.encode(variantContext.getContig(), "UTF-8") + "[chr]+AND+" + URLEncoder.encode(String.valueOf(variantContext.getStart()), "UTF-8") + ":" + URLEncoder.encode(String.valueOf(variantContext.getEnd()), "UTF-8") + "[chrpos37]&retmode=json");
        return objectMapper.convertValue(objectMapper.readTree(url).get("esearchresult").get("idlist"), Long[].class);
    }

    public static ArrayList<ClinvarRecord> getClinvarRecordsFromIdList(Long[] identifiers) throws IOException {
        ArrayList<ClinvarRecord> clinvarRecords = new ArrayList<>();

        for (Long identifier : identifiers){
            URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=clinvar&id=" + URLEncoder.encode(identifier.toString(), "UTF-8") + "&retmode=json");
            clinvarRecords.add(objectMapper.convertValue(objectMapper.readTree(url).get("result").get(identifier.toString()), ClinvarRecord.class));
        }

        return clinvarRecords;
    }

}
