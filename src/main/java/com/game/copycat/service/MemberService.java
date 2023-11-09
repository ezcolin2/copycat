package com.game.copycat.service;

import com.game.copycat.domain.JoinRequest;
import com.game.copycat.domain.LoginRequest;
import com.game.copycat.domain.Member;
import com.game.copycat.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    public boolean join(JoinRequest joinRequest, BindingResult bindingResult) {
        Optional<Member> findMember = memberRepository.findByMemberId(joinRequest.getMemberId());
        // 중복 아이디가 존재한다면 BindingResult에 오류 담아서 보내기
        if (findMember.isPresent()) {
            bindingResult.rejectValue("memberId", "duplication", "아이디 생성에 실패했습니다. 같은 아이디가 존재합니다.");
            return false;
        }
        Member member = Member.builder()
                .memberId(joinRequest.getMemberId())
                .password(encoder.encode(joinRequest.getPassword()))
                .nickname(joinRequest.getNickname()).build();
        memberRepository.save(member);
        return true;
    }

    public boolean login(LoginRequest joinRequest, BindingResult bindingResult) {
        Optional<Member> findMember = memberRepository.findByMemberId(joinRequest.getMemberId());
        // 해당 아이디가 존재하지 않는다면 BindingResult에 오류 담아서 보내기
        if (findMember.isEmpty()) {
            bindingResult.rejectValue("id","notfound", "해당 아이디가 존재하지 않습니다.");
            return false;
        }
        // 비밀번호가 같다면 true 반환
        if (findMember.get().getPassword().equals(encoder.encode(joinRequest.getPassword()))) {
            return true;
        }
    return true;

    }
}
