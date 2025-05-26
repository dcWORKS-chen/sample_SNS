package com.example.eg_sns.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.eg_sns.entity.PostImages;

/**
 * 投稿画像関連リポジトリインターフェース。
 *
 * @author chink
 */
public interface PostImagesRepository extends CrudRepository<PostImages, Long> {

	/**
	 * 投稿画像検索を行う。
	 * 投稿IDを指定し、投稿画像を検索する。
	 *
	 * @param id 投稿ID
	 * @return 投稿画像の情報を返す。
	 */
    List<PostImages> findByPostsId(Long postsId);

}
