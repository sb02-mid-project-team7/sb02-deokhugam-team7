package com.sprint.deokhugamteam7.domain.book.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class BarcodeService {
  public String extractIsbn(MultipartFile file) {
    try {
      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
      }
      BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
          image);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Result result = new MultiFormatReader().decode(bitmap);
      if (result.getBarcodeFormat() != BarcodeFormat.EAN_13) {
        throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
      }
      return result.getText();
    } catch (NotFoundException e) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    } catch (IOException e) {
      throw new BookException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
