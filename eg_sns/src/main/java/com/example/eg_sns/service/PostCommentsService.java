package com.example.eg_sns.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.eg_sns.dto.RequestPostComment;
import com.example.eg_sns.entity.PostComments;
import com.example.eg_sns.repository.PostCommentsRepository;

import lombok.extern.log4j.Log4j2;

/**
 * コメント関連サービスクラス。
 *
 * @author chink
 */
@Log4j2
@Service
public class PostCommentsService {
	/** リポジトリインターフェース。 */
	@Autowired
	private PostCommentsRepository repository;
	
	/**
	 * コメント登録処理を行う。
	 *
	 * @param requestPostComment コメントDTO
	 * @param usersId ユーザーID
	 * @param postsId 投稿ID
	 */
	public PostComments save(RequestPostComment requestPostComment, Long usersId, Long postsId) {
		PostComments comments = new PostComments();
		
		comments.setUsersId(usersId);
		comments.setPostsId(postsId);
		comments.setBody(requestPostComment.getBody());
		
		return repository.save(comments);
	}

	/**
	 * コメントの削除処理を行う。
	 *
	 * @param id コメントID
	 * @param usersId ユーザーID
	 * @param postsId 投稿ID
	 */
	public void delete(Long id, Long usersId, Long postsId) {
		log.info("コメントを削除します。：id={}, usersId={}, postsId={}", id, usersId, postsId);

		repository.deleteByIdAndUsersIdAndPostsId(id, usersId, postsId);
	}

	/**
	 * コメントの削除処理を行う。
	 *
	 * @param commentsList コメントリスト
	 */
	public void delete(List<PostComments> commentsList) {
		repository.deleteAll(commentsList);
	}

}
