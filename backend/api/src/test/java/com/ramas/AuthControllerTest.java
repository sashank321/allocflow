package com.ramas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramas.dto.AuthDtos.LoginRequest;
import com.ramas.dto.AuthDtos.RegisterRequest;
import com.ramas.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Admin can log in with seeded credentials and receive JWT token")
    void testAdminLogin() throws Exception {
        LoginRequest request = new LoginRequest("admin@allocflow.io", "Password123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("admin@allocflow.io"))
                .andExpect(jsonPath("$.user.role").value("SUPER_ADMIN"));
    }

    @Test
    @DisplayName("New user can register successfully")
    void testUserRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "new.author@oxford.ac.uk",
                "Password123!",
                "Dr. New Author",
                "Oxford University",
                UserRole.AUTHOR
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("new.author@oxford.ac.uk"))
                .andExpect(jsonPath("$.user.role").value("AUTHOR"));
    }
}
