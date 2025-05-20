package com.example.eg_sns.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.eg_sns.core.AppNotFoundException;
import com.example.eg_sns.core.annotation.LoginCheck;
import com.example.eg_sns.dto.RequestTopic;
import com.example.eg_sns.dto.RequestTopicComment;
import com.example.eg_sns.entity.Topics;
import com.example.eg_sns.service.CommentsService;
import com.example.eg_sns.service.LikesService;
import com.example.eg_sns.service.StorageService;
import com.example.eg_sns.service.TopicsService;

import lombok.extern.log4j.Log4j2;

/**
 * トピックコントローラー。
 *
 * @author tomo-sato
 */
@LoginCheck
@Log4j2
@Controller
@RequestMapping("/topic")
public class TopicController extends AppController {

	/** トピック関連サービスクラス。 */
	@Autowired
	private TopicsService topicsService;

	/** コメント関連サービスクラス。 */
	@Autowired
	private CommentsService commentsService;
	
	/** いいね関連サービスクラス。 */
	@Autowired
	private LikesService likesService;
	
	/** ファイルアップロード関連サービスクラス。 */
	@Autowired
	private StorageService storageService;

	/**
	 * [GET]トピック作成入力フォームのアクション。
	 *
	 * @param model 画面にデータを送るためのオブジェクト
	 */
	@GetMapping("/input")
	public String input(Model model) {

		log.info("トピック作成入力画面のアクションが呼ばれました。");

		if (!model.containsAttribute("requestTopic")) {
			model.addAttribute("requestTopic", new RequestTopic());
		}
		return "topic/input";
	}

	/**
	 * [POST]トピック作成アクション。
	 *
	 * @param requestTopic 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/regist")
	public String regist(@Validated @ModelAttribute RequestTopic requestTopic,
			@RequestParam("imageFiles") MultipartFile[] imageFiles,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("トピック作成処理のアクションが呼ばれました。：requestTopic={}", requestTopic);

		// バリデーション。
		if (result.hasErrors()) {
			log.warn("バリデーションエラーが発生しました。：requestTopic={}, result={}", requestTopic, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestTopic", requestTopic);

			// 入力画面へリダイレクト。
			return "redirect:/topic/input";
		}
		
	    // 画像ファイルのチェックと保存
	    List<String> imageUrls = new ArrayList<>();
	    if (imageFiles != null) {
	        for (MultipartFile imageFile : imageFiles) {
	            if (!imageFile.isEmpty()) {
	                if (!StorageService.isImageFile(imageFile)) {
	                    log.warn("指定されたファイルは画像ではありません。：filename={}", imageFile.getOriginalFilename());

	                    result.rejectValue("imageUrls", "", "画像ファイルを指定してください。");
	                    redirectAttributes.addFlashAttribute("validationErrors", result);
	                    redirectAttributes.addFlashAttribute("requestTopic", requestTopic);
	                    return "redirect:/topic/input";
	                }

	                String imageUrl = storageService.store(imageFile);
	                if (imageUrl != null) {
	                    imageUrls.add(imageUrl);
	                }
	            }
	        }
	    }

	    requestTopic.setImageUrls(imageUrls);

		// ログインユーザー情報取得
		Long usersId = getUsersId();

		// データ登録処理
		topicsService.save(requestTopic, usersId);

		redirectAttributes.addFlashAttribute("isSuccess", "true");

		return "redirect:/home";
	}

	/**
	 * [POST]トピック詳細アクション。
	 *
	 * @param topicsId トピックID
	 * @param isSuccess コメント投稿完了からの正常の遷移であるか、否か。（true.正常）
	 * @param model 画面にデータを送るためのオブジェクト
	 */
	@GetMapping("/detail/{topicsId}")
	public String detail(@PathVariable Long topicsId,
			@ModelAttribute("isSuccess") String isSuccess,
			Model model) {

		log.info("トピック詳細画面のアクションが呼ばれました。");

		if (!model.containsAttribute("requestTopicComment")) {
			model.addAttribute("requestTopicComment", new RequestTopicComment());
		}

		// トピック情報を取得する。
		Topics topics = topicsService.findTopics(topicsId);

		if (topics == null) {
			// トピックが取得できない場合は、Not Found。
			throw new AppNotFoundException();
		}

		model.addAttribute("topics", topics);
		model.addAttribute("isSuccess", BooleanUtils.toBoolean(isSuccess));

		return "redirect:/home";
	}

	/**
	 * [GET]トピック削除アクション。
	 *
	 * @param topicsId トピックID
	 */
	@GetMapping("/delete/{topicsId}")
	public String delete(@PathVariable Long topicsId) {

		log.info("トピック削除処理のアクションが呼ばれました。：topicsId={}", topicsId);

		// ログインユーザー情報取得（※自分が投稿したコメント以外を削除しない為の制御。）
		Long usersId = getUsersId();

		// コメント削除処理
		topicsService.delete(topicsId, usersId);

		// ホーム画面へリダイレクト。
		return "redirect:/home";
	}

	/**
	 * [POST]コメント投稿アクション。
	 *
	 * @param topicsId トピックID
	 * @param requestTopicComment 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/comment/regist/{topicsId}")
	public String commentRegist(@PathVariable Long topicsId,
			@Validated @ModelAttribute RequestTopicComment requestTopicComment,
			BindingResult result,
			@RequestParam(name = "from", required = false, defaultValue = "home") String from,
			RedirectAttributes redirectAttributes) {

		log.info("コメント投稿処理のアクションが呼ばれました。：topicsId={}, requestTopicComment={}, from={}", topicsId, requestTopicComment, from);

		// バリデーション。
		if (result.hasErrors()) {
			log.warn("バリデーションエラーが発生しました。：topicsId={}, requestTopicComment={}, result={}", topicsId, requestTopicComment, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestTopicComment", requestTopicComment);

			// 入力画面へリダイレクト。
			return "redirect:/" + from;
		}

		// ログインユーザー情報取得
		Long usersId = getUsersId();

		// コメント登録処理
		commentsService.save(requestTopicComment, usersId, topicsId);

		return "redirect:/" + from;
	}

	/**
	 * [GET]コメント削除アクション。
	 *
	 * @param topicsId トピックID
	 * @param commentsId コメントID
	 */
	@GetMapping("/comment/delete/{topicsId}/{commentsId}")
	public String commentDelete(@PathVariable Long topicsId, @PathVariable Long commentsId) {

		log.info("コメント削除処理のアクションが呼ばれました。：topicsId={}, commentsId={}", topicsId, commentsId);

		// ログインユーザー情報取得（※自分が投稿したコメント以外を削除しない為の制御。）
		Long usersId = getUsersId();

		// コメント削除処理
		commentsService.delete(commentsId, usersId, topicsId);

		// 入力画面へリダイレクト。
		return "redirect:/home";
	}
	
	/**
	 * [POST] いいねのトグル（追加または削除）アクション。
	 *
	 * @param topicsId トピックID
	 * @param redirectAttributes リダイレクト用の属性
	 */
	@PostMapping("/like/{topicsId}")
	public String toggleLike(@PathVariable Long topicsId,
	                         @RequestParam(name = "from", required = false, defaultValue = "home") String from,
	                         RedirectAttributes redirectAttributes) {

	    log.info("いいねトグル処理が呼ばれました。：topicsId={}, from={}", topicsId, from);

	    // ログインユーザーID取得
	    Long usersId = getUsersId();

	    // トグル実行（追加 or 削除）
	    boolean isLiked = likesService.toggleLike(usersId, topicsId);

	    log.info("いいねトグル完了：isLiked={}, topicsId={}, usersId={}", isLiked, topicsId, usersId);

	    redirectAttributes.addFlashAttribute("likeStatus", isLiked);
	    return "redirect:/" + from;
	}
}
