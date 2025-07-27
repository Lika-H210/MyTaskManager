package com.portfolio.taskapp.MyTaskManager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
    title = "タスク管理システム",
    description = "個人タスクを管理するためのシステムです。"
))
@SpringBootApplication
public class MyTaskManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyTaskManagerApplication.class, args);
  }

}
