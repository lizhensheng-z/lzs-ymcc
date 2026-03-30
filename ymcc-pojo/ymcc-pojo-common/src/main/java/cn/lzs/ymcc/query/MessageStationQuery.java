package cn.lzs.ymcc.query;


/**
 *
 * @author lzs
 * @since 2025-07-15
 */
public class MessageStationQuery extends BaseQuery{

    //用户ID
    private Long userId;

    //消息类型
    private String type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}