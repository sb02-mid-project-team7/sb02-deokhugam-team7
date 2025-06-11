package com.sprint.deokhugamteam7.domain.book.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.deokhugamteam7.exception.book.BookException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;

@SpringBootTest
public class BarcodeServiceTest {

  @Autowired
  BarcodeService barcodeService;

  @Test
  void extractIsbn_Success() throws Exception {
    ClassPathResource resource = new ClassPathResource("file/barcode.png");
    byte[] fileBytes = StreamUtils.copyToByteArray(resource.getInputStream());
    MockMultipartFile file = new MockMultipartFile(
        "image", "barcode1.png", "image/png", fileBytes
    );
    // when
    String isbn = barcodeService.extractIsbn(file);

    // then
    assertEquals("9788996870678", isbn);
  }

  @Test
  void extractIsbn_WithEan8_Fail() throws Exception {
    ClassPathResource resource = new ClassPathResource("file/ean.png");
    byte[] fileBytes = StreamUtils.copyToByteArray(resource.getInputStream());
    MockMultipartFile file = new MockMultipartFile(
        "image", "ean.png", "image/png", fileBytes
    );
    // then
    assertThrows(BookException.class, () -> barcodeService.extractIsbn(file));
  }

  @Test
  void extractIsbn_WithNoBarcode_Fail()throws Exception {
    // given
    ClassPathResource resource = new ClassPathResource("file/base.jpg");
    byte[] fileBytes = StreamUtils.copyToByteArray(resource.getInputStream());
    MockMultipartFile file = new MockMultipartFile(
        "image", "base.jpg", "image/jpeg", fileBytes
    );
    // then
    assertThrows(BookException.class, () -> barcodeService.extractIsbn(file));
  }

  @Test
  void extractIsbn_Fail() {
    // given
    MockMultipartFile file = new MockMultipartFile("image", new byte[0]);
    // then
    assertThrows(BookException.class, () -> barcodeService.extractIsbn(file));
  }

}
