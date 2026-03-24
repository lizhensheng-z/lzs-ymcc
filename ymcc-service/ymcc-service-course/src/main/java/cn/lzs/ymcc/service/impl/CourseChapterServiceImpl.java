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
        //远程调用，查出所有视频
        JSONResult mediaFilesByCourseId = mediaFileApi.getMediaFilesByCourseId(courseId);
        Object data = mediaFilesByCourseId.getData();
        String jsonString = JSONObject.toJSONString(data);
        List<MediaFile> mediaFiles = JSONObject.parseArray(jsonString, MediaFile.class);
        //将章节和视频进行配对
        courseChapters.forEach(chapter -> {
            List<MediaFile> files = Optional.ofNullable(mediaFiles)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(file -> Objects.equals(file.getChapterId(), chapter.getId()))
                    .collect(Collectors.toList());
            chapter.setMediaFiles(files);
        });

        return courseChapters;
    }
}
