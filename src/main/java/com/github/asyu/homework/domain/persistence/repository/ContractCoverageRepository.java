package com.github.asyu.homework.domain.persistence.repository;

import com.github.asyu.homework.domain.persistence.entity.ContractCoverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractCoverageRepository extends JpaRepository<ContractCoverage, Long> {

}
