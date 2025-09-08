package com.portfolio.taskapp.MyTaskManager.user.service.mapper;

import com.portfolio.taskapp.MyTaskManager.domain.entity.UserAccount;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountRegisterRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.AccountResponse;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountEmailUpdateRequest;
import com.portfolio.taskapp.MyTaskManager.user.dto.update.AccountUserInfoUpdateRequest;
import org.springframework.stereotype.Component;

/**
 * アカウント関連処理におけるマッパークラス。
 * <p>
 * 主に以下の変換を行います：
 * <ul>
 *   <li>リクエストオブジェクト → UserAccount エンティティ</li>
 *   <li>UserAccount エンティティ → レスポンス用オブジェクト（AccountResponse）</li>
 * </ul>
 */
@Component
public class UserAccountMapper {

  /**
   * アカウント登録用リクエストから UserAccount エンティティを生成します。
   *
   * @param request        アカウント登録用リクエスト情報
   * @param publicId       公開ID
   * @param hashedPassword ハッシュ化済みパスワード
   * @return UserAccount 登録用のアカウントのエンティティ
   */
  public UserAccount createRequestToUserAccount(AccountRegisterRequest request, String publicId,
      String hashedPassword) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .email(request.getEmail())
        .password(hashedPassword)
        .build();
  }

  /**
   * アカウントのユーザー情報更新用リクエストから UserAccount エンティティを生成します。
   *
   * @param request  ユーザー情報更新用リクエスト
   * @param publicId 公開ID
   * @return UserAccount ユーザー情報更新用エンティティ
   */
  public UserAccount updateUserInfoRequestToUserAccount(AccountUserInfoUpdateRequest request,
      String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .userName(request.getUserName())
        .build();
  }

  /**
   * アカウントのメールアドレス更新用リクエストから UserAccount エンティティを生成します。
   *
   * @param request  メールアドレス更新用リクエスト
   * @param publicId 公開ID
   * @return UserAccount メールアドレス更新用エンティティ
   */
  public UserAccount updateEmailRequestToUserAccount(AccountEmailUpdateRequest request,
      String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .email(request.getEmail())
        .build();
  }

  /**
   * アカウント公開IDと新しいハッシュ化済みパスワードから UserAccount エンティティを生成します。
   *
   * @param newHashedPassword ハッシュ化された新しいpassword
   * @param publicId          公開ID
   * @return UserAccount パスワード更新用エンティティ
   */

  public UserAccount updatePasswordRequestToUserAccount(String newHashedPassword,
      String publicId) {
    return UserAccount.builder()
        .publicId(publicId)
        .password(newHashedPassword)
        .build();
  }

  /**
   * UserAccount エンティティをレスポンス用オブジェクト (AccountResponse) に変換します。
   *
   * @param account 変換対象の UserAccount エンティティ
   * @return AccountResponse アカウント情報のレスポンスオブジェクト
   */
  public AccountResponse toUserAccountResponse(UserAccount account) {
    return new AccountResponse(
        account.getPublicId(),
        account.getUserName(),
        account.getEmail(),
        account.getCreatedAt(),
        account.getUpdatedAt()
    );
  }

}
