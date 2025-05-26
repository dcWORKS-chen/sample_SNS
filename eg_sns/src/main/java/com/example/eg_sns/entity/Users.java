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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ユーザーEntityクラス。
 *
 * @author chink
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class Users extends EntityBase {

	/** ID */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** ログインID */
	@Column(name = "login_id", nullable = false)
	private String loginId;

	/** パスワード */
	@Column(name = "password", nullable = false)
	private String password;

	/** 名前 */
	@Column(name = "name", nullable = false)
	private String name;

	/** メールアドレス */
    @Column(name = "email_address", nullable = false)
    private String emailAddress;
    
	/** プロフィールアイコンURI */
	@Column(name = "icon_uri", nullable = true)
	private String iconUri;
	
	/** プロフィール自己紹介文 */
	@Column(name = "profile", columnDefinition = "TEXT", nullable = true)
	private String profile;
	
	/** 投稿情報とのJOIN */
	@OneToMany(mappedBy = "users", fetch = FetchType.LAZY)
	@JsonManagedReference  // 循環参照を防ぐ
    private List<Posts> postsList = new ArrayList<>();
}
