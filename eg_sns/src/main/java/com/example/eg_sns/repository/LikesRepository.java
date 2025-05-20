package com.example.eg_sns.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.eg_sns.entity.Likes;

/**
 * いいね関連リポジトリインターフェース。
 *
 * @author chink
 */
public interface LikesRepository extends PagingAndSortingRepository<Likes, Long>, CrudRepository<Likes, Long> {

	/**
	 * 指定されたユーザーIDと投稿IDに対応するLikeが存在するかを確認します。
	 *
	 * @param usersId ユーザーID
	 * @param topicsId 投稿ID
	 * @return Likeが存在すれば true、存在しなければ false
	 */
	boolean existsByUsersIdAndTopicsId(Long usersId, Long topicsId);

	/**
	 * 指定されたユーザーIDと投稿IDに対応するLikeを削除します。
	 *
	 * @param usersId ユーザーID
	 * @param topicsId 投稿ID
	 */
	void deleteByUsersIdAndTopicsId(Long usersId, Long topicsId);

	/**
	 * 指定された投稿に対するLike数をカウントします。
	 *
	 * @param topicsId 投稿ID
	 * @return その投稿に対するLikeの数
	 */
	int countByTopicsId(Long topicsId);
}
