package com.game.copycat.controller;

import com.game.copycat.dto.ImageRequest;
import com.game.copycat.dto.ResultResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Value("${image.path}")
    private String path; // 이미 저장 경로
    @PostMapping
    public ResponseEntity<ResultResponse> postImage(@RequestBody ImageRequest imageRequest) {
        String imagePath = path + imageRequest.getRoomId() + ".png";
        try {
            // 디렉토리 경로가 존재하지 않으면 생성
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 이미지 데이터 디코딩 및 저장
            byte[] imageData = Base64.getDecoder().decode(imageRequest.getBase64Image());

            try (FileOutputStream fos = new FileOutputStream(imagePath)) {
                fos.write(imageData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ResultResponse res = ResultResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("올바른 데이터가 아닙니다.").build();
            return new ResponseEntity<>(res, HttpStatus.BAD_GATEWAY);
        }
        ResultResponse res = ResultResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("이미지 저장에 성공했습니다.").build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);

    }
}
