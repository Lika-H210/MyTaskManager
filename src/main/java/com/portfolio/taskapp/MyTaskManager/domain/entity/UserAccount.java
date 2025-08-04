package com.portfolio.taskapp.MyTaskManager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "ユーザーを定義するエンティティクラス")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccount {

  // AUTO_INCREMENT PRIMARY KEY
  @Schema(description = "ユーザーID（自動採番）")
  @JsonIgnore
  private Integer id;

  // CHAR(36) NOT NULL UNIQUE
  @Schema(description = "UUID形式の公開ID")
  private String publicId;

  // VARCHAR(50) NOT NULL
  @Schema(description = "ユーザー表示名")
  private String userName;

  // VARCHAR(255) NOT NULL UNIQUE
  @Schema(description = "メールアドレス（ユニーク）")
  private String email;

  // VARCHAR(255) NOT NULL
  @Schema(description = "ハッシュ化済みパスワード")
  private String password;

  // DATETIME DEFAULT CURRENT_TIMESTAMP
  @Schema(description = "作成日時")
  private LocalDateTime createdAt;

  // DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除用の削除フラグ(削除=true)")
  private boolean is_deleted;

}
