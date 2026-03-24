package cn.lzs.ymcc.mapper;

import cn.lzs.ymcc.domain.MediaFile;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzs
 * @since 2025-07-08
 */
public interface MediaFileMapper extends BaseMapper<MediaFile> {

    List<MediaFile> getMediaFilesByIds(String ids);

    List<MediaFile> getMediaFilesByCourseId(Long courseId);
}
