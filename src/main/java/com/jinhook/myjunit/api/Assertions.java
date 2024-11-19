package com.jinhook.myjunit.api;

public class Assertions {

    // 检查两个对象是否相等
    public static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual != null || expected != null && !expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }

    //检查两个int值是否相等
    public static void assertEquals(int expected, int actual) {
        if (expected!= actual) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }

    // 检查条件是否为真
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition is false");
        }
    }

    // 检查条件是否为假
    public static void assertFalse(boolean condition) {
        if (condition) {    //如果为真
            throw new AssertionError("Condition is true");
        }
    }
}

