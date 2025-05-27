package com.example.eg_sns.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投稿コメントDTOクラス
 *
 * @author chink
 */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentsDto {
	/** ID */
	private Long id;
	
	/** 投稿文章 */
	private String body;
	
	/** コメント者ユーザーID */
	private Long userId;
	
	/** コメント者ユーザー名前 */
	private String userName;
	
	/** コメント者ユーザーアイコン */
	private String userIconUri;
	
}
