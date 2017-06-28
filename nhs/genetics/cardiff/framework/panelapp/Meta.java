package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for PanelApp object
 */
public class Meta {

    @JsonProperty("numOfResults")
    private Integer numOfResults;

    public Integer getNumOfResults() {
        return numOfResults;
    }

    @Override
    public String toString(){
        return "numOfResults:" + numOfResults;
    }

}
