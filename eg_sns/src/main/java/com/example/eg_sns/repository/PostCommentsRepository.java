package com.example.eg_sns.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.eg_sns.entity.PostComments;

/**
 * 投稿コメント関連リポジトリインターフェース。
 *
 * @author chink
 */
public interface PostCommentsRepository extends PagingAndSortingRepository<PostComments, Long>, CrudRepository<PostComments, Long> {
	/**
	 * コメントの削除処理を行う。
	 * ※物理削除（データが完全に消える。）
	 *
	 * @param id コメントID
	 * @param usersId ユーザーID
	 * @param postsId 投稿ID
	 */
	@Transactional
	void deleteByIdAndUsersIdAndPostsId(Long id, Long usersId, Long postsId);
	
	/**
	 * 指定されたトピックIDに紐づくコメント一覧を取得
	 *
	 * @param postsId 投稿ID
	 * @return コメントリスト
	 */
	List<PostComments> findByPostsId(Long postsId);


}
