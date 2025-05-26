package com.example.eg_sns.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 投稿Entityクラス。
 *
 * @author chink
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "posts")
public class Posts extends EntityBase{
	/** ID */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** ユーザーID */
	@Column(name = "users_id", nullable = false)
	private Long usersId;

	/** タイトル */
	@Column(name = "title", nullable = false)
	private String title;

	/** 本文 */
	@Column(name = "body", nullable = false)
	private String body;
	

	/** ユーザー情報とのJOIN */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Users users;
	
	/** 投稿画像情報とのJOIN */
	@OneToMany(mappedBy = "posts", fetch = FetchType.LAZY)
	@JsonManagedReference  // 循環参照を防ぐ
    private List<PostImages> postImagesList = new ArrayList<>();
	
	/** 投稿コメント情報とのJOIN */
	@OneToMany(mappedBy = "posts", fetch = FetchType.LAZY)
	@JsonManagedReference  // 循環参照を防ぐ
    private List<PostComments> postCommentsList = new ArrayList<>();

	/** 投稿いいね情報とのJOIN */
	@OneToMany(mappedBy = "posts", fetch = FetchType.LAZY)
	@JsonManagedReference  // 循環参照を防ぐ
    private List<PostLikes> postLikesList = new ArrayList<>();

}
