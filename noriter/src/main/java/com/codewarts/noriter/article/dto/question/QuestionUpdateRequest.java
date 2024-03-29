package com.codewarts.noriter.article.dto.question;

import com.codewarts.noriter.article.dto.article.ArticleUpdateRequest;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionUpdateRequest extends ArticleUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private  String title;

    @NotBlank(message = "내용은 필수입니다.")
    private  String content;

    private  List<String> hashtags;
}
