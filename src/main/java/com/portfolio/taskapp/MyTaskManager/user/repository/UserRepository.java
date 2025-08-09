package com.portfolio.taskapp.MyTaskManager.user.repository;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {

  UserAccount findAccountByEmail(@Param("email") String email);

  UserAccount findAccountByPublicId(@Param("publicId") String publicId);

  void registerUserAccount(UserAccount userAccount);

  void updateProfile(UserAccount userAccount);

  void deleteAccount(@Param("publicId") String publicId);

  // 登録時email重複チェック
  boolean existsByEmail(@Param("email") String email);

  // 更新時email重複チェック
  boolean existsByEmailExcludingUser(@Param("publicId") String publicId,
      @Param("email") String email);

}
