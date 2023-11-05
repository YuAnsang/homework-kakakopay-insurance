package com.github.asyu.homework.domain.dto;

import java.util.List;

public record EmailEvent(
    String from,
    List<String> recipients,
    String subject,
    String contents
) {

}
