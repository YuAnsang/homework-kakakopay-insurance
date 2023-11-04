package com.github.asyu.homework.domain.service;

import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.dto.ContractDto.TotalPremiumCriteria;
import com.github.asyu.homework.domain.implement.ContractValidator;
import com.github.asyu.homework.domain.mapper.ContractMapper;
import com.github.asyu.homework.domain.persistence.dao.InsuranceDao;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.entity.Product;
import com.github.asyu.homework.domain.utils.PremiumCalculator;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ContractService {

  private final InsuranceDao insuranceDao;

  private final ContractValidator validator;

  @Transactional
  public ContractDto.Detail save(ContractDto.Post request) {
    // business validation
    this.validator.validate(request.productId(), request.coverageIds(), request.durationInMonths());
    Product product = this.insuranceDao.findProductById(request.productId()); // TODO 맘에 안듦. 고민

    // map entity & save
    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(request.coverageIds());
    Contract contract = ContractMapper.postToEntity(request);
    contract.setProduct(product);
    contract.addCoverages(coverages);
    Contract saved = this.insuranceDao.saveContract(contract);

    return ContractMapper.entityToDetail(saved, coverages);
  }

  @Transactional(readOnly = true)
  public ContractDto.Detail getContract(Long id) {
    Contract contract = this.insuranceDao.findContractById(id);
    List<Long> coverageIds = contract.getContractCoverages().stream()
        .map(contractCoverage -> contractCoverage.getCoverage().getId())
        .toList();
    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(coverageIds);
    return ContractMapper.entityToDetail(contract, coverages);
  }

  @Transactional
  public ContractDto.Detail patch(Long id, ContractDto.Patch request) {
    Contract contract = this.insuranceDao.findContractById(id);
    Product product = contract.getProduct();
    this.validator.validate(product.getId(), request.coverageIds(), request.durationInMonths());

    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(request.coverageIds());
    contract.patch(request, coverages);

    return ContractMapper.entityToDetail(contract, coverages);
  }

  @Transactional(readOnly = true)
  public ContractDto.TotalPremiumDetail getExpectedTotalPremium(TotalPremiumCriteria criteria) {
    this.validator.validate(criteria.productId(), criteria.coverageIds(), criteria.durationInMonths());

    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(criteria.coverageIds());
    BigDecimal totalPremium = PremiumCalculator.calculateTotalPremium(coverages, criteria.durationInMonths());

    return new ContractDto.TotalPremiumDetail(totalPremium);
  }

}
