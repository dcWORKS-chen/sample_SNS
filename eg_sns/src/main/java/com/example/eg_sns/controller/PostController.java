package com.example.eg_sns.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.example.eg_sns.core.annotation.LoginCheck;
import com.example.eg_sns.dto.RequestPost;
import com.example.eg_sns.dto.RequestPostComment;
import com.example.eg_sns.entity.Users;
import com.example.eg_sns.service.PostCommentsService;
import com.example.eg_sns.service.PostLikesService;
import com.example.eg_sns.service.PostsService;
import com.example.eg_sns.service.StorageService;
import com.example.eg_sns.service.UsersService;

import lombok.extern.log4j.Log4j2;

/**
 * 投稿コントローラー。
 *
 * @author chink
 */
@LoginCheck
@Log4j2
@Controller
@RequestMapping("/post")
public class PostController extends AppController{
	/** サービスクラス。 */
	@Autowired
	private PostsService postsService;

	@Autowired
	private PostCommentsService postCommentsService;
	
	@Autowired
	private PostLikesService postLikesService;
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private UsersService usersService;
	
	/**
	 * [GET]投稿作成入力フォームのアクション。
	 *
	 * @param model 画面にデータを送るためのオブジェクト
	 */
	@GetMapping("/input")
	public String input(Model model) {

		log.info("投稿作成入力画面のアクションが呼ばれました。");

		if (!model.containsAttribute("requestPost")) {
			model.addAttribute("requestPost", new RequestPost());
		}
		return "post/input";
	}
	
	/**
	 * [POST]投稿作成アクション。
	 *
	 * @param requestPost 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/regist")
	public String regist(@Validated @ModelAttribute RequestPost requestPost,
			@RequestParam("imageFiles") MultipartFile[] imageFiles,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("投稿作成処理のアクションが呼ばれました。：requestPost={}", requestPost);

		// バリデーション。
		if (result.hasErrors()) {
			log.warn("バリデーションエラーが発生しました。：requestPost={}, result={}", requestPost, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestPost", requestPost);

			// 入力画面へリダイレクト。
			return "redirect:/post/input";
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
	                    redirectAttributes.addFlashAttribute("requestPost", requestPost);
	                    return "redirect:/post/input";
	                }

	                String imageUrl = storageService.store(imageFile);
	                if (imageUrl != null) {
	                    imageUrls.add(imageUrl);
	                }
	            }
	        }
	    }

	    requestPost.setImageUrls(imageUrls);

		// ログインユーザー情報取得
		Long usersId = getUsersId();

		// データ登録処理
		postsService.save(requestPost, usersId);

		redirectAttributes.addFlashAttribute("isSuccess", "true");

		return "redirect:/home";
	}
	
	/**
	 * [GET]投稿削除アクション。
	 *
	 * @param postsId 投稿ID
	 */
	@GetMapping("/delete/{postsId}")
	public String delete(@PathVariable Long postsId) {

		log.info("トピック削除処理のアクションが呼ばれました。：postsId={}", postsId);

		// ログインユーザー情報取得（※自分が投稿したコメント以外を削除しない為の制御。）
		Long usersId = getUsersId();

		// コメント削除処理
		postsService.delete(postsId, usersId);

		// ホーム画面へリダイレクト。
		return "redirect:/home";
	}

	/**
	 * [POST]コメント投稿アクション。
	 *
	 * @param postsId 投稿ID
	 * @param requestTopicComment 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/comment/regist/{postsId}")
	public ResponseEntity<Map<String, Object>> commentRegist(@PathVariable Long postsId,
			@Validated @ModelAttribute RequestPostComment requestPostComment) {

		log.info("コメント投稿処理のアクションが呼ばれました。：postsId={}, requestTopicComment={}", postsId, requestPostComment);

		// ログインユーザー情報取得
		Long usersId = getUsersId();
		Users user = usersService.findById(usersId);

		// コメント登録処理
		postCommentsService.save(requestPostComment, usersId, postsId);

		Map<String, Object> response = new HashMap<>();
	    response.put("usersId", usersId);
	    response.put("userName", user.getName());
	    response.put("iconUri", user.getIconUri());
	    response.put("comment", requestPostComment);

		log.info("コメント投稿処理完了。：postsId={}, requestTopicComment={}", postsId, requestPostComment);
		
        return ResponseEntity.ok(response);
	}

	/**
	 * [GET]投稿削除アクション。
	 *
	 * @param postsId 投稿ID
	 * @param commentsId コメントID
	 */
	@GetMapping("/comment/delete/{postsId}/{commentsId}")
	public String commentDelete(@PathVariable Long postsId, @PathVariable Long commentsId) {

		log.info("コメント削除処理のアクションが呼ばれました。：postsId={}, commentsId={}", postsId, commentsId);

		// ログインユーザー情報取得（※自分が投稿したコメント以外を削除しない為の制御。）
		Long usersId = getUsersId();

		// コメント削除処理
		postCommentsService.delete(commentsId, usersId, postsId);

		// 入力画面へリダイレクト。
		return "redirect:/home";
	}
	
	/**
	 * [POST] 非同期でいいねのトグル（追加または削除）アクション。
	 *
	 * @param postsId 投稿ID
	 * @param redirectAttributes リダイレクト用の属性
	 */
	@PostMapping("/like/{postsId}")
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long postsId) {

		log.info("非同期いいねトグル処理：postsId={}", postsId);

	    // ログインユーザーID取得
	    Long usersId = getUsersId();

	    // トグル実行（追加 or 削除）
	    boolean isLiked = postLikesService.toggleLike(usersId, postsId);
	    int likeCount = postLikesService.countLikes(postsId);
	    
	    Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likeCount", likeCount);

	    log.info("いいねトグル完了：isLiked={}, postsId={}, usersId={}", isLiked, postsId, usersId);
        return ResponseEntity.ok(response);


	}

}
