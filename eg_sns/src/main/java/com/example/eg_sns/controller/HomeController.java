package com.example.eg_sns.controller;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.eg_sns.core.annotation.LoginCheck;
import com.example.eg_sns.dto.RequestTopicComment;
import com.example.eg_sns.entity.Topics;
import com.example.eg_sns.service.TopicsService;
import com.example.eg_sns.util.AppPageWrapper;

import lombok.extern.log4j.Log4j2;

/**
 * ホームコントローラー。
 *
 * @author tomo-sato
 */
@LoginCheck
@Log4j2
@Controller
@RequestMapping("/home")
public class HomeController {

	/** トピック関連サービスクラス。 */
	@Autowired
	private TopicsService topicsService;

	/**
	 * [GET]ホーム画面のアクション。
	 *
	 * @param model 画面にデータを送るためのオブジェクト
	 */
	@GetMapping(path = {"", "/"})
	public String index(@RequestParam(defaultValue = "0") String page,
						@ModelAttribute("isSuccess") String isSuccess,
						Model model) {
		log.info("ホーム画面のアクションが呼ばれました。");
		
		int ipage = NumberUtils.toInt(page, 0);

		// トピック取得する。
		Page<Topics> pageTopics = topicsService.findAllTopics(ipage);
		AppPageWrapper<Topics> pager = new AppPageWrapper<Topics>(pageTopics);
		
		List<Topics> topicsList = pager.getContent();
		Long sinceId = 0L;
		for (Topics topics : topicsList) {
			sinceId = (sinceId < topics.getId()) ? topics.getId() : sinceId;
		}
		
		model.addAttribute("topicsList", topicsList);
		model.addAttribute("sinceId", sinceId);
		model.addAttribute("pager", pager);
		model.addAttribute("isSuccess", BooleanUtils.toBoolean(isSuccess));
		
		 // コメント投稿フォーム用のオブジェクト（バリデーションエラー後も考慮）
	    if (!model.containsAttribute("requestTopicComment")) {
	        model.addAttribute("requestTopicComment", new RequestTopicComment());
	    }

		return "home/index";
	}
	
	@GetMapping("/test/shoulder")
	public String testShoulder(Model model) {
		Page<Topics> topicsList = topicsService.findAllTopics(0);
		AppPageWrapper<Topics> pager = new AppPageWrapper<Topics>(topicsList);
		model.addAttribute("pager", pager);
		return "common/shoulder_fragment";
	}
}
