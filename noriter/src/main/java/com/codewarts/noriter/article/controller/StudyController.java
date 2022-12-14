package com.codewarts.noriter.article.controller;

import com.codewarts.noriter.article.domain.dto.study.StudyDetailResponse;
import com.codewarts.noriter.article.domain.dto.study.StudyEditRequest;
import com.codewarts.noriter.article.domain.dto.study.StudyListResponse;
import com.codewarts.noriter.article.domain.dto.study.StudyPostRequest;
import com.codewarts.noriter.article.domain.type.StatusType;
import com.codewarts.noriter.article.service.StudyService;
import com.codewarts.noriter.exception.GlobalNoriterException;
import com.codewarts.noriter.exception.type.CommonExceptionType;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/gathering")
@Validated
public class StudyController {

    private final StudyService studyService;
    private final ConversionService conversionService;

    @PostMapping
    public Long register(@RequestBody @Valid StudyPostRequest studyPostRequest,
        HttpServletRequest request) {
        Long memberId = getMemberId(request);
        return studyService.create(studyPostRequest, memberId);
    }

    @GetMapping
    public List<StudyListResponse> gatheringList(
        @RequestParam(required = false)  Map<String, String> paramMap, HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (paramMap.isEmpty()) {
            return studyService.findList(null, accessToken);
        }
        if (paramMap.size() == 1 && paramMap.containsKey("status")) {
            StatusType status = conversionService.convert(paramMap.get("status"), StatusType.class);
            return studyService.findList(status, accessToken);
        }
        throw new GlobalNoriterException(CommonExceptionType.INCORRECT_REQUEST_PARAM);
    }

    @GetMapping("/{id}")
    public StudyDetailResponse gatheringDetail(@PathVariable(required = false)
    @NotNull(message = "ID??? ??????????????????.")
    @Positive(message = "????????? ID??? ??????????????? ?????????.") Long id, HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        return studyService.findDetail(id, accessToken);
    }

    @DeleteMapping("/{id}")
    public void gatheringRemove(@PathVariable(required = false)
    @NotNull(message = "ID??? ??????????????????.")
    @Positive(message = "????????? ID??? ??????????????? ?????????.") Long id, HttpServletRequest request) {
        Long memberId = getMemberId(request);
        studyService.delete(id, memberId);
    }

    @PatchMapping("/{id}")
    public void recruitmentCompletionUpdate(@PathVariable(required = false)
    @NotNull(message = "ID??? ??????????????????.")
    @Positive(message = "????????? ID??? ??????????????? ?????????.") Long id,
        @RequestBody Map<String, StatusType> map,
        HttpServletRequest request) {
        Long memberId = getMemberId(request);
        studyService.updateCompletion(id, memberId, map.get("status"));
    }

    @PutMapping("/{id}")
    public void gatheringEdit(@PathVariable(required = false)
    @NotNull(message = "ID??? ??????????????????.")
    @Positive(message = "????????? ID??? ??????????????? ?????????.") Long id,
        @RequestBody @Valid StudyEditRequest studyEditRequest,
        HttpServletRequest request) {
        Long memberId = getMemberId(request);
        studyService.update(id, studyEditRequest, memberId);
    }

    private Long getMemberId(HttpServletRequest request) {
        return (Long) request.getAttribute("memberId");
    }
}
