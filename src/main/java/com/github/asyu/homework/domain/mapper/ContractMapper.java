package com.github.asyu.homework.domain.mapper;

import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.enums.ContractStatus;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import java.time.LocalDate;

public class ContractMapper {

  public static Contract postToEntity(ContractDto.Post post) {
    LocalDate startDate = post.startDate() == null ? LocalDate.now() : post.startDate(); // 시작일이 없으면 오늘이 계약 시작을로 간주한다.
    Integer durationInMonths = post.durationInMonths();
    return new Contract(
        startDate,
        startDate.plusMonths(durationInMonths),
        durationInMonths,
        ContractStatus.NORMAL
    );
  }

  public static ContractDto.Detail entityToDetail(Contract contract) {
    return new ContractDto.Detail(
        contract.getId(),
        contract.getStartDate(),
        contract.getEndDate(),
        contract.getDurationInMonths(),
        contract.getTotalPremium(),
        contract.getStatus()
    );
  }

}