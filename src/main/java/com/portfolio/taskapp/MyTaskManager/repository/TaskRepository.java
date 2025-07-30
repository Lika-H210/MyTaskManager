package com.portfolio.taskapp.MyTaskManager.repository;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskRepository {

  Integer findUserIdByUserPublicId(@Param("userPublicId") String userPublicId);

  Integer findProjectIdByProjectPublicId(@Param("projectPublicId") String projectPublicId);

  Integer findTaskIdByTaskPublicId(@Param("taskPublicId") String taskPublicId);

  List<Project> findProjectsByUserId(@Param("userId") Integer userId);

  List<Task> findTasksByProjectId(@Param("projectId") Integer projectId);

  // 引数のtaskIdは親タスクのIdです。
  List<Task> findTasksByTaskId(@Param("taskId") Integer taskId);

}
