package com.codewarts.noriter.article.service;

import com.codewarts.noriter.article.domain.Study;
import com.codewarts.noriter.article.domain.dto.study.StudyDetailResponse;
import com.codewarts.noriter.article.domain.dto.study.StudyEditRequest;
import com.codewarts.noriter.article.domain.dto.study.StudyListResponse;
import com.codewarts.noriter.article.domain.dto.study.StudyPostRequest;
import com.codewarts.noriter.article.domain.type.StatusType;
import com.codewarts.noriter.article.repository.ArticleRepository;
import com.codewarts.noriter.article.repository.StudyRepository;
import com.codewarts.noriter.exception.GlobalNoriterException;
import com.codewarts.noriter.exception.type.ArticleExceptionType;
import com.codewarts.noriter.member.domain.Member;
import com.codewarts.noriter.member.service.MemberService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyService {

    private final MemberService memberService;
    private final ArticleRepository articleRepository;
    private final StudyRepository studyRepository;
    private final ArticleUtils articleUtils;

    @Transactional
    public Long create(StudyPostRequest studyPostRequest, Long memberId) {
        Member member = memberService.findMember(memberId);
        Study study = studyPostRequest.toEntity(member);
        study.addHashtags(studyPostRequest.getHashtags());
        return studyRepository.save(study).getId();
    }

    public List<StudyListResponse> findList(StatusType status, String accessToken) {
        if (status == null) {
            return studyRepository.findAllStudy().stream()
                .map(study -> new StudyListResponse(study,
                    articleUtils.isSameWriter(study.getWriter().getId(), accessToken)))
                .collect(Collectors.toList());
        }
        return studyRepository.findStudyByCompleted(status).stream()
            .map(study -> new StudyListResponse(study,
                articleUtils.isSameWriter(study.getWriter().getId(), accessToken)))
            .collect(Collectors.toList());
    }

    public StudyDetailResponse findDetail(Long id, String accessToken) {
        Study study = findStudy(id);
        boolean sameWriter = articleUtils.isSameWriter(study.getWriter().getId(), accessToken);
        return new StudyDetailResponse(study, sameWriter);
    }

    @Transactional
    public void delete(Long id, Long writerId) {
        memberService.findMember(writerId);
        Study study = findStudy(id);
        study.validateWriterOrThrow(writerId);
        articleRepository.deleteByIdAndWriterId(id, writerId);
    }

    @Transactional
    public void updateCompletion(Long id, Long writerId, StatusType status) {
        memberService.findMember(writerId);
        Study study = findStudy(id);
        study.validateWriterOrThrow(writerId);

        if (study.getStatus() == status) {
            throw new GlobalNoriterException(ArticleExceptionType.ALREADY_CHANGED_STATUS);
        }
        if (status == StatusType.COMPLETE) {
            study.completion();
        } else {
            study.incomplete();
        }
    }

    @Transactional
    public void update(Long id, StudyEditRequest request, Long writerId) {
        memberService.findMember(writerId);
        Study study = findStudy(id);
        study.validateWriterOrThrow(writerId);
        study.update(request.getTitle(), request.getContent(), request.getHashtags());
    }

    public Study findStudy(Long id) {
        return studyRepository.findById(id).
            orElseThrow(() -> new GlobalNoriterException(ArticleExceptionType.ARTICLE_NOT_FOUND));
    }
}
