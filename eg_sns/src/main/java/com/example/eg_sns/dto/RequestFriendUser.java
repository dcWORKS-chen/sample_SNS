package com.example.eg_sns.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * フレンド申請用ユーザーDTOクラス。
 * 
 * @author chink
 * 
 * 表示用のユーザーデータに加え、承認ステータスやリクエストIDなどの情報も含みます。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RequestFriendUser extends DtoBase{
    /** ユーザーID */
    private Long id;

    /** 名前 */
    private String name;

    /** プロフィール文 */
    private String profile;

    /** プロフィール画像URL */
    private String profileImageUrl;

    /** フレンド申請のID（nullの場合、申請なし） */
    private Long requestId;

    /**
     * 承認ステータス（NULL=フレンド申請可能, 0: 申請中, 1=承認済み, 2=申請されている（承認・却下））
     */
    private int approvalStatus;
}
