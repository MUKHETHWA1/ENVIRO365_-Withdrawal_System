package com.enviro.assessment.junior.mukhethwa.enviro365_investments.controller;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.PortfolioDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    @GetMapping("/investor/{investorId}")
    public ResponseEntity<List<PortfolioDTO>> getInvestorPortfolios(@PathVariable Long investorId) {
        List<PortfolioDTO> portfolios = portfolioService.getInvestorPortfolios(investorId);
        return ResponseEntity.ok(portfolios);
    }
    
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long portfolioId) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }
}
