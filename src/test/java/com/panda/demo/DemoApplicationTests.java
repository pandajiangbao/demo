package com.panda.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URLEncoder;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println(URLEncoder.encode("ync=aP3=FrqU8soDK5Hw-Qp[vafS7:JU"));
    }

}
