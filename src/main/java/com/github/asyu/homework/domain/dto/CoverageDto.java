package com.github.asyu.homework.domain.dto;

public record CoverageDto() {

  public record Detail(
      Long id,
      String name,
      Integer insuredAmount,
      Integer baseAmount
  ) {

  }

}
