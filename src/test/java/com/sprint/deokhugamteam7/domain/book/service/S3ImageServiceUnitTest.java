package com.sprint.deokhugamteam7.domain.book.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugamteam7.exception.book.BookException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
public class S3ImageServiceUnitTest {

  @InjectMocks
  private S3ImageComponent s3ImageComponent;

  @Mock
  private S3Client s3Client;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(s3ImageComponent, "bucketName", "test-bucket");
    ReflectionTestUtils.setField(s3ImageComponent, "baseUrl",
        "https://test-bucket.s3.amazonaws.com");
  }

  @Test
  void uploadImage() throws Exception {
    // given
    String originalFilename = "test-image.jpg";
    byte[] content = "dummy image".getBytes(StandardCharsets.UTF_8);
    MockMultipartFile file = new MockMultipartFile("image", originalFilename, "image/jpeg",
        content);

    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(
        PutObjectRequest.class);
    ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

    // when
    String url = s3ImageComponent.uploadImage(file);

    // then
    verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());
    PutObjectRequest request = requestCaptor.getValue();
    assertAll(
        () -> assertTrue(url.contains("https://test-bucket.s3.amazonaws.com")),
        () -> assertEquals("test-bucket", request.bucket()),
        () -> assertTrue(request.key().contains(originalFilename))
    );
  }


  @Test
  void uploadImage_fail() throws Exception {
    // given
    MultipartFile mockFile = mock(MultipartFile.class);
    when(mockFile.getInputStream()).thenThrow(new IOException("fail"));
    // then
    assertThrows(BookException.class,
        () -> s3ImageComponent.uploadImage(mockFile));
  }
}
