package com.sparta.backend.workspace.controller;

import com.sparta.backend.workspace.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/foot/teams")
@RequiredArgsConstructor
@Slf4j
public class S3ImageController {
    private final S3ImageService s3ImageService;

    //클라이언트 -> 이미지 파일 받음 -> 서비스 클래스로 전달 -> 서비스에서 S3에 업로드 -> 업로드 된 URL을 클라이언트한테 반환
    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String profileImage = s3ImageService.upload(image); //service에서 이미지를 s3에 업로드하고, 업로드 된 이미지의 url을 반환
        return ResponseEntity.ok(profileImage); //s3에 업로드된 이미지의 url을 http 응답으로 반환 -> 프론트엔드는 업로드된 이미지의 url을 받음
        ////MultipartFile 형태로 이미지를 받고, S3ImageService를 이용해 이미지를 업로드한 후 반환된 URL을 응답으로 보냄
    }

    @GetMapping("/s3/delete")
    public ResponseEntity<?> s3delete(@RequestParam String addr){
        s3ImageService.deleteImageFromS3(addr);
        return ResponseEntity.ok(null);
    }
}
