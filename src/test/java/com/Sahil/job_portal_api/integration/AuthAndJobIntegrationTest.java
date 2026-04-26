package com.Sahil.job_portal_api.integration;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class AuthAndJobIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void registerLoginAndCreateJobAsEmployer() throws Exception {
        String registerJson = """
                {
                  "name": "Acme Recruiter",
                  "email": "recruiter@example.com",
                  "password": "password123",
                  "role": "EMPLOYER"
                }
                """;

        String registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(registerResponse);

        String jobJson = """
                {
                  "title": "Backend Engineer",
                  "description": "Build secure APIs with Spring Boot",
                  "company": "Acme",
                  "location": "Pune",
                  "employmentType": "FULL_TIME",
                  "minSalary": 800000,
                  "maxSalary": 1600000
                }
                """;

        mockMvc.perform(post("/jobs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Backend Engineer"));

        mockMvc.perform(get("/jobs/search")
                        .param("keyword", "Spring")
                        .param("location", "Pune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].company").value("Acme"));
    }

    @Test
    void anonymousUserCannotCreateJob() throws Exception {
        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private String extractToken(String json) {
        String marker = "\"token\":\"";
        int start = json.indexOf(marker) + marker.length();
        int end = json.indexOf('"', start);
        return json.substring(start, end);
    }
}
