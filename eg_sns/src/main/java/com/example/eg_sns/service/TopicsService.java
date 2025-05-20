package com.example.eg_sns.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.example.eg_sns.core.AppNotFoundException;
import com.example.eg_sns.dto.RequestTopic;
import com.example.eg_sns.entity.Comments;
import com.example.eg_sns.entity.PostImages;
import com.example.eg_sns.entity.Topics;
import com.example.eg_sns.repository.CommentsRepository;
import com.example.eg_sns.repository.PostImagesRepository;
import com.example.eg_sns.repository.TopicsRepository;
import com.example.eg_sns.util.CollectionUtil;

import lombok.extern.log4j.Log4j2;

/**
 * トピック関連サービスクラス。
 *
 * @author tomo-sato
 */
@Log4j2
@Service
public class TopicsService {

	/** リポジトリインターフェース。 */
	@Autowired
	private TopicsRepository repository;

	@Autowired
	private CommentsRepository commentsRepository;

	/** コメント関連サービスクラス。 */
	@Autowired
	private CommentsService commentsService;

	/** ポスト画像リポジトリインターフェース。 */
	@Autowired
	private PostImagesRepository postImagesRepository;

	/**
	 *  トピック全件取得する。
	 *
	 * @return  トピックを全件取得する。
	 */
	public Page<Topics> findAllTopics(Integer page) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(Direction.DESC, "id"));

		Pageable pageable = PageRequest.of(page, 5, Sort.by(orderList));

		return repository.findAll(pageable);
	}

	/**
	 * トピック全件取得する。
	 *
	 * @return トピックを全件取得する。
	 */
	public Page<Topics> findAllTopics(Long sinceId, Integer page) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(Direction.DESC, "id"));

		Pageable pageable = PageRequest.of(page, 5, Sort.by(orderList));

		return repository.findByIdLessThan(sinceId, pageable);
	}

	/**
	 * トピック全件取得する。
	 *
	 * @return トピックを全件取得する。
	 */
	public List<Topics> findAllTopics() {
		return repository.findAllByOrderByIdDesc();
	}

	/**
	 * トピック一覧を取得する。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return 指定したIDよりも新しいデータを10件取得する。
	 */
	public List<Topics> findTopics(Integer sinceId) {
		return repository.findFirst10ByIdGreaterThan(sinceId);
	}


	/**
	 * トピック検索を行う。
	 * トピックIDと、ログインIDを指定し、トピックを検索する。
	 *
	 * @param id トピックID
	 * @return トピック情報を返す。
	 */
	public Topics findTopics(Long id) {
		log.info("トピックを検索します。：id={}", id);

		Topics topics = repository.findById(id).orElse(null);
		log.info("ユーザー検索結果。：id={}, topics={}", id, topics);

		return topics;
	}

	/**
	 * トピック検索を行う。
	 * ユーザーIDと、ログインIDを指定し、トピックを検索する。
	 *
	 * @param userId ユーザーID
	 * @return トピック情報を返す。
	 */
	public List<Topics> findUserTopics(Long userId) {
		log.info("ユーザーのトピックを検索します：userId={}", userId);

		List<Topics> topicsList = repository.findByUsersIdOrderByCreatedDesc(userId);
		log.info("検索結果件数：{}", topicsList.size());

		return topicsList;
	}

	/**
	 * トピック登録処理を行う。
	 *
	 * @param requestTopic トピックDTO
	 * @param usersId ユーザーID
	 */
	public Topics save(RequestTopic requestTopic, Long usersId) {
		Topics topics = new Topics();
		topics.setUsersId(usersId);
		topics.setTitle(requestTopic.getTitle());
		topics.setBody(requestTopic.getBody());
		// トピックを保存
		topics = repository.save(topics);

		// 画像URLが存在する場合
		List<String> imageUrls = requestTopic.getImageUrls();
		if (imageUrls != null && !imageUrls.isEmpty()) {
			for (String imageUrl : imageUrls) {
				PostImages postImage = new PostImages();
				postImage.setPostsId(topics.getId());
				postImage.setUsersId(usersId);
				postImage.setImageUrl(imageUrl);

				postImagesRepository.save(postImage);
			}
		}

		return topics;

	}

	/**
	 * トピックの削除処理を行う。
	 *
	 * @param topicsId トピックID
	 * @param usersId ユーザーID
	 */
	public void delete(Long topicsId, Long usersId) {
		log.info("トピックを削除します。：topicsId={}, usersId={}", topicsId, usersId);

		// 対象のトピックを検索。
		Topics topics = repository.findByIdAndUsersId(topicsId, usersId).orElse(null);
		if (topics == null) {
			// データが取得できない場合は不正操作の為エラー。（404エラーとする。）
			throw new AppNotFoundException();
		}

		// トピックにぶら下がってるコメントを削除。
		List<Comments> commentsList = topics.getCommentsList();
		if (CollectionUtil.isNotEmpty(commentsList)) {
			commentsService.delete(commentsList);
		}

		// トピックを削除。
		repository.delete(topics);
	}

	/**
	 * 指定されたトピックIDに紐づく画像URLのリストを取得する。
	 *
	 * @param topicId トピックID
	 * @return 画像URLのリスト
	 */
	public List<PostImages> findImagePathsByTopicId(Long topicId) {
		return postImagesRepository.findByPostsId(topicId);

	}

	/**
	 * 指定されたトピックIDに紐づくコメントのリストを取得する。
	 *
	 * @param topicId トピックID
	 * @return コメントのリスト（投稿日時昇順）
	 */
	public List<Comments> findCommentsByTopicId(Long topicId) {
		return commentsRepository.findByTopicsId(topicId);
	}

}
