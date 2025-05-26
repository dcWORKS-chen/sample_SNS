package com.example.eg_sns.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.eg_sns.entity.PostLikes;

/**
 * いいね関連リポジトリインターフェース。
 *
 * @author chink
 */
public interface PostLikesRepository extends PagingAndSortingRepository<PostLikes, Long>, CrudRepository<PostLikes, Long> {
	/**
	 * 指定されたユーザーIDと投稿IDに対応するLikeが存在するかを確認します。
	 *
	 * @param usersId ユーザーID
	 * @param postsId 投稿ID
	 * @return Likeが存在すれば true、存在しなければ false
	 */
	boolean existsByUsersIdAndPostsId(Long usersId, Long postsId);

	/**
	 * 指定されたユーザーIDと投稿IDに対応するLikeを削除します。
	 *
	 * @param usersId ユーザーID
	 * @param postsId 投稿ID
	 */
	void deleteByUsersIdAndPostsId(Long usersId, Long postsId);

	/**
	 * 指定された投稿に対するLike数をカウントします。
	 *
	 * @param postsId 投稿ID
	 * @return その投稿に対するLikeの数
	 */
	int countByPostsId(Long postsId);


}
