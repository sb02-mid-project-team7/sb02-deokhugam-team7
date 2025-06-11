package com.sprint.deokhugamteam7.domain.log;

import com.sprint.deokhugamteam7.constant.LogType;
import com.sprint.deokhugamteam7.exception.DeokhugamException;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogService {

  @Value("${aws.s3.bucket}")
  private String bucketName;

  private final S3Client s3Client;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String LOG_EXTENSION = ".log";
  private static final String TXT_EXTENSION = ".txt";
  private static final String BASE_DIR = "logs/";

  @Scheduled(cron = "0 10 0 * * *")
  public void logUploadToS3() {

    LocalDate yesterday = LocalDate.now().minusDays(1);
    String dateStr = yesterday.format(FORMATTER);

    File myappLog = new File(LogType.MYAPP.getLogDir() + dateStr + LOG_EXTENSION);
    File errorLog =  new File(LogType.ERROR.getLogDir() + dateStr + TXT_EXTENSION);

    uploadIfFileExists(myappLog, myappLog.getName());
    uploadIfFileExists(errorLog, errorLog.getName());
  }

  private void uploadIfFileExists(File file, String s3Key) {
    String s3Url = BASE_DIR + s3Key;
    if (file.exists()) {
      try {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Url)
          .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
      } catch (Exception e) {
         throw new DeokhugamException(ErrorCode.INTERNAL_SERVER_ERROR);
      }
    }
  }
}
