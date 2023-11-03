package com.github.asyu.homework.domain.mapper;

import com.github.asyu.homework.domain.dto.ProductDto;
import com.github.asyu.homework.domain.persistence.entity.Product;

public class ProductMapper {

  public static ProductDto.Detail entityToDetail(Product product) {
    return new ProductDto.Detail(
        product.getId(),
        product.getName()
    );
  }

}
