package com.example.demo.downloder;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Boxからファイルをダウンロードするクラス。
 */
public class BoxDownloader {
    private static final Logger LOGGER = Logger.getLogger(BoxDownloader.class.getName());
    private final BoxAPIConnection api;

    /**
     * BoxDownloaderのインスタンスを作成します。
     *
     * @param accessToken Box APIにアクセスするための認証トークン
     */
    public BoxDownloader(String accessToken) {
        this.api = new BoxAPIConnection(accessToken);
    }

    /**
     * 指定されたBoxフォルダからファイルをダウンロードします。
     *
     * @param folderId ダウンロード元のBoxフォルダID
     * @param fileNameRegex ダウンロードするファイルを特定する正規表現
     * @param localDownloadPath ダウンロードしたファイルを保存するローカルフォルダパス
     * @return ダウンロードしたファイルのローカルパスのリスト
     */
    public List<Path> download(String folderId, String fileNameRegex, String localDownloadPath) {
        var downloadedFiles = new ArrayList<Path>();
        var pattern = Pattern.compile(fileNameRegex);
        
        try {
            var folder = new BoxFolder(api, folderId);
            var downloadDir = new File(localDownloadPath);
            
            // ダウンロードディレクトリが存在しない場合は作成
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            
            folder.forEach(itemInfo -> {
                if (itemInfo instanceof BoxFile.Info fileInfo) {
                    if (pattern.matcher(fileInfo.getName()).matches()) {
                        try {
                            var file = new BoxFile(api, fileInfo.getID());
                            var localFilePath = Paths.get(localDownloadPath, fileInfo.getName());
                            
                            // ファイルをダウンロード
                            try (OutputStream outputStream = new FileOutputStream(localFilePath.toFile())) {
                                file.download(outputStream);
                                downloadedFiles.add(localFilePath);
                                LOGGER.info("ファイルをダウンロードしました: " + localFilePath);
                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, 
                                "ファイルのダウンロード中にエラーが発生しました: " + fileInfo.getName(), e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "フォルダの処理中にエラーが発生しました", e);
        }
        
        return downloadedFiles;
    }
}
