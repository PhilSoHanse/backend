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
import com.codewarts.noriter.exception.type.ArticleExceptionType;
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
public class StudyDetailTest {

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
    void ???????????????_??????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", 1)

            .when()
            .get("/community/gathering/{id}")

            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(1))
            .body("title", equalTo("???????????? ???????????????"))
            .body("content", equalTo("?????????????????????"))
            .body("writer.id", equalTo(1))
            .body("writer.nickname", equalTo("admin1"))
            .body("writer.profileImage",
                equalTo("https://avatars.githubusercontent.com/u/111111?v=4"))
            .body("sameWriter", equalTo(false))
            .body("hashtags[0]", equalTo("SPRING"))
            .body("hashtags[1]", equalTo("JPA"))
            .body("hashtags[2]", equalTo("????????????"))
            .body("wishCount", equalTo(0))
            .body("status", equalTo("INCOMPLETE"))
            .body("comment[0].id", equalTo(1))
            .body("comment[0].content", equalTo("?????? ????????????"))
            .body("comment[0].writer.id", equalTo(2))
            .body("comment[0].writer.nickname", equalTo("admin2"))
            .body("comment[0].writer.profileImage",
                equalTo("https://avatars.githubusercontent.com/u/222222?v=4"));
    }

    @Test
    void Path_Variable???_??????_??????_?????????_???????????????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", " ")

            .when()
            .get("/community/gathering/{id}")

            .then()
            .statusCode(CommonExceptionType.INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(CommonExceptionType.INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("gatheringDetail.id: ID??? ??????????????????."));
    }

    @Test
    void id???_????????????_??????_??????_?????????_???????????????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", -1)

            .when()
            .get("/community/gathering/{id}")

            .then()
            .statusCode(CommonExceptionType.INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(CommonExceptionType.INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("gatheringDetail.id: ????????? ID??? ??????????????? ?????????."));
    }

    @Test
    void id???_????????????_??????_??????_?????????_???????????????() {

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .pathParam("id", 999999999)

            .when()
            .get("/community/gathering/{id}")

            .then()
            .statusCode(ArticleExceptionType.ARTICLE_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorMessage()));

    }
}
