package com.enviro.assessment.junior.mukhethwa.enviro365_investments;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class TestDataFactory {
    
    public static Investor createInvestor(Long id, int age) {
        Investor investor = new Investor();
        investor.setId(id);
        investor.setFirstName("John");
        investor.setLastName("Doe");
        investor.setEmail("john.doe@example.com");
        investor.setDateOfBirth(LocalDate.now().minusYears(age));
        investor.setPortfolios(new ArrayList<>());
        return investor;
    }
    
    public static Portfolio createPortfolio(Long id, String type, BigDecimal balance, Investor investor) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(id);
        portfolio.setName("Test Portfolio");
        portfolio.setType(type);
        portfolio.setBalance(balance);
        portfolio.setInvestor(investor);
        portfolio.setProducts(new ArrayList<>());
        return portfolio;
    }
    
    public static Product createProduct(Long id, String name, BigDecimal value, Portfolio portfolio) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setType("EQUITY");
        product.setValue(value);
        product.setPortfolio(portfolio);
        return product;
    }
}