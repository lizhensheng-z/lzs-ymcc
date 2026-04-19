/**
 * 日期工具函数
 */

/**
 * 日期格式化
 * @param {Date|string} date 日期对象或日期字符串
 * @param {string} fmt 格式化模式，如 'yyyy-MM-dd hh:mm:ss'
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
 * 日期减法 - 减去指定天数
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

/**
 * 解析日期字符串为Date对象
 * @param {string} dateStr 日期字符串，格式 'yyyy-MM-dd'
 * @returns {Date}
 */
export function parseDate(dateStr) {
  if (!dateStr) return null
  const parts = dateStr.split('-')
  if (parts.length === 3) {
    return new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]))
  }
  return new Date(dateStr)
}