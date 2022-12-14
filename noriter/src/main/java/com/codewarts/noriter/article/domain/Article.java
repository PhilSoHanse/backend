package com.codewarts.noriter.article.domain;

import com.codewarts.noriter.article.domain.type.ArticleType;
import com.codewarts.noriter.comment.domain.Comment;
import com.codewarts.noriter.exception.GlobalNoriterException;
import com.codewarts.noriter.exception.type.ArticleExceptionType;
import com.codewarts.noriter.member.domain.Member;
import com.codewarts.noriter.wish.domain.Wish;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ObjectUtils;

@Getter
@Entity
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member writer;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Hashtag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "article", orphanRemoval = true)
    private final List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "article", orphanRemoval = true)
    private final List<Wish> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "article", orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    private LocalDateTime writtenTime;
    private LocalDateTime editedTime;

    @Enumerated(EnumType.STRING)
    private ArticleType articleType;
    private boolean deleted;

    public Article(String title, String content, Member writer, LocalDateTime writtenTime,
        LocalDateTime editedTime, ArticleType articleType) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.writtenTime = writtenTime;
        this.editedTime = editedTime;
        this.articleType = articleType;
        this.deleted = false;
    }

    public void addHashtags(List<String> requestHashtags) {
        if (ObjectUtils.isEmpty(requestHashtags)) {
            return;
        }
        for (String requestHashtag : requestHashtags) {
            this.hashtags.add(Hashtag.builder().article(this).content(requestHashtag).build());
        }
    }

    public void update(String title, String content, List<String> requestHashtags) {
        this.title = title;
        this.content = content;
        this.editedTime = LocalDateTime.now();
        this.hashtags.clear();
        addHashtags(requestHashtags);
    }

    public void validateWriterOrThrow(Long writerId) {
        if (!Objects.equals(this.writer.getId(), writerId)) {
            throw new GlobalNoriterException(ArticleExceptionType.ARTICLE_NOT_MATCHED_WRITER);
        }
    }
}
