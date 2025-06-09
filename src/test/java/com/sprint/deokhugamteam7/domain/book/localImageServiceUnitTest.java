package com.sprint.deokhugamteam7.domain.book;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.deokhugamteam7.domain.book.service.LocalImageComponent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class localImageServiceUnitTest {

  @TempDir
  Path tempDir;

  @Test
  void initTest_Success() {
    // given
    Path customPath = tempDir.resolve("new-folder");
    String path = customPath.toString();
    // when
    LocalImageComponent localImageComponent = new LocalImageComponent(path);
    // then
    assertAll(
        () -> assertTrue(Files.exists(customPath)),
        () -> assertTrue(Files.isDirectory(customPath))
    );
  }

  @Test
  void uploadImage_Success() {
    // given
    Path customPath = tempDir.resolve("new-folder");
    String path = customPath.toString();
    MockMultipartFile file1 = new MockMultipartFile("test.jpg", new byte[0]);
    MockMultipartFile file2 = new MockMultipartFile("image", "test-image.jpg", "image/jpeg",
        "dummy image".getBytes(StandardCharsets.UTF_8));
    // when
    LocalImageComponent localImageComponent = new LocalImageComponent(path);
    String s1 = localImageComponent.uploadImage(file1);
    String s2 = localImageComponent.uploadImage(file2);
    // then
    assertNotNull(s1);
    assertNotNull(s2);
  }

}
