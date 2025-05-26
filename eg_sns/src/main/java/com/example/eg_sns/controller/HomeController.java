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
import com.example.eg_sns.dto.RequestPostComment;
import com.example.eg_sns.entity.Posts;
import com.example.eg_sns.service.PostsService;
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
	/** サービスクラス。 */
	@Autowired
	private PostsService postsService;

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
		Page<Posts> pagePosts = postsService.findAllPosts(ipage);
		AppPageWrapper<Posts> pager = new AppPageWrapper<Posts>(pagePosts);
		
		List<Posts> postsList = pager.getContent();
		Long sinceId = 0L;
		for (Posts posts : postsList) {
			sinceId = (sinceId < posts.getId()) ? posts.getId() : sinceId;
		}
		
		model.addAttribute("postsList", postsList);
		model.addAttribute("sinceId", sinceId);
		model.addAttribute("pager", pager);
		model.addAttribute("isSuccess", BooleanUtils.toBoolean(isSuccess));
		
		 // コメント投稿フォーム用のオブジェクト（バリデーションエラー後も考慮）
	    if (!model.containsAttribute("requestPostComment")) {
	        model.addAttribute("requestPostComment", new RequestPostComment());
	    }

		return "home/index";
	}
	
	@GetMapping("/test/shoulder")
	public String testShoulder(Model model) {
		Page<Posts> postsList = postsService.findAllPosts(0);
		AppPageWrapper<Posts> pager = new AppPageWrapper<Posts>(postsList);
		model.addAttribute("pager", pager);
		return "common/shoulder_fragment";
	}
}
