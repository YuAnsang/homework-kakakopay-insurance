package com.github.asyu.homework.domain.mapper;

import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.enums.ContractStatus;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.entity.Product;
import java.time.LocalDate;
import java.util.List;

public class ContractMapper {

  public static Contract postToEntity(ContractDto.Post post) {
    LocalDate startDate = post.startDate() == null ? LocalDate.now() : post.startDate(); // 시작일이 없으면 오늘이 계약 시작을로 간주한다.
    Integer durationInMonths = post.durationInMonths();
    return new Contract(
        startDate,
        startDate.plusMonths(durationInMonths),
        durationInMonths,
        ContractStatus.NORMAL,
        false
    );
  }

  public static ContractDto.Detail entityToDetail(Contract contract, List<Coverage> coverages) {
    Product product = contract.getProduct();
    return new ContractDto.Detail(
        contract.getId(),
        contract.getStartDate(),
        contract.getEndDate(),
        contract.getDurationInMonths(),
        contract.getTotalPremium(),
        contract.getStatus(),
        ProductMapper.entityToDetail(product),
        coverages.stream().map(CoverageMapper::entityToDetail).toList()
    );
  }

}
