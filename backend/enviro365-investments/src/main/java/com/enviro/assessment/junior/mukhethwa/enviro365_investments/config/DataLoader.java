// src/main/java/com/enviro/assessment/junior/mukhethwa/enviro365_investments/config/DataLoader.java
package com.enviro.assessment.junior.mukhethwa.enviro365_investments.config;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Product;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.InvestorRepository;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.PortfolioRepository;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    
    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public void run(String... args) {
        // Skip if data already exists
        if (investorRepository.count() > 0) {
            return;
        }
        
        // ===== Create Investors =====
        Investor investor1 = new Investor();
        investor1.setFirstName("Mukhethwa");
        investor1.setLastName("Magadani");
        investor1.setEmail("mukhethwa.magadani@magadaniltd.com");
        investor1.setDateOfBirth(LocalDate.of(1955, 6, 15)); // Age > 65
        
        Investor investor2 = new Investor();
        investor2.setFirstName("Atshilaho");
        investor2.setLastName("Magadani");
        investor2.setEmail("atshilaho.magadani@magadaniltd.com");
        investor2.setDateOfBirth(LocalDate.of(1980, 3, 20)); // Age < 65
        
        investorRepository.saveAll(Arrays.asList(investor1, investor2));
        
        // ===== Create Portfolios =====
        Portfolio retirementPortfolio = new Portfolio();
        retirementPortfolio.setName("Retirement Fund");
        retirementPortfolio.setType("RETIREMENT");
        retirementPortfolio.setBalance(new BigDecimal("150000.00"));
        retirementPortfolio.setInvestor(investor1);
        retirementPortfolio.setProducts(new ArrayList<>()); //  Mutable list
        
        Portfolio taxablePortfolio = new Portfolio();
        taxablePortfolio.setName("Taxable Account");
        taxablePortfolio.setType("TAXABLE");
        taxablePortfolio.setBalance(new BigDecimal("75000.00"));
        taxablePortfolio.setInvestor(investor1);
        taxablePortfolio.setProducts(new ArrayList<>()); //  Mutable list
        
        Portfolio growthPortfolio = new Portfolio();
        growthPortfolio.setName("Growth Portfolio");
        growthPortfolio.setType("TAXABLE");
        growthPortfolio.setBalance(new BigDecimal("200000.00"));
        growthPortfolio.setInvestor(investor2);
        growthPortfolio.setProducts(new ArrayList<>()); //  Mutable list
        
        portfolioRepository.saveAll(Arrays.asList(
            retirementPortfolio, taxablePortfolio, growthPortfolio
        ));
        
        // ===== Create Products =====
        Product product1 = new Product();
        product1.setName("S&P 500 ETF");
        product1.setType("EQUITY");
        product1.setValue(new BigDecimal("75000.00"));
        product1.setPortfolio(retirementPortfolio);
        
        Product product2 = new Product();
        product2.setName("Bond Fund");
        product2.setType("FIXED_INCOME");
        product2.setValue(new BigDecimal("45000.00"));
        product2.setPortfolio(retirementPortfolio);
        
        Product product3 = new Product();
        product3.setName("Tech Stock");
        product3.setType("EQUITY");
        product3.setValue(new BigDecimal("30000.00"));
        product3.setPortfolio(taxablePortfolio);
        
        Product product4 = new Product();
        product4.setName("Index Fund");
        product4.setType("EQUITY");
        product4.setValue(new BigDecimal("100000.00"));
        product4.setPortfolio(growthPortfolio);
        
        Product product5 = new Product();
        product5.setName("Corporate Bonds");
        product5.setType("FIXED_INCOME");
        product5.setValue(new BigDecimal("50000.00"));
        product5.setPortfolio(growthPortfolio);
        
        // Save products separately and add to portfolios
        productRepository.saveAll(Arrays.asList(
            product1, product2, product3, product4, product5
        ));
        
        // Add products to portfolios (using ArrayList for mutability)
        retirementPortfolio.getProducts().add(product1);
        retirementPortfolio.getProducts().add(product2);
        
        taxablePortfolio.getProducts().add(product3);
        
        growthPortfolio.getProducts().add(product4);
        growthPortfolio.getProducts().add(product5);
        
        // Save portfolios with products
        portfolioRepository.saveAll(Arrays.asList(
            retirementPortfolio, taxablePortfolio, growthPortfolio
        ));
        
        System.out.println(" Sample data loaded successfully!");
        System.out.println("   Investors: " + investorRepository.count());
        System.out.println("   Portfolios: " + portfolioRepository.count());
        System.out.println("   Products: " + productRepository.count());
    }
}