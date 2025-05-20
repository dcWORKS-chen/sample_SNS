package com.example.eg_sns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.eg_sns.entity.Likes;
import com.example.eg_sns.repository.LikesRepository;

/**
 * いいね関連サービスクラス。
 *
 * @author chink
 */
@Service
public class LikesService {
	/** リポジトリインターフェース。 */
    @Autowired
    private LikesRepository repository;

    public boolean toggleLike(Long usersId, Long topicsId) {
        if (repository.existsByUsersIdAndTopicsId(usersId, topicsId)) {
        	repository.deleteByUsersIdAndTopicsId(usersId, topicsId);
            return false; // 取り消し
        } else {
            Likes likes = new Likes();
            likes.setUsersId(usersId);
            likes.setTopicsId(topicsId);
            repository.save(likes);
            return true; // いいね追加
        }
    }

    public int countLikes(Long topicsId) {
        return repository.countByTopicsId(topicsId);
    }

    public boolean isLiked(Long usersId, Long topicsId) {
        return repository.existsByUsersIdAndTopicsId(usersId, topicsId);
    }

}
