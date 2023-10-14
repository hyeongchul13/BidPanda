package com.panda.back.domain.member.service;

import com.panda.back.domain.member.dto.ProfileRequestDto;
import com.panda.back.domain.member.dto.ProfileResponseDto;
import com.panda.back.domain.member.dto.SignupRequestDto;
import com.panda.back.domain.member.entity.Member;
import com.panda.back.domain.member.repository.MemberRepository;
import com.panda.back.global.S3.S3Uploader;
import com.panda.back.global.dto.BaseResponse;
import com.panda.back.global.exception.CustomException;
import com.panda.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    public BaseResponse membernameExists(String membername) {
        if (memberRepository.findByMembername(membername).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_MEMBERNAME);
        }
        return BaseResponse.successMessage("중복 체크 완료");
    }

    public BaseResponse nicknameExists(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        return BaseResponse.successMessage("중복 체크 완료");
    }

    public ResponseEntity<BaseResponse<String>> signup(SignupRequestDto requestDto, BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError e : fieldErrors) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(BaseResponse.error(e.getDefaultMessage()));
        }

        String membername = requestDto.getMembername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        Optional<Member> checkUsername = memberRepository.findByMembername(membername);
        if (checkUsername.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBERNAME);
        }

        String email = requestDto.getEmail();
        Optional<Member> checkEmail = memberRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String nickname = requestDto.getNickname();
        Optional<Member> checkNickname = memberRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = new Member(membername, password, email, nickname);
        memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.successMessage("회원가입이 완료되었습니다."));
    }

    @Transactional(readOnly = true)
    public BaseResponse<ProfileResponseDto> getProfile(String membername) {
        Optional<Member> member = memberRepository.findByMembername(membername);
        ProfileResponseDto profile = new ProfileResponseDto(member);
        return BaseResponse.successData(profile);
    }

    @Transactional
    public BaseResponse updateProfile(ProfileRequestDto requestDto, Member member) {
        try {
            // 현재 로그인한 사용자의 정보를 가져옴
            Member myprofile = findByMembername(member.getMembername());

            // 입력한 비밀번호를 BCryptPasswordEncoder를 사용하여 검사
            if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
                throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
            }

            myprofile.setNickname(requestDto.getNickname());

            String newPassword = requestDto.getNewPassword();
            if (newPassword != null && !newPassword.isEmpty()) {
                myprofile.setPassword(passwordEncoder.encode(newPassword));
            }

            myprofile.setIntro(requestDto.getIntro());

            memberRepository.save(myprofile);

            return BaseResponse.successMessage("회원정보 수정 성공");
        } catch (IllegalArgumentException e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    @Transactional
    public BaseResponse deleteMember(Member member) {
       Member currentMember = findByMembername(member.getMembername());
        memberRepository.delete(currentMember);
        return BaseResponse.successMessage("회원 삭제 성공");
    }

    @Transactional
    public BaseResponse uploadProfileImage(MultipartFile file, Member member) throws IOException {
        String url = s3Uploader.upload(file, "profile");
        member.profileImageUrlUpdate(url);
        memberRepository.save(member);
        return BaseResponse.successData(url);
    }
  
  
    public Member findByMembername(String membername) {
        return memberRepository.findByMembername(membername).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
