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
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
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
        "classpath:database/rentals/insert-rentals.sql",
        "classpath:database/payments/insert-payments.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String SESSION_ID = "cs_test_a1ATNr0UKM0uFxvpf1A3hgZMHSKxvLNCCdmSszbYE88z7"
            + "Dff8vctHLMuqT";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails(value = "admin@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
}
