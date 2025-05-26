package com.example.eg_sns.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.eg_sns.entity.Posts;

/**
 * 投稿関連リポジトリインターフェース。
 *
 * @author chink
 */
public interface PostsRepository extends PagingAndSortingRepository<Posts, Long>, CrudRepository<Posts, Long> {
	/**
	 * 投稿検索を行う。
	 * 投稿IDを指定し、投稿を検索する。
	 *
	 * @param id 投稿ID
	 * @return 投稿情報を返す。
	 */
	Optional<Posts> findById(Long id);

	/**
	 * 投稿検索を行う。
	 * 投稿ID、ユーザーIDを指定し、投稿を検索する。
	 *
	 * @param id 投稿ID
	 * @param usersId ユーザーID
	 * @return 投稿情報を返す。
	 */
	Optional<Posts> findByIdAndUsersId(Long id, Long usersId);

	/**
	 * 投稿一覧を取得する。
	 * 投稿IDの降順。
	 * @return 投稿一覧を返す。
	 */
	List<Posts> findAllByOrderByIdDesc();
	
	/**
	 * 投稿一覧を取得する。
	 * 作成時間の降順。
	 * @return 投稿一覧を返す。
	 */
	Page<Posts> findAllByOrderByCreatedDesc(Pageable pageable);
	
	/**
	 * 投稿一覧を取得する。
	 * 作成時間の降順。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return 投稿一覧を返す。
	 */
	Page<Posts> findByIdLessThanOrderByCreatedDesc(@Param("id") Long sinceId, Pageable pageable);
	
	/**
	 * 指定なユーザーの投稿一覧を取得する。
	 * 投稿IDの降順。
	 * @return 指定なユーザーの投稿一覧を返す。
	 */
	List<Posts> findByUsersIdOrderByCreatedDesc(Long usersId);
}
