<template>
  <section class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <div class="stat-card card-primary">
          <div class="stat-icon"><i class="el-icon-s-custom"></i></div>
          <div class="stat-info">
            <p class="stat-label">总用户数</p>
            <p class="stat-value">12,580</p>
            <p class="stat-change up"><i class="el-icon-top"></i> +12.5%</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card-success">
          <div class="stat-icon"><i class="el-icon-video-camera"></i></div>
          <div class="stat-info">
            <p class="stat-label">课程总数</p>
            <p class="stat-value">386</p>
            <p class="stat-change up"><i class="el-icon-top"></i> +8.2%</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card-warning">
          <div class="stat-icon"><i class="el-icon-s-order"></i></div>
          <div class="stat-info">
            <p class="stat-label">今日订单</p>
            <p class="stat-value">156</p>
            <p class="stat-change up"><i class="el-icon-top"></i> +23.1%</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card card-danger">
          <div class="stat-icon"><i class="el-icon-money"></i></div>
          <div class="stat-info">
            <p class="stat-label">今日收入</p>
            <p class="stat-value">¥28,560</p>
            <p class="stat-change down"><i class="el-icon-bottom"></i> -5.3%</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 订单趋势图 -->
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-data-line"></i>
              订单与收入趋势
            </h3>
            <el-radio-group v-model="trendPeriod" size="small" @change="updateTrendChart">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
              <el-radio-button label="year">全年</el-radio-button>
            </el-radio-group>
          </div>
          <div id="orderTrendChart" style="width:100%; height:350px;"></div>
        </div>
      </el-col>

      <!-- 课程分类占比 -->
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-pie-chart"></i>
              课程分类占比
            </h3>
          </div>
          <div id="courseCategoryChart" style="width:100%; height:350px;"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 用户注册趋势 -->
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-user-solid"></i>
              用户注册趋势
            </h3>
          </div>
          <div id="userTrendChart" style="width:100%; height:300px;"></div>
        </div>
      </el-col>

      <!-- 热门课程排行 -->
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-trophy"></i>
              热门课程 TOP10
            </h3>
          </div>
          <div id="hotCourseChart" style="width:100%; height:300px;"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 订单状态分布 -->
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-s-data"></i>
              订单状态分布
            </h3>
          </div>
          <div id="orderStatusChart" style="width:100%; height:280px;"></div>
        </div>
      </el-col>

      <!-- 讲师课程数量分布 -->
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-s-cooperation"></i>
              讲师课程分布
            </h3>
          </div>
          <div id="teacherCourseChart" style="width:100%; height:280px;"></div>
        </div>
      </el-col>

      <!-- 支付方式占比 -->
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <i class="el-icon-wallet"></i>
              支付方式占比
            </h3>
          </div>
          <div id="paymentChart" style="width:100%; height:280px;"></div>
        </div>
      </el-col>
    </el-row>
  </section>
</template>

<script>
import echarts from 'echarts'

export default {
  data() {
    return {
      trendPeriod: 'week',
      charts: {}
    }
  },

  methods: {
    // 订单与收入趋势图
    drawOrderTrendChart() {
      const chart = echarts.init(document.getElementById('orderTrendChart'))
      this.charts.orderTrend = chart

      const data = {
        week: {
          xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          orders: [120, 132, 101, 134, 290, 230, 210],
          revenue: [22000, 25000, 18000, 26000, 48000, 42000, 38000]
        },
        month: {
          xAxis: ['1日', '5日', '10日', '15日', '20日', '25日', '30日'],
          orders: [450, 520, 480, 590, 680, 720, 650],
          revenue: [85000, 98000, 92000, 115000, 132000, 142000, 128000]
        },
        year: {
          xAxis: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
          orders: [3200, 3500, 4100, 3800, 4500, 5200, 4800, 5100, 5600, 5900, 6200, 6800],
          revenue: [580000, 620000, 750000, 680000, 820000, 960000, 880000, 920000, 1050000, 1120000, 1180000, 1280000]
        }
      }

      const currentData = data[this.trendPeriod]

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' }
        },
        legend: {
          data: ['订单数', '收入(元)'],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: currentData.xAxis,
          axisLine: { lineStyle: { color: '#ccc' } },
          axisLabel: { color: '#666' }
        },
        yAxis: [
          {
            type: 'value',
            name: '订单数',
            position: 'left',
            axisLine: { show: false },
            axisTick: { show: false },
            splitLine: { lineStyle: { color: '#f0f0f0' } }
          },
          {
            type: 'value',
            name: '收入(元)',
            position: 'right',
            axisLine: { show: false },
            axisTick: { show: false },
            splitLine: { show: false },
            axisLabel: {
              formatter: (value) => value >= 10000 ? (value / 10000) + '万' : value
            }
          }
        ],
        series: [
          {
            name: '订单数',
            type: 'bar',
            data: currentData.orders,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ])
            },
            barWidth: '40%'
          },
          {
            name: '收入(元)',
            type: 'line',
            yAxisIndex: 1,
            data: currentData.revenue,
            smooth: true,
            itemStyle: { color: '#f093fb' },
            lineStyle: { width: 3 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(240, 147, 251, 0.3)' },
                { offset: 1, color: 'rgba(240, 147, 251, 0.05)' }
              ])
            }
          }
        ]
      })
    },

    // 课程分类占比
    drawCourseCategoryChart() {
      const chart = echarts.init(document.getElementById('courseCategoryChart'))
      this.charts.courseCategory = chart

      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: ['Java开发', '前端开发', 'Python', '大数据', '人工智能', '移动开发', '其他']
        },
        series: [
          {
            name: '课程分类',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 18,
                fontWeight: 'bold'
              }
            },
            labelLine: { show: false },
            data: [
              { value: 85, name: 'Java开发', itemStyle: { color: '#667eea' } },
              { value: 72, name: '前端开发', itemStyle: { color: '#764ba2' } },
              { value: 58, name: 'Python', itemStyle: { color: '#f093fb' } },
              { value: 45, name: '大数据', itemStyle: { color: '#f368e0' } },
              { value: 38, name: '人工智能', itemStyle: { color: '#ff6b6b' } },
              { value: 52, name: '移动开发', itemStyle: { color: '#feca57' } },
              { value: 36, name: '其他', itemStyle: { color: '#48dbfb' } }
            ]
          }
        ]
      })
    },

    // 用户注册趋势
    drawUserTrendChart() {
      const chart = echarts.init(document.getElementById('userTrendChart'))
      this.charts.userTrend = chart

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['1月', '2月', '3月', '4月', '5月', '6月'],
          axisLine: { lineStyle: { color: '#ccc' } },
          axisLabel: { color: '#666' }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '新增用户',
            type: 'line',
            smooth: true,
            data: [820, 932, 1201, 1434, 1690, 1830],
            itemStyle: { color: '#667eea' },
            lineStyle: { width: 3 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.4)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
              ])
            }
          },
          {
            name: '活跃用户',
            type: 'line',
            smooth: true,
            data: [620, 732, 901, 1134, 1290, 1530],
            itemStyle: { color: '#48dbfb' },
            lineStyle: { width: 3 },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(72, 219, 251, 0.4)' },
                { offset: 1, color: 'rgba(72, 219, 251, 0.05)' }
              ])
            }
          }
        ]
      })
    },

    // 热门课程排行
    drawHotCourseChart() {
      const chart = echarts.init(document.getElementById('hotCourseChart'))
      this.charts.hotCourse = chart

      const courses = [
        'SpringBoot实战开发', 'Vue3全家桶教程', 'Python数据分析',
        'Java高并发编程', 'React入门到精通', '微服务架构设计',
        'MySQL性能优化', 'Docker容器技术', 'Redis缓存实战', 'Linux运维基础'
      ]
      const sales = [1250, 1180, 1050, 980, 920, 860, 780, 720, 650, 580]

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        yAxis: {
          type: 'category',
          data: courses.reverse(),
          axisLine: { lineStyle: { color: '#ccc' } },
          axisLabel: { color: '#666', fontSize: 11 }
        },
        series: [
          {
            name: '销量',
            type: 'bar',
            data: sales.reverse(),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                { offset: 0, color: '#f093fb' },
                { offset: 1, color: '#f368e0' }
              ])
            },
            barWidth: '60%',
            label: {
              show: true,
              position: 'right',
              formatter: '{c}人'
            }
          }
        ]
      })
    },

    // 订单状态分布
    drawOrderStatusChart() {
      const chart = echarts.init(document.getElementById('orderStatusChart'))
      this.charts.orderStatus = chart

      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        series: [
          {
            name: '订单状态',
            type: 'pie',
            radius: '65%',
            center: ['50%', '50%'],
            data: [
              { value: 1856, name: '已完成', itemStyle: { color: '#52c41a' } },
              { value: 328, name: '待支付', itemStyle: { color: '#faad14' } },
              { value: 156, name: '已取消', itemStyle: { color: '#ff4d4f' } },
              { value: 89, name: '退款中', itemStyle: { color: '#1890ff' } }
            ],
            label: {
              formatter: '{b}\n{c}单'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    },

    // 讲师课程分布
    drawTeacherCourseChart() {
      const chart = echarts.init(document.getElementById('teacherCourseChart'))
      this.charts.teacherCourse = chart

      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}位'
        },
        xAxis: {
          type: 'category',
          data: ['1-2门', '3-5门', '6-10门', '10门以上'],
          axisLine: { lineStyle: { color: '#ccc' } },
          axisLabel: { color: '#666' }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '讲师数量',
            type: 'bar',
            data: [
              { value: 28, itemStyle: { color: '#667eea' } },
              { value: 35, itemStyle: { color: '#764ba2' } },
              { value: 18, itemStyle: { color: '#f093fb' } },
              { value: 8, itemStyle: { color: '#f368e0' } }
            ],
            barWidth: '50%',
            label: {
              show: true,
              position: 'top',
              formatter: '{c}位'
            }
          }
        ]
      })
    },

    // 支付方式占比
    drawPaymentChart() {
      const chart = echarts.init(document.getElementById('paymentChart'))
      this.charts.payment = chart

      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'horizontal',
          bottom: 0,
          data: ['支付宝', '微信支付', '银联支付', '其他']
        },
        series: [
          {
            name: '支付方式',
            type: 'pie',
            radius: ['30%', '60%'],
            center: ['50%', '45%'],
            roseType: 'radius',
            itemStyle: {
              borderRadius: 5
            },
            data: [
              { value: 1850, name: '支付宝', itemStyle: { color: '#1677ff' } },
              { value: 1620, name: '微信支付', itemStyle: { color: '#07c160' } },
              { value: 580, name: '银联支付', itemStyle: { color: '#ff6b6b' } },
              { value: 280, name: '其他', itemStyle: { color: '#909399' } }
            ]
          }
        ]
      })
    },

    // 更新趋势图
    updateTrendChart() {
      this.drawOrderTrendChart()
    },

    // 渲染所有图表
    drawAllCharts() {
      this.drawOrderTrendChart()
      this.drawCourseCategoryChart()
      this.drawUserTrendChart()
      this.drawHotCourseChart()
      this.drawOrderStatusChart()
      this.drawTeacherCourseChart()
      this.drawPaymentChart()
    },

    // 响应式处理
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        chart && chart.resize()
      })
    }
  },

  mounted() {
    this.$nextTick(() => {
      this.drawAllCharts()
      window.addEventListener('resize', this.handleResize)
    })
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach(chart => {
      chart && chart.dispose()
    })
  }
}
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}

// 统计卡片
.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .stat-icon {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;

    i {
      font-size: 32px;
      color: #fff;
    }
  }

  .stat-info {
    flex: 1;

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin: 0 0 8px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px;
    }

    .stat-change {
      font-size: 13px;
      margin: 0;

      &.up {
        color: #67c23a;
      }

      &.down {
        color: #f56c6c;
      }

      i {
        margin-right: 4px;
      }
    }
  }
}

// 卡片颜色主题
.card-primary .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-success .stat-icon {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
}

.card-warning .stat-icon {
  background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
}

.card-danger .stat-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4d4f 100%);
}

// 图表区域
.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  height: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;

  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    display: flex;
    align-items: center;

    i {
      margin-right: 8px;
      color: #667eea;
      font-size: 18px;
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .stat-card {
    padding: 16px;

    .stat-icon {
      width: 48px;
      height: 48px;

      i {
        font-size: 24px;
      }
    }

    .stat-info {
      .stat-value {
        font-size: 22px;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }

  .chart-card {
    margin-bottom: 15px;
  }
}
</style>
