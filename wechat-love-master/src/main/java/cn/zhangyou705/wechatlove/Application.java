package cn.zhangyou705.wechatlove;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ZhangYou
 * @description
 * @date 2022/8/23
 */
@SpringBootApplication
@EnableScheduling
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

}
