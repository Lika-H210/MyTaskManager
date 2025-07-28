package com.portfolio.taskapp.MyTaskManager.repository;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskRepository {

  Integer findUserIdByUserPublicId(@Param("userPublicId") String userPublicId);

  List<Project> findProjectsByUserId(@Param("userId") Integer userId);

}
