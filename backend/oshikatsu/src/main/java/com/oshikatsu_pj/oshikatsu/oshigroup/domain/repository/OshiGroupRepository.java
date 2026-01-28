package com.oshikatsu_pj.oshikatsu.oshigroup.domain.repository;

import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OshiGroupRepository extends JpaRepository<OshiGroup, Long> {
    Optional<OshiGroup> findByUserId(Long userId);
    Optional<OshiGroup> findByGroupName(String groupName, Long userId);
    boolean existsByGroupName(String groupName, Long userId);
    Optional<OshiGroup> findByCompany(String company, Long userId);
    Optional<OshiGroup> findByGroupNameFuzzy(String groupName, Long userId);
}
