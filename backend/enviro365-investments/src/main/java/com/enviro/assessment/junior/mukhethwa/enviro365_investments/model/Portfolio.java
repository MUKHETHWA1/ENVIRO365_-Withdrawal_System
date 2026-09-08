package com.enviro.assessment.junior.mukhethwa.enviro365_investments.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type; // RETIREMENT, TAXABLE, etc.
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @ManyToOne
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;
    
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    private List<Product> products;
}
