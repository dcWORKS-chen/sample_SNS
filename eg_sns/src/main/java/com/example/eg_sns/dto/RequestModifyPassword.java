package com.example.eg_sns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * パスワード変更用DTOクラス。
 * 
 * 現在のパスワード、新しいパスワード、再入力確認を保持。
 * 
 * @author chink
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestModifyPassword extends DtoBase{
	/** 現在のパスワード */
	@NotBlank(message = "現在のパスワードを入力してください。")
	@Size(max = 32, message = "現在のパスワードは最大32文字です。")
	private String currentPassword;

	/** 新しいパスワード */
	@NotBlank(message = "新しいパスワードを入力してください。")
	@Size(max = 32, message = "新しいパスワードは最大32文字です。")
	private String newPassword;

	/** 新しいパスワード（確認） */
	@NotBlank(message = "新しいパスワード（確認）を入力してください。")
	@Size(max = 32, message = "確認用パスワードは最大32文字です。")
	private String confirmPassword;
}
