package com.github.asyu.homework.domain.dto;

import com.github.asyu.homework.domain.enums.ContractStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContractDto() {

  public record Post(
      @FutureOrPresent(message = "Contract startDate must be equals or after today.") LocalDate startDate,
      @NotNull @Positive Integer durationInMonths,
      @NotNull Long productId,
      @NotEmpty List<Long> coverageIds
  ) {

  }

  public record Detail(
      Long id,
      LocalDate startDate,
      LocalDate endDate,
      Integer durationInMonths,
      BigDecimal totalPremium,
      ContractStatus status,
      ProductDto.Detail product,
      List<CoverageDto.Detail> coverages
  ) {

  }

  public record Patch(
      @NotNull @Positive Integer durationInMonths,
      @NotEmpty List<Long> coverageIds,
      @NotNull ContractStatus status
  ) {

  }

  public record TotalPremiumCriteria(
      @NotNull @Positive Integer durationInMonths,
      @NotNull Long productId,
      @NotEmpty List<Long> coverageIds
  ) {

  }

  public record TotalPremiumDetail(
      BigDecimal totalPremium
  ) {

  }

}
