package cn.lzs.ymcc.service;

import cn.lzs.ymcc.domain.MediaFile;
import cn.lzs.ymcc.query.MediaFileQuery;
import cn.lzs.ymcc.result.JSONResult;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzs
 * @since 2025-07-08
 */
public interface IMediaFileService extends IService<MediaFile> {
    /**
     * 文件上传之前的注册功能
     */
    JSONResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);

    /**
     * 校验文件块是否已经存在了
     */
    JSONResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize);

    /**
     * 上传文件块
     */
    JSONResult uploadchunk(MultipartFile file, String fileMd5, Integer chunk);

    /**
     * 合并文件快
     */
    JSONResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt,
                           Long courseChapterId, Long courseId, Integer number, String name, String courseName, String chapterName);

    //处理文件
    JSONResult handleFile2m3u8(MediaFile mediaFile);


    List<MediaFile> getMediaFilesByIds(String ids);

    List<MediaFile> getMediaFilesByCourseId(Long courseId);


    JSONResult getForUrl(Long mediaId);

    /**
     * 带条件分页查询媒体文件
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    Page<MediaFile> selectPageWithCondition(Page<MediaFile> page, MediaFileQuery query);
}
