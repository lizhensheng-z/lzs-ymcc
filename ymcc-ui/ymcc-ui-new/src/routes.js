// 用户端页面
import Login from './views/common/Login.vue'
import Register from './views/common/Register.vue'
import CourseIndex from './views/course/Index.vue'
import CourseList from './views/course/List.vue'
import CourseDetail from './views/course/Detail.vue'
import CourseCart from './views/course/Cart.vue'
import UserIndex from './views/user/Index.vue'
import UserCourses from './views/user/Courses.vue'
import UserHome from './views/user/Home.vue'
import Profile from './views/user/Profile.vue'
import Orders from './views/user/Orders.vue'
import CourseLearn from './views/user/CourseLearn.vue'
import OrderConfirm from './views/course/OrderConfirm.vue'
import OrderSuccess from './views/course/OrderSuccess.vue'

// 管理后台页面
import AdminLogin from './views/itsource/Login.vue'
import AdminHome from './views/Home.vue'
import NotFound from './views/404.vue'
import Systemdictionary from './views/itsource/Systemdictionary.vue'
import Systemdictionaryitem from './views/itsource/Systemdictionaryitem.vue'
import CourseVideo from './views/itsource/CourseVideo.vue'
import CourseType from './views/itsource/CourseType.vue'
import CourseChapter from './views/itsource/CourseChapter.vue'
import Department from './views/itsource/department.vue'
import Course from './views/itsource/Course.vue'
import Role from './views/itsource/role.vue'
import echarts from './views/charts/echarts.vue'
import Teacher from './views/itsource/Teacher.vue'
import CouseOrder from './views/itsource/CouseOrder.vue'
import PayFlow from './views/itsource/PayFlow.vue'
import AliPay from './views/itsource/AliPay.vue'
import SMSMessage from './views/itsource/SMSMessage.vue'
import StationMessage from './views/itsource/StationMessage.vue'
import EmailMessage from './views/itsource/EmailMessage'
import Employee from './views/itsource/Employee'
import Table from './views/itsource/Table'
import Permission from './views/itsource/Permission'
import CourseKill from './views/itsource/CourseKill'
import KillActivity from './views/itsource/KillActivity'

let routes = [
    // ==================== 用户端路由 ====================
    {
        path: '/',
        component: CourseIndex,
        name: '首页',
        hidden: false
    },
    {
        path: '/login',
        component: Login,
        name: '登录',
        hidden: true
    },
    {
        path: '/register',
        component: Register,
        name: '注册',
        hidden: true
    },
    {
        path: '/course/list',
        component: CourseList,
        name: '课程列表',
        hidden: false
    },
    {
        path: '/course/detail/:id',
        component: CourseDetail,
        name: '课程详情',
        hidden: true
    },
    {
        path: '/cart',
        component: CourseCart,
        name: '购物车',
        hidden: false
    },
    {
        path: '/order/confirm',
        component: OrderConfirm,
        name: '订单确认',
        hidden: true,
        meta: { requiresAuth: true }
    },
    {
        path: '/order/success',
        component: OrderSuccess,
        name: '订单成功',
        hidden: true
    },
    {
        path: '/user',
        component: UserIndex,
        name: '用户首页',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/home',
        component: UserHome,
        name: '欢迎页',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/courses',
        component: UserCourses,
        name: '我的课程',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/course/learn/:id',
        component: CourseLearn,
        name: '课程学习',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/profile',
        component: Profile,
        name: '个人资料',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/security',
        component: UserIndex,
        name: '账户安全',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/orders',
        component: Orders,
        name: '我的订单',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/account',
        component: UserIndex,
        name: '资金账户',
        hidden: false,
        meta: { requiresAuth: true }
    },
    {
        path: '/user/msg',
        component: UserIndex,
        name: '我的消息',
        hidden: false,
        meta: { requiresAuth: true }
    },

    // ==================== 管理后台路由 ====================
    {
        path: '/admin/login',
        component: AdminLogin,
        name: '',
        hidden: true
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '首页',
        iconCls: 'el-icon-s-home',
        children: [
            { path: '/admin/echarts', component: echarts, name: '首页' }
        ]
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '组织机构管理',
        iconCls: 'el-icon-s-grid',
        children: [
            { path: '/admin/employee', component: Employee, name: '员工管理' },
            { path: '/admin/department', component: Department, name: '部门管理' },
            { path: '/admin/role', component: Role, name: '角色管理' },
            { path: '/admin/permission', component: Permission, name: '权限管理' },
        ]
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '系统基础设置',
        iconCls: 'el-icon-s-tools',
        children: [
            { path: '/admin/systemdictionary', component: Systemdictionary, name: '数据字典' },
            { path: '/admin/systemdictionaryitem', component: Systemdictionaryitem, name: '数据字典明细' },
        ]
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '课程相关',
        iconCls: 'el-icon-video-camera-solid',
        children: [
            { path: '/admin/courseType', component: CourseType, name: '课程类型' },
            { path: '/admin/teacher', component: Teacher, name: '讲师管理' },
            { path: '/admin/course', component: Course, name: '课程管理' },
            { path: '/admin/courseChapter', component: CourseChapter, name: '课程章节' },
            { path: '/admin/courseVideo', component: CourseVideo, name: '课程视频' }
        ]
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '订单中心',
        iconCls: 'el-icon-s-order',
        children: [
            { path: '/admin/couseOrder', component: CouseOrder, name: '课程订单' },
            { path: '/admin/payFlow', component: PayFlow, name: '支付账单' },
            { path: '/admin/AliPay', component: AliPay, name: '支付宝设置' },
        ]
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '秒杀中心',
        iconCls: 'el-icon-s-goods',
        children: [
            { path: '/admin/killActivity', component: KillActivity, name: '秒杀活动' },
            { path: '/admin/kourseKill', component: CourseKill, name: '秒杀课程' }
        ]
    },
    {
        path: '/admin',
        component: AdminHome,
        name: '消息中心',
        iconCls: 'el-icon-message-solid',
        children: [
            { path: '/admin/SMSMessage', component: SMSMessage, name: '短信管理' },
            { path: '/admin/emailMessage', component: EmailMessage, name: '邮件管理' },
            { path: '/admin/stationMessage', component: StationMessage, name: '站内信管理' },
        ]
    },

    // ==================== 其他 ====================
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    {
        path: '*',
        hidden: true,
        redirect: { path: '/404' }
    }
];

export default routes;