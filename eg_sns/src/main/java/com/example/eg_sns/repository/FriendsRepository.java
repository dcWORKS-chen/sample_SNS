package com.example.eg_sns.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.eg_sns.entity.Friends;

/**
 * フレンド関連リポジトリインターフェース。
 * 
 *  @author chink
 */
public interface FriendsRepository extends PagingAndSortingRepository<Friends, Long>, CrudRepository<Friends, Long>{
    /**
     * フレンドIDによる検索。
     *
     * @param id フレンドID
     * @return フレンド情報
     */
    Optional<Friends> findById(Long id);

    /**
     * 自分のユーザーIDとフレンドのユーザーIDによる検索。
     *
     * @param usersId 自分のユーザーID
     * @param friendUsersId 相手のユーザーID
     * @return フレンド情報
     */
    Optional<Friends> findByUsersIdAndFriendUsersId(Long usersId, Long friendUsersId);

    /**
     * 自分のユーザーIDに関連するすべてのフレンド申請を取得。
     *
     * @param usersId 自分のユーザーID
     * @return フレンド一覧
     */
    List<Friends> findByUsersId(Long usersId);

    /**
     * フレンド申請の承認状態による検索。
     *
     * @param usersId 自分のユーザーID
     * @param approvalStatus 承認ステータス（0=未承認, 1=承認済み, 2=却下）
     * @return フレンド一覧
     */
    List<Friends> findByUsersIdAndApprovalStatus(Long usersId, Integer approvalStatus);

    /**
     * 相互関係を考慮し、対象ユーザーが自分をフレンド登録しているか確認するためのメソッド。
     *
     * @param friendUsersId 相手のユーザーID
     * @param usersId 自分のユーザーID
     * @return フレンド情報
     */
    Optional<Friends> findByFriendUsersIdAndUsersId(Long friendUsersId, Long usersId);
    
    /**
     * フレンド申請が承認されていない状態で、他のユーザーから申請されたリストを取得。
     *
     * @param usersId 自分のユーザーID
     * @return フレンド申請リスト（未承認）
     */
    List<Friends> findByFriendUsersIdAndApprovalStatus(Long usersId, Integer approvalStatus);

}
