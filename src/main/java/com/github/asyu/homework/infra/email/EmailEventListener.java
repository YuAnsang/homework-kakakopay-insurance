package com.github.asyu.homework.infra.email;

import com.github.asyu.homework.domain.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailEventListener {

  private final IEmailSender emailSender;

  @EventListener
  public void subscribeQueryEvent(EmailEvent event) {
    this.emailSender.send(event.from(), event.recipients(), event.subject(), event.contents());
  }

}
