package com.example.demo.service;

import com.example.demo.entity.FunctionalObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FunctionalObjectService {
    List<FunctionalObject> save(MultipartFile file,String accessToken);
    List<FunctionalObject> findAll();
}
