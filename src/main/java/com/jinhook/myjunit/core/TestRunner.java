package com.jinhook.myjunit.core;

import com.jinhook.myjunit.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TestRunner {

    public static void run(Class<?> testClass) {
        List<Method> testMethods = new ArrayList<>();
        Method beforeAllMethod = null;
        Method afterAllMethod = null;

        // 遍历给定类中的方法，查找测试方法和生命周期方法
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeAll.class)) {
                beforeAllMethod = method;
            } else if (method.isAnnotationPresent(AfterAll.class)) {
                afterAllMethod = method;
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }

        try {
            // 执行 BeforeAll 方法
            if (beforeAllMethod != null) {
                /*
                使得 beforeAllMethod 方法能够绕过 Java 的访问控制检查，变得可以访问。
                这样，即使 beforeAllMethod 是一个 private 或 protected 方法，我们依然能够通过反射来调用它。
                */
                beforeAllMethod.setAccessible(true);
                /*
                invoke() 的第一个参数是调用该方法的对象实例（对于静态方法，传入 null）。
                由于 beforeAllMethod 是一个静态方法（通常标记为 @BeforeAll 的方法是静态的），所以我们传递 null 作为实例。
                */
                beforeAllMethod.invoke(null);  // static 方法
            }

            // 执行测试方法
            for (Method testMethod : testMethods) {
                String displayName = null;

                // 跳过被@Disabled注解标记的测试方法
                if (testMethod.isAnnotationPresent(Disabled.class)) {
                    System.out.println(testMethod.getName() + "=============DISABLED");
                    continue;
                }

                //如果有@DisplayName注解，把注解的值先存起来，后面显示方法名用
                if(testMethod.isAnnotationPresent(DisplayName.class)){
                    displayName = testMethod.getAnnotation(DisplayName.class).value();
                }

                Object tempInstance = null;
                try {
                    /*
                    每次执行一个被 @Test 注解标记的测试方法时，都会新创建一个测试类的实例来运行该方法，
                    这是为了保证每个测试方法之间的独立性和隔离性。
                    */
                    Object testInstance = testClass.getDeclaredConstructor().newInstance();
                    tempInstance = testInstance;
                    // 执行 Before 方法
                    executeBefore(testInstance);

                    // 执行测试方法
                    testMethod.setAccessible(true);

                    if(testMethod.isAnnotationPresent(TimeOut.class)){
                        TimeOut timeOut = testMethod.getAnnotation(TimeOut.class);

                        Thread thread = new Thread(() -> {
                            try {
                                testMethod.invoke(testInstance);
                            } catch (Exception e) {
                                throw new RuntimeException("time out");
                            }
                        });
                        thread.start();

                        /*等待测试方法执行完成：thread.join(timeOut.value())使主线程等待 测试线程最多timeOut.value()毫秒。
                                          如果测试方法在指定的时间内没有完成，主线程会继续执行并检查超时。*/
                        thread.join(timeOut.value());

                        /*检查线程是否超时：thread.isAlive() 用于判断子线程是否还在运行。
                                      如果子线程还在运行，说明测试方法没有在规定时间内执行完毕。*/
                        if(thread.isAlive()){
                            thread.interrupt();
                            throw new TimeoutException("timed out");
                        }
                    }else{
                        testMethod.invoke(testInstance);
                    }


                } catch (Exception e) {
                    if(displayName != null){
                        System.out.println(displayName + " failed: " + e.getMessage());
                    }else{
                        System.out.println(testMethod.getName() + " failed: " + e.getMessage());
                    }
                } finally {
                    // 无论是否发生异常都执行 After 方法
                    executeAfter(tempInstance);
                }
            }

            // 执行 AfterAll 方法
            if (afterAllMethod != null) {
                afterAllMethod.setAccessible(true);
                afterAllMethod.invoke(null);  // static 方法
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeBefore(Object testInstance) throws Exception {
        // 执行 Before 方法
        for (Method method : testInstance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(BeforeEach.class)) {
                method.setAccessible(true);
                method.invoke(testInstance);
            }
        }
    }

    private static void executeAfter(Object testInstance) throws Exception {
        // 执行 After 方法
        for (Method method : testInstance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterEach.class)) {
                method.setAccessible(true);
                method.invoke(testInstance);
            }
        }
    }
}

