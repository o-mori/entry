package com.example.demo.controller;

import com.example.demo.downloder.NetworkFolderDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/network-folder")
public class NetworkFolderDownloadController {

    @Autowired
    private NetworkFolderDownloader networkFolderDownloader;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String filePath) {
        try {
            // ネットワークフォルダからファイルをダウンロード
            File downloadedFile = networkFolderDownloader.downloadFile(filePath);

            // ファイルが存在しない場合は404エラーを返す
            if (downloadedFile == null || !downloadedFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // ファイルをバイト配列に変換
            byte[] content = Files.readAllBytes(downloadedFile.toPath());

            // HTTPレスポンスヘッダーを設定
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("filename", downloadedFile.getName());
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            // エラーハンドリング
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download-multiple")
    public ResponseEntity<List<byte[]>> downloadFiles(
            @RequestParam String folderPath,
            @RequestParam String fileNamePattern) {
        try {
            // ネットワークフォルダからファイルをダウンロード
            List<File> downloadedFiles = networkFolderDownloader.downloadFiles(folderPath, fileNamePattern);

            // ファイルが存在しない場合は404エラーを返す
            if (downloadedFiles == null || downloadedFiles.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // ファイルをバイト配列に変換
            List<byte[]> contents = new ArrayList<>();
            for (File file : downloadedFiles) {
                contents.add(Files.readAllBytes(file.toPath()));
            }

            // HTTPレスポンスヘッダーを設定
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(contents, headers, HttpStatus.OK);

        } catch (IOException e) {
            // エラーハンドリング
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
