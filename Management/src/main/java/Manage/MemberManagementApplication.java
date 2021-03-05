package Manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author snow create 2021/02/14 16:20
 */
@SpringBootApplication(scanBasePackages = {"Manage", "Core"})
public class MemberManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberManagementApplication.class);
    }
}
