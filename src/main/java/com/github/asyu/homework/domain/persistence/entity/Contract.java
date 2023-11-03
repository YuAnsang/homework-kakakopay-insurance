package com.github.asyu.homework.domain.persistence.entity;

import com.github.asyu.homework.common.entity.BaseEntity;
import com.github.asyu.homework.domain.enums.ContractStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Contract extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private Integer durationInMonths;

  @Column(nullable = false)
  private BigDecimal totalPremium;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ContractStatus status;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @OneToMany(mappedBy = "contract")
  private List<ContractCoverage> contractCoverages = new ArrayList<>();

  public Contract(LocalDate startDate, LocalDate endDate, Integer durationInMonths, ContractStatus status) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.durationInMonths = durationInMonths;
    this.status = status;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public void addCoverages(List<Coverage> coverages) {
    for (Coverage coverage : coverages) {
      ContractCoverage contractCoverage = new ContractCoverage(this, coverage);
      this.contractCoverages.add(contractCoverage);
    }

    double sum = coverages.stream().mapToDouble(Coverage::getPremium).sum();
    BigDecimal totalPremium = new BigDecimal(this.durationInMonths * sum);
    this.totalPremium = totalPremium.setScale(2, RoundingMode.HALF_UP);
  }

}