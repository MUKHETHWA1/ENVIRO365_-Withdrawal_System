package com.enviro.assessment.junior.mukhethwa.enviro365_investments.service;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.TestDataFactory;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.InvestorDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.InvestorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvestorService Tests")
class InvestorServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @InjectMocks
    private InvestorService investorService;

    @Test
    @DisplayName("Should return all investors with computed age")
    void shouldReturnAllInvestors() {
        // Arrange
        Investor john = TestDataFactory.createInvestor(1L, 71);
        Investor jane = TestDataFactory.createInvestor(2L, 46);
        when(investorRepository.findAll()).thenReturn(Arrays.asList(john, jane));

        // Act
        List<InvestorDTO> result = investorService.getAllInvestors();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAge()).isEqualTo(71);
        assertThat(result.get(0).getFullName()).isEqualTo("John Doe");
        assertThat(result.get(1).getAge()).isEqualTo(46);
    }

    @Test
    @DisplayName("Should return empty list when no investors exist")
    void shouldReturnEmptyList() {
        when(investorRepository.findAll()).thenReturn(Collections.emptyList());

        List<InvestorDTO> result = investorService.getAllInvestors();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return investor by ID")
    void shouldReturnInvestorById() {
        Investor john = TestDataFactory.createInvestor(1L, 71);
        when(investorRepository.findById(1L)).thenReturn(Optional.of(john));

        InvestorDTO result = investorService.getInvestorById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getAge()).isEqualTo(71);
    }

    @Test
    @DisplayName("Should throw when investor not found by ID")
    void shouldThrowWhenInvestorNotFound() {
        when(investorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investorService.getInvestorById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Investor not found");
    }
}