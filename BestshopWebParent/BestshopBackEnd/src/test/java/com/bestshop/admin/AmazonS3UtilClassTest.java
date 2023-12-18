package com.bestshop.admin;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

class AmazonS3UtilClassTest {

    @Test
    public void testListFolder(){
        String folderName = "test-upload";
        List<String> listKeys = AmazonS3Util.listFolder(folderName);
        listKeys.forEach(System.out::println);
    }

    @Test
    public void testUploadFile() throws FileNotFoundException {
        String folderName = "test-image";
        String fileName = "test-image.png";
        String filePath = "D:\\test\\" + fileName;

        InputStream inputStream = new FileInputStream(filePath);

        AmazonS3Util.uploadFile(folderName, fileName, inputStream);
    }

    @Test
    public void testDeleteFile() {
        String fileName = "test-image - Cópia (2).png";
        AmazonS3Util.deleteFile(fileName);
    }

    @Test
    public void testRemoveFolder() {
        String folderName = "test-image";
        AmazonS3Util.removeFolder(folderName);
    }

}