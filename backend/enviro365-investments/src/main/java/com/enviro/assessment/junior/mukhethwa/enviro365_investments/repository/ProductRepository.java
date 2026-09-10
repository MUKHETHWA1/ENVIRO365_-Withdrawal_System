// src/main/java/com/enviro/assessment/junior/mukhethwa/enviro365_investments/repository/ProductRepository.java
package com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}