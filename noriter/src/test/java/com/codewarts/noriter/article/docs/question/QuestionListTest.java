package com.codewarts.noriter.article.docs.question;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codewarts.noriter.article.docs.InitIntegrationRestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("질문게시판 전체 조회 기능 통합 테스트")
class QuestionListTest extends InitIntegrationRestDocsTest {

    @Test
    @DisplayName("미해결 질문 글 조회")
    void findIncompleteQuestion() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)

        .when()
            .get("/community/question?status=incomplete")

        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(2));
    }

    @Test
    @DisplayName("해결된 질문 글 조회")
    void findCompleteQuestion() {

        // expected
        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)

        .when()
            .get("/community/question?status=complete")

        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(2));
    }
    @Test
    @DisplayName("모든 질문 글 조회")
    void findAllQuestion() {
        // expected
        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)

        .when()
            .get("/community/question")

        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(4))
            .body("[0].id", equalTo(6))
            .body("[0].title", equalTo("질문1"))
            .body("[0].content", equalTo("궁금1"))
            .body("[0].writerNickname", equalTo("admin1"))
            .body("[0].sameWriter", equalTo(false))
            .body("[0].createdTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].lastModifiedTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].hashtags[0]", equalTo("스프링"))
            .body("[0].hashtags[1]", equalTo("코린이"))
            .body("[0].hashtags[2]", equalTo("도와줘요"))
            .body("[0].wish", equalTo(false))
            .body("[0].wishCount", equalTo(1))
            .body("[0].commentCount", equalTo(0));
    }
    @Test
    @DisplayName("로그인 후 모든 질문 글 조회")
    void findAllQuestion_After_Login() {
        String accessToken = jwtProvider.issueAccessToken(2L);

        // expected
        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)

        .when()
            .get("/community/question")

        .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(4))
            .body("[0].id", equalTo(6))
            .body("[0].title", equalTo("질문1"))
            .body("[0].content", equalTo("궁금1"))
            .body("[0].writerNickname", equalTo("admin1"))
            .body("[0].sameWriter", equalTo(false))
            .body("[0].createdTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].lastModifiedTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].hashtags[0]", equalTo("스프링"))
            .body("[0].hashtags[1]", equalTo("코린이"))
            .body("[0].hashtags[2]", equalTo("도와줘요"))
            .body("[0].wish", equalTo(true))
            .body("[0].wishCount", equalTo(1))
            .body("[0].commentCount", equalTo(0));
    }
}
