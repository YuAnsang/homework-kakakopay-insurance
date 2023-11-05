package com.github.asyu.homework.domain.implement;

import com.github.asyu.homework.domain.dto.EmailEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailEventPublisher {

  private final ApplicationEventPublisher publisher;

  public void publish(List<String> recipients) {
    EmailEvent emailEvent = new EmailEvent(
        "no_reply@kakaoinsurecorp.com",
        recipients,
        "계약 만료 예정 안내",
        "계약 만료에 대한 이메일 내용"
    );
    publisher.publishEvent(emailEvent);
  }

}
