package com.enviro.assessment.junior.mukhethwa.enviro365_investments.service;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.InvestorDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.InvestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestorService {
    
    private final InvestorRepository investorRepository;
    
    @Transactional(readOnly = true)
    public List<InvestorDTO> getAllInvestors() {
        return investorRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public InvestorDTO getInvestorById(Long id) {
        Investor investor = investorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Investor not found"));
        return convertToDTO(investor);
    }
    
    private InvestorDTO convertToDTO(Investor investor) {
        InvestorDTO dto = new InvestorDTO();
        dto.setId(investor.getId());
        dto.setFirstName(investor.getFirstName());
        dto.setLastName(investor.getLastName());
        dto.setEmail(investor.getEmail());
        dto.setDateOfBirth(investor.getDateOfBirth());
        dto.setAge(investor.getAge());
        dto.setFullName(investor.getFirstName() + " " + investor.getLastName());
        return dto;
    }
}