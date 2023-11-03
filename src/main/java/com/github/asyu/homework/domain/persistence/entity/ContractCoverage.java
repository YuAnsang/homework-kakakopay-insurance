package com.github.asyu.homework.domain.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ContractCoverage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "contract_id")
  private Contract contract;

  @ManyToOne
  @JoinColumn(name = "coverage_id")
  private Coverage coverage;

  public ContractCoverage(Contract contract, Coverage coverage) {
    this.contract = contract;
    this.coverage = coverage;
  }
}
