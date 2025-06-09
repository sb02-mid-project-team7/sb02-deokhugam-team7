package com.sprint.deokhugamteam7.domain.book.service;

import com.sprint.deokhugamteam7.exception.ErrorCode;
import com.sprint.deokhugamteam7.exception.book.BookException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "local")
@Component
public class LocalImageComponent implements ImageComponent {

  private final Path storagePath;

  public LocalImageComponent(
      @Value("${deokhugam.storage.local.root-path}") String storagePath) {
    this.storagePath = Paths.get(storagePath);
    init();
  }

  private void init() {
    if (Files.notExists(storagePath)) {
      try {
        Files.createDirectories(storagePath);
      } catch (IOException e) {
        throw new BookException(ErrorCode.INTERNAL_SERVER_ERROR);
      }
    }
  }

  private static String extractExtension(String fileName) {
    String[] nameSplit = fileName.split("\\.");
    if (nameSplit.length > 1) {
      return "." + nameSplit[1].toLowerCase();
    } else {
      return "";
    }
  }

  @Override
  public String uploadImage(MultipartFile file) {
    String filename = file.getOriginalFilename();
    String extension = ".png";
    if (filename != null) {
      extension = extractExtension(filename);
    }
    Path destination = storagePath.resolve(
        UUID.randomUUID() + extension);
    try (OutputStream outputStream = Files.newOutputStream(destination)) {
      outputStream.write(file.getBytes());
    } catch (IOException e) {
      throw new BookException(ErrorCode.INTERNAL_BAD_REQUEST);
    }
    return "/images/" + destination.getFileName();
  }
}
