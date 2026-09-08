package com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByInvestorId(Long investorId);
}