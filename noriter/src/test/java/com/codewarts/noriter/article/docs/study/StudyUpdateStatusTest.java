package com.codewarts.noriter.article.docs.study;

import static com.codewarts.noriter.exception.type.ArticleExceptionType.ALREADY_CHANGED_STATUS;
import static com.codewarts.noriter.exception.type.ArticleExceptionType.ARTICLE_NOT_FOUND;
import static com.codewarts.noriter.exception.type.ArticleExceptionType.ARTICLE_NOT_MATCHED_WRITER;
import static com.codewarts.noriter.exception.type.CommonExceptionType.INCORRECT_REQUEST_VALUE;
import static com.codewarts.noriter.exception.type.CommonExceptionType.INVALID_REQUEST;
import static com.codewarts.noriter.exception.type.MemberExceptionType.MEMBER_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONNECTION;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.DATE;
import static org.springframework.http.HttpHeaders.HOST;
import static org.springframework.http.HttpHeaders.TRANSFER_ENCODING;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import com.codewarts.noriter.auth.jwt.JwtProvider;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:/data.sql")
class StudyUpdateStatusTest {

    @LocalServerPort
    int port;

    @Autowired
    protected JwtProvider jwtProvider;

    protected RequestSpecification documentationSpec;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        documentationSpec = new RequestSpecBuilder()
            .addFilter(
                documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(
                        prettyPrint(),
                        removeHeaders(HOST, CONTENT_LENGTH))
                    .withResponseDefaults(
                        prettyPrint(),
                        removeHeaders(CONTENT_LENGTH, CONNECTION, DATE, TRANSFER_ENCODING,
                            "Keep-Alive",
                            HttpHeaders.VARY))
            )
            .build();
    }

    @Test
    void ????????????_???????????????_????????????() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 1)
            .body(Collections.singletonMap("status", "complete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void Path_Variable???_??????_??????_?????????_???????????????() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", " ")
            .body(Collections.singletonMap("status", "complete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("recruitmentCompletionUpdate.id: ID??? ??????????????????."));
    }

    @Test
    void id???_????????????_??????_??????_?????????_???????????????() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", -1)
            .body(Collections.singletonMap("status", "complete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("recruitmentCompletionUpdate.id: ????????? ID??? ??????????????? ?????????."));
    }

    @Test
    void ????????????_????????????_??????_?????????_????????????() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 1)
            .body(Collections.singletonMap("status", " "))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(INCORRECT_REQUEST_VALUE.getStatus().value())
            .body("errorCode", equalTo(INCORRECT_REQUEST_VALUE.getErrorCode()))
            .body("message", equalTo(INCORRECT_REQUEST_VALUE.getErrorMessage()));

    }

    @Test
    void ????????????_??????_?????????_??????_?????????_????????????() {
        String accessToken = jwtProvider.issueAccessToken(99999L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 9999999)
            .body(Collections.singletonMap("status", "complete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(MEMBER_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(MEMBER_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(MEMBER_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void id???_????????????_??????_??????_?????????_???????????????() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 9999999)
            .body(Collections.singletonMap("status", "complete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(ARTICLE_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(ARTICLE_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(ARTICLE_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void ??????_?????????_???????????????_?????????_??????_?????????_???????????????() {
        String accessToken = jwtProvider.issueAccessToken(2L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 1)
            .body(Collections.singletonMap("status", "incomplete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(ARTICLE_NOT_MATCHED_WRITER.getStatus().value())
            .body("errorCode", equalTo(ARTICLE_NOT_MATCHED_WRITER.getErrorCode()))
            .body("message", equalTo(ARTICLE_NOT_MATCHED_WRITER.getErrorMessage()));
    }

    @Test
    void ?????????_??????_???????????????_?????????_??????_?????????_????????????() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("id", 1)
            .body(Collections.singletonMap("status", "incomplete"))

            .when()
            .patch("/community/gathering/{id}")

            .then()
            .statusCode(ALREADY_CHANGED_STATUS.getStatus().value())
            .body("errorCode", equalTo(ALREADY_CHANGED_STATUS.getErrorCode()))
            .body("message", equalTo(ALREADY_CHANGED_STATUS.getErrorMessage()));
    }
}
