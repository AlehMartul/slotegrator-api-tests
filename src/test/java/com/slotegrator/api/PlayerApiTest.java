package com.slotegrator.api;

import com.slotegrator.api.client.AuthClient;
import com.slotegrator.api.client.PlayersClient;
import com.slotegrator.api.config.ApiConfig;
import com.slotegrator.api.dto.CredentialsDTO;
import com.slotegrator.api.dto.PlayerRequestDTO;
import com.slotegrator.api.dto.PlayerRequestOneDTO;
import com.slotegrator.api.dto.PlayerResponseDTO;
import com.slotegrator.api.dto.TokenDTO;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerApiTest {

    @Test
    void completePlayerLifecycle() {
        ApiConfig config = new ApiConfig();

        // 1. Login and get access token
        AuthClient authClient = new AuthClient(
                config.baseUrl(),
                config.basicUsername(),
                config.basicPassword()
        );

        Response loginResponse = authClient.login(
                new CredentialsDTO(
                        config.accountEmail(),
                        config.accountPassword()
                )
        );

        // Assignment/OpenAPI specify 200; the actual API returns 201.
        assertThat(loginResponse.statusCode()).isEqualTo(201);

        TokenDTO token = loginResponse.as(TokenDTO.class);
        assertThat(token.accessToken()).isNotBlank();

        PlayersClient playersClient = new PlayersClient(
                config.baseUrl(),
                token.accessToken()
        );

        List<PlayerRequestDTO> playerRequests = new ArrayList<>();
        List<PlayerResponseDTO> createdPlayers = new ArrayList<>();

        String runId = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        try {
            // 2. Create 12 players
            for (int i = 1; i <= 12; i++) {
                String number = String.format("%02d", i);

                PlayerRequestDTO playerRequest = new PlayerRequestDTO(
                        "EUR",
                        "test.player." + runId + "." + number + "@example.com",
                        "PlayerName" + number,
                        "Test1234",
                        "Test1234",
                        "Surname" + number,
                        "user" + runId + number
                );

                Response createResponse =
                        playersClient.createPlayer(playerRequest);

                assertThat(createResponse.statusCode()).isEqualTo(201);

                PlayerResponseDTO createdPlayer =
                        createResponse.as(PlayerResponseDTO.class);

                // Store the created player before assertions so it can
                // still be deleted if one of the validations below fails.
                createdPlayers.add(createdPlayer);

                assertThat(createdPlayer.id()).isNotBlank();
                assertThat(createdPlayer.email()).isEqualTo(playerRequest.email());
                assertThat(createdPlayer.username()).isEqualTo(playerRequest.username());
                assertThat(createdPlayer.name()).isEqualTo(playerRequest.name());
                assertThat(createdPlayer.surname()).isEqualTo(playerRequest.surname());
                assertThat(createdPlayer.currencyCode()).isEqualTo(playerRequest.currencyCode());

                playerRequests.add(playerRequest);
            }

            assertThat(createdPlayers).hasSize(12);

            // 3. Get profile of one created player
            PlayerRequestDTO playerToCheck = playerRequests.get(0);

            Response getOneResponse = playersClient.getOne(
                    new PlayerRequestOneDTO(playerToCheck.email())
            );

            // Assignment expects 200; OpenAPI and actual API return 201.
            assertThat(getOneResponse.statusCode()).isEqualTo(201);

            PlayerResponseDTO playerProfile =
                    getOneResponse.as(PlayerResponseDTO.class);

            assertThat(playerProfile.id()).isEqualTo(createdPlayers.get(0).id());
            assertThat(playerProfile.email()).isEqualTo(playerToCheck.email());
            assertThat(playerProfile.username()).isEqualTo(playerToCheck.username());
            assertThat(playerProfile.name()).isEqualTo(playerToCheck.name());
            assertThat(playerProfile.surname()).isEqualTo(playerToCheck.surname());
            assertThat(playerProfile.currencyCode()).isEqualTo(playerToCheck.currencyCode());

            // 4. Get all players and sort them by name
            Response getAllResponse = playersClient.getAll();

            assertThat(getAllResponse.statusCode()).isEqualTo(200);

            PlayerResponseDTO[] allPlayers =
                    getAllResponse.as(PlayerResponseDTO[].class);

            assertThat(allPlayers).hasSize(12);

            List<PlayerResponseDTO> sortedPlayers = Arrays.stream(allPlayers)
                    .sorted(Comparator.comparing(PlayerResponseDTO::name))
                    .toList();

            assertThat(sortedPlayers)
                    .extracting(PlayerResponseDTO::name)
                    .isSorted();

        } finally {
            // 5. Delete every player that was successfully created.
            // This cleanup runs even if an assertion above fails.
            for (PlayerResponseDTO player : createdPlayers) {
                Response deleteResponse =
                        playersClient.deleteOne(player.id());

                assertThat(deleteResponse.statusCode()).isEqualTo(200);
            }
        }

        // 6. Verify that no players remain
        Response finalGetAllResponse = playersClient.getAll();

        assertThat(finalGetAllResponse.statusCode()).isEqualTo(200);

        PlayerResponseDTO[] remainingPlayers =
                finalGetAllResponse.as(PlayerResponseDTO[].class);

        assertThat(remainingPlayers).isEmpty();
    }
}