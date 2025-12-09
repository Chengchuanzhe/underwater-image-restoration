import service from './index'

// 注册
export const register = (data) => service.post('/user/register', data)

// 登录
export const login = (data) => service.post('/user/login', data)

// 获取用户信息
export const getUserInfo = () => service.get('/user/info')

// 获取验证码（可选）
export const getEmailCode = (email, type) => service.get(`/email/code?email=${email}&type=${type}`)