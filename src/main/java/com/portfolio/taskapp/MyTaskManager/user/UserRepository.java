package com.portfolio.taskapp.MyTaskManager.user;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

  UserAccount findAccountByEmail(@Param("email") String email);

  void registerUserAccount(UserAccount userAccount);
}
