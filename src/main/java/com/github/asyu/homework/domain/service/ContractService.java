package com.github.asyu.homework.domain.service;

import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.dto.ContractDto.TotalPremiumCriteria;
import com.github.asyu.homework.domain.implement.ContractValidator;
import com.github.asyu.homework.domain.implement.EmailEventPublisher;
import com.github.asyu.homework.domain.mapper.ContractMapper;
import com.github.asyu.homework.domain.persistence.dao.InsuranceDao;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.entity.Product;
import com.github.asyu.homework.domain.utils.PremiumCalculator;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ContractService {

  private final InsuranceDao insuranceDao;

  private final ContractValidator validator;

  private final EmailEventPublisher eventPublisher;

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

//  @Scheduled(cron = "0 0 0 * * *")
  @Scheduled(fixedDelay = 30_000L)
  @Transactional
  public void sendExpiresEmailForContractsDueInSevenDays() {
    List<Contract> contracts = this.insuranceDao.findExpiresContractsInSevenDays();
    if (!contracts.isEmpty()) {
      // TODO 계약자(또는 피보험자) 정보 추출 (해당 과제에서는 계약자 및 피보험자 데이터를 관리하지 않기 때문에 임의 값을 설정함)
      List<String> recipients = contracts.stream()
          .map(contract -> String.format("%s번 계약 고객", contract.getId()))
          .toList();
      this.eventPublisher.publish(recipients);
      contracts.forEach(Contract::sendEmail); // 위 publish만으로는 실제로 이메일이 전송되었느냐는 보장되지 않지만, 여기서는 구분을 위해 이렇게 호출함.
    }
  }

}
