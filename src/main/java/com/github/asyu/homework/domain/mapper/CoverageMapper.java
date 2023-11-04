package com.github.asyu.homework.domain.mapper;

import com.github.asyu.homework.domain.dto.CoverageDto;
import com.github.asyu.homework.domain.persistence.entity.Coverage;

public class CoverageMapper {

  public static CoverageDto.Detail entityToDetail(Coverage coverage) {
    return new CoverageDto.Detail(
        coverage.getId(),
        coverage.getName(),
        coverage.getInsuredAmount(),
        coverage.getBaseAmount()
    );
  }

}
