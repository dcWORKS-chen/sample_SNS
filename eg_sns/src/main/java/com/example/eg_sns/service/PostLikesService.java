package com.example.eg_sns.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.eg_sns.entity.PostLikes;
import com.example.eg_sns.repository.PostLikesRepository;

/**
 * いいね関連サービスクラス。
 *
 * @author chink
 */
@Service
public class PostLikesService {
	/** リポジトリインターフェース。 */
    @Autowired
    private PostLikesRepository repository;

	/**
	 * コメント登録処理を行う。
	 *
	 * @param usersId ユーザーID
	 * @param postsId 投稿ID
	 */
    @Transactional
    public boolean toggleLike(Long usersId, Long postsId) {
        if (repository.existsByUsersIdAndPostsId(usersId, postsId)) {
        	repository.deleteByUsersIdAndPostsId(usersId, postsId);
            return false; // 取り消し
        } else {
            PostLikes likes = new PostLikes();
            likes.setUsersId(usersId);
            likes.setPostsId(postsId);
            repository.save(likes);
            return true; // いいね追加
        }
    }

    public int countLikes(Long postsId) {
        return repository.countByPostsId(postsId);
    }

    public boolean isLiked(Long usersId, Long postsId) {
        return repository.existsByUsersIdAndPostsId(usersId, postsId);
    }
    
	/**
	 * いいねの削除処理を行う。
	 *
	 * @param likesList コメントリスト
	 */
	public void delete(List<PostLikes> likesList) {
		repository.deleteAll(likesList);
	}

}
