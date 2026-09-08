package com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {
    List<WithdrawalNotice> findByInvestorId(Long investorId);
    List<WithdrawalNotice> findByWithdrawalDateBetween(LocalDateTime start, LocalDateTime end);
    List<WithdrawalNotice> findByInvestorIdAndStatus(Long investorId, String status);
}
