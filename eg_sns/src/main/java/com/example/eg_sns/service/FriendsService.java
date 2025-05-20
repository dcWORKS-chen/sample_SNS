package com.example.eg_sns.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.eg_sns.entity.Friends;
import com.example.eg_sns.repository.FriendsRepository;

import lombok.extern.log4j.Log4j2;

/**
 * フレンド関連サービスクラス。
 * 
 * @author chink
 */
@Log4j2
@Service
public class FriendsService {
	@Autowired
	private FriendsRepository friendsRepository;

	/**
	 * 指定したユーザーIDのフレンドリストを取得する。
	 *
	 * @param usersId ユーザーID
	 * @return フレンドの一覧
	 */
	public List<Friends> findFriendsByUsersId(Long usersId) {
		log.info("フレンド一覧を取得：usersId={}", usersId);
		return friendsRepository.findByUsersId(usersId);
	}

	/**
	 * フレンドリクエストを送信する。
	 *
	 * @param usersId 自分のユーザーID
	 * @param friendUsersId フレンド申請相手のユーザーID
	 */
	public boolean sendFriendRequest(Long usersId, Long friendUsersId) {
		log.info("フレンド申請：from={}, to={}", usersId, friendUsersId);

		// 既にフレンド申請が送られていないかを確認
		Optional<Friends> existingRequest = friendsRepository.findByUsersIdAndFriendUsersId(usersId, friendUsersId);
		if (existingRequest.isPresent()) {
			log.warn("既にフレンド申請が送信されています：from={}, to={}", usersId, friendUsersId);
			return false; // 重複申請の場合は送信しない
		}

		Friends friend = new Friends();
		friend.setUsersId(usersId);
		friend.setFriendUsersId(friendUsersId);
		friend.setApprovalStatus(0);

		friendsRepository.save(friend);
		return true;  // 申請送信成功
	}

	/**
	 * フレンドリクエストを承認する。
	 *
	 * @param requestId フレンドリクエストのID
	 */
	public void approveFriendRequest(Long requestId) {
		Friends request = friendsRepository.findById(requestId).orElse(null);
		if (request != null) {
            request.setApprovalStatus(1); // 承認
            friendsRepository.save(request);
            log.info("フレンドリクエストを承認しました：requestId={}", requestId);
        } else {
            log.error("フレンドリクエストが見つかりません：requestId={}", requestId);
        }
	}

	/**
	 * フレンドリクエストを拒否または削除する。
	 *
	 * @param requestId フレンドリクエストのID
	 */
	public void rejectFriendRequest(Long requestId) {
		if (friendsRepository.existsById(requestId)) {
            friendsRepository.deleteById(requestId);
            log.info("フレンドリクエストを拒否または削除しました：requestId={}", requestId);
        } else {
            log.error("フレンドリクエストが見つかりません：requestId={}", requestId);
        }
	}
	
	/**
	 * フレンドの状態確認。
	 *
	 * @param requestId フレンドリクエストのID
	 */
	public Optional<Friends> findByUsersIdAndFriendUsersId(Long usersId, Long friendUsersId) {
		return friendsRepository.findByUsersIdAndFriendUsersId(usersId, friendUsersId);
	}
	
	/**
	 * フレンドを解除。
	 *
	 * @param usersId 自分のユーザーID
	 * @param friendUsersId フレンド解除相手のユーザーID
	 */
	public void removeFriendship(Long usersId, Long friendUsersId) {
	    Optional<Friends> existingRequest = findByUsersIdAndFriendUsersId(usersId, friendUsersId);
	    if (existingRequest.isPresent()) {
	        friendsRepository.delete(existingRequest.get());
	    } else {
	        Optional<Friends> reverseRequest = findByUsersIdAndFriendUsersId(friendUsersId, usersId);
	        reverseRequest.ifPresent(friendsRepository::delete);
	    }
	}

}
