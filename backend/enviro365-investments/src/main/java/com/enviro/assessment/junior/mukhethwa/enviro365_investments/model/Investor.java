package com.enviro.assessment.junior.mukhethwa.enviro365_investments.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL)
    private List<Portfolio> portfolios;
    
    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}
