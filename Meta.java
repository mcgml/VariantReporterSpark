package nhs.genetics.cardiff.framework.panelapp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ml on 20/06/2017.
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
