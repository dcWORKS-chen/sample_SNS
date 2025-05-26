package com.example.eg_sns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 投稿コメントDTOクラス。
 *
 * @author chink
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestPostComment extends DtoBase {

	/** 本文 */
	@NotBlank(message = "コメント本文を入力してください。")
	@Size(max = 2000, message = "コメント本文は最大2000文字です。")
	private String body;
}