package com.springbootboard._config;

import com.springbootboard.member.domain.Member;
import com.springbootboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String nickname : new String[]{"user1", "ueer2", "user3"}) {
            String email = nickname + "@mail.com";
            if (!memberRepository.existsByNicknameOrEmail(nickname, email)) {
                memberRepository.save(new Member(email, passwordEncoder.encode("1234"), nickname));
            }
        }
    }
}
