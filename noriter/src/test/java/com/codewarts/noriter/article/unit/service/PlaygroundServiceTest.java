package com.codewarts.noriter.article.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codewarts.noriter.article.domain.Article;
import com.codewarts.noriter.article.dto.article.ArticleListResponse;
import com.codewarts.noriter.article.dto.playground.PlaygroundCreateRequest;
import com.codewarts.noriter.article.dto.playground.PlaygroundDetailResponse;
import com.codewarts.noriter.article.dto.playground.PlaygroundUpdateRequest;
import com.codewarts.noriter.article.repository.ArticleRepository;
import com.codewarts.noriter.article.service.PlaygroundService;
import com.codewarts.noriter.auth.oauth.type.ResourceServer;
import com.codewarts.noriter.config.DatabaseCleanup;
import com.codewarts.noriter.exception.GlobalNoriterException;
import com.codewarts.noriter.exception.type.ArticleExceptionType;
import com.codewarts.noriter.exception.type.MemberExceptionType;
import com.codewarts.noriter.member.domain.Member;
import com.codewarts.noriter.member.repository.MemberRepository;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PlaygroundServiceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    PlaygroundService playgroundService;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    MemberRepository memberRepository;

    private final Long NON_EXIST_INDEX = Long.MAX_VALUE;
    private Long writerId;

    @BeforeEach
    void setUp() {
        Member member = new Member(ResourceServer.GITHUB, 1L, "admin", "admin@code.com", null,
            null);
        writerId = memberRepository.save(member).getId();
    }

    @AfterEach
    void cleanup() {
        databaseCleanup.afterPropertiesSet();
        databaseCleanup.execute();
    }

    @DisplayName("자유게시판 글을 생성한다.")
    @Test
    void create() {
        // given
        PlaygroundCreateRequest request = new PlaygroundCreateRequest("테스트 제목", "테스트 내용", null);
        Long articleId = playgroundService.create(request, writerId);

        // when
        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new GlobalNoriterException(ArticleExceptionType.ARTICLE_NOT_FOUND));

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(article.getId()).isEqualTo(articleId);
            softAssertions.assertThat(article.getId()).isEqualTo(articleId);
            softAssertions.assertThat(article.getTitle()).isEqualTo(request.getTitle());
            softAssertions.assertThat(article.getContent()).isEqualTo(request.getContent());
            softAssertions.assertThat(article.getWriter().getId()).isEqualTo(writerId);
        });
    }

    @DisplayName("존재하지 않는 회원이 자유게시판 글을 생성 요청하면 예외를 발생시킨다.")
    @Test
    void createWithWrongWriterIdThenThrow() {
        // when
        PlaygroundCreateRequest request = new PlaygroundCreateRequest("테스트 제목", "테스트 내용", null);

        // expected
        assertThatThrownBy(() -> playgroundService.create(request, NON_EXIST_INDEX))
            .message()
            .isEqualTo(MemberExceptionType.MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("자유게시판 글을 상세 조회한다.")
    @Test
    void findDetail() {
        // given
        PlaygroundCreateRequest request = new PlaygroundCreateRequest("테스트 제목", "테스트 내용", null);
        Long articleId = playgroundService.create(request, writerId);

        // when
        PlaygroundDetailResponse response = playgroundService.findDetail(articleId, writerId);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getId()).isEqualTo(articleId);
            softAssertions.assertThat(response.getTitle()).isEqualTo(request.getTitle());
            softAssertions.assertThat(response.getContent()).isEqualTo(request.getContent());
            softAssertions.assertThat(response.getWriter().getId()).isEqualTo(writerId);
        });
    }

    @DisplayName("존재하지 않는 자유게시판 글을 상세 조회 요청하면 예외를 발생시킨다.")
    @Test
    void findDetailWithWrongArticleIdThenThrow() {
        // expected
        assertThatThrownBy(() -> playgroundService.findDetail(NON_EXIST_INDEX, writerId))
            .message()
            .isEqualTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("삭제된 자유게시판 글을 상세 조회 요청하면 예외를 발생시킨다.")
    @Test
    void findDetailWithAlreadyDeletedArticleIdThenThrow() {
        // when
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목", "테스트 내용",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        playgroundService.delete(articleId, writerId);

        // expected
        assertThatThrownBy(() -> playgroundService.findDetail(articleId, writerId))
            .message()
            .isEqualTo(ArticleExceptionType.DELETED_ARTICLE.getErrorMessage());
    }

    @DisplayName("자유게시판 글을 전체 조회한다.")
    @Test
    void findList() {
        // given
        PlaygroundCreateRequest request1 = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        playgroundService.create(request1, writerId);

        PlaygroundCreateRequest request2 = new PlaygroundCreateRequest("테스트 제목2", "테스트 내용2",
            List.of("해시태그1", "해시태그2"));
        playgroundService.create(request2, writerId);

        // when
        List<ArticleListResponse> list = playgroundService.findList(null);

        // then
        assertThat(list).hasSize(2);
    }

    @DisplayName("삭제되지 않은 자유게시판 글을 전체 조회한다.")
    @Test
    void findListWithoutDeletedArticle() {
        // given
        PlaygroundCreateRequest request1 = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        playgroundService.create(request1, writerId);

        PlaygroundCreateRequest request2 = new PlaygroundCreateRequest("테스트 제목2", "테스트 내용2",
            List.of("해시태그1", "해시태그2"));
        playgroundService.create(request2, writerId);

        PlaygroundCreateRequest request3 = new PlaygroundCreateRequest("테스트 제목3", "테스트 내용3",
            List.of("해시태그1", "해시태그2"));
        Long deleteArticleId = playgroundService.create(request3, writerId);

        playgroundService.delete(deleteArticleId, writerId);

        // when
        List<ArticleListResponse> list = playgroundService.findList(null);

        // then
        assertThat(list).hasSize(2);
    }

    @DisplayName("자유게시판 글을 수정한다.")
    @Test
    void update() {
        // given
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        PlaygroundUpdateRequest updateRequest = new PlaygroundUpdateRequest("수정된 제목", "수정된 내용",
            List.of("수정된 해시태그"));

        // when
        playgroundService.update(articleId, updateRequest, writerId);
        PlaygroundDetailResponse response = playgroundService.findDetail(articleId, writerId);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(response.getId()).isEqualTo(articleId);
            softAssertions.assertThat(response.getTitle()).isEqualTo(updateRequest.getTitle());
            softAssertions.assertThat(response.getContent()).isEqualTo(updateRequest.getContent());
            softAssertions.assertThat(response.getHashtags()).hasSize(updateRequest.getHashtags().size());
            softAssertions.assertThat(response.getWriter().getId()).isEqualTo(writerId);
        });
    }

    @DisplayName("존재하지 않는 자유게시판 글을 수정 요청하는 경우 예외를 발생시킨다.")
    @Test
    void updateWithWrongArticleIdThenThrow() {
        // when
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목", "테스트 내용",
            List.of("해시태그1", "해시태그2"));
        playgroundService.create(createRequest, writerId);

        PlaygroundUpdateRequest updateRequest = new PlaygroundUpdateRequest("수정된 제목", "수정된 내용",
            List.of("수정된 해시태그"));

        // expected
        assertThatThrownBy(
                () -> playgroundService.update(NON_EXIST_INDEX, updateRequest, writerId))
            .message()
            .isEqualTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("존재하지 않는 회원이 자유게시판 글을 수정 요청하는 경우 예외를 발생시킨다.")
    @Test
    void updateWithWrongWriterIdThenThrow() {
        // when
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목", "테스트 내용",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        PlaygroundUpdateRequest updateRequest = new PlaygroundUpdateRequest("수정된 제목", "수정된 내용",
            List.of("수정된 해시태그"));

        // expected
        assertThatThrownBy(
                () -> playgroundService.update(articleId, updateRequest, NON_EXIST_INDEX))
            .message()
            .isEqualTo(MemberExceptionType.MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("작성자가 아닌 회원이 자유게시판 글을 수정 요청하는 경우 예외를 발생시킨다.")
    @Test
    void updateWithNotMatchedWriterThenThrow() {
        // when
        Member newMember = new Member(ResourceServer.GITHUB, 1L, "admin", "admin@code.com", null,
            null);
        Long wrongWriterId = memberRepository.save(newMember).getId();

        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목", "테스트 내용",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);


        PlaygroundUpdateRequest updateRequest = new PlaygroundUpdateRequest("수정된 제목", "수정된 내용",
            List.of("수정된 해시태그"));

        // expected
        assertThatThrownBy(
                () -> playgroundService.update(articleId, updateRequest, wrongWriterId))
            .message()
            .isEqualTo(ArticleExceptionType.ARTICLE_NOT_MATCHED_WRITER.getErrorMessage());
    }

    @DisplayName("삭제된 자유게시판 글을 수정 요청하는 경우 예외를 발생시킨다.")
    @Test
    void updateWithAlreadyDeletedArticleIdThenThrow() {
        // when
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목", "테스트 내용",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        playgroundService.delete(articleId, writerId);
        PlaygroundUpdateRequest updateRequest = new PlaygroundUpdateRequest("수정된 제목", "수정된 내용",
            List.of("수정된 해시태그"));

        // expected
        assertThatThrownBy(
                () -> playgroundService.update(articleId, updateRequest, writerId))
            .message()
            .isEqualTo(ArticleExceptionType.DELETED_ARTICLE.getErrorMessage());
    }

    @DisplayName("자유게시판 글을 삭제한다.")
    @Test
    void delete() {
        // given
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        // when
        playgroundService.delete(articleId, writerId);
        Article article = articleRepository.findById(articleId).get();

        // then
        assertThat(article.getDeleted()).isNotNull();
    }

    @DisplayName("존재하지 않는 회원이 자유게시판 글을 삭제 요청하는 경우 예외를 발생시킨다.")
    @Test
    void deleteWithWrongWriterIdThenThrow() {
        // when
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        // expected
        assertThatThrownBy(() -> playgroundService.delete(articleId, NON_EXIST_INDEX))
            .message()
            .isEqualTo(MemberExceptionType.MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("작성자가 아닌 회원이 자유게시판 글을 삭제 요청하는 경우 예외를 발생시킨다.")
    @Test
    void deleteWithNotMatchedWriterIdThenThrow() {
        // when
        Member newMember = new Member(ResourceServer.GITHUB, 1L, "admin", "admin@code.com", null,
            null);
        Long wrongWriterId = memberRepository.save(newMember).getId();

        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        // expected
        assertThatThrownBy(() -> playgroundService.delete(articleId, wrongWriterId))
            .message()
            .isEqualTo(ArticleExceptionType.ARTICLE_NOT_MATCHED_WRITER.getErrorMessage());
    }

    @DisplayName("존재하지 않은 자유게시판 글을 삭제 요청하는 경우 예외를 발생시킨다.")
    @Test
    void deleteWithWrongArticleIdThenThrow() {
        // when
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        playgroundService.create(createRequest, writerId);

        // expected
        assertThatThrownBy(() -> playgroundService.delete(NON_EXIST_INDEX, writerId))
            .message()
            .isEqualTo(ArticleExceptionType.ARTICLE_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("이미 삭제된 자유게시판 글을 삭제 요청하는 경우 예외를 발생시킨다.")
    @Test
    void deleteWithAlreadyDeletedArticleIdThenThrow() {
        // given
        PlaygroundCreateRequest createRequest = new PlaygroundCreateRequest("테스트 제목1", "테스트 내용1",
            List.of("해시태그1", "해시태그2"));
        Long articleId = playgroundService.create(createRequest, writerId);

        // when
        playgroundService.delete(articleId, writerId);

        // expected
        assertThatThrownBy(() -> playgroundService.delete(articleId, writerId))
            .message()
            .isEqualTo(ArticleExceptionType.DELETED_ARTICLE.getErrorMessage());
    }
}
