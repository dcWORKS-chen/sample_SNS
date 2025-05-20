package com.example.eg_sns.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.eg_sns.api.TopicApiController.Response.PageInfo;
import com.example.eg_sns.api.dto.TopicsDto;
import com.example.eg_sns.entity.PostImages;
import com.example.eg_sns.entity.Topics;
import com.example.eg_sns.entity.Users;
import com.example.eg_sns.repository.PostImagesRepository;
import com.example.eg_sns.service.TopicsService;
import com.example.eg_sns.util.AppPageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * トピックスに関するRestAPI.
 *
 * @author chink
 */
@Log4j2
@RestController
@RequestMapping("/api")
public class TopicApiController {
	/** トピック関連サービスクラス。 */
	@Autowired
	private TopicsService topicsService;
	
	/** トピック関連サービスクラス。 */
	@Autowired
	private PostImagesRepository postImagesRepository;
	/**
	 * トピックスを取得する。
	 *
	 * @param sinceid 指定されたIDよりも古いものを取得する。
	 * @return
	 * @throws JsonProcessingException
	 */
	@GetMapping(value = "/getTopics")
	public Response getTopics(@RequestParam Long sinceId) throws JsonProcessingException {
		Response response = new Response(0);
		PageInfo pageInfo = new PageInfo();
		System.out.println(sinceId);

		// 投稿を取得（id < sinceId）
		Page<Topics> pageTopics = topicsService.findAllTopics(sinceId, 0);
		AppPageWrapper<Topics> pager = new AppPageWrapper<Topics>(pageTopics);
		log.info(pageTopics);

		List<Topics> topicsList = pageTopics.getContent();

		Long retSinceId = Long.MAX_VALUE;
		for (Topics topics : topicsList) {
			retSinceId = Math.min(retSinceId, topics.getId());
		}

		pageInfo.setHasNext(pager.isHasNextPage());
		pageInfo.setSinceId(retSinceId);

		List<TopicsDto> data = topicsList.stream().map(topics -> {
			Users user = topics.getUsers();

			TopicsDto dto = new TopicsDto();
			dto.setId(topics.getId());
			dto.setUsersId(user != null ? user.getId() : null);
			dto.setUserName(user != null ? user.getName() : null);
			dto.setIconUri(user != null ? user.getIconUri() : null);
			dto.setTitle(topics.getTitle());
			dto.setBody(topics.getBody());
			dto.setCreated(topics.getCreated());
			dto.setUpdated(topics.getUpdated());
			dto.getFormatedCreated(); // オプション
			
			// 画像URLのリスト取得
			List<String> imageUrls = postImagesRepository.findByPostsId(topics.getId())
					.stream()
					.map(PostImages::getImageUrl)
					.collect(Collectors.toList());
			dto.setImageUrls(imageUrls);
			
			// コメントをマッピング
			List<TopicsDto.CommentDto> commentDtos = topics.getCommentsList().stream().map(c -> {
				TopicsDto.CommentDto cdto = new TopicsDto.CommentDto();
				cdto.setId(c.getId());
				cdto.setUsersId(c.getUsersId());
				cdto.setUserName(c.getUsers().getName());
				cdto.setIconUri(c.getUsers().getIconUri());
				cdto.setBody(c.getBody());
				return cdto;
			}).collect(Collectors.toList());
			dto.setComments(commentDtos);
			
			return dto;
		}).collect(Collectors.toList());
		
		response.setData(data);
		response.setPageInfo(pageInfo);

		log.info(response);
		return response;
	}

	/**
	 * レスポンスデータ。
	 *
	 * @author tomo-sato
	 */
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class Response {

		/** レスポンスコード：0.成功、-1.失敗 */
		private Integer responseCode;

		/** ページ情報。 */
		private PageInfo pageInfo;

		/** 取得結果。 */
		private List<TopicsDto> data;

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
		 * @author tomo-sato
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