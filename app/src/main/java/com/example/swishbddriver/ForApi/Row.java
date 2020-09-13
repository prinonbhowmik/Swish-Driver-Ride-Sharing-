package com.example.swishbddriver.ForApi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Row {
    @JsonProperty("elements")
  private List<Element> elements = null;

    @JsonProperty("elements")
    public List<Element> getElements() {
        return elements;
    }

}
