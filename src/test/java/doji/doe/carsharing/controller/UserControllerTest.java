package doji.doe.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.dto.user.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.dto.user.UserUpdateRoleRequestDto;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
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
@Sql(scripts = {"classpath:database/users/remove-users.sql",
        "classpath:database/users/insert-users.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @DisplayName("Update user role as an admin")
    void updateRole_ValidRequestDto_Success() throws Exception {
        // Given
        Long userId = 2L;
        UserUpdateRoleRequestDto requestDto = new UserUpdateRoleRequestDto(User.Role.ROLE_MANAGER);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        mockMvc.perform(put("/users/{id}/role", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "dojidoe@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Get your profile info of authenticated user")
    void getProfileInfo_ValidUser_ReturnsUserResponseDto() throws Exception {
        // Given
        UserResponseDto expected = UserTestUtil.getUserResponseDto(UserTestUtil.getUser());
        // When
        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual));
    }

    @Test
    @WithUserDetails(value = "dojidoe@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("Update your profile info as authenticated user")
    void updateProfileIfo_ValidRequestDto_ReturnsUserResponseDto() throws Exception {
        // Given
        UserUpdateProfileInfoRequestDto requestDto = UserTestUtil
                .getUserUpdateProfileInfoRequestDto();
        UserResponseDto expected = UserTestUtil.getUserResponseDto(UserTestUtil.getUser());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual));
    }

}
