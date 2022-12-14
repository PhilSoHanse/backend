package com.codewarts.noriter.article.docs.study;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
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
import com.codewarts.noriter.exception.type.CommonExceptionType;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile({"test"})
@Sql("classpath:/data.sql")
class StudyListTest {

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
    void ????????????_????????????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .param("status", "INCOMPLETE")

            .when()
            .get("/community/gathering")

            .then()
            .statusCode(HttpStatus.OK.value())
            .body("[0].id", equalTo(1))
            .body("[0].title", equalTo("???????????? ???????????????"))
            .body("[0].content", equalTo("?????????????????????"))
            .body("[0].writerNickname", equalTo("admin1"))
            .body("[0].sameWriter", equalTo(false))
            .body("[0].writtenTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].editedTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].hashtags[0]", equalTo("SPRING"))
            .body("[0].hashtags[1]", equalTo("JPA"))
            .body("[0].wishCount", equalTo(0))
            .body("[0].commentCount", equalTo(4));
    }

    @Test
    void ?????????_requestParam_??????_??????_?????????_???????????????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .param("comp", "INCOMPLETE")

            .when()
            .get("/community/gathering")

            .then()
            .statusCode(CommonExceptionType.INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(CommonExceptionType.INCORRECT_REQUEST_PARAM.getErrorCode()))
            .body("message", equalTo(CommonExceptionType.INCORRECT_REQUEST_PARAM.getErrorMessage()));
    }
    @Test
    void ?????????_requestParam_?????????_??????_?????????_???????????????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .param("status", "dd")

            .when()
            .get("/community/gathering")

            .then()
            .statusCode(CommonExceptionType.INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(CommonExceptionType.INCORRECT_REQUEST_VALUE.getErrorCode()))
            .body("message", equalTo(CommonExceptionType.INCORRECT_REQUEST_VALUE.getErrorMessage()));
    }

}
