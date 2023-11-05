package com.github.asyu.homework.infra.email;

import static com.github.asyu.homework.common.SpringProfiles.DEFAULT;
import static com.github.asyu.homework.common.SpringProfiles.TEST;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({TEST, DEFAULT})
@RequiredArgsConstructor
@Component
public class MockEmailSender implements IEmailSender {

  @Override
  public void send(String from, List<String> recipients, String subject, String contents) {
    log.info("""
            from : {},
            tos : {},
            subject : {},
            contents : {}
            """,
        from, String.join(", ", recipients), subject, contents
    );
  }


}
