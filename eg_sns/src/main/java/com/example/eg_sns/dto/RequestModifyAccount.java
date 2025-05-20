package com.example.eg_sns.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * アカウント編集DTOクラス。
 *
 * @author tomo-sato
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestModifyAccount extends DtoBase {

	/** お名前 */
	@NotBlank(message = "お名前を入力してください。")
	@Size(max = 32, message = "お名前は最大32文字です。")
	private String name;
	
	/** メールアドレス */
	@NotBlank(message = "メールアドレスを入力してください。")
	@Email(message = "正しいメールアドレスを入力してください。")
	@Size(max = 256, message = "メールアドレスは最大256文字です。")
	private String emailAddress;

	/** プロフィール（自己紹介） */
	@Size(max = 1000, message = "プロフィールは最大1000文字です。")
	private String profile;

	/** 設定済みのファイル */
	private String profileFileHidden;
}
