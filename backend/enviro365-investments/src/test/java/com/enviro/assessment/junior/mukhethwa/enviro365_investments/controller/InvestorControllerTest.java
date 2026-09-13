package com.enviro.assessment.junior.mukhethwa.enviro365_investments.controller;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.InvestorDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.service.InvestorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvestorController.class)
@DisplayName("InvestorController Tests")
class InvestorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    
    @MockitoBean
    private InvestorService investorService;

    @Test
    @DisplayName("GET /api/investors - returns list of investors")
    void getAllInvestors_returnsList() throws Exception {
        InvestorDTO john = new InvestorDTO(1L, "Atshilaho", "Magadani",
                "atshilaho@magadaniltd.com", LocalDate.of(1955, 6, 15), 71, "Atshilaho Magadani");
        InvestorDTO jane = new InvestorDTO(2L, "Thendo", "Magadani",
                "thendo@magadaniltd.com", LocalDate.of(1980, 3, 20), 46, "Thendo Magadani");
        
        when(investorService.getAllInvestors()).thenReturn(Arrays.asList(john, jane));

        mockMvc.perform(get("/api/investors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Atshilaho Magadani"))
                .andExpect(jsonPath("$[0].age").value(71))
                .andExpect(jsonPath("$[1].fullName").value("Thendo Magadani"));
    }

    @Test
    @DisplayName("GET /api/investors/{id} - returns investor by ID")
    void getInvestorById_returnsInvestor() throws Exception {
        InvestorDTO john = new InvestorDTO(1L, "Atshilaho", "Magadani",
                "atshilaho@magadaniltd.com", LocalDate.of(1955, 6, 15), 71, "Atshilaho Magadani");

        when(investorService.getInvestorById(1L)).thenReturn(john);

        mockMvc.perform(get("/api/investors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("atshilaho@magadaniltd.com"));
    }
}