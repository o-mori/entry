package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class BoxUtil {
    private static final Logger logger = LoggerFactory.getLogger(BoxUtil.class);

    @Value("${box.folder.base.path}")
    private String basePath;

    /**
     * BoxフォルダIDからフォルダパスを取得します。
     * 
     * @param boxFolderId Boxフォルダのユニークな識別子
     * @return フォルダの絶対パス
     */
    public String getBoxFolderPath(String boxFolderId) {
        try {
            Path folderPath = Paths.get(basePath, boxFolderId);
            return folderPath.toString();
        } catch (Exception e) {
            logger.error("フォルダパスの生成中にエラーが発生しました: ", e);
            return null;
        }
    }
}
