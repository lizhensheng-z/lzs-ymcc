package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.api.MediaFileApi;
import cn.lzs.ymcc.domain.CourseChapter;
import cn.lzs.ymcc.domain.MediaFile;
import cn.lzs.ymcc.mapper.CourseChapterMapper;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.ICourseChapterService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程章节 ， 一个课程，多个章节，一个章节，多个视频 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-23
 */
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements ICourseChapterService {

    @Autowired
    private MediaFileApi mediaFileApi;
    @Autowired
    private CourseChapterMapper courseChapterMapper;
    @Override
    public List<CourseChapter> listByCourseId(Long id) {
        EntityWrapper<CourseChapter> courseChapterEntityWrapper = new EntityWrapper<>();
        courseChapterEntityWrapper.eq("course_id",id);
        List<CourseChapter> courseChapters = courseChapterMapper.selectList(courseChapterEntityWrapper);
        if (courseChapters == null) {
            return Collections.emptyList();
        }
        return courseChapters;
    }
    @Override
    public List<CourseChapter> getCourseChaptersByCourseId(Long courseId) {
        //根据courseId查出对应的章节，和视频，然后进行配对
       //查出所有章节
        List<CourseChapter> courseChapters = listByCourseId(courseId);
        if (courseChapters == null) {
            return Collections.emptyList();
        }
        
        //远程调用，查出所有视频 - 添加异常处理防止Media服务超时影响主流程
        List<MediaFile> mediaFiles = null;
        try {
            JSONResult mediaFilesByCourseId = mediaFileApi.getMediaFilesByCourseId(courseId);
            Object data = mediaFilesByCourseId.getData();
            String jsonString = JSONObject.toJSONString(data);
            mediaFiles = JSONObject.parseArray(jsonString, MediaFile.class);
        } catch (feign.RetryableException e) {
            // Media服务超时，记录日志，返回章节但不包含视频
            System.err.println("Media服务调用超时，courseId=" + courseId + ", 错误: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常也捕获，保证主流程不受影响
            System.err.println("Media服务调用异常，courseId=" + courseId + ", 错误: " + e.getMessage());
        }
        
        //将章节和视频进行配对
        final List<MediaFile> finalMediaFiles = mediaFiles;
        
        // 调试日志：打印章节和视频的ID对比
        if (finalMediaFiles != null && !finalMediaFiles.isEmpty()) {
            System.out.println("=== 调试信息：章节与视频配对 ===");
            System.out.println("章节数量: " + courseChapters.size());
            for (CourseChapter chapter : courseChapters) {
                System.out.println("章节ID: " + chapter.getId() + " (类型: " + chapter.getId().getClass().getName() + ")");
            }
            System.out.println("视频数量: " + finalMediaFiles.size());
            for (MediaFile file : finalMediaFiles) {
                System.out.println("视频 chapterId: " + file.getChapterId() + " (类型: " + 
                    (file.getChapterId() != null ? file.getChapterId().getClass().getName() : "null") + ")");
            }
        }
        
        courseChapters.forEach(chapter -> {
            List<MediaFile> files = Optional.ofNullable(finalMediaFiles)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(file -> Objects.equals(file.getChapterId(), chapter.getId()))
                    .collect(Collectors.toList());
            System.out.println("章节 " + chapter.getId() + " 匹配到 " + files.size() + " 个视频");
            chapter.setMediaFiles(files);
        });

        return courseChapters;
    }
}
