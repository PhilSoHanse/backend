package com.codewarts.noriter.article.docs.comment;

import static com.codewarts.noriter.exception.type.ArticleExceptionType.ARTICLE_NOT_FOUND;
import static com.codewarts.noriter.exception.type.ArticleExceptionType.DELETED_ARTICLE;
import static com.codewarts.noriter.exception.type.AuthExceptionType.EMPTY_ACCESS_TOKEN;
import static com.codewarts.noriter.exception.type.AuthExceptionType.TAMPERED_ACCESS_TOKEN;
import static com.codewarts.noriter.exception.type.CommentExceptionType.COMMENT_NOT_FOUND;
import static com.codewarts.noriter.exception.type.CommentExceptionType.DELETED_COMMENT;
import static com.codewarts.noriter.exception.type.CommentExceptionType.NOT_MATCHED_ARTICLE;
import static com.codewarts.noriter.exception.type.CommentExceptionType.NOT_MATCHED_WRITER;
import static com.codewarts.noriter.exception.type.CommonExceptionType.INVALID_REQUEST;
import static com.codewarts.noriter.exception.type.MemberExceptionType.MEMBER_NOT_FOUND;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codewarts.noriter.article.docs.InitIntegrationRestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("댓글 삭제 기능 통합 테스트")
class CommentDeleteTest extends InitIntegrationRestDocsTest {

    @Test
    void 댓글을_삭제한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void Access_Token이_비어있는_경우_예외_발생() {
        String accessToken = " ";

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(EMPTY_ACCESS_TOKEN.getStatus().value())
            .body("errorCode", equalTo(EMPTY_ACCESS_TOKEN.getErrorCode()))
            .body("message", equalTo(EMPTY_ACCESS_TOKEN.getErrorMessage()));
    }

    @Test
    void Access_Token이_변조된_경우_예외_발생() {
        String accessToken = jwtProvider.issueAccessToken(1L) + "123";

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(TAMPERED_ACCESS_TOKEN.getStatus().value())
            .body("errorCode", equalTo(TAMPERED_ACCESS_TOKEN.getErrorCode()))
            .body("message", equalTo(TAMPERED_ACCESS_TOKEN.getErrorMessage()));
    }

    @Test
    void 존재하지_않는_회원인_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(99999999L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(MEMBER_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(MEMBER_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(MEMBER_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void articleId가_Null이면_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", " ")
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("removeComment.articleId: ID가 비어있습니다."));
    }

    @Test
    void articleId가_음수이면_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", -1)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("removeComment.articleId: 게시글 ID는 양수이어야 합니다."));
    }

    @Test
    void articleId가_존재하지_않는_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 9999999)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(ARTICLE_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(ARTICLE_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(ARTICLE_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void articleId가_삭제된_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 13)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(DELETED_ARTICLE.getStatus().value())
            .body("errorCode", equalTo(DELETED_ARTICLE.getErrorCode()))
            .body("message", equalTo(DELETED_ARTICLE.getErrorMessage()));
    }

    @Test
    void id가_Null이면_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", " ")

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("removeComment.id: ID가 비어있습니다."));
    }

    @Test
    void id가_음수이면_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", -1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(INVALID_REQUEST.getStatus().value())
            .body("errorCode", equalTo(INVALID_REQUEST.getErrorCode()))
            .body("message", equalTo("removeComment.id: 댓글 ID는 양수이어야 합니다."));
    }

    @Test
    void id가_존재하지_않는_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", 9999999)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(COMMENT_NOT_FOUND.getStatus().value())
            .body("errorCode", equalTo(COMMENT_NOT_FOUND.getErrorCode()))
            .body("message", equalTo(COMMENT_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void id가_삭제된_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(2L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 12)
            .pathParam("id", 6)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(DELETED_COMMENT.getStatus().value())
            .body("errorCode", equalTo(DELETED_COMMENT.getErrorCode()))
            .body("message", equalTo(DELETED_COMMENT.getErrorMessage()));
    }

    @Test
    void 작성자가_일치하지_않는_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(2L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 1)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(NOT_MATCHED_WRITER.getStatus().value())
            .body("errorCode", equalTo(NOT_MATCHED_WRITER.getErrorCode()))
            .body("message", equalTo(NOT_MATCHED_WRITER.getErrorMessage()));
    }

    @Test
    void 해당_게시글의_댓글이_아닌_경우_예외가_발생한다() {
        String accessToken = jwtProvider.issueAccessToken(1L);

        given(documentationSpec)
            .contentType(APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION, accessToken)
            .pathParam("articleId", 2)
            .pathParam("id", 1)

        .when()
            .delete("/{articleId}/comment/{id}")

        .then()
            .statusCode(NOT_MATCHED_ARTICLE.getStatus().value())
            .body("errorCode", equalTo(NOT_MATCHED_ARTICLE.getErrorCode()))
            .body("message", equalTo(NOT_MATCHED_ARTICLE.getErrorMessage()));
    }
}
