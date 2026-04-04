package com.yao.learn;

import org.junit.jupiter.api.Test;
public class LocationTest {
    @Test
    public void testLocation() {
        Location location = new Location("上海", "310000", "***", 121.5, 31.0, "Asia/Shanghai");

        if(location.isValid()) {
            System.out.println(location.toFormattedString());
        }
        else {
            System.out.println("数据记录失败\n");
        }
    }
}
