package com.hwq.dataloom.service.impl.strategy;

import com.hwq.dataloom.config.WebSocketConfig;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.utils.datasource.ExcelUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;


/**
 * @author HWQ
 * @date 2024/8/28 09:48
 * @description
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestDatasource {
    @Resource
    private ExcelDatasourceServiceImpl excelDatasourceService;

    @Resource
    private ExcelUtils excelUtils;

    private User testUser = new User();

    private MultipartFile multipartFile;

    @PostConstruct
    public void init() {
        User user = new User();
        user.setId(1697633200786403330L);
        user.setUserAccount("goat");
        user.setUserName("goat");
        this.testUser = user;

        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return "example.xlsx";
            }

            @Override
            public String getOriginalFilename() {
                return "example.xlsx";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @SneakyThrows
            @Override
            public long getSize() {
                return getInputStream().available(); // 获取实际大小
            }

            @Override
            public byte[] getBytes() throws IOException {
                // 获取当前类的类加载器
                ClassLoader classLoader = getClass().getClassLoader();

                // 从 resources 目录加载 example.xlsx 文件
                InputStream inputStream = classLoader.getResourceAsStream("example.xlsx");
                try (
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    return outputStream.toByteArray();
                }
            }

            @Override
            public InputStream getInputStream() throws IOException {
                // 获取当前类的类加载器
                ClassLoader classLoader = getClass().getClassLoader();

                // 从 resources 目录加载 example.xlsx 文件
                InputStream inputStream = classLoader.getResourceAsStream("example.xlsx");

                // 确保文件存在
                if (inputStream == null) {
                    throw new FileNotFoundException("File not found in resources directory");
                }

                return inputStream;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        this.multipartFile = multipartFile;
    }

    @Test
    public void testDatasource() {
        // 获取当前类的类加载器
        ClassLoader classLoader = getClass().getClassLoader();

        // 从 resources 目录加载 example.xlsx 文件
        InputStream inputStream = classLoader.getResourceAsStream("example.xlsx");
        excelUtils.parseAndSaveFile(1L, "example.xlsx", inputStream);
    }
    //        DatasourceDTO datasourceDTO = new DatasourceDTO();
//        datasourceDTO.setPid(0L);
//        datasourceDTO.setName("测试excel");
//        datasourceDTO.setType("excel");
//        datasourceDTO.setConfiguration("ascase");
//        datasourceDTO.setFileName("example");
//        datasourceDTO.setMultipartFile(this.multipartFile);
//        excelDatasourceService.addCoreData(datasourceDTO, testUser);
}
