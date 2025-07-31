package com.portfolio.taskapp.MyTaskManager.auth.repository;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAccountRepository {

  UserAccount findAccountByEmail(@Param("email") String email);

}
