package com.example.eg_sns.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * フレンドEntityクラス。
 *
 * @author chink
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "friends")
public class Friends extends EntityBase{
	/** ID */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    /** 自分のユーザーID */
    @Column(name = "users_id", nullable = false)
    private Long usersId;

    /** フレンドのユーザーID */
    @Column(name = "friend_users_id", nullable = false)
    private Long friendUsersId;

    /** 承認ステータス（例：0 = 未承認, 1 = 承認済み, 2 = 却下） */
    @Column(name = "approval_status", nullable = false)
    private Integer approvalStatus;

    /** ユーザー情報とのJOIN（自分） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users user;

    /** ユーザー情報とのJOIN（フレンド） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_users_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Users friendUser;


}
