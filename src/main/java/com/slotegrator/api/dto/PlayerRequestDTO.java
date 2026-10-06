package com.slotegrator.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerRequestDTO(
        @JsonProperty("currency_code") String currencyCode,
        String email,
        String name,
        @JsonProperty("password_change") String passwordChange,
        @JsonProperty("password_repeat") String passwordRepeat,
        String surname,
        String username
) {
}
