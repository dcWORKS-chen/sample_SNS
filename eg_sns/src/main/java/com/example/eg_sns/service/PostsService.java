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
import com.example.eg_sns.dto.RequestPost;
import com.example.eg_sns.entity.PostComments;
import com.example.eg_sns.entity.PostImages;
import com.example.eg_sns.entity.PostLikes;
import com.example.eg_sns.entity.Posts;
import com.example.eg_sns.repository.PostImagesRepository;
import com.example.eg_sns.repository.PostsRepository;
import com.example.eg_sns.util.CollectionUtil;

import lombok.extern.log4j.Log4j2;

/**
 * 投稿関連サービスクラス。
 *
 * @author chink
 */
@Log4j2
@Service
public class PostsService {
	/** リポジトリインターフェース。 */
	@Autowired
	private PostsRepository repository;
	
	@Autowired
	private PostImagesRepository postImagesRepository;

	/** サービスクラス。 */
	@Autowired
	private PostCommentsService postCommentsService;
	
	@Autowired
	private PostLikesService postLikesService;
	
	/**
	 *  投稿5件取得する。
	 *
	 * @return  投稿を５件取得する。
	 */
	public Page<Posts> findAllPosts(Integer page) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(Direction.DESC, "id"));

		Pageable pageable = PageRequest.of(page, 5, Sort.by(orderList));

		return repository.findAllByOrderByCreatedDesc(pageable);
	}

	/**
	 * 指定した投稿IDから古い投稿５件取得する。
	 *
	 * @param sinceId 表示している古い投稿ID
	 * @return 投稿を５件取得する。
	 */
	public Page<Posts> findAllPosts(Long sinceId, Integer page) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(Direction.DESC, "id"));

		Pageable pageable = PageRequest.of(page, 5, Sort.by(orderList));

		return repository.findByIdLessThanOrderByCreatedDesc(sinceId, pageable);
	}
	
	/**
	 * 投稿検索を行う。
	 * ユーザーIDを指定し、投稿を検索する。
	 *
	 * @param userId ユーザーID
	 * @return 投稿情報を返す。
	 */
	public List<Posts> findUserPosts(Long userId) {
		log.info("ユーザーのトピックを検索します：userId={}", userId);

		List<Posts> postsList = repository.findByUsersIdOrderByCreatedDesc(userId);
		log.info("検索結果件数：{}", postsList.size());

		return postsList;
	}
	
	/**
	 * 投稿登録処理を行う。
	 *
	 * @param requestPost 投稿DTO
	 * @param usersId ユーザーID
	 */
	public Posts save(RequestPost requestPost, Long usersId) {
		Posts posts = new Posts();
		
		posts.setUsersId(usersId);
		posts.setTitle(requestPost.getTitle());
		posts.setBody(requestPost.getBody());
		
		// 投稿を保存
		posts = repository.save(posts);

		// 画像URLが存在する場合
		List<String> imageUrls = requestPost.getImageUrls();
		if (imageUrls != null && !imageUrls.isEmpty()) {
			for (String imageUrl : imageUrls) {
				PostImages postImage = new PostImages();
				
				postImage.setPostsId(posts.getId());
				postImage.setUsersId(usersId);
				postImage.setImageUrl(imageUrl);

				postImagesRepository.save(postImage);
			}
		}
		return posts;
	}
	
	/**
	 * 投稿の削除処理を行う。
	 *
	 * @param postsId 投稿ID
	 * @param usersId ユーザーID
	 */
	public void delete(Long postsId, Long usersId) {
		log.info("トピックを削除します。：topicsId={}, usersId={}", postsId, usersId);

		// 対象のトピックを検索。
		Posts posts = repository.findByIdAndUsersId(postsId, usersId).orElse(null);
		if (posts == null) {
			// データが取得できない場合は不正操作の為エラー。（404エラーとする。）
			throw new AppNotFoundException();
		}

		// 投稿にぶら下がってるコメントを削除。
		List<PostComments> commentsList = posts.getPostCommentsList();
		if (CollectionUtil.isNotEmpty(commentsList)) {
			postCommentsService.delete(commentsList);
		}
		
		// 投稿にぶら下がってる画像を削除。
		List<PostImages> imagesList = posts.getPostImagesList();
		if (CollectionUtil.isNotEmpty(imagesList)) {
			postImagesRepository.deleteAll(imagesList);
		}
		
		// 投稿にぶら下がってるいいねを削除。
		List<PostLikes> likesList = posts.getPostLikesList();
		if (CollectionUtil.isNotEmpty(likesList)) {
			postLikesService.delete(likesList);
		}

		// 投稿を削除。
		repository.delete(posts);
		log.info("投稿の削除が完了しました");
	}
}
