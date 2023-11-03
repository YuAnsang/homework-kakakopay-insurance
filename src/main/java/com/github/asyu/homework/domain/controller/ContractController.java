package com.github.asyu.homework.domain.controller;

import com.github.asyu.homework.domain.dto.ContractDto;
import com.github.asyu.homework.domain.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public ContractDto.Detail save(@RequestBody @Valid ContractDto.Post request) {
    return this.service.save(request);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ContractDto.Detail getContract(@PathVariable Long id) {
    return this.service.getContract(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ContractDto.Detail patchContract(@PathVariable Long id, @RequestBody @Valid ContractDto.Patch request) {
    return this.service.patch(id, request);
  }

}
