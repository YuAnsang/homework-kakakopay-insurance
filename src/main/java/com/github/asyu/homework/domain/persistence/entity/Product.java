package com.github.asyu.homework.domain.persistence.entity;

import com.github.asyu.homework.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Entity
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer minDurationInMonths;

  @Column(nullable = false)
  private Integer maxDurationInMonths;

  @Column
  @OneToMany(mappedBy = "product")
  private List<Coverage> coverages = new ArrayList<>();

  @Column
  @OneToMany(mappedBy = "product")
  private List<Contract> contracts = new ArrayList<>();

  public boolean contains(List<Long> coverageIds) {
    Set<Long> savedIds = this.coverages.stream()
        .map(Coverage::getId)
        .collect(Collectors.toSet());
    return savedIds.containsAll(coverageIds);
  }

}
