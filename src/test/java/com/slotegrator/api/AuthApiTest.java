package com.slotegrator.api;

import com.slotegrator.api.client.AuthClient;
import com.slotegrator.api.config.ApiConfig;
import com.slotegrator.api.dto.CredentialsDTO;
import com.slotegrator.api.dto.TokenDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthApiTest {

    @Test
    void loginReturnsAccessToken() {
        ApiConfig config = new ApiConfig();
        AuthClient authClient = new AuthClient(
                config.baseUrl(),
                config.basicUsername(),
                config.basicPassword()
        );

        Response response = authClient.login(
                new CredentialsDTO(config.accountEmail(), config.accountPassword())
        );

        // Assignment/OpenAPI specify 200; the actual API returns 201 on successful login.
        assertThat(response.statusCode()).isEqualTo(201);

        TokenDTO token = response.as(TokenDTO.class);
        assertThat(token.accessToken()).isNotBlank();
    }
}
