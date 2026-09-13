package com.enviro.assessment.junior.mukhethwa.enviro365_investments.controller;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.InvestorDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.service.InvestorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvestorController {
    
    private final InvestorService investorService;
    
    @GetMapping
    public ResponseEntity<List<InvestorDTO>> getAllInvestors() {
        return ResponseEntity.ok(investorService.getAllInvestors());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvestorDTO> getInvestorById(@PathVariable Long id) {
        return ResponseEntity.ok(investorService.getInvestorById(id));
    }
}