package com.example.eg_sns.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.eg_sns.entity.PostComments;
import com.example.eg_sns.entity.PostImages;
import com.example.eg_sns.entity.PostLikes;
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
	
	/** 投稿画像URLリスト */
	private List<String> postImagesUri;
	
	/** 投稿コメントリスト */
	private List<CommentsDto> comments;
	
	/** いいね数 */
	private int likeCount;
	
	/** いいねしました？ */
	private boolean liked;
	
	public String getFormatedCreated() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.formatedCreated = sdf.format(this.created);
		return this.formatedCreated;	
	}
	
	// 手動マッピング
	public PostsDto(Posts posts, Long loginUserId) {
		if (posts == null) return;

		this.id = posts.getId();
		this.title = posts.getTitle();
		this.body = posts.getBody();
		this.created = posts.getCreated();

		// formattedCreated を即時に設定
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.formatedCreated = this.created != null ? sdf.format(this.created) : null;

		// ユーザー情報
		Users user = posts.getUsers();
		if (user != null) {
			this.userId = user.getId();
			this.userName = user.getName();
			this.userIconUri = user.getIconUri();
		}
		
		// 投稿画像
		List<PostImages> postImages = posts.getPostImagesList();
		if (postImages != null && !postImages.isEmpty()) {
			this.postImagesUri = postImages.stream()
				.map(PostImages::getImageUrl)
				.collect(Collectors.toList());
		}
		
		// 投稿コメント
		List<PostComments> postComments = posts.getPostCommentsList();
		if (postComments != null && !postComments.isEmpty()) {
			this.comments = postComments.stream().map(pc -> {
				CommentsDto dto = new CommentsDto();
				dto.setId(pc.getId());
				dto.setBody(pc.getBody());

				Users commentUser = pc.getUsers();
				if (commentUser != null) {
					dto.setUserId(commentUser.getId());
					dto.setUserName(commentUser.getName());
					dto.setUserIconUri(commentUser.getIconUri());
				}

				return dto;
			}).collect(Collectors.toList());
		}
		
		List<PostLikes> postLikes = posts.getPostLikesList();
		if (postLikes != null) {
			this.likeCount = postLikes.size();
			this.liked = postLikes.stream()
					.anyMatch(like -> like.getUsers().getId().equals(loginUserId));
		}
	}
	
}
