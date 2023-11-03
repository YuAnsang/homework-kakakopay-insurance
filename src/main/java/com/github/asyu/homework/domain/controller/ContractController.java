package com.github.asyu.homework.domain.controller;

import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/apis/v1/contracts")
@RequiredArgsConstructor
@RestController
public class ContractController {

  private final ContractService service;

  @ResponseStatus(code = HttpStatus.CREATED)
  @PostMapping("")
  public ContractDto.Detail save(@RequestBody @Valid ContractDto.Post request) {

    return this.service.save(request);
  }

}
