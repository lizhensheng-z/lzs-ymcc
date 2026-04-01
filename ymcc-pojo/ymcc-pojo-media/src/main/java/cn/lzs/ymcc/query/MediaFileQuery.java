package cn.lzs.ymcc.query;


/**
 * 媒体文件查询对象
 * @author lzs
 * @since 2025-07-08
 */
public class MediaFileQuery extends BaseQuery{

    /**
     * 视频名称模糊查询
     */
    private String name;

    /**
     * 课程 ID 精确查询
     */
    private Long courseId;

    /**
     * 章节 ID 精确查询
     */
    private Long chapterId;

    /**
     * 文件状态查询（1 未处理 2 处理成功 3 处理失败 4 无需处理）
     */
    private Integer fileStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Integer getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }
}