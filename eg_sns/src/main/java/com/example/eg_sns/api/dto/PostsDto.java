package com.example.eg_sns.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.eg_sns.entity.Posts;
import com.example.eg_sns.entity.Users;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 投稿DTOクラス
 *
 * @author chink
 */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostsDto {
	/** ID */
	private Long id;
	
	/** 投稿タイトル */
	private String title;

	/** 投稿文章 */
	private String body;
	
	/** 作成日時 */
	private Date created;
	
	/** 作成日時（yyyy-MM-dd HH:mm） */
	private String formatedCreated;

	/** ユーザーID */
	private Long userId;
	
	/** ユーザー名前 */
	private String userName;
	
	/** ユーザーアイコン */
	private String userIconUri;
	
	public String getFormatedCreated() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.formatedCreated = sdf.format(this.created);
		return this.formatedCreated;	
	}
	
	// 手動マッピング
	public PostsDto(Posts posts) {
		if (posts == null) return;

		this.id = posts.getId();
		this.title = posts.getTitle();
		this.body = posts.getBody();
		this.created = posts.getCreated();

		// formattedCreated を即時に設定
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.formatedCreated = this.created != null ? sdf.format(this.created) : null;

		Users user = posts.getUsers();
		if (user != null) {
			this.userId = user.getId();
			this.userName = user.getName();
			this.userIconUri = user.getIconUri();
		}
	}
	
}
