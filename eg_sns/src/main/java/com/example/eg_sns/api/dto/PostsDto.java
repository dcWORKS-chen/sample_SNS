package com.example.eg_sns.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.eg_sns.entity.PostComments;
import com.example.eg_sns.entity.PostImages;
import com.example.eg_sns.entity.Posts;
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

	/** 投稿画像 */
	private List<String> imageUrls;

	/** 投稿コメント */
	private List<CommentDto> comments;

	// 手動マッピング
	public PostDto(Posts post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.created = post.getCreated();
        this.formatedCreated = formatDate(post.getCreated());

        if (post.getUsers() != null) {
            this.userId = post.getUsers().getId();
            this.userName = post.getUsers().getName();
            this.userIconUri = post.getUsers().getIconUri();
        }

        this.imageUrls = post.getPostImagesList().stream()
                .map(PostImages::getImageUrl)
                .collect(Collectors.toList());

        this.comments = post.getPostCommentsList().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }
	
	private String formatDate(Date date) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    @Data
    public static class CommentDto {
        private Long id;
        private String body;
        private Long userId;
        private String userName;
        private String userIconUri;

        public CommentDto(PostComments comment) {
            this.id = comment.getId();
            this.body = comment.getBody();
            if (comment.getUsers() != null) {
                this.userId = comment.getUsers().getId();
                this.userName = comment.getUsers().getName();
                this.userIconUri = comment.getUsers().getIconUri();
            }
        }
    }

}
