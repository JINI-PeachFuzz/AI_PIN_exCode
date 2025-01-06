package org.koreait.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.member.controllers.RequestJoin;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@SpringBootTest
public class Ex02 {

    private RestTemplate restTemplate;

    @BeforeEach
    void init() {

        restTemplate = new RestTemplate();
    }

    @Test
    @DisplayName("POST & PUT & PATCh 일시 요청 header & body 데이터 작성")
    void test1() {

        /* 요청 header S */
        HttpHeaders headers = new HttpHeaders();

        // 요청 body - Content-Type
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Content-Type : application/x-www-form.urlencoded

        /* 요청 header E */

        /* 요청 body S */
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("email", "user01@test.org");
        params.add("password", "1234");
        params.add("confirmPassword", "1234");
        params.add("name", "user01");
        /* 요청 body E */

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 원격에 요청 보내는 것이라 전체 주소(스키마 + 도메인 + path) 필요
        String url = "http://localhost:3000/member/join";

        // Controller - public void joinPs() 라서 Void
        ResponseEntity<Void> response = restTemplate.postForEntity(URI.create(url), request, Void.class);

        System.out.println(response);
        // RequestJoin(email=user01@test.org, password=1234, confirmPassword=1234, name=user01)
    }

    @Test
    void test2() throws Exception {
        ObjectMapper om = new ObjectMapper();

        // JSON으로 POST 요청
        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디
        RequestJoin form = RequestJoin.builder()
                .email("user01@test.org")
                .password("1234")
                .confirmPassword("1234")
                .name("사용자01")
                .build();

        String params = om.writeValueAsString(form);

        HttpEntity<String> request = new HttpEntity<>(params, headers);

        String url = "http://localhost:3000/member/join";
        ResponseEntity<Void> response = restTemplate.postForEntity(URI.create(url), request, Void.class);
        System.out.println(response);
    }
}