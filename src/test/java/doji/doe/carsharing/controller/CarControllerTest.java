package doji.doe.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.service.car.CarService;
import doji.doe.carsharing.util.CarTestUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/cars/remove-cars.sql",
        "classpath:database/cars/insert-cars.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarService carService;
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
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Create a new car as admin")
    void createCar_ValidRequestDto_ReturnsCarDetailedResponseDto() throws Exception {
        // Given
        CarCreateRequestDto requestDto = CarTestUtil.getCarCreateRequestDto();
        CarDetailedResponseDto expected = CarTestUtil.getCarDetailedResponseDto(requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        CarDetailedResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarDetailedResponseDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("Get all cars as user with pagination")
    void getAll_ValidData_ReturnsListOfCarResponseDto() throws Exception {
        // Given
        List<CarResponseDto> expected = CarTestUtil.getListOfCarResponseDto();
        // When
        MvcResult result = mockMvc.perform(get("/cars")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CarResponseDto[] content = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarResponseDto[].class);
        List<CarResponseDto> actual = Arrays.stream(content).toList();
        assertEquals(2, content.length);
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(reflectionEquals(expected.get(i), actual.get(i)));
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Get car by valid id as admin")
    void getCarById_ValidId_ReturnsCarDetailedResponseDto() throws Exception {
        // Given
        Long carId = 1L;
        CarDetailedResponseDto expected = CarTestUtil
                .getCarDetailedResponseDto(CarTestUtil.getCar());
        // When
        MvcResult result = mockMvc.perform(get("/cars/{id}", carId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CarDetailedResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDetailedResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Update a car as admin")
    void updateCar_ValidRequestDto_ReturnsCarDetailedResponseDto() throws Exception {
        // Given
        Long carId = 1L;
        CarCreateRequestDto requestDto = CarTestUtil.getCarCreateRequestDto();
        CarDetailedResponseDto expected = CarTestUtil
                .getCarDetailedResponseDto(CarTestUtil.getCar());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(put("/cars/{id}", carId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CarDetailedResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDetailedResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Update car inventory as admin")
    void updateCarInventory_ValidRequestDto_ReturnsCarDetailedResponseDto() throws Exception {
        // Given
        Long carId = 1L;
        CarCreateRequestDto requestDto = CarTestUtil.getCarCreateRequestDto();
        CarDetailedResponseDto expected = CarTestUtil
                .getCarDetailedResponseDto(CarTestUtil.getCar());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(patch("/cars/{id}", carId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        CarDetailedResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDetailedResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Delete car as admin")
    void delete_ValidId_ShouldReturnNoContent() throws Exception {
        // Given
        Long carId = 1L;
        // When/Then
        mockMvc.perform(delete("/cars/{id}", carId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        //Then
        assertThrows(EntityNotFoundException.class,
                () -> carService.getById(carId));
    }

}
