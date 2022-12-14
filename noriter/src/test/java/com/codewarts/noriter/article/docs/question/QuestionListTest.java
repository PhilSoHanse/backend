package com.codewarts.noriter.article.docs.question;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionListTest {

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
    @DisplayName("????????? ?????? ??? ??????")
    void findIncompleteQuestion() throws Exception {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)

            .when()
            .get("/community/question?status=incomplete")

            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(2));
    }

    @Test
    @DisplayName("????????? ?????? ??? ??????")
    void findCompleteQuestion() throws Exception {

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
    @DisplayName("?????? ?????? ??? ??????")
    void findAllQuestion() throws Exception {
        // expected
        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)

            .when()
            .get("/community/question")

            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", is(4))
            .body("[0].id", equalTo(6))
            .body("[0].title", equalTo("??????1"))
            .body("[0].content", equalTo("??????1"))
            .body("[0].writerNickname", equalTo("admin1"))
            .body("[0].sameWriter", equalTo(false))
            .body("[0].writtenTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].editedTime", equalTo("2022-11-11 16:25:58"))
            .body("[0].hashtags[0]", equalTo("?????????"))
            .body("[0].hashtags[1]", equalTo("?????????"))
            .body("[0].hashtags[2]", equalTo("????????????"))
            .body("[0].wishCount", equalTo(0))
            .body("[0].commentCount", equalTo(0));
    }
}
