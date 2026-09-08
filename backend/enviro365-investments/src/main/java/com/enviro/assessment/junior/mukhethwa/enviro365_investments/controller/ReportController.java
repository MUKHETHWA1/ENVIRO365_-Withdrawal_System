package com.enviro.assessment.junior.mukhethwa.enviro365_investments.controller;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.service.WithdrawalService;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final WithdrawalService withdrawalService;
    
    @GetMapping("/withdrawals/csv")
    public ResponseEntity<byte[]> exportWithdrawalsCSV(
            @RequestParam(required = false) Long investorId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        List<WithdrawalResponseDTO> withdrawals;
        
        if (investorId != null) {
            withdrawals = withdrawalService.getInvestorWithdrawals(investorId);
        } else if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            withdrawals = withdrawalService.getWithdrawalsByDateRange(start, end);
        } else {
            // If no filters, return all withdrawals (you might want to add a method for this)
            throw new RuntimeException("Please specify either investorId or date range");
        }
        
        String csv = generateCSV(withdrawals);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "withdrawals_report.csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.getBytes());
    }
    
    private String generateCSV(List<WithdrawalResponseDTO> withdrawals) {
        StringWriter sw = new StringWriter();
        CSVWriter writer = new CSVWriter(sw);
        
        // Header
        String[] header = {"ID", "Amount", "Date", "Status", "Rejection Reason", "Remaining Balance"};
        writer.writeNext(header);
        
        // Data
        for (WithdrawalResponseDTO withdrawal : withdrawals) {
            String[] row = {
                withdrawal.getId().toString(),
                withdrawal.getAmount().toString(),
                withdrawal.getWithdrawalDate().toString(),
                withdrawal.getStatus(),
                withdrawal.getRejectionReason() != null ? withdrawal.getRejectionReason() : "",
                withdrawal.getRemainingBalance().toString()
            };
            writer.writeNext(row);
        }
        
        return sw.toString();
    }
}
