package com.codewarts.noriter.article.dto.question;

import com.codewarts.noriter.article.domain.Hashtag;
import com.codewarts.noriter.article.domain.Question;
import com.codewarts.noriter.article.domain.type.StatusType;
import com.codewarts.noriter.article.dto.article.ArticleDetailResponse;
import com.codewarts.noriter.comment.dto.comment.CommentResponse;
import com.codewarts.noriter.member.dto.WriterInfoResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class QuestionDetailResponse extends ArticleDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final WriterInfoResponse writer;
    private final boolean sameWriter;
    private final List<String> hashtags;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime lastModifiedTime;
    private final boolean wish;
    private final int wishCount;
    private final List<CommentResponse> comment;
    private final StatusType status;

    public QuestionDetailResponse(Question question, boolean sameWriter, boolean wish) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.writer = WriterInfoResponse.from(question.getWriter());
        this.sameWriter = sameWriter;
        this.hashtags = question.getHashtags().stream()
            .map(Hashtag::getContent)
            .collect(Collectors.toList());
        this.createdTime = question.getCreatedTime();
        this.lastModifiedTime = question.getLastModifiedTime();
        this.wish = wish;
        this.wishCount = question.getWishList().size();
        this.comment = question.getComments().stream()
            .map(CommentResponse::new)
            .collect(Collectors.toList());
        this.status = question.getStatus();
    }

    public static QuestionDetailResponse from(Question question, boolean sameWriter, boolean wish) {
        return new QuestionDetailResponse(question, sameWriter, wish);
    }
}
