package com.panda.back.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND, "적어도 1개 이상의 이미지를 추가해주세요."),
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다."),
    NOT_FOUND_MY_ITEM(HttpStatus.NOT_FOUND, "본인이 등록한 상품이 아닙니다."),

    DUPLICATE_MEMBERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    NOT_MODIFIED_BIDDING_ITEM(HttpStatus.UNAUTHORIZED, "경매 진행중인 상품은 수정할 수 없습니다."),
    NOT_MODIFIED_BIDDED_ITEM(HttpStatus.UNAUTHORIZED, "입찰이 된 상품은 수정할 수 없습니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰입니다."),
    WRONG_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INVALID_INPUT_AUTHKEY(HttpStatus.UNAUTHORIZED, "인증키가 유효하지 않습니다."),
    EXPIRED_AUTHKEY(HttpStatus.UNAUTHORIZED,"만료된 인증키 입니다."),
    NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    NOT_VALID_BID_AMOUNT(HttpStatus.NOT_ACCEPTABLE,"입찰가가 현재가와 같거나 낮습니다."),
    NOT_VALID_MIN_BID_AMOUNT(HttpStatus.NOT_ACCEPTABLE, "최소 입찰 단위로 입찰해주세요."),

    CLOSED_BIDDING_ITEM(HttpStatus.GONE, "경매가 종료된 상품입니다."),

    REQUIRED_EMAIL_AND_AUTHKEY(HttpStatus.UNPROCESSABLE_ENTITY, "이메일과 인증키를 모두 입력해주세요");





    private final HttpStatus httpStatus;
    private final String message;
}
