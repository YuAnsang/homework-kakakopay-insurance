package com.github.asyu.homework.infra.email;

import java.util.List;

public interface IEmailSender {

  void send(String from, List<String> recipients, String subject, String contents);

}
