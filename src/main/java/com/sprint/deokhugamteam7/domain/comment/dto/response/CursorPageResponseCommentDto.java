package com.sprint.deokhugamteam7.domain.comment.dto.response;

import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CursorPageResponseCommentDto(
	List<CommentDto> content,
	UUID nextCursor, // 다음 페이지 찾을때의 기준 ID (현재 페이지의 마지막 요소의 ID)
	LocalDateTime nextAfter, // 다음 페이지 찾을때의 기준 시간 (현재 페이지의 마지막 요소의 시간)
	int size,
	Long totalElements, // TODO 전체 댓글 수를 여기서 반환하지 말고 따로 뺴는 것이 더 효율적일 것 같음 .
	boolean hasNext
) {

}
