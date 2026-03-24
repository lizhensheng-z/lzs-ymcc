package cn.lzs.ymcc.api;


import cn.lzs.ymcc.domain.MediaFile;
import cn.lzs.ymcc.result.JSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李振生
 */
@FeignClient(name = "service-media")
@RequestMapping("/mediaFile")
public interface MediaFileApi {

    @GetMapping("/getMediaFilesByCourseId/{courseId}")
    JSONResult getMediaFilesByCourseId(@PathVariable("courseId") Long courseId);
}


