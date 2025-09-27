package com.portfolio.taskapp.MyTaskManager.task.repository;

import com.portfolio.taskapp.MyTaskManager.domain.entity.Project;
import com.portfolio.taskapp.MyTaskManager.domain.entity.Task;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * プロジェクトおよびタスクに関する DB 操作を提供するインターフェース。MyBatis でマッピングしています。
 * <p>
 * このリポジトリでは、プロジェクトおよびタスクに関する取得、登録、更新、論理削除処理を行います。 また、プロジェクト関連操作時の関連情報としてユーザー内部IDを取得するメソッドも含みます。
 * 基本方針として、必要がある場合を除き論理削除済みの情報は取得・更新・再削除されません。
 */
@Mapper
public interface TaskRepository {

  /**
   * 指定されたユーザー公開IDに紐づくユーザー内部IDを取得します。
   * <p>
   * このメソッドはプロジェクトの登録・取得時の紐づけ情報(user_id)の取得に利用されます。
   *
   * @param userPublicId ユーザー公開ID
   * @return 該当するユーザーの内部ID、存在しない場合は null
   */
  Integer findUserIdByUserPublicId(@Param("userPublicId") String userPublicId);

  /**
   * 指定ユーザーIDに紐づくプロジェクト一覧を取得します。
   *
   * @param userAccountId 内部ユーザーID
   * @return プロジェクトのリスト、存在しない場合は空リスト
   */
  List<Project> findProjectsByUserId(@Param("userAccountId") Integer userAccountId);

  /**
   * 指定プロジェクトIDに紐づくタスク一覧を取得します。
   *
   * @param projectId 内部プロジェクトID
   * @return タスクのリスト、存在しない場合は空リスト
   */
  List<Task> findTasksByProjectId(@Param("projectId") Integer projectId);

  /**
   * 指定したIDに紐づくタスクとその子タスクを一覧取得します。
   *
   * @param taskId 親タスクの内部ID
   * @return 親タスクとその子タスクを含むタスク一覧、存在しない場合は空リスト
   */
  List<Task> findTasksByTaskId(@Param("taskId") Integer taskId);

  /**
   * 指定されたプロジェクト公開IDに紐づくプロジェクトを取得します。
   *
   * @param projectPublicId プロジェクト公開ID
   * @return 該当プロジェクト、存在しない場合は null
   */
  Project findProjectByProjectPublicId(@Param("projectPublicId") String projectPublicId);

  /**
   * 指定されたタスク公開IDに紐づくタスクを取得します。
   *
   * @param taskPublicId タスク公開ID
   * @return 該当タスク、存在しない場合は null
   */
  Task findTaskByTaskPublicId(@Param("taskPublicId") String taskPublicId);

  /**
   * 新しいプロジェクトを登録します。
   * <p>
   * 登録時に 削除フラグは false に設定され、内部ID(id)は自動採番されます。
   *
   * @param project 登録するプロジェクトの登録情報
   */
  void createProject(Project project);

  /**
   * 新しいタスクを登録します。
   * <p>
   * 登録時に 削除フラグは false に設定され、内部ID(id)は自動採番されます。
   *
   * @param task 登録するタスクの登録情報
   */
  void createTask(Task task);

  /**
   * 既存プロジェクトを更新します。
   * <p>
   * 削除済みプロジェクトは更新されません。
   *
   * @param project 更新対象のプロジェクトの更新情報
   * @return 更新件数（通常は 0 または 1）
   */
  int updateProject(Project project);

  /**
   * 既存タスクを更新します。
   * <p>
   * 削除済みタスクは更新されません。
   *
   * @param task 更新対象のタスクの更新情報
   * @return 更新件数（通常は 0 または 1）
   */
  int updateTask(Task task);

  /**
   * 指定されたプロジェクト公開IDのプロジェクトを論理削除します。
   * <p>
   * 削除フラグを true に設定し、削除済みプロジェクトは再度削除されません。
   *
   * @param projectPublicId プロジェクト公開ID
   */
  void deleteProject(@Param("projectPublicId") String projectPublicId);

  /**
   * 指定されたタスク公開IDのタスクを論理削除します。
   * <p>
   * 削除フラグを true に設定し、削除済みタスクは再度削除されません。
   *
   * @param taskPublicId タスク公開ID
   */
  void deleteTask(@Param("taskPublicId") String taskPublicId);
}
