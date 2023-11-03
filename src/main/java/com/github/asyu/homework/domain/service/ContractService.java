package com.github.asyu.homework.domain.service;

import com.github.asyu.homework.common.exception.InvalidRequestException;
import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.dto.ContractDto.Detail;
import com.github.asyu.homework.domain.mapper.ContractMapper;
import com.github.asyu.homework.domain.persistence.dao.InsuranceDao;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.entity.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ContractService {

  private final InsuranceDao insuranceDao;

  @Transactional
  public ContractDto.Detail save(ContractDto.Post request) {
    // business validation
    Product product = this.insuranceDao.findProductById(request.productId());
    if (!product.contains(request.coverageIds())) {
      throw new InvalidRequestException("Coverage must be in the product");
    }

    if (request.durationInMonths() > product.getMaxDurationInMonths()) {
      throw new InvalidRequestException("DurationInMonths must be less than product's maxDurationInMonths");
    }

    if (request.durationInMonths() < product.getMinDurationInMonths()) {
      throw new InvalidRequestException("DurationInMonths must be greater than product's minDurationInMonths");
    }

    // map entity & save
    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(request.coverageIds());
    Contract contract = ContractMapper.postToEntity(request);
    contract.setProduct(product);
    contract.addCoverages(coverages);
    Contract saved = this.insuranceDao.saveContract(contract);

    return ContractMapper.entityToDetail(saved, coverages);
  }

  @Transactional(readOnly = true)
  public Detail getContract(Long id) {
    Contract contract = insuranceDao.findContractById(id);
    List<Long> coverageIds = contract.getContractCoverages().stream()
        .map(contractCoverage -> contractCoverage.getCoverage().getId())
        .toList();
    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(coverageIds);
    return ContractMapper.entityToDetail(contract, coverages);
  }

}
