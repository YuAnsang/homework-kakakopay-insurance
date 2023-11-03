package com.github.asyu.homework.domain.dto;

public record ProductDto() {

  public record Detail(
      Long id,
      String name
  ) {

  }

}
