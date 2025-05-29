package doji.doe.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.dto.rental.RentalReturnRequestDto;
import doji.doe.carsharing.util.RentalTestUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/clean-up-data.sql",
        "classpath:database/users/insert-users.sql",
        "classpath:database/cars/insert-cars.sql",
        "classpath:database/rentals/insert-rentals.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails(value = "dojidoe@gmail.com")
    @DisplayName("Create a new rental as user")
    void createRental_ValidRequestDto_ReturnsRentalDetailedResponseDto() throws Exception {
        // Given
        RentalCreateRequestDto requestDto = RentalTestUtil.getRentalCreateRequestDto();
        RentalDetailedResponseDto expected = RentalTestUtil
                .getRentalDetailedResponseDto(RentalTestUtil.getRental());
        expected.setCarResponseDto(expected.getCarResponseDto()
                .setInventory(expected.getCarResponseDto().getInventory() - 1));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        RentalDetailedResponseDto actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(),
                RentalDetailedResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "userResponseDto", "id"));
        assertTrue(reflectionEquals(expected.getUserResponseDto(), actual.getUserResponseDto()));
    }

    @Test
    @WithUserDetails(value = "dojidoe@gmail.com")
    @DisplayName("Get category by valid id as user")
    void getRentalById_ValidId_ReturnsRentalDetailedResponseDto() throws Exception {
        // Given
        Long rentalId = 1L;
        RentalDetailedResponseDto expected = RentalTestUtil
                .getRentalDetailedResponseDto(RentalTestUtil.getRental());
        // When
        MvcResult result = mockMvc.perform(get("/rentals/{id}", rentalId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        RentalDetailedResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalDetailedResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "userResponseDto"));
        assertTrue(reflectionEquals(expected.getUserResponseDto(), actual.getUserResponseDto()));
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    @DisplayName("Return rental as user")
    void returnRental_ValidRequestDto_ReturnsRentalDetailedResponseDto() throws Exception {
        // Given
        Long rentalId = 1L;
        RentalReturnRequestDto requestDto
                = new RentalReturnRequestDto(LocalDate.parse("2025-05-28",
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        RentalDetailedResponseDto expected = RentalTestUtil
                .getRentalDetailedResponseDto(RentalTestUtil.getRental());
        expected.setActualReturnDate(requestDto.actualReturnDate());
        expected.setCarResponseDto(expected.getCarResponseDto()
                .setInventory(expected.getCarResponseDto().getInventory() + 1));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(post("/rentals/{id}/return", rentalId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        RentalDetailedResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        RentalDetailedResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "userResponseDto"));
        assertTrue(reflectionEquals(expected.getUserResponseDto(), actual.getUserResponseDto()));
    }
}
