# 用户端订单日期范围查询功能实现文档

## 一、需求说明

实现用户端"我的订单"页面按照日期范围查询用户订单的功能，支持：
- 按日期范围筛选订单
- 按订单状态筛选
- 分页查询
- 返回订单明细信息

---

## 二、表结构分析

### 2.1 课程订单主表 `t_course_order`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID(自增) |
| order_no | VARCHAR | 订单编号 |
| user_id | BIGINT | 用户ID |
| total_amount | DECIMAL | 支付总价格 |
| total_count | INT | 订单数量 |
| status_order | INT | 订单状态(0待支付/1完成/2取消/3失败/4超时取消) |
| pay_type | INT | 支付方式(0余额/1支付宝/2微信/3银联) |
| title | VARCHAR | 订单标题 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| version | INT | 版本号 |

### 2.2 订单明细表 `t_course_order_item`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| order_id | BIGINT | 关联主订单ID |
| order_no | VARCHAR | 订单编号 |
| course_id | BIGINT | 课程ID |
| course_name | VARCHAR | 课程名称 |
| course_pic | VARCHAR | 课程封面 |
| amount | DECIMAL | 课程价格 |
| count | INT | 数量 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 2.3 表关系

```
t_course_order (1) ←→ (N) t_course_order_item
    通过 order_id / order_no 关联
```

---

## 三、后端实现

### 3.1 新增查询参数DTO

**文件路径**: `ymcc-pojo/ymcc-pojo-order/src/main/java/cn/lzs/ymcc/dto/UserOrderQueryDTO.java`

```java
package cn.lzs.ymcc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户端订单查询DTO
 */
@Data
public class UserOrderQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 开始日期（订单创建时间范围查询）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 结束日期（订单创建时间范围查询）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    /**
     * 订单状态（可选，不传则查询所有状态）
     * 0-待支付 1-支付成功 2-用户取消 3-支付失败 4-超时取消
     */
    private Integer statusOrder;

    /**
     * 订单编号（模糊查询，可选）
     */
    private String orderNo;
}
```

### 3.2 新增返回VO类

**文件路径**: `ymcc-pojo/ymcc-pojo-order/src/main/java/cn/lzs/ymcc/vo/UserOrderVO.java`

```java
package cn.lzs.ymcc.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户订单列表VO
 */
@Data
public class UserOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单数量
     */
    private Integer totalCount;

    /**
     * 订单状态：0待支付 1支付成功 2用户取消 3支付失败 4超时取消
     */
    private Integer statusOrder;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 支付方式：0余额 1支付宝 2微信 3银联
     */
    private Integer payType;

    /**
     * 支付方式描述
     */
    private String payTypeDesc;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 订单明细列表
     */
    private List<OrderItemVO> items;

    /**
     * 订单明细VO
     */
    @Data
    public static class OrderItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 明细ID
         */
        private Long id;

        /**
         * 课程ID
         */
        private Long courseId;

        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 课程封面
         */
        private String coursePic;

        /**
         * 课程价格
         */
        private BigDecimal amount;

        /**
         * 数量
         */
        private Integer count;
    }
}
```

### 3.3 Service接口新增方法

**文件路径**: `ymcc-service/ymcc-service-order/src/main/java/cn/lzs/ymcc/service/ICourseOrderService.java`

在接口中新增方法：

```java
/**
 * 用户端分页查询订单列表（支持日期范围查询）
 * @param userId 用户ID
 * @param query 查询参数
 * @return 分页结果
 */
Page<UserOrderVO> queryUserOrderPage(Long userId, UserOrderQueryDTO query);
```

### 3.4 Service实现类

**文件路径**: `ymcc-service/ymcc-service-order/src/main/java/cn/lzs/ymcc/service/impl/CourseOrderServiceImpl.java`

新增实现方法：

```java
/**
 * 用户端分页查询订单列表（支持日期范围查询）
 */
@Override
public Page<UserOrderVO> queryUserOrderPage(Long userId, UserOrderQueryDTO query) {
    // 1. 构建分页对象
    Page<UserOrderVO> page = new Page<>(query.getPageNum(), query.getPageSize());

    // 2. 构建查询条件
    EntityWrapper<CourseOrder> wrapper = new EntityWrapper<>();
    wrapper.eq("user_id", userId);  // 只查询当前用户的订单

    // 2.1 日期范围查询
    if (query.getStartDate() != null) {
        wrapper.ge("create_time", query.getStartDate());
    }
    if (query.getEndDate() != null) {
        // 结束日期加一天，实现包含结束日期当天的效果
        Date endDate = DateUtils.addDays(query.getEndDate(), 1);
        wrapper.lt("create_time", endDate);
    }

    // 2.2 订单状态筛选
    if (query.getStatusOrder() != null) {
        wrapper.eq("status_order", query.getStatusOrder());
    }

    // 2.3 订单编号模糊查询
    if (StringUtils.isNotBlank(query.getOrderNo())) {
        wrapper.like("order_no", query.getOrderNo().trim());
    }

    // 2.4 按创建时间倒序排列
    wrapper.orderBy("create_time", false);

    // 3. 查询订单主表数据
    List<CourseOrder> orders = courseOrderMapper.selectPage(
        new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

    // 4. 转换为VO并填充明细
    List<UserOrderVO> voList = new ArrayList<>();
    for (CourseOrder order : orders) {
        UserOrderVO vo = convertToVO(order);

        // 查询订单明细
        EntityWrapper<CourseOrderItem> itemWrapper = new EntityWrapper<>();
        itemWrapper.eq("order_id", order.getId());
        List<CourseOrderItem> items = courseOrderItemMapper.selectList(itemWrapper);

        // 转换明细
        List<UserOrderVO.OrderItemVO> itemVOList = items.stream()
            .map(this::convertItemToVO)
            .collect(Collectors.toList());
        vo.setItems(itemVOList);

        voList.add(vo);
    }

    // 5. 查询总数
    Integer total = courseOrderMapper.selectCount(wrapper);

    // 6. 设置分页结果
    page.setRecords(voList);
    page.setTotal(total);

    return page;
}

/**
 * 订单实体转VO
 */
private UserOrderVO convertToVO(CourseOrder order) {
    UserOrderVO vo = new UserOrderVO();
    vo.setId(order.getId());
    vo.setOrderNo(order.getOrderNo());
    vo.setTitle(order.getTitle());
    vo.setTotalAmount(order.getTotalAmount());
    vo.setTotalCount(order.getTotalCount());
    vo.setStatusOrder(order.getStatusOrder());
    vo.setStatusDesc(getStatusDesc(order.getStatusOrder()));
    vo.setPayType(order.getPayType());
    vo.setPayTypeDesc(getPayTypeDesc(order.getPayType()));
    vo.setCreateTime(order.getCreateTime());
    vo.setUpdateTime(order.getUpdateTime());
    return vo;
}

/**
 * 订单明细实体转VO
 */
private UserOrderVO.OrderItemVO convertItemToVO(CourseOrderItem item) {
    UserOrderVO.OrderItemVO vo = new UserOrderVO.OrderItemVO();
    vo.setId(item.getId());
    vo.setCourseId(item.getCourseId());
    vo.setCourseName(item.getCourseName());
    vo.setCoursePic(item.getCoursePic());
    vo.setAmount(item.getAmount());
    vo.setCount(item.getCount());
    return vo;
}

/**
 * 获取订单状态描述
 */
private String getStatusDesc(Integer statusOrder) {
    if (statusOrder == null) {
        return "未知";
    }
    switch (statusOrder) {
        case 0: return "待支付";
        case 1: return "已完成";
        case 2: return "已取消";
        case 3: return "支付失败";
        case 4: return "超时取消";
        default: return "未知";
    }
}

/**
 * 获取支付方式描述
 */
private String getPayTypeDesc(Integer payType) {
    if (payType == null) {
        return "未知";
    }
    switch (payType) {
        case 0: return "余额支付";
        case 1: return "支付宝";
        case 2: return "微信支付";
        case 3: return "银联支付";
        default: return "未知";
    }
}
```

### 3.5 新增用户端Controller

**文件路径**: `ymcc-service/ymcc-service-order/src/main/java/cn/lzs/ymcc/web/controller/UserOrderController.java`

```java
package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.dto.UserOrderQueryDTO;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.ICourseOrderService;
import cn.lzs.ymcc.util.LoginContext;
import cn.lzs.ymcc.vo.UserOrderVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端订单控制器
 */
@RestController
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private ICourseOrderService courseOrderService;

    /**
     * 用户端订单列表查询（支持日期范围查询）
     *
     * @param query 查询参数
     * @return 订单分页列表
     */
    @PostMapping("/list")
    public JSONResult queryOrderList(@RequestBody UserOrderQueryDTO query) {
        // 1. 获取当前登录用户
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }

        // 2. 查询订单列表
        Page<UserOrderVO> page = courseOrderService.queryUserOrderPage(login.getId(), query);

        // 3. 返回结果
        return JSONResult.success(page);
    }

    /**
     * 用户端订单详情查询
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public JSONResult getOrderDetail(@PathVariable("orderNo") String orderNo) {
        // 1. 获取当前登录用户
        Login login = LoginContext.getLogin();
        if (login == null) {
            return JSONResult.error("用户未登录");
        }

        // 2. 查询订单详情
        UserOrderVO vo = courseOrderService.getUserOrderDetail(login.getId(), orderNo);
        if (vo == null) {
            return JSONResult.error("订单不存在");
        }

        return JSONResult.success(vo);
    }
}
```

### 3.6 需要注入的依赖

在 `CourseOrderServiceImpl` 中需要注入：

```java
@Autowired
private CourseOrderItemMapper courseOrderItemMapper;
```

### 3.7 需要导入的工具类

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
```

---

## 四、前端实现

### 4.1 API接口定义

**文件路径**: `src/api/order.js`

```javascript
import request from '@/utils/request'

/**
 * 用户订单列表查询（支持日期范围）
 * @param {Object} params 查询参数
 * @param {number} params.pageNum - 当前页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.startDate - 开始日期 (格式: yyyy-MM-dd)
 * @param {string} params.endDate - 结束日期 (格式: yyyy-MM-dd)
 * @param {number} params.statusOrder - 订单状态 (可选)
 * @param {string} params.orderNo - 订单编号 (可选)
 */
export function getUserOrderList(params) {
  return request({
    url: '/user/order/list',
    method: 'post',
     params
  })
}

/**
 * 获取订单详情
 * @param {string} orderNo 订单编号
 */
export function getOrderDetail(orderNo) {
  return request({
    url: `/user/order/detail/${orderNo}`,
    method: 'get'
  })
}
```

### 4.2 订单列表页面

**文件路径**: `src/views/user/order/index.vue`

```vue
<template>
  <div class="order-container">
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :model="queryParams" inline ref="queryForm">
        <!-- 日期范围选择 -->
        <el-form-item label="订单日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            @change="handleDateChange"
          />
        </el-form-item>

        <!-- 订单状态筛选 -->
        <el-form-item label="订单状态">
          <el-select
            v-model="queryParams.statusOrder"
            placeholder="全部状态"
            clearable
            @change="handleSearch"
          >
            <el-option label="全部状态" :value="null" />
            <el-option label="待支付" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="支付失败" :value="3" />
            <el-option label="超时取消" :value="4" />
          </el-select>
        </el-form-item>

        <!-- 订单编号搜索 -->
        <el-form-item label="订单编号">
          <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单编号"
            clearable
            @keyup.enter.native="handleSearch"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 快捷日期筛选 -->
    <div class="quick-date">
      <el-radio-group v-model="quickDate" @change="handleQuickDate">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="today">今天</el-radio-button>
        <el-radio-button label="week">近一周</el-radio-button>
        <el-radio-button label="month">近一个月</el-radio-button>
        <el-radio-button label="threeMonth">近三个月</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-if="orderList.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无订单数据" />
      </div>

      <div
        v-for="order in orderList"
        :key="order.id"
        class="order-card"
        @click="goToDetail(order.orderNo)"
      >
        <div class="order-header">
          <span class="order-no">订单编号：{{ order.orderNo }}</span>
          <span class="order-time">{{ order.createTime | formatDate }}</span>
        </div>

        <div class="order-body">
          <div class="order-items">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="order-item"
            >
              <img :src="item.coursePic" class="course-img" alt="课程封面" />
              <div class="item-info">
                <div class="course-name">{{ item.courseName }}</div>
                <div class="item-price">¥{{ item.amount }} × {{ item.count }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-amount">
            共{{ order.totalCount }}件商品，实付款：
            <span class="price">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.statusOrder)">
              {{ order.statusDesc }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-area" v-if="total > 0">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :total="total"
        :page-size.sync="queryParams.pageSize"
        :current-page.sync="queryParams.pageNum"
        :page-sizes="[10, 20, 50]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script>
import { getUserOrderList } from '@/api/order'
import { formatDate, getStartOfDay, getEndOfDay, subtractDays } from '@/utils/date'

export default {
  name: 'UserOrderList',
  data() {
    return {
      // 加载状态
      loading: false,
      // 订单列表
      orderList: [],
      // 总数
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startDate: null,
        endDate: null,
        statusOrder: null,
        orderNo: ''
      },
      // 日期范围
      dateRange: [],
      // 快捷日期选择
      quickDate: '',
      // 日期选择器配置
      pickerOptions: {
        disabledDate(time) {
          // 不能选择未来日期
          return time.getTime() > Date.now()
        }
      }
    }
  },
  created() {
    this.loadOrderList()
  },
  methods: {
    // 加载订单列表
    async loadOrderList() {
      this.loading = true
      try {
        const res = await getUserOrderList(this.queryParams)
        if (res.success) {
          this.orderList = res.data.records || []
          this.total = res.data.total || 0
        } else {
          this.$message.error(res.message || '查询失败')
        }
      } catch (error) {
        console.error('查询订单失败:', error)
        this.$message.error('查询失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 日期范围变化
    handleDateChange(val) {
      if (val && val.length === 2) {
        this.queryParams.startDate = val[0]
        this.queryParams.endDate = val[1]
      } else {
        this.queryParams.startDate = null
        this.queryParams.endDate = null
      }
      this.quickDate = '' // 清除快捷选择
      this.handleSearch()
    },

    // 快捷日期选择
    handleQuickDate(val) {
      if (!val) {
        this.dateRange = []
        this.queryParams.startDate = null
        this.queryParams.endDate = null
      } else {
        const today = new Date()
        let startDate
        const endDate = formatDate(today, 'yyyy-MM-dd')

        switch (val) {
          case 'today':
            startDate = endDate
            break
          case 'week':
            startDate = formatDate(subtractDays(today, 7), 'yyyy-MM-dd')
            break
          case 'month':
            startDate = formatDate(subtractDays(today, 30), 'yyyy-MM-dd')
            break
          case 'threeMonth':
            startDate = formatDate(subtractDays(today, 90), 'yyyy-MM-dd')
            break
        }

        this.dateRange = [startDate, endDate]
        this.queryParams.startDate = startDate
        this.queryParams.endDate = endDate
      }
      this.handleSearch()
    },

    // 查询
    handleSearch() {
      this.queryParams.pageNum = 1
      this.loadOrderList()
    },

    // 重置
    handleReset() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        startDate: null,
        endDate: null,
        statusOrder: null,
        orderNo: ''
      }
      this.dateRange = []
      this.quickDate = ''
      this.loadOrderList()
    },

    // 页码变化
    handlePageChange(page) {
      this.queryParams.pageNum = page
      this.loadOrderList()
    },

    // 每页条数变化
    handleSizeChange(size) {
      this.queryParams.pageSize = size
      this.queryParams.pageNum = 1
      this.loadOrderList()
    },

    // 获取状态标签类型
    getStatusType(status) {
      const typeMap = {
        0: 'warning',  // 待支付
        1: 'success',  // 已完成
        2: 'info',     // 已取消
        3: 'danger',   // 支付失败
        4: 'info'      // 超时取消
      }
      return typeMap[status] || 'info'
    },

    // 跳转订单详情
    goToDetail(orderNo) {
      this.$router.push(`/user/order/detail/${orderNo}`)
    }
  },
  filters: {
    formatDate(val) {
      if (!val) return ''
      return formatDate(new Date(val), 'yyyy-MM-dd hh:mm:ss')
    }
  }
}
</script>

<style scoped>
.order-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.search-area {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.quick-date {
  background: #fff;
  padding: 15px 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.order-list {
  min-height: 300px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 15px;
  padding: 15px 20px;
  cursor: pointer;
  transition: box-shadow 0.3s;
}

.order-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-weight: 500;
  color: #333;
}

.order-time {
  color: #999;
  font-size: 13px;
}

.order-body {
  padding: 15px 0;
}

.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.course-img {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.course-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.item-price {
  color: #666;
  font-size: 13px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.order-amount {
  color: #666;
}

.order-amount .price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: 500;
}

.pagination-area {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  text-align: right;
}

.empty-tip {
  padding: 50px 0;
}
</style>
```

### 4.3 日期工具函数

**文件路径**: `src/utils/date.js`

```javascript
/**
 * 日期格式化
 * @param {Date} date 日期对象
 * @param {string} fmt 格式化模式
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, fmt) {
  if (!date) return ''
  if (typeof date === 'string') {
    date = new Date(date)
  }

  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'h+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    S: date.getMilliseconds() // 毫秒
  }

  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }

  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  }
  return fmt
}

/**
 * 日期减法
 * @param {Date} date 日期对象
 * @param {number} days 要减去的天数
 * @returns {Date} 新日期对象
 */
export function subtractDays(date, days) {
  const result = new Date(date)
  result.setDate(result.getDate() - days)
  return result
}

/**
 * 获取一天的开始时间 (00:00:00)
 * @param {Date} date 日期对象
 * @returns {Date}
 */
export function getStartOfDay(date) {
  const result = new Date(date)
  result.setHours(0, 0, 0, 0)
  return result
}

/**
 * 获取一天的结束时间 (23:59:59)
 * @param {Date} date 日期对象
 * @returns {Date}
 */
export function getEndOfDay(date) {
  const result = new Date(date)
  result.setHours(23, 59, 59, 999)
  return result
}
```

---

## 五、接口文档

### 5.1 用户订单列表查询接口

**接口地址**: `POST /user/order/list`

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 当前页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| startDate | String | 否 | 开始日期，格式yyyy-MM-dd |
| endDate | String | 否 | 结束日期，格式yyyy-MM-dd |
| statusOrder | Integer | 否 | 订单状态：0待支付/1完成/2取消/3失败/4超时 |
| orderNo | String | 否 | 订单编号，模糊查询 |

**请求示例**:
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "startDate": "2025-01-01",
  "endDate": "2025-01-31",
  "statusOrder": 1
}
```

**响应参数**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| records | Array | 订单列表 |
| total | Long | 总记录数 |
| size | Long | 每页条数 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

**records数组元素**:
| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单编号 |
| title | String | 订单标题 |
| totalAmount | BigDecimal | 订单总金额 |
| totalCount | Integer | 商品数量 |
| statusOrder | Integer | 订单状态 |
| statusDesc | String | 订单状态描述 |
| payType | Integer | 支付方式 |
| payTypeDesc | String | 支付方式描述 |
| createTime | Date | 创建时间 |
| updateTime | Date | 更新时间 |
| items | Array | 订单明细列表 |

**响应示例**:
```json
{
  "success": true,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 1001,
        "orderNo": "ORD202501150001",
        "title": "Java高级课程",
        "totalAmount": 299.00,
        "totalCount": 2,
        "statusOrder": 1,
        "statusDesc": "已完成",
        "payType": 1,
        "payTypeDesc": "支付宝",
        "createTime": "2025-01-15 10:30:00",
        "updateTime": "2025-01-15 10:35:00",
        "items": [
          {
            "id": 1,
            "courseId": 101,
            "courseName": "Java基础入门",
            "coursePic": "http://xxx.com/pic1.jpg",
            "amount": 199.00,
            "count": 1
          },
          {
            "id": 2,
            "courseId": 102,
            "courseName": "Java进阶实战",
            "coursePic": "http://xxx.com/pic2.jpg",
            "amount": 100.00,
            "count": 1
          }
        ]
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 5.2 订单详情查询接口

**接口地址**: `GET /user/order/detail/{orderNo}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |

**响应示例**:
```json
{
  "success": true,
  "message": "成功",
  "data": {
    "id": 1001,
    "orderNo": "ORD202501150001",
    "title": "Java高级课程",
    "totalAmount": 299.00,
    "totalCount": 2,
    "statusOrder": 1,
    "statusDesc": "已完成",
    "payType": 1,
    "payTypeDesc": "支付宝",
    "createTime": "2025-01-15 10:30:00",
    "updateTime": "2025-01-15 10:35:00",
    "items": [...]
  }
}
```

---

## 六、开发注意事项

1. **日期范围查询边界处理**：结束日期需要加一天进行 `<` 比较，确保包含结束日期当天的所有订单

2. **用户权限校验**：查询时必须通过 `LoginContext.getLoginId()` 获取当前用户ID，确保只能查询自己的订单

3. **索引优化建议**：
   ```sql
   -- 建议添加复合索引优化查询性能
   CREATE INDEX idx_user_create_time ON t_course_order(user_id, create_time);
   CREATE INDEX idx_user_status ON t_course_order(user_id, status_order);
   ```

4. **分页性能**：当数据量大时，建议使用游标分页替代传统分页

5. **前后端日期格式**：
   - 前端传参格式：`yyyy-MM-dd`
   - 后端接收格式：`@DateTimeFormat(pattern = "yyyy-MM-dd")`

6. **订单状态枚举说明**：
   | 状态值 | 状态描述 | 说明 |
   |--------|----------|------|
   | 0 | 待支付 | 订单创建成功，等待支付 |
   | 1 | 已完成 | 支付成功，订单完成 |
   | 2 | 已取消 | 用户手动取消（未支付） |
   | 3 | 支付失败 | 支付过程失败 |
   | 4 | 超时取消 | 超时未支付自动取消 |

---

## 七、测试用例

### 7.1 后端单元测试

```java
@Test
public void testQueryUserOrderPage() {
    // 准备测试数据
    UserOrderQueryDTO query = new UserOrderQueryDTO();
    query.setPageNum(1);
    query.setPageSize(10);
    query.setStartDate(DateUtils.parseDate("2025-01-01", "yyyy-MM-dd"));
    query.setEndDate(DateUtils.parseDate("2025-01-31", "yyyy-MM-dd"));
    query.setStatusOrder(1);

    // 执行查询
    Page<UserOrderVO> page = courseOrderService.queryUserOrderPage(1L, query);

    // 验证结果
    assertNotNull(page);
    assertTrue(page.getRecords().size() <= 10);
}
```

### 7.2 接口测试用例

| 测试场景 | 请求参数 | 预期结果 |
|----------|----------|----------|
| 查询全部订单 | {} | 返回用户所有订单分页数据 |
| 按日期范围查询 | {startDate: "2025-01-01", endDate: "2025-01-31"} | 返回指定日期范围内的订单 |
| 按状态查询 | {statusOrder: 1} | 只返回已完成的订单 |
| 按订单号查询 | {orderNo: "ORD2025"} | 返回订单号包含"ORD2025"的订单 |
| 组合查询 | {startDate: "2025-01-01", statusOrder: 0} | 返回指定日期范围内的待支付订单 |
| 未登录查询 | 不传Authorization头 | 返回"用户未登录"错误 |