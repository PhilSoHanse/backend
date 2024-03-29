package com.codewarts.noriter.article.docs.question;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codewarts.noriter.article.docs.InitIntegrationRestDocsTest;
import com.codewarts.noriter.exception.type.ArticleExceptionType;
import com.codewarts.noriter.exception.type.CommonExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("질문게시판 상세 조회 기능 통합 테스트")
class QuestionDetailTest extends InitIntegrationRestDocsTest {

    @Test
    void 상세조회를_한다() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", 6)

        .when()
            .get("/community/question/{id}")

        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(6))
            .body("title", equalTo("질문1"))
            .body("content", equalTo("궁금1"))
            .body("writer.id", equalTo(1))
            .body("writer.nickname", equalTo("admin1"))
            .body("writer.profileImage",
                equalTo("https://avatars.githubusercontent.com/u/111111?v=4"))
            .body("sameWriter", equalTo(false))
            .body("wish", equalTo(false))
            .body("hashtags[0]", equalTo("스프링"))
            .body("hashtags[1]", equalTo("코린이"))
            .body("hashtags[2]", equalTo("도와줘요"));
    }

    @Test
    void 로그인_후_상세조회를_한다() {
        String accessToken = jwtProvider.issueAccessToken(2L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 6)

        .when()
            .get("/community/question/{id}")

        .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(6))
            .body("title", equalTo("질문1"))
            .body("content", equalTo("궁금1"))
            .body("writer.id", equalTo(1))
            .body("writer.nickname", equalTo("admin1"))
            .body("writer.profileImage",
                equalTo("https://avatars.githubusercontent.com/u/111111?v=4"))
            .body("sameWriter", equalTo(false))
            .body("wish", equalTo(true))
            .body("hashtags[0]", equalTo("스프링"))
            .body("hashtags[1]", equalTo("코린이"))
            .body("hashtags[2]", equalTo("도와줘요"));
    }


    @Test
    void id가_유효하지_않는_경우_예외를_발생시킨다() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", -1)

        .when()
            .get("/community/question/{id}")

        .then()
            .statusCode(CommonExceptionType.INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(CommonExceptionType.INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("getDetail.id: 게시글 ID는 양수이어야 합니다."));
    }

    @Test
    void Path_Variable이_없는_경우_예외를_발생시킨다() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", " ")

        .when()
            .get("/community/question/{id}")

        .then()
            .statusCode(CommonExceptionType.INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(CommonExceptionType.INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("getDetail.id: ID가 비어있습니다."));
    }

    @Test
    void id가_존재하지_않는_경우_예외를_발생시킨다() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", 999999999)

        .when()
            .get("/community/question/{id}")

        .then()
            .statusCode(ArticleExceptionType.ARTICLE_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorMessage()));
    }
}
