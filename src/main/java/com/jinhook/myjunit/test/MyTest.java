package com.jinhook.myjunit.test;


import com.jinhook.myjunit.annotation.*;
import com.jinhook.myjunit.api.Assertions;

public class MyTest {

    @BeforeAll
    public static void setUpAll() {
        System.out.println("BeforeAll");
    }

    @AfterAll
    public static void tearDownAll() {
        System.out.println("AfterAll");
    }

    @BeforeEach
    public void setUp() {
        System.out.println("BeforeEach");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("AfterEach");
    }

    @Test
    @Disabled
    public void testAddition() {
        System.out.println("Running testAddition");
        Assertions.assertEquals(2, 1 + 1);
    }

    @Test
    @DisplayName("sub!!!!")
    public void testSubtraction() {
        System.out.println("Running testSubtraction");
        Assertions.assertEquals(0, 2 - 1);
    }

    @Test
    public void testFail() {
        System.out.println("Running testFail");
        Assertions.assertTrue(false);  // This will fail
    }

    @TimeOut(500)
    @Test
    public void testTimeOut() throws Exception {
        Thread.sleep(1005);
        System.out.println("输出了，说明测试失败");
    }
}


