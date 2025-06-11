package com.sprint.deokhugamteam7.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.deokhugamteam7.domain.comment.dto.CommentDto;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentCreateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.request.CommentUpdateRequest;
import com.sprint.deokhugamteam7.domain.comment.dto.response.CursorPageResponseCommentDto;
import com.sprint.deokhugamteam7.domain.comment.repository.CommentRepository;
import com.sprint.deokhugamteam7.domain.comment.service.CommentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private CommentService commentService;

	@InjectMocks
	private CommentController commentController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
	}

	@Test
	@DisplayName("POST /api/comments - 댓글 생성 API 테스트")
	void createTest() throws Exception {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String content = "테스트 댓글 내용";
		CommentCreateRequest request = new CommentCreateRequest(reviewId, userId, content);
		CommentDto responseDto = new CommentDto(UUID.randomUUID(), reviewId, userId, "유저닉네임",
			content, LocalDateTime.now(), LocalDateTime.now());

		given(commentService.create(any(CommentCreateRequest.class))).willReturn(responseDto);

		// when & then
		mockMvc.perform(post("/api/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(responseDto.id().toString()));
	}

	@Test
	@DisplayName("PATCH /api/comments/{commentId} - 댓글 수정 API 테스트")
	void updateTest() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String newContent = "수정된 댓글 내용";
		CommentUpdateRequest request = new CommentUpdateRequest(newContent);
		CommentDto responseDto = new CommentDto(commentId, UUID.randomUUID(), userId, "유저닉네임",
			newContent, LocalDateTime.now(), LocalDateTime.now());

		given(commentService.update(eq(commentId), eq(userId),
			any(CommentUpdateRequest.class))).willReturn(responseDto);

		// when
		ResultActions actions = mockMvc.perform(patch("/api/comments/{commentId}", commentId)
			.header("Deokhugam-Request-User-ID", userId.toString())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(commentId.toString()))
			.andExpect(jsonPath("$.content").value(newContent))
			.andDo(print());
	}

	@Test
	@DisplayName("DELETE /api/comments/{commentId} - 댓글 논리적 삭제 API 테스트")
	void deleteTest() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		// when
		ResultActions actions = mockMvc.perform(delete("/api/comments/{commentId}", commentId)
			.header("Deokhugam-Request-User-ID", userId.toString()));

		// then
		actions
			.andExpect(status().isNoContent())
			.andDo(print());
	}

	@Test
	@DisplayName("GET /api/comments - 댓글 목록 조회 API 테스트")
	void readTest() throws Exception {
		// given
		UUID reviewId = UUID.randomUUID();
		CommentDto comment1 = new CommentDto(UUID.randomUUID(), reviewId, UUID.randomUUID(),
			"user1", "content1", LocalDateTime.now(), LocalDateTime.now());
		CommentDto comment2 = new CommentDto(UUID.randomUUID(), reviewId, UUID.randomUUID(),
			"user2", "content2", LocalDateTime.now(), LocalDateTime.now());
		CursorPageResponseCommentDto response = new CursorPageResponseCommentDto(
			List.of(comment1, comment2), comment2.id(), comment2.createdAt(), 2, 10L, true);

		given(commentService.getCommentList(eq(reviewId), any(String.class), any(), any(),
			any(int.class))).willReturn(response);

		// when
		ResultActions actions = mockMvc.perform(get("/api/comments")
			.param("reviewId", reviewId.toString())
			.param("limit", "10"));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].content").value("content1"))
			.andExpect(jsonPath("$.hasNext").value(true))
			.andDo(print());
	}

	@Test
	void deleteHardApiTest() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		// when
		mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
				.header("Deokhugam-Request-User-ID", userId.toString()))
			.andExpect(status().isNoContent()); // HTTP 204 응답을 기대

		// then
		verify(commentService).deleteHard(eq(commentId), eq(userId));
	}

	@Test
	void getCommentSuccess() throws Exception {
		// given
		UUID commentId = UUID.randomUUID();
		UUID reviewId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		CommentDto responseDto = new CommentDto(commentId, reviewId, userId, "test-user",
			"test-content", LocalDateTime.now(), LocalDateTime.now());

		given(commentService.getComment(commentId)).willReturn(responseDto);

		// when
		ResultActions actions = mockMvc.perform(get("/api/comments/{commentId}", commentId)
			.accept(MediaType.APPLICATION_JSON));

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(commentId.toString()))
			.andExpect(jsonPath("$.content").value("test-content"))
			.andExpect(jsonPath("$.userNickname").value("test-user"))
			.andDo(print());
	}
}
