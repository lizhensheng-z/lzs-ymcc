import axios from 'axios'

/**
 * 用户订单列表查询（支持日期范围）
 * @param {Object} params 查询参数
 * @param {number} params.pageNum - 当前页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.startDate - 开始日期 (格式: yyyy-MM-dd)
 * @param {string} params.endDate - 结束日期 (格式: yyyy-MM-dd)
 * @param {number} params.statusOrder - 订单状态 (可选: 0待支付/1完成/2取消/3失败/4超时)
 * @param {string} params.orderNo - 订单编号 (可选，模糊查询)
 */
export function getUserOrderList(params) {
  return axios.post('/user/order/list', params).then(res => res.data)
}

/**
 * 获取订单详情
 * @param {string} orderNo 订单编号
 */
export function getOrderDetail(orderNo) {
  return axios.get(`/user/order/detail/${orderNo}`).then(res => res.data)
}