package com.example.swishbddriver.ForApi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Element {
    @JsonProperty("distance")
    private Distance distance;
    @JsonProperty("duration")
    private Duration duration;
    @JsonProperty("status")
    private String status;

    @JsonProperty("distance")
    public Distance getDistance() {
        return distance;
    }

    @JsonProperty("duration")
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
}
