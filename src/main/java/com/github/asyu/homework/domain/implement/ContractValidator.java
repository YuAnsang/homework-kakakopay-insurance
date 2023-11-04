package com.github.asyu.homework.domain.implement;

import com.github.asyu.homework.common.exception.InvalidRequestException;
import com.github.asyu.homework.domain.persistence.dao.InsuranceDao;
import com.github.asyu.homework.domain.persistence.entity.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ContractValidator {

  private final InsuranceDao insuranceDao;

  public void validate(Long productId, List<Long> coverageIds, Integer contractDuration) {
    Product product = this.insuranceDao.findProductById(productId);
    if (!product.contains(coverageIds)) {
      throw new InvalidRequestException("Coverage must be in the product");
    }

    if (contractDuration > product.getMaxDurationInMonths()) {
      throw new InvalidRequestException("DurationInMonths must be less than product's maxDurationInMonths");
    }

    if (contractDuration < product.getMinDurationInMonths()) {
      throw new InvalidRequestException("DurationInMonths must be greater than product's minDurationInMonths");
    }
  }

}
