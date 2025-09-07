package com.portfolio.taskapp.MyTaskManager.user.repository;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ユーザーアカウントに関する DB 操作を提供するインターフェース。MyBatis でマッピングしています
 * <p>
 * このリポジトリは、アカウントの情報取得、登録、更新、論理削除、重複チェックなどを行います。 基本方針として、必要がある場合を除き論理削除済みのアカウント情報は取得しません。
 * また、認証情報としてアカウント情報を取得する場合を除き、パスワード情報は取得しません。
 */
@Mapper
public interface UserRepository {

  /**
   * 指定されたメールアドレスに紐づくユーザーアカウントを取得します。
   * <p>
   * ログイン認証時に使用され、削除済みアカウント（is_deleted = true）は除外されます。
   *
   * @param email 検索するメールアドレス
   * @return 該当するユーザーアカウント情報、存在しない場合は null
   */
  UserAccount findAccountByEmail(@Param("email") String email);

  /**
   * 指定された公開IDに紐づくユーザーアカウントを取得します。
   * <p>
   * 削除済みアカウントは除外されます。また、パスワード情報は含みません。
   *
   * @param publicId ユーザーアカウントの公開ID
   * @return 該当するユーザーアカウント情報、存在しない場合は null
   */
  UserAccount findAccountByPublicId(@Param("publicId") String publicId);

  /**
   * 新しいユーザーアカウントを登録します。
   * <p>
   * 登録時に 削除フラグは false に設定され、内部ID(id)は自動採番されます。
   *
   * @param userAccount 登録するユーザーアカウント情報
   */
  void registerUserAccount(UserAccount userAccount);

  /**
   * 既存ユーザーアカウントの情報を更新します。
   * <p>
   * 更新対象フィールドが null の場合は変更されません。削除済みアカウントは更新されません。
   *
   * @param userAccount 更新対象のユーザーアカウント情報（publicId 必須）
   */
  void updateAccount(UserAccount userAccount);

  /**
   * 指定した公開IDのユーザーアカウントを論理削除します。
   * <p>
   * 削除フラグを true に設定し、削除済みアカウントは再度削除されません。
   *
   * @param publicId 削除対象のユーザーアカウント公開ID
   */
  void deleteAccount(@Param("publicId") String publicId);

  /**
   * 指定されたメールアドレスが既に登録済みかどうかをチェックします。
   * <p>
   * 削除済みも含む全レコードが検証対象です。(DBにもUnique設定があり登録不可のため）
   *
   * @param email チェック対象のメールアドレス
   * @return 登録済みの場合は true、未登録の場合は false
   */
  boolean existsByEmail(@Param("email") String email);

}
