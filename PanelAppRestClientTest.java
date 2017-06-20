package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ml on 20/06/2017.
 */
public class PanelAppRestClientTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getPanelAppResult() throws Exception {
        String json = "{\"meta\":{\"numOfResults\":5},\"results\":[{\"ModeOfInheritance\":\"monoallelic\",\"EnsembleGeneIds\":[\"LRG_778\",\"ENSG00000166147\"],\"Evidences\":[\"Illumina TruGenome Clinical Sequencing Services\",\"Radboud University Medical Center, Nijmegen\",\"Emory Genetics Laboratory\",\"UKGTN\",\"Expert list\",\"Eligibility statement prior genetic testing\",\"Expert Review Green\"],\"Publications\":null,\"DiseaseGroup\":\"Cardiovascular disorders\",\"LevelOfConfidence\":\"HighEvidence\",\"SpecificDiseaseName\":\"Familial Thoracic Aortic Aneurysm Disease\",\"DiseaseSubGroup\":\"Connective tissue disorders and aortopathies\",\"ModeOfPathogenicity\":null,\"Phenotypes\":[\"Aortic aneurysm, ascending, and dissection \",\"ongenital contracturalarachnodactyly\",\"Marfan Syndrome \"],\"Penetrance\":\"Complete\",\"version\":\"1.33\",\"GeneSymbol\":\"FBN1\"},{\"ModeOfInheritance\":\"monoallelic_not_imprinted\",\"EnsembleGeneIds\":[\"LRG_778\",\"ENSG00000166147\"],\"Evidences\":[\"Emory Genetics Laboratory\",\"Expert list\",\"Expert Review Green\"],\"Publications\":null,\"DiseaseGroup\":\"Cardiovascular disorders\",\"LevelOfConfidence\":\"HighEvidence\",\"SpecificDiseaseName\":\"Ehlers-Danlos syndrome type 3\",\"DiseaseSubGroup\":\"Connective tissue disorders and aortopathies\",\"ModeOfPathogenicity\":null,\"Phenotypes\":[\"Marfan syndrome\"],\"Penetrance\":\"Complete\",\"version\":\"1.1\",\"GeneSymbol\":\"FBN1\"},{\"ModeOfInheritance\":\"monoallelic_not_imprinted\",\"EnsembleGeneIds\":[\"LRG_778\",\"ENSG00000166147\"],\"Evidences\":[\"\",\"Expert Review Green\"],\"Publications\":null,\"DiseaseGroup\":\"Skeletal disorders\",\"LevelOfConfidence\":\"HighEvidence\",\"SpecificDiseaseName\":\"Unexplained skeletal dysplasia\",\"DiseaseSubGroup\":\"Skeletal dysplasias\",\"ModeOfPathogenicity\":null,\"Phenotypes\":[\"Acromicric dysplasia 102370\",\"Geleophysic dysplasia 2 614185\",\"Marfan syndrome 154700\",\"Stiff skin syndrome 184900\",\"Weill-Marchesani syndrome 2, dominant 608328\"],\"Penetrance\":\"Complete\",\"version\":\"1.46\",\"GeneSymbol\":\"FBN1\"},{\"ModeOfInheritance\":\"monoallelic\",\"EnsembleGeneIds\":[\"LRG_778\",\"ENSG00000166147\"],\"Evidences\":[\"Expert list\",\"Expert Review Green\",\"Eligibility statement prior genetic testing\"],\"Publications\":[\"12598898\",\"15161620\",\"11786720\",\"1864149\",\"2595640\"],\"DiseaseGroup\":\"Respiratory disorders\",\"LevelOfConfidence\":\"HighEvidence\",\"SpecificDiseaseName\":\"Familial Pneumothorax\",\"DiseaseSubGroup\":\"Structural lung disorders\",\"ModeOfPathogenicity\":null,\"Phenotypes\":[\"Marfan syndrome\"],\"Penetrance\":\"Complete\",\"version\":\"1.1\",\"GeneSymbol\":\"FBN1\"},{\"ModeOfInheritance\":\"monoallelic_not_imprinted\",\"EnsembleGeneIds\":[\"LRG_778\",\"ENSG00000166147\"],\"Evidences\":[\"Emory Genetics Laboratory\",\"Expert list\",\"Expert Review Green\"],\"Publications\":null,\"DiseaseGroup\":\"Rheumatological disorders\",\"LevelOfConfidence\":\"HighEvidence\",\"SpecificDiseaseName\":\"Ehlers-Danlos syndromes\",\"DiseaseSubGroup\":\"Connective tissues disorders\",\"ModeOfPathogenicity\":null,\"Phenotypes\":[\"Marfan syndrome,154700\"],\"Penetrance\":\"Complete\",\"version\":\"0.398\",\"GeneSymbol\":\"FBN1\"}]}";
        System.out.println(json);
        PanelAppResponse panelAppResponse = objectMapper.readValue(json, PanelAppResponse.class);
        System.out.println(panelAppResponse.getMeta());
    }

}