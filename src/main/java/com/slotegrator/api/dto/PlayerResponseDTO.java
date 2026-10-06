package com.slotegrator.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlayerResponseDTO(
        @JsonAlias({"_id", "id"}) String id,
        @JsonProperty("currency_code") String currencyCode,
        String email,
        String name,
        String surname,
        String username
) {
}