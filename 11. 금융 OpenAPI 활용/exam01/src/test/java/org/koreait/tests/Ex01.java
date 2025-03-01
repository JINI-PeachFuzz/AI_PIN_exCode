package org.koreait.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.member.controllers.RequestJoin;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class Ex01 {

    private ObjectMapper om; // 레스트 컨트롤러 쪽에도 이걸로 구현되어있음
    private RestTemplate restTemplate;

    @BeforeEach
    void init() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // 자바타임패키지에 대한 변환작업을 할 수 있게 추가함

        restTemplate = new RestTemplate();
    }

    @Test
    @DisplayName("ObjectMapper - JAVA 객체 <-> JSON 문자열 변환")
    void test1() throws Exception { // 테스트의 편의성을 위해 Exception을 던졌음

        RequestJoin form = new RequestJoin();
        form.setEmail("user01@test.org");
        form.setPassword("1234");
        form.setConfirmPassword("1234");
        form.setName("사용자01");

        String json = om.writeValueAsString(form); // 자바 객체 -> JSON 문자열
        System.out.println(json);


        // JSON 문자열 -> 자바 객체 변환
        RequestJoin form2 = om.readValue(json, RequestJoin.class); // 단일 클래스 형태의 자료형으로 변환 Class 클래스가 매개변수 // 두번째 매개변수가 클래스클래스형태로 되어있으면 RequestJoin은 클래스객체가 되는거임
        System.out.println("form2 : " + form2);
    }
    // {"email":"user01@test.org","password":"1234","confirmPassword":"1234","name":"사용자01"}
    //form2 : RequestJoin(email=user01@test.org, password=1234, confirmPassword=1234, name=사용자01)
    // 테스트시 이렇게 나왔음



    @Test
    @DisplayName("ObjectMapper - Collection 활용")
    void test2() throws Exception {
        List<RequestJoin> items = IntStream.rangeClosed(1, 10) // 1부터 10개 생성
                .mapToObj(i -> RequestJoin.builder()
                        .email("user" + i + "@test.org")
                        .password("1234")
                        .confirmPassword("1234")
                        .name("사용자" + i).build()).toList();

        // List -> JSON 문자열
        String json = om.writeValueAsString(items);
        System.out.println(json); // [{"email":"user1@test.org","password":"1234" 이런식으로 대괄호 형태로 담김

        // List<T>, Map<K, V>, Set<...>  -> TypeReference(추상클래스) 객체형태로 형성함 / 구현체가 없이 넣어도 바뀜 단, TypeReference를 써야함

        //List<RequestJoin> items2 = om.readValue(json, new TypeReference<List<RequestJoin>>() {구현체});
        // 지네릭타입으로 넣어줌 / 리스트라던지 셋형태를 다시 원래 자바객체로 바꿔줄수 있음
        // -------------------

        // JSON 문자열 -> List<T>, Map<K, V>, Set<...> 등등 복합적 Collection 으로 변환 시에는
        // TypeReference (추상 클래스) 객체 필요
        // List<RequestJoin> items2 = om.readValue(json, new TypeReference<List<RequestJoin>>() {});
        List<RequestJoin> items2 = om.readValue(json, new TypeReference<>() {});

        items2.forEach(System.out::println);
    } // RequestJoin(email=user1@test.org, password=1234, confirmPassword=1234, name=사용자1) 이렇게 나옴

    @Test
    @DisplayName("UriComponents")
    void test3() {
        //UriComponents url = UriComponentsBuilder.fromUri(URI.create("https://www.naver.com"))
        UriComponents url = UriComponentsBuilder.fromUriString("https://www.naver.com")
                .queryParam("k1", "v1")
                .queryParam("k2", "v2")
                .queryParam("k3", "한글")
                .fragment("header")
                .encode() // 멀티 바이트 문자 -> 16진수 형태로 변경 - URL 인코딩
                .build();
        String _url = url.toString();
        System.out.println(_url);
        // www.naver.com?k1=v1&k2=v2#header
    }

    @Test
    @DisplayName("restTemplate - getForObject()")
    void test4() {
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        //String body = restTemplate.getForObject(URI.create(url), String.class); // 응답 바디
        // -------------------

        // 응답 Body Data
        // 문자열로 가져오려면 String.class
        // String body = restTemplate.getForObject(URI.create(url), String.class);

        // Body Data -> Post.class 형태로 자동 변환
        Post body = restTemplate.getForObject(URI.create(url), Post.class);
        System.out.println(body);
    }

    @Test
    @DisplayName("restTemplate - getForEntity()")
    void test5() {
        String url = "https://jsonplaceholder.typicode.com/posts/1";
        //ResponseEntity<String> response = restTemplate.getForEntity(URI.create(url), String.class);
        // -------------------

        // 응답 header & body
        // <String> = Body Data 형식
        // ResponseEntity<String> response = restTemplate.getForEntity(URI.create(url), String.class);
        // System.out.println(response);
        ResponseEntity<Post> response = restTemplate.getForEntity(URI.create(url), Post.class);

        // 응답 상태 코드
        HttpStatusCode status = response.getStatusCode();
        System.out.println("status:" + status);

        // 응답 header
        HttpHeaders headers = response.getHeaders(); // 응답 헤더
        System.out.println("headers:" + headers);

        // 응답 Body
        System.out.println("body:" + response.getBody());
    }
}
