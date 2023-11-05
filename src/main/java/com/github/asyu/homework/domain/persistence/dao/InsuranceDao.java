package com.github.asyu.homework.domain.persistence.dao;

import static com.github.asyu.homework.domain.persistence.entity.QContract.contract;

import com.github.asyu.homework.common.exception.EntityNotExistsException;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.entity.Product;
import com.github.asyu.homework.domain.persistence.repository.ContractCoverageRepository;
import com.github.asyu.homework.domain.persistence.repository.ContractRepository;
import com.github.asyu.homework.domain.persistence.repository.CoverageRepository;
import com.github.asyu.homework.domain.persistence.repository.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Repository
public class InsuranceDao {

  private final JPAQueryFactory queryFactory;

  private final ContractRepository contractRepository;

  private final ProductRepository productRepository;

  private final CoverageRepository coverageRepository;

  private final ContractCoverageRepository contractCoverageRepository;

  public Contract saveContract(Contract contract) {
    Contract saved = this.contractRepository.save(contract);
    this.contractCoverageRepository.saveAll(saved.getContractCoverages());
    return saved;
  }

  public Product findProductById(Long productId) {
    return this.productRepository.findById(productId)
        .orElseThrow(() -> new EntityNotExistsException("Not exists Product. id : " + productId));
  }

  public List<Coverage> findCoveragesByIdIn(List<Long> coverageIds) {
    return this.coverageRepository.findByIdIn(coverageIds);
  }

  public Contract findContractById(Long contractId) {
    return this.contractRepository.findById(contractId)
        .orElseThrow(() -> new EntityNotExistsException("Not exists Contract. id : " + contractId));
  }

  public List<Contract> findExpiresContractsInSevenDays() {
    return queryFactory
        .select(contract)
        .from(contract)
        .where(
            contract.isSentExpiresEmail.eq(false),
            contract.endDate.loe(LocalDate.now().plusDays(7))
        )
        .fetch();
  }

}
