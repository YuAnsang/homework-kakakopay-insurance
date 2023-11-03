package com.github.asyu.homework.domain.dto;

import com.github.asyu.homework.domain.enums.ContractStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContractDto() {

  public record Post(
      @FutureOrPresent(message = "Contract startDate must be equals or after today.") LocalDate startDate,
      @NotNull Integer durationInMonths,
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
      ContractStatus status
  ) {

  }

}
