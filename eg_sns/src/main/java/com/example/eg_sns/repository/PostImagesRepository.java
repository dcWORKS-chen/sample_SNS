package com.example.eg_sns.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.eg_sns.entity.PostImages;

public interface PostImagesRepository extends CrudRepository<PostImages, Long> {

	/**
	 * ポスト検索を行う。
	 * ポストIDを指定し、ポストを検索する。
	 *
	 * @param id トピックID
	 * @return トピック情報を返す。
	 */
    List<PostImages> findByPostsId(Long postsId);

}
