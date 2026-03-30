import axios from 'axios';

let base = '';

// ==================== User 用户接口 ====================

// 用户手机注册
export const registerByPhone = params => {
  return axios.post(`${base}/user/register`, params).then(res => res.data);
};

// 保存或修改用户
export const saveUser = params => {
  return axios.post(`${base}/user/save`, params).then(res => res.data);
};

// 删除用户
export const deleteUser = id => {
  return axios.delete(`${base}/user/${id}`).then(res => res.data);
};

// 根据loginId获取用户
export const getUserByLoginId = loginId => {
  return axios.get(`${base}/user/loginId/${loginId}`).then(res => res.data);
};

// 获取用户详情
export const getUser = id => {
  return axios.get(`${base}/user/${id}`).then(res => res.data);
};

// 获取用户列表（所有）
export const getUserList = params => {
  return axios.get(`${base}/user/list`, { params: params }).then(res => res.data);
};

// 分页查询用户
export const getUserPageList = params => {
  return axios.post(`${base}/user/pagelist`, params).then(res => res.data);
};

// ==================== UserAccount 用户账户接口 ====================

// 保存或修改账户
export const saveUserAccount = params => {
  return axios.post(`${base}/userAccount/save`, params).then(res => res.data);
};

// 删除账户
export const deleteUserAccount = id => {
  return axios.delete(`${base}/userAccount/${id}`).then(res => res.data);
};

// 获取账户详情
export const getUserAccount = id => {
  return axios.get(`${base}/userAccount/${id}`).then(res => res.data);
};

// 获取账户列表（所有）
export const getUserAccountList = params => {
  return axios.get(`${base}/userAccount/list`, { params: params }).then(res => res.data);
};

// 分页查询账户
export const getUserAccountPageList = params => {
  return axios.post(`${base}/userAccount/pagelist`, params).then(res => res.data);
};

// ==================== UserAddress 用户地址接口 ====================

// 保存或修改地址
export const saveUserAddress = params => {
  return axios.post(`${base}/userAddress/save`, params).then(res => res.data);
};

// 删除地址
export const deleteUserAddress = id => {
  return axios.delete(`${base}/userAddress/${id}`).then(res => res.data);
};

// 获取地址详情
export const getUserAddress = id => {
  return axios.get(`${base}/userAddress/${id}`).then(res => res.data);
};

// 获取地址列表（所有）
export const getUserAddressList = params => {
  return axios.get(`${base}/userAddress/list`, { params: params }).then(res => res.data);
};

// 分页查询地址
export const getUserAddressPageList = params => {
  return axios.post(`${base}/userAddress/pagelist`, params).then(res => res.data);
};

// ==================== UserBaseInfo 用户基本信息接口 ====================

// 保存或修改基本信息
export const saveUserBaseInfo = params => {
  return axios.post(`${base}/userBaseInfo/save`, params).then(res => res.data);
};

// 删除基本信息
export const deleteUserBaseInfo = id => {
  return axios.delete(`${base}/userBaseInfo/${id}`).then(res => res.data);
};

// 获取基本信息详情
export const getUserBaseInfo = id => {
  return axios.get(`${base}/userBaseInfo/${id}`).then(res => res.data);
};

// 获取基本信息列表（所有）
export const getUserBaseInfoList = params => {
  return axios.get(`${base}/userBaseInfo/list`, { params: params }).then(res => res.data);
};

// 分页查询基本信息
export const getUserBaseInfoPageList = params => {
  return axios.post(`${base}/userBaseInfo/pagelist`, params).then(res => res.data);
};

// ==================== UserRealInfo 用户实名信息接口 ====================

// 保存或修改实名信息
export const saveUserRealInfo = params => {
  return axios.post(`${base}/userRealInfo/save`, params).then(res => res.data);
};

// 删除实名信息
export const deleteUserRealInfo = id => {
  return axios.delete(`${base}/userRealInfo/${id}`).then(res => res.data);
};

// 获取实名信息详情
export const getUserRealInfo = id => {
  return axios.get(`${base}/userRealInfo/${id}`).then(res => res.data);
};

// 获取实名信息列表（所有）
export const getUserRealInfoList = params => {
  return axios.get(`${base}/userRealInfo/list`, { params: params }).then(res => res.data);
};

// 分页查询实名信息
export const getUserRealInfoPageList = params => {
  return axios.post(`${base}/userRealInfo/pagelist`, params).then(res => res.data);
};

// ==================== AccountFlow 账户流水接口 ====================

// 保存或修改流水
export const saveAccountFlow = params => {
  return axios.post(`${base}/accountFlow/save`, params).then(res => res.data);
};

// 删除流水
export const deleteAccountFlow = id => {
  return axios.delete(`${base}/accountFlow/${id}`).then(res => res.data);
};

// 获取流水详情
export const getAccountFlow = id => {
  return axios.get(`${base}/accountFlow/${id}`).then(res => res.data);
};

// 获取流水列表（所有）
export const getAccountFlowList = params => {
  return axios.get(`${base}/accountFlow/list`, { params: params }).then(res => res.data);
};

// 分页查询流水
export const getAccountFlowPageList = params => {
  return axios.post(`${base}/accountFlow/pagelist`, params).then(res => res.data);
};

// ==================== UserGrowLog 用户成长日志接口 ====================

// 保存或修改成长日志
export const saveUserGrowLog = params => {
  return axios.post(`${base}/userGrowLog/save`, params).then(res => res.data);
};

// 删除成长日志
export const deleteUserGrowLog = id => {
  return axios.delete(`${base}/userGrowLog/${id}`).then(res => res.data);
};

// 获取成长日志详情
export const getUserGrowLog = id => {
  return axios.get(`${base}/userGrowLog/${id}`).then(res => res.data);
};

// 获取成长日志列表（所有）
export const getUserGrowLogList = params => {
  return axios.get(`${base}/userGrowLog/list`, { params: params }).then(res => res.data);
};

// 分页查询成长日志
export const getUserGrowLogPageList = params => {
  return axios.post(`${base}/userGrowLog/pagelist`, params).then(res => res.data);
};