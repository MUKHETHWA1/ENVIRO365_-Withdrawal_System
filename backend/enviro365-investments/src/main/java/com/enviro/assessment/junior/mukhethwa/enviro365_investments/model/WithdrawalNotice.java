package com.enviro.assessment.junior.mukhethwa.enviro365_investments.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDateTime withdrawalDate;
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED
    
    @Column(length = 500)
    private String rejectionReason;
    
    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;
    
    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    
    @PrePersist
    protected void onCreate() {
        withdrawalDate = LocalDateTime.now();
        status = "PENDING";
    }
}
