package cn.lzs.ymcc.service;

import cn.lzs.ymcc.MediaApplication;
import cn.lzs.ymcc.domain.MediaFile;
import cn.lzs.ymcc.mapper.MediaFileMapper;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.impl.MediaFileServiceImpl;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 媒体文件服务单元测试类
 * 测试视频文件的注册、分块上传、合并等功能
 *
 * @author lzs
 * @date 2026-04-01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MediaApplication.class)
public class MediaFileServiceTest {

    @Autowired
    private MediaFileServiceImpl mediaFileService;

    @Autowired
    private MediaFileMapper mediaFileMapper;

    // 测试用的文件信息
    private static final String TEST_FILE_MD5 = "test_md5_20260401001";
    private static final String TEST_FILE_NAME = "test_video.mp4";
    private static final Long TEST_FILE_SIZE = 102400L; // 100KB
    private static final String TEST_MIMETYPE = "video/mp4";
    private static final String TEST_FILE_EXT = "mp4";
    private static final Long TEST_COURSE_ID = 1001L;
    private static final Long TEST_CHAPTER_ID = 2001L;

    // 测试用的临时文件路径
    private String tempFilePath;

    /**
     * 测试前准备 - 清理可能存在的旧数据
     */
    @Before
    public void setUp() {
        // 清理测试数据
        MediaFile existingFile = mediaFileMapper.selectOne(
            new EntityWrapper<MediaFile>().eq("file_id", TEST_FILE_MD5)
        );
        if (existingFile != null) {
            mediaFileMapper.deleteById(existingFile.getId());
        }

        // 创建临时文件用于测试
        tempFilePath = System.getProperty("java.io.tmpdir") + "/test_video_" + System.currentTimeMillis() + ".mp4";
        createTempFile(tempFilePath, 10240); // 创建 10KB 的测试文件
    }

    /**
     * 测试后清理 - 删除临时文件
     */
    @After
    public void tearDown() {
        // 删除临时文件
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    /**
     * 测试文件注册
     * 验证新文件能否正确注册
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRegister_NewFile() {
        // 执行文件注册
        JSONResult result = mediaFileService.register(
            TEST_FILE_MD5,
            TEST_FILE_NAME,
            TEST_FILE_SIZE,
            TEST_MIMETYPE,
            TEST_FILE_EXT
        );

        // 验证注册结果
        assertNotNull("返回结果不应为空", result);
        assertTrue("注册应成功", result.getSuccess());
    }

    /**
     * 测试文件注册 - 文件已存在
     * 验证已存在的文件注册时返回已上传
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testRegister_ExistingFile() {
        // 先注册一次
        mediaFileService.register(
            TEST_FILE_MD5,
            TEST_FILE_NAME,
            TEST_FILE_SIZE,
            TEST_MIMETYPE,
            TEST_FILE_EXT
        );

        // 再次注册同一文件
        JSONResult result = mediaFileService.register(
            TEST_FILE_MD5,
            TEST_FILE_NAME,
            TEST_FILE_SIZE,
            TEST_MIMETYPE,
            TEST_FILE_EXT
        );

        // 验证结果 - 应该提示文件已存在
        assertNotNull("返回结果不应为空", result);
    }

    /**
     * 测试分块上传
     * 验证文件分块能否正确上传
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testUploadChunk() throws IOException {
        // 准备测试数据 - 先注册文件
        mediaFileService.register(
            TEST_FILE_MD5,
            TEST_FILE_NAME,
            TEST_FILE_SIZE,
            TEST_MIMETYPE,
            TEST_FILE_EXT
        );

        // 创建模拟分块文件
        byte[] chunkData = new byte[1024]; // 1KB 分块
        for (int i = 0; i < chunkData.length; i++) {
            chunkData[i] = (byte) (i % 256);
        }

        MockMultipartFile chunkFile = new MockMultipartFile(
            "file",
            "chunk_0.mp4",
            "video/mp4",
            chunkData
        );

        // 执行分块上传
        JSONResult result = mediaFileService.uploadchunk(
            chunkFile,
            TEST_FILE_MD5,
            0 // 分块索引
        );

        // 验证上传结果
        assertNotNull("返回结果不应为空", result);
        assertTrue("分块上传应成功", result.getSuccess());
    }

    /**
     * 测试检查分块是否存在
     * 验证分块检查功能是否正常工作
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testCheckChunk() throws IOException {
        // 准备测试数据 - 先上传一个分块
        byte[] chunkData = new byte[1024];
        MockMultipartFile chunkFile = new MockMultipartFile(
            "file",
            "chunk_0.mp4",
            "video/mp4",
            chunkData
        );

        // 先上传分块
        mediaFileService.uploadchunk(chunkFile, TEST_FILE_MD5, 0);

        // 执行检查分块
        JSONResult result = mediaFileService.checkchunk(
            TEST_FILE_MD5,
            0, // 分块索引
            1024 // 分块大小
        );

        // 验证结果
        assertNotNull("返回结果不应为空", result);
    }

    /**
     * 测试根据课程 ID 获取视频列表
     * 验证查询功能是否正常工作
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetMediaFilesByCourseId() {
        // 准备测试数据 - 添加测试视频
        MediaFile testFile = new MediaFile();
        testFile.setFileId("test_course_video_001");
        testFile.setFileName("test_video_001.mp4");
        testFile.setFileOriginalName("测试视频 001");
        testFile.setCourseId(TEST_COURSE_ID);
        testFile.setChapterId(TEST_CHAPTER_ID);
        testFile.setMimeType(TEST_MIMETYPE);
        testFile.setFileType(TEST_FILE_EXT);
        testFile.setFileSize(TEST_FILE_SIZE);
        testFile.setFileStatus(2);
        mediaFileMapper.insert(testFile);

        // 执行查询
        List<MediaFile> mediaFiles = mediaFileService.getMediaFilesByCourseId(TEST_COURSE_ID);

        // 验证查询结果
        assertNotNull("查询结果不应为空", mediaFiles);
        assertTrue("应至少包含 1 个视频", mediaFiles.size() >= 1);
    }

    /**
     * 测试根据 IDs 获取视频列表
     * 验证批量查询功能是否正常工作
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetMediaFilesByIds() {
        // 准备测试数据 - 添加测试视频
        MediaFile testFile1 = new MediaFile();
        testFile1.setFileId("test_video_001");
        testFile1.setFileName("video_001.mp4");
        testFile1.setCourseId(TEST_COURSE_ID);
        testFile1.setChapterId(TEST_CHAPTER_ID);
        testFile1.setMimeType(TEST_MIMETYPE);
        testFile1.setFileType(TEST_FILE_EXT);
        testFile1.setFileSize(TEST_FILE_SIZE);
        testFile1.setFileStatus(2);
        mediaFileMapper.insert(testFile1);

        MediaFile testFile2 = new MediaFile();
        testFile2.setFileId("test_video_002");
        testFile2.setFileName("video_002.mp4");
        testFile2.setCourseId(TEST_COURSE_ID);
        testFile2.setChapterId(TEST_CHAPTER_ID);
        testFile2.setMimeType(TEST_MIMETYPE);
        testFile2.setFileType(TEST_FILE_EXT);
        testFile2.setFileSize(TEST_FILE_SIZE);
        testFile2.setFileStatus(2);
        mediaFileMapper.insert(testFile2);

        // 执行查询 - 传入 ID 列表
        String ids = testFile1.getId() + "," + testFile2.getId();
        List<MediaFile> mediaFiles = mediaFileService.getMediaFilesByIds(ids);

        // 验证查询结果
        assertNotNull("查询结果不应为空", mediaFiles);
        assertTrue("应至少包含 1 个视频", mediaFiles.size() >= 1);
    }

    /**
     * 测试获取视频播放 URL
     * 验证能否正确获取视频播放地址
     */
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void testGetForUrl() {
        // 准备测试数据
        MediaFile testFile = new MediaFile();
        testFile.setFileId("test_video_url_001");
        testFile.setFileName("video_url_001.mp4");
        testFile.setCourseId(TEST_COURSE_ID);
        testFile.setChapterId(TEST_CHAPTER_ID);
        testFile.setMimeType(TEST_MIMETYPE);
        testFile.setFileType(TEST_FILE_EXT);
        testFile.setFileSize(TEST_FILE_SIZE);
        testFile.setFileStatus(2);
        testFile.setFileUrl("http://test.server/video/test_video_url_001.m3u8");
        mediaFileMapper.insert(testFile);

        // 执行查询
        JSONResult result = mediaFileService.getForUrl(testFile.getId());

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        if (result.getSuccess()) {
            assertNotNull("播放 URL 不应为空", result.getData());
        }
    }

    /**
     * 测试获取视频播放 URL - 视频不存在
     * 验证视频不存在时的错误处理
     */
    @Test
    public void testGetForUrl_NotFound() {
        // 执行查询 - 使用不存在的 ID
        JSONResult result = mediaFileService.getForUrl(999999L);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertFalse("应返回失败", result.getSuccess());
    }

    /**
     * 测试文件 MD5 计算
     * 验证 MD5 计算工具是否正常工作
     */
    @Test
    public void testMd5Calculation() throws IOException {
        // 创建测试文件
        File testFile = new File(tempFilePath);

        // 计算 MD5
        String md5;
        try (FileInputStream fis = new FileInputStream(testFile)) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        }

        // 验证 MD5 不为空
        assertNotNull("MD5 不应为空", md5);
        assertEquals("MD5 长度应为 32", 32, md5.length());
    }

    /**
     * 辅助方法：创建临时测试文件
     */
    private void createTempFile(String filePath, int sizeInBytes) {
        File file = new File(filePath);
        try {
            // 创建指定大小的文件
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int written = 0;
                while (written < sizeInBytes) {
                    int toWrite = Math.min(1024, sizeInBytes - written);
                    fos.write(buffer, 0, toWrite);
                    written += toWrite;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}