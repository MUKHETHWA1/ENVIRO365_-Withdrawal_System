package com.enviro.assessment.junior.mukhethwa.enviro365_investments.controller;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WithdrawalController {
    
    private final WithdrawalService withdrawalService;
    
    @PostMapping("/create")
    public ResponseEntity<WithdrawalResponseDTO> createWithdrawal(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/investor/{investorId}")
    public ResponseEntity<List<WithdrawalResponseDTO>> getInvestorWithdrawals(@PathVariable Long investorId) {
        List<WithdrawalResponseDTO> withdrawals = withdrawalService.getInvestorWithdrawals(investorId);
        return ResponseEntity.ok(withdrawals);
    }
}
