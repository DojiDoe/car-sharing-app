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
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static final String CLEAN_UP_SCRIPT = "database/clean-up-data.sql";
    private static final String REMOVE_CARS_SCRIPT = "database/cars/remove-cars.sql";
    private static final String REMOVE_RENTALS_SCRIPT = "database/rentals/remove-rentals.sql";
    private static final String INSERT_CARS_CLASSPATH = "classpath:database/cars/insert-cars.sql";
    private static final String INSERT_RENTALS_CLASSPATH = "classpath:database/"
            + "rentals/insert-rentals.sql";
    private static final String INSERT_USER_SCRIPT = "database/users/insert-users.sql";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_USER_SCRIPT));
        }
    }

    @Test
    @Sql(scripts = {INSERT_CARS_CLASSPATH},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
    @Sql(scripts = {INSERT_CARS_CLASSPATH, INSERT_RENTALS_CLASSPATH},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
    @Sql(scripts = {INSERT_CARS_CLASSPATH, INSERT_RENTALS_CLASSPATH},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardownRentalsAndCars(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardownRentalsAndCars(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(REMOVE_CARS_SCRIPT)
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(REMOVE_RENTALS_SCRIPT)
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(CLEAN_UP_SCRIPT)
            );
        }
    }
}
