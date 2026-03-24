package cn.lzs.ymcc.aspect;// 引入必要的注解和类
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
 
// 定义一个切面类，使用 @Aspect 注解标记
@Aspect
// 使用 @Component 注解将该切面类注册为 Spring 容器中的一个 Bean
@Component
public class PerformanceAspect {
    // 定义一个环绕通知，使用 @Around 注解指定切入点表达式
    // 切入点表达式 "execution(* com.example.service.*.*(..))" 表示匹配 com.example.service 包下所有类的所有方法
    @Around("execution(* cn.lzs.ymcc.web.controller.*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录方法开始执行的时间
        long startTime = System.currentTimeMillis();
 
        // 调用目标方法（即被拦截的方法），通过 ProceedingJoinPoint 的 proceed() 方法
        // 这里是环绕通知的核心，它允许我们控制目标方法的执行，并在执行前后添加自定义逻辑
        Object result = joinPoint.proceed();
 
        // 记录方法执行结束的时间
        long endTime = System.currentTimeMillis();
 
        // 计算方法执行所花费的时间
        long duration = endTime - startTime;
 
        // 获取被拦截方法的名称
        String methodName = joinPoint.getSignature().getName();
 
        // 打印方法执行时间的日志信息
        System.out.println("Method " + methodName + " 用时" + duration + "ms");
 
        // 返回目标方法的执行结果
        return result;
    }
}