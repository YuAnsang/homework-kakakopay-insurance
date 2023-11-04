package com.github.asyu.homework.domain.persistence.repository;

import com.github.asyu.homework.domain.persistence.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

}
