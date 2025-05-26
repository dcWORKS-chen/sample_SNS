package com.example.eg_sns.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eg_sns.api.PostApiController.Response.PageInfo;
import com.example.eg_sns.api.dto.PostsDto;
import com.example.eg_sns.entity.Posts;
import com.example.eg_sns.service.PostsService;
import com.example.eg_sns.util.AppPageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * 投稿に関するRestAPI.
 *
 * @author chink
 */
@Log4j2
@RestController
@RequestMapping("/api")
public class PostApiController {
	/** サービスクラス。 */
	@Autowired
	private PostsService postsService;

	/**
	 * 投稿を取得する。
	 *
	 * @param sinceid 指定されたIDよりも古いものを取得する。
	 * @return
	 * @throws JsonProcessingException
	 */
	@GetMapping(value = "/getPosts")
	public Response getPosts(@RequestParam Long sinceId) throws JsonProcessingException {
		Response response = new Response(0);
		PageInfo pageInfo = new PageInfo();
		System.out.println(sinceId);

		// 投稿を取得
		Page<Posts> pagePosts = postsService.findAllPosts(sinceId, 0);
		AppPageWrapper<Posts> pager = new AppPageWrapper<Posts>(pagePosts);
		log.info(pagePosts);

		List<Posts> postsList = pagePosts.getContent();

		Long retSinceId = Long.MAX_VALUE;
		for (Posts posts : postsList) {
			retSinceId = Math.min(retSinceId, posts.getId());
		}

		pageInfo.setHasNext(pager.isHasNextPage());
		pageInfo.setSinceId(retSinceId);

		// DTOにマッピング。
		List<PostsDto> data = postsList.stream()
				.map(PostsDto::new)
				.collect(Collectors.toList());
		
		response.setData(data);
		response.setPageInfo(pageInfo);

		log.info(response);
		return response;
	}

	/**
	 * レスポンスデータ。
	 *
	 * @author chink
	 */
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class Response {

		/** レスポンスコード：0.成功、-1.失敗 */
		private Integer responseCode;

		/** ページ情報。 */
		private PageInfo pageInfo;

		/** 取得結果。 */
		private List<PostsDto> data;

		/**
		 * コンストラクタ。
		 *
		 * @param responseCode レスポンスコード
		 */
		public Response(Integer responseCode) {
			this.responseCode = responseCode;
		}

		/**
		 * ページ情報。
		 *
		 * @author chink
		 */
		@Data
		@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
		public static class PageInfo {

			/** 指定されたIDよりも新しいものを取得する為のID。 */
			private Long sinceId = 0L;

			/** 次のページがあるか、否か。 */
			private boolean hasNext = false;
		}

	}

}
