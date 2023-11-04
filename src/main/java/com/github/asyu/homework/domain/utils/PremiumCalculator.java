package com.github.asyu.homework.domain.utils;

import com.github.asyu.homework.domain.persistence.entity.Coverage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PremiumCalculator {

  private static final int SCALE = 2;

  public static BigDecimal calculateTotalPremium(List<Coverage> coverages, Integer durationInMonths) {
    double sum = coverages.stream().mapToDouble(Coverage::getPremium).sum();
    BigDecimal totalPremium = new BigDecimal(durationInMonths * sum);
    return totalPremium.setScale(SCALE, RoundingMode.DOWN);
  }

}
