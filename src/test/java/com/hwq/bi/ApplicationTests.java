package com.hwq.bi;

import com.hwq.bi.utils.datasource.ExcelUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootTest
public class ApplicationTests {

    @Resource
    private MongoTemplate mongoTemplate;

    @MockBean
    private ServerEndpointExporter serverEndpointExporter;

    @Resource
    private ExcelUtils excelUtils;



    @Test
    public void testMySQL() {
        ExcelUtils excelUtils = new ExcelUtils();
        String curDir = System.getProperty("user.dir");
        // 指定Excel文件的文件名
        String fileName = "example2.xlsx";  // 请替换为你的Excel文件名
        // 构造文件路径
        Path filePath = Paths.get(curDir, fileName);
        // 获取InputStream
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            // 你可以在这里使用InputStream处理Excel文件
            long start = System.currentTimeMillis();
            System.out.println("成功获取文件的InputStream: " + filePath);
            excelUtils.saveDataToMySQL(inputStream, 1000002L);
            long end = System.currentTimeMillis();
            System.out.println("mysql：" + (end - start) + "ms");
        } catch (IOException e) {
            System.err.println("无法读取文件: " + filePath);
            e.printStackTrace();
        }
    }

    @Test
    public void testRedis() {
        System.out.println("hello");
    }

}