package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.api.MediaFileApi;
import cn.lzs.ymcc.domain.MediaFile;
import cn.lzs.ymcc.result.JSONResult;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courseVideo")
public class CourseVideoController {

    @Autowired
    private MediaFileApi mediaFileApi;

    /**
     * 根据章节ID获取视频列表
     */
    @GetMapping("/listByChapterId/{chapterId}")
    public JSONResult listByChapterId(@PathVariable("chapterId") Long chapterId) {
        // 从章节服务获取章节信息（包括courseId）
        // 这里简化处理，假设chapterId就是章节ID
        // 需要通过章节获取课程ID，再获取视频
        // 由于没有直接的章节查询接口，这里返回空列表
        // 前端可以先调用 /courseChapter/listByCourseId 获取章节和课程关系
        return JSONResult.success(new java.util.ArrayList<>());
    }

    /**
     * 根据课程ID获取视频列表
     */
    @GetMapping("/listByCourseId/{courseId}")
    public JSONResult listByCourseId(@PathVariable("courseId") Long courseId) {
        try {
            JSONResult result = mediaFileApi.getMediaFilesByCourseId(courseId);
            if (result != null && result.getData() != null) {
                String jsonString = JSONObject.toJSONString(result.getData());
                List<MediaFile> mediaFiles = JSONObject.parseArray(jsonString, MediaFile.class);
                // 按章节ID分组
                return JSONResult.success(mediaFiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONResult.success(new java.util.ArrayList<>());
    }
}