package doji.doe.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import doji.doe.carsharing.dto.payment.CreatePaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.dto.payment.PaymentStatusResponseDto;
import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.util.PaymentTestUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String CLEAN_UP_SCRIPT = "database/clean-up-data.sql";
    private static final String REMOVE_CARS_SCRIPT = "database/cars/remove-cars.sql";
    private static final String REMOVE_RENTALS_SCRIPT = "database/rentals/remove-rentals.sql";
    private static final String REMOVE_PAYMENTS_SCRIPT = "database/payments/remove-payments.sql";
    private static final String INSERT_USER_SCRIPT = "database/users/insert-users.sql";
    private static final String INSERT_CARS_SCRIPT = "database/cars/insert-cars.sql";
    private static final String INSERT_RENTALS_SCRIPT = "database/rentals/insert-rentals.sql";
    private static final String INSERT_PAYMENTS_SCRIPT = "database/payments/insert-payments.sql";
    private static final String SESSION_ID = "cs_test_a1ATNr0UKM0uFxvpf1A3hgZMHSKxvLNCCdmSszbYE88z7"
            + "Dff8vctHLMuqT";
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

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_CARS_SCRIPT));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_RENTALS_SCRIPT));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(INSERT_PAYMENTS_SCRIPT));
        }
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com")
    @DisplayName("Get all payments as user with pagination")
    void getAll_ValidId_ReturnsListOfPaymentDetailedResponseDto() throws Exception {
        // Given
        Long userId = 2L;
        List<PaymentDetailedResponseDto> expected = PaymentTestUtil
                .getListOfPaymentDetailedResponseDto(PaymentTestUtil.getListOfPayments());
        // When
        MvcResult result = mockMvc.perform(get("/payments/{id}", userId)
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        PaymentDetailedResponseDto[] content = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        PaymentDetailedResponseDto[].class);
        List<PaymentDetailedResponseDto> actual = Arrays.stream(content).toList();
        assertEquals(2, content.length);
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(reflectionEquals(expected.get(i), actual.get(i)));
        }
    }

    @Test
    @WithMockUser(username = "dojidoe@gmail.com", roles = "CUSTOMER")
    @DisplayName("Create a new payment session as user")
    void createPaymentSession_ValidCreatePaymentRequestDto_ReturnsPaymentResponseDto()
            throws Exception {
        // Given
        CreatePaymentRequestDto requestDto = PaymentTestUtil.getCreatePaymentRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        PaymentResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), PaymentResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getSessionId());
        assertFalse(actual.getSessionId().isEmpty());
        assertNotNull(actual.getSessionUrl());
        assertFalse(actual.getSessionUrl().isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Handle cancel payment")
    void handleCancel_ValidSessionId_ReturnsPaymentStatusResponseDto() throws Exception {
        // Given
        PaymentStatusResponseDto expected = new PaymentStatusResponseDto(SESSION_ID,
                Payment.Status.CANCEL);
        // When
        MvcResult result = mockMvc.perform(get("/payments/cancel")
                        .param("sessionId", SESSION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        PaymentStatusResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        PaymentStatusResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardownPaymentsAndRentalsAndCars(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardownPaymentsAndRentalsAndCars(DataSource dataSource) {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(REMOVE_PAYMENTS_SCRIPT)
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
