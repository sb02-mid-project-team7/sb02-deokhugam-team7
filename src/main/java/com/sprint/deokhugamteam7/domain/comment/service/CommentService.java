package com.sprint.deokhugamteam7.domain.comment.service;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;

  public CommentDto create(CommentCreateRequest commentCreateRequest) {
    log.debug("메시지 생성 시작: request={}", commentCreateRequest);
    return null;
  }

  public CommentDto update(CommentUpdateRequest commentUpdateRequest) {
    log.debug("메시지 수정 시작: request={}", commentUpdateRequest);
    return null;
  }

  public void deleteHard(UUID commentId, UUID userId) {

  }

  public void deleteSoft(UUID commentId, UUID userId) {

  }


  public CursorPageResponseCommentDto getCommentList(UUID reviewId, String direction, String cursor,
      LocalDateTime after, int limit) {

    return null;
  }
}
