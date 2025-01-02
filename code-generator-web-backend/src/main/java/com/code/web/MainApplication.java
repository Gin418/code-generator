package com.code.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 主类（项目启动入口）
 */
@SpringBootApplication
@MapperScan("com.code.web.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
        /*try {
            // 创建一个 ProcessBuilder 实例
            ProcessBuilder processBuilder = new ProcessBuilder();

            // 设置 MAVEN_HOME 环境变量
            processBuilder.environment().put("MAVEN_HOME", "/opt/apache-maven-3.9.9");

            // 设置要执行的命令
            processBuilder.command("sh", "-c", "echo $MAVEN_HOME && source ~/.bashrc && mvn -v && echo $PATH");

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待进程结束
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
        SpringApplication.run(MainApplication.class, args);
    }

}
