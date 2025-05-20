package com.example.eg_sns.controller;

import java.util.List;
import java.util.Optional;

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
import org.thymeleaf.util.StringUtils;

import com.example.eg_sns.core.annotation.LoginCheck;
import com.example.eg_sns.dto.RequestChangePassword;
import com.example.eg_sns.dto.RequestModifyAccount;
import com.example.eg_sns.dto.RequestTopicComment;
import com.example.eg_sns.entity.Friends;
import com.example.eg_sns.entity.Topics;
import com.example.eg_sns.entity.Users;
import com.example.eg_sns.service.FriendsService;
import com.example.eg_sns.service.StorageService;
import com.example.eg_sns.service.TopicsService;
import com.example.eg_sns.service.UsersService;
import com.example.eg_sns.util.StringUtil;

import lombok.extern.log4j.Log4j2;

/**
 * プロフィールコントローラー。
 *
 * @author tomo-sato
 */
@LoginCheck
@Log4j2
@Controller
@RequestMapping("/profile")
public class ProfileController extends AppController {

	/** ファイルアップロード関連サービスクラス。 */
	@Autowired
	private StorageService storageService;

	/** ユーザー関連サービスクラス。 */
	@Autowired
	private UsersService usersService;

	/** ポスト関連サービスクラス。 */
	@Autowired
	private TopicsService topicsService;

	@Autowired
	private FriendsService friendsService;

	/**
	 * [GET]プロフィール画面のアクション。
	 *
	 * @param model 入力フォームのオブジェクト
	 */
	@GetMapping(path = { "", "/" })
	public String index(Model model) {
		log.info("プロフィール画面のアクションが呼ばれました。");

		Users currentUser = getUsers();

		model.addAttribute("requestModifyAccount", getUsers());

		// パスワード変更用フォームオブジェクトも渡す
		model.addAttribute("requestChangePassword", new RequestChangePassword());

		// 自分の投稿のみを取得して渡す
		List<Topics> myTopicsList = topicsService.findUserTopics(currentUser.getId());
		model.addAttribute("myTopicsList", myTopicsList);

		// コメント投稿フォーム用のオブジェクト（バリデーションエラー後も考慮）
		if (!model.containsAttribute("requestTopicComment")) {
			model.addAttribute("requestTopicComment", new RequestTopicComment());
		}

		return "profile/index";
	}

	/**
	 * [GET] 他のユーザーのプロフィールを表示
	 *
	 * @param userId 表示対象のユーザーID
	 * @param model モデル
	 * @return プロフィールページ
	 */
	@GetMapping("/view/{userId}")
	public String viewOtherProfile(@PathVariable Long userId, Model model) {
		log.info("他ユーザープロフィール画面のアクションが呼ばれました。：userId={}", userId);

		// 表示対象ユーザーの情報取得
		Users targetUser = usersService.findById(userId);
		if (targetUser == null) {
			log.warn("指定されたユーザーが存在しません。：userId={}", userId);
			return "redirect:/home"; // またはエラーページへ
		}

		Long loginUserId = getUsersId();

		// 自分のユーザーIDと比較
		boolean isSelf = loginUserId.equals(userId);
		if (isSelf) {
			return "redirect:/profile/";
		}

		// 自分から相手への申請
		Optional<Friends> sentRequestOpt = friendsService.findByUsersIdAndFriendUsersId(loginUserId, userId);

		// 相手から自分への申請
		Optional<Friends> receivedRequestOpt = friendsService.findByUsersIdAndFriendUsersId(userId, loginUserId);
		
		String friendStatus;
		Long requestId = null; // リクエストID用変数

	    if (sentRequestOpt.isPresent()) {
	        Friends request = sentRequestOpt.get();
	        requestId = request.getId(); // リクエストIDを取得
	        if (request.getApprovalStatus() == 0) {
	            friendStatus = "申請済み";
	        } else if (request.getApprovalStatus() == 1) {
	            friendStatus = "フレンド";
	        } else {
	            friendStatus = "却下";
	        }
	    } else if (receivedRequestOpt.isPresent()) {
	        Friends request = receivedRequestOpt.get();
	        requestId = request.getId(); // リクエストIDを取得
	        if (request.getApprovalStatus() == 0) {
	            friendStatus = "承認待ち"; // ここで「承認」「却下」ボタンを出す想定
	        } else if (request.getApprovalStatus() == 1) {
	            friendStatus = "フレンド";
	        } else {
	            friendStatus = "却下";
	        }
	    } else {
	        friendStatus = "友達申請";
	    }

	    // ユーザー情報・投稿リストをビューに渡す
	    model.addAttribute("user", targetUser);
	    model.addAttribute("topicsList", topicsService.findUserTopics(userId));
	    model.addAttribute("loginUser", getUsers());
	    model.addAttribute("friendStatus", friendStatus);
	    model.addAttribute("requestId", requestId); // リクエストIDをビューに渡す

	    return "profile/view"; // templates/profile/view.html
	}

	/**
	 * [POST]アカウント編集アクション。
	 *
	 * @param requestModifyAccount 入力フォームの内容
	 * @param profileFile プロフィール画像ファイル
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/regist")
	public String regist(@Validated @ModelAttribute RequestModifyAccount requestModifyAccount,
			@RequestParam("profileFile") MultipartFile profileFile,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("プロフィール編集処理のアクションが呼ばれました。");

		// バリデーション。
		if (result.hasErrors()) {
			// javascriptのバリデーションを改ざんしてリクエストした場合に通る処理。
			log.warn("バリデーションエラーが発生しました。：requestModifyAccount={}, result={}", requestModifyAccount, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestModifyAccount", requestModifyAccount);

			// 入力画面へリダイレクト。
			return "redirect:/profile";
		}

		// ファイルチェックを行う。
		if (!StorageService.isImageFile(profileFile)) {
			log.warn("指定されたファイルは、画像ファイルではありません。：requestModifyAccount={}", requestModifyAccount);

			// エラーメッセージをセット。
			result.rejectValue("profileFileHidden", StringUtil.BLANK, "画像ファイルを指定してください。");

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestModifyAccount", requestModifyAccount);

			// 入力画面へリダイレクト。
			return "redirect:/profile";
		}

		// ユーザー検索を行う。
		Users users = getUsers();

		// ファイルアップロード処理。
		String fileUri = storageService.store(profileFile);

		// fileUriが取得できない且つ、hiddenの値にファイルが設定されている場合は「設定済みのファイルが変更されていない状態」である為、hiddenの値で更新する。
		if (StringUtils.isEmpty(fileUri) && !StringUtils.isEmpty(requestModifyAccount.getProfileFileHidden())) {
			fileUri = requestModifyAccount.getProfileFileHidden();
			// DBから取得したデータと比較し、改ざんされた値ではないことの確認。
			if (!fileUri.equals(users.getIconUri())) {
				// 改ざんの可能性がある場合はnullをセットしファイルをクリアする。
				fileUri = null;
			}
		}

		users.setName(requestModifyAccount.getName());
		users.setEmailAddress(requestModifyAccount.getEmailAddress());
		users.setProfile(requestModifyAccount.getProfile());
		users.setIconUri(fileUri);
		usersService.save(users);

		return "redirect:/profile";
	}

	/**
	 * [POST]パスワード変更アクション。
	 *
	 * @param form 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/change-password")
	public String changePassword(@Validated @ModelAttribute RequestChangePassword requestChangePassword,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("パスワード変更処理のアクションが呼ばれました。");

		// バリデーション。
		if (result.hasErrors()) {
			// javascriptのバリデーションを改ざんしてリクエストした場合に通る処理。
			log.warn("バリデーションエラーが発生しました。：requestChangePassword={}, result={}", requestChangePassword, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestChangePassword", requestChangePassword);

			// 入力画面へリダイレクト。
			return "redirect:/profile";
		}

		// ユーザー検索を行う。
		Users users = getUsers();

		// 現在のパスワードチェック
		if (!requestChangePassword.getCurrentPassword().equals(users.getPassword())) {
			result.rejectValue("currentPassword", "", "現在のパスワードが正しくありません。");
			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestChangePassword", requestChangePassword);
			return "redirect:/profile";
		}

		// 新旧パスワードの一致確認
		if (!requestChangePassword.getNewPassword().equals(requestChangePassword.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "", "新しいパスワードが一致しません。");
			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestChangePassword", requestChangePassword);
			return "redirect:/profile";
		}

		// パスワード更新
		users.setPassword(requestChangePassword.getNewPassword());
		usersService.save(users);

		return "redirect:/profile";
	}
}
