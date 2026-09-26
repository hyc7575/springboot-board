package com.springbootboard.member.repository;

import com.springbootboard.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    boolean existsByNicknameOrEmail(String nickname, String email);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
}
