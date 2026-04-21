package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 * 切面：通知+切面点
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点(对哪些类的哪些方法进行拦截)
     * execution: 指示器类型，表示匹配方法执行的连接点。
     * 第一个*: 匹配任意返回类型。
     * com.sky.mapper: 限定包名为com.sky.mapper下的所有类。
     * 第二个.*: 匹配该包下的任意类。
     * 第三个.*(..):
     * .*：匹配类中的任意方法。
     * (..)：匹配任意参数列表（0个或多个参数）。
     * 总的来说：拦截com.sky.mapper包下的所有类的所有方法(任意返回类型、任意方法名、任意参数)
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){

    }
    //通知（对代码增强），使用前置通知，在通知中进行公共字段的赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        //需要传进来一个参数：连接点，通过连接点就可以知道我们当前哪些方法被拦截到了，以及被拦截到的方法参数是什么样的
        log.info("开始进行公共字段的自动填充...");

        //获取当前被拦截的方法上的数据库操作类型：insert or update，反射代码：
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//数据库操作类型
        //获取当前被拦截方法参数:实体对象(员工、分类、套餐、菜品...)，约定：实体对象放在第一个参数
        Object[] args = joinPoint.getArgs();
        if(args ==  null || args.length == 0){return;}
        Object entity = args[0];//获得实体对象
        //准备赋值的数据(时间，当前用户id)
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据当前不同操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为四个公共字段赋值,通过反射来赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType == OperationType.UPDATE){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }
}
