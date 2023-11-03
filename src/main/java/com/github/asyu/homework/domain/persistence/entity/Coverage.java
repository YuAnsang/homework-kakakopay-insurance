package com.github.asyu.homework.domain.persistence.entity;

import com.github.asyu.homework.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Coverage extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer insuredAmount;

  @Column(nullable = false)
  private Integer baseAmount;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  public Double getPremium() {
    return (double) (this.insuredAmount / this.baseAmount);
  }

}
