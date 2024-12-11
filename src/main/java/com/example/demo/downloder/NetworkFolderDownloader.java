package com.example.demo.downloder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * ネットワークフォルダからファイルをダウンロードするクラス。
 */
@Component
public class NetworkFolderDownloader {
    private static final Logger LOGGER = Logger.getLogger(NetworkFolderDownloader.class.getName());

    /**
     * ネットワークフォルダからファイルをダウンロードします。
     *
     * @param networkFolderPath ネットワークフォルダのパス
     * @return ダウンロードしたファイル
     * @throws IOException ファイルのコピー中にエラーが発生した場合
     */
    public File downloadFile(String networkFolderPath) throws IOException {
        File sourceFile = new File(networkFolderPath);
        
        // ファイルが存在しない場合はnullを返す
        if (!sourceFile.exists()) {
            LOGGER.warning("指定されたファイルが見つかりません: " + networkFolderPath);
            return null;
        }
        
        // 一時的なダウンロードディレクトリを作成
        File tempDownloadDir = new File(System.getProperty("java.io.tmpdir"), "network-folder-downloads");
        if (!tempDownloadDir.exists()) {
            tempDownloadDir.mkdirs();
        }
        
        // ダウンロード先のファイルパスを生成
        File destinationFile = new File(tempDownloadDir, sourceFile.getName());
        
        // ファイルをコピー
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("ネットワークフォルダからファイルをダウンロードしました: " + destinationFile.getAbsolutePath());
            return destinationFile;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "ファイルのコピー中にエラーが発生しました", e);
            throw e;
        }
    }

    /**
     * 指定されたディレクトリ内のファイルをダウンロードします。
     *
     * @param networkFolderPath ネットワークフォルダのパス
     * @param fileNamePattern ダウンロードするファイル名のパターン（正規表現）
     * @return ダウンロードしたファイルのリスト
     * @throws IOException ファイルのコピー中にエラーが発生した場合
     */
    public List<File> downloadFiles(String networkFolderPath, String fileNamePattern) throws IOException {
        File sourceDir = new File(networkFolderPath);
        List<File> downloadedFiles = new ArrayList<>();
        
        // ディレクトリが存在しない場合は空のリストを返す
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            LOGGER.warning("指定されたディレクトリが見つかりません: " + networkFolderPath);
            return downloadedFiles;
        }
        
        // 一時的なダウンロードディレクトリを作成
        File tempDownloadDir = new File(System.getProperty("java.io.tmpdir"), "network-folder-downloads");
        if (!tempDownloadDir.exists()) {
            tempDownloadDir.mkdirs();
        }
        
        // パターンをコンパイル
        Pattern pattern = Pattern.compile(fileNamePattern);
        
        // ファイルをフィルタリングしてダウンロード
        File[] sourceFiles = sourceDir.listFiles(file -> 
            file.isFile() && pattern.matcher(file.getName()).matches()
        );
        
        if (sourceFiles != null) {
            for (File sourceFile : sourceFiles) {
                try {
                    File destinationFile = new File(tempDownloadDir, sourceFile.getName());
                    Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.info("ネットワークフォルダからファイルをダウンロードしました: " + destinationFile.getAbsolutePath());
                    downloadedFiles.add(destinationFile);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "ファイルのコピー中にエラーが発生しました: " + sourceFile.getName(), e);
                }
            }
        }
        
        return downloadedFiles;
    }
}
