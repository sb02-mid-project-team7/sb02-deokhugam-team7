package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "s3")
@Component
@RequiredArgsConstructor
public class S3ImageComponent implements ImageComponent {

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Value("${aws.s3.base-url}")
  private String baseUrl;

  private final S3Client s3Client;

  public String uploadImage(MultipartFile file) {
    String s3Key = UUID.randomUUID() + "-" + file.getOriginalFilename();
    String s3Url = baseUrl + "/" + s3Key;
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(s3Key)
        .contentType(file.getContentType())
        .build();
    try (InputStream is = file.getInputStream()) {
      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(is, file.getSize()));
    } catch (IOException e) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    }
    return s3Url;
  }
}
