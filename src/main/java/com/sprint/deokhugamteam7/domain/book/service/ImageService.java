package com.sprint.deokhugamteam7.domain.book.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  String uploadImage(MultipartFile file);
}
