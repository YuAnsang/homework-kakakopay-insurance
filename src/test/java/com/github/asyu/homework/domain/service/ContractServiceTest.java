package com.github.asyu.homework.domain.service;

import static com.github.asyu.homework.common.SpringProfiles.TEST;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.asyu.homework.domain.enums.ContractStatus;
import com.github.asyu.homework.domain.persistence.dao.InsuranceDao;
import com.github.asyu.homework.domain.persistence.entity.Contract;
import com.github.asyu.homework.domain.persistence.entity.Coverage;
import com.github.asyu.homework.domain.persistence.entity.Product;
import com.github.asyu.homework.domain.persistence.repository.ContractRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

@SqlGroup(
    value = {
        @Sql(value = "/sql/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    }
)
@ActiveProfiles(TEST)
@SpringBootTest
class ContractServiceTest {

  @Autowired
  private ContractService contractService;

  @Autowired
  private InsuranceDao insuranceDao;

  @Autowired
  private ContractRepository contractRepository;

  @Test
  void send_expires_email_for_contracts_due_in_seven_days() {
    // Given
//    this.emailSender = mock(MockEmailSender.class);
    Product product = this.insuranceDao.findProductById(1L);
    List<Coverage> coverages = this.insuranceDao.findCoveragesByIdIn(List.of(1L));
    LocalDate endDate = LocalDate.now().plusDays(7);
    int contractDurationInMonths = 3;
    Contract contract = new Contract(
        endDate.minusMonths(contractDurationInMonths),
        endDate,
        contractDurationInMonths,
        ContractStatus.NORMAL,
        false
    );
    contract.setProduct(product);
    contract.addCoverages(coverages);
    Contract saved = this.insuranceDao.saveContract(contract);

    // When
    this.contractService.sendExpiresEmailForContractsDueInSevenDays();

    // Then
    Contract expectedSentEmail = contractRepository.findById(saved.getId()).orElseThrow();
    assertThat(expectedSentEmail.getIsSentExpiresEmail()).isTrue();
  }

}