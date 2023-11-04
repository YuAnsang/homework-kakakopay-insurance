package com.github.asyu.homework.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  COMMON("COMMON-001", "요청 처리에 실패하였습니다."),

  INVALID_PARAMETER("VALID-001", "잘못된 요청 값입니다. 파라미터를 확인해주세요."),
  INVALID_PARAMETER_BUSINESS("VALID-002", "잘못된 요청 값입니다. 파라미터를 확인해주세요."),

  NOT_FOUND("NOT_FOUND", "대상이 존재하지 않습니다.")
  ;

  private final String code;

  private final String message;

}
