package com.github.asyu.homework.domain.persistence.entity;

import com.github.asyu.homework.common.entity.BaseEntity;
import com.github.asyu.homework.common.exception.InvalidRequestException;
import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.enums.ContractStatus;
import com.github.asyu.homework.domain.utils.PremiumCalculator;
import jakarta.persistence.CascadeType;
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

  @Column(nullable = false)
  private Boolean isSentExpiresEmail;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @OneToMany(mappedBy = "contract", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  private List<ContractCoverage> contractCoverages = new ArrayList<>();

  public Contract(LocalDate startDate, LocalDate endDate, Integer durationInMonths, ContractStatus status, Boolean isSentExpiresEmail) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.durationInMonths = durationInMonths;
    this.status = status;
    this.isSentExpiresEmail = isSentExpiresEmail;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public void addCoverages(List<Coverage> coverages) {
    for (Coverage coverage : coverages) {
      ContractCoverage contractCoverage = new ContractCoverage(this, coverage);
      this.contractCoverages.add(contractCoverage);
    }
    this.totalPremium = PremiumCalculator.calculateTotalPremium(coverages, this.durationInMonths);
  }

  public void patch(ContractDto.Patch request, List<Coverage> coverages) {
    if (this.status == ContractStatus.EXPIRED) {
      throw new InvalidRequestException("Expired contract cannot be modified.");
    }

    this.durationInMonths = request.durationInMonths();
    this.endDate = this.startDate.plusMonths(request.durationInMonths());
    this.status = request.status();
    this.contractCoverages.clear();
    this.addCoverages(coverages);
  }

  public void sendEmail() {
    this.isSentExpiresEmail = true;
  }

}
