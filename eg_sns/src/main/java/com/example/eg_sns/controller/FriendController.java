package com.example.eg_sns.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.eg_sns.dto.RequestFriendUser;
import com.example.eg_sns.entity.Friends;
import com.example.eg_sns.entity.Users;
import com.example.eg_sns.service.FriendsService;
import com.example.eg_sns.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

/**
 * フレンド関連コントローラー。
 * 
 * @author chink
 */
@Log4j2
@Controller
@RequestMapping("/friend")
public class FriendController {
	/** フレンド関連サービスクラス。 */
	@Autowired
	private FriendsService friendsService;

	/** ユーザー関連サービスクラス。 */
	@Autowired
	private UsersService usersService;

	/**
	 * [GET]フレンドリスト表示。
	 *
	 * @param model 画面にデータを送るためのオブジェクト
	 */
	@GetMapping("/list/{usersId}")
	public String list(@PathVariable Long usersId, Model model) {
		log.info("フレンドリスト表示のアクションが呼ばれました。");

		// 自分以外の全ユーザーを取得
		List<Users> allOtherUsers = usersService.findAllExceptSelf(usersId);
		List<RequestFriendUser> requestFriendUsers = new ArrayList<>();

		for (Users user : allOtherUsers) {
			RequestFriendUser dto = new RequestFriendUser();
			dto.setId(user.getId());
			dto.setName(user.getName());
			dto.setProfile(user.getProfile());
			dto.setProfileImageUrl(user.getIconUri());

			// フレンド関係を確認
			Optional<Friends> myRequestOpt = friendsService.findByUsersIdAndFriendUsersId(usersId, user.getId());
			Optional<Friends> theirRequestOpt = friendsService.findByUsersIdAndFriendUsersId(user.getId(), usersId);

			if (myRequestOpt.isPresent()) {
				Friends myRequest = myRequestOpt.get();
				dto.setRequestId(myRequest.getId());
				dto.setApprovalStatus(getApprovalStatusForMyRequest(myRequest.getApprovalStatus()));
			} else if (theirRequestOpt.isPresent()) {
				Friends theirRequest = theirRequestOpt.get();
				dto.setRequestId(theirRequest.getId());
				dto.setApprovalStatus(getApprovalStatusForTheirRequest(theirRequest.getApprovalStatus()));
			} else {
				dto.setApprovalStatus(3); // 申請可能
			}

			requestFriendUsers.add(dto);
		}

		model.addAttribute("userList", requestFriendUsers);
		model.addAttribute("currentUserId", usersId);
		return "friends/list"; // フレンドリストのビューに移動
	}

	/**
	 * [POST]フレンドリクエスト送信アクション。
	 *
	 * @param usersId 自分のユーザーID
	 * @param friendUsersId 申請先のユーザーID
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/sendRequest")
	public String sendFriendRequest(@RequestParam Long usersId, @RequestParam Long friendUsersId,
			RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		log.info("フレンドリクエスト送信のアクションが呼ばれました：from={}, to={}", usersId, friendUsersId);

		// フレンドリクエストを送信
		boolean isSuccess = friendsService.sendFriendRequest(usersId, friendUsersId);

		// 成功した場合、リダイレクトで元のページに戻る
		if (isSuccess) {
			redirectAttributes.addFlashAttribute("isSuccess", "true");
		} else {
			redirectAttributes.addFlashAttribute("isSuccess", "false");
		}

		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty()) {
			return "redirect:" + referer;
		}

		return "redirect:/friend/list/" + usersId;
	}

	/**
	 * [POST]フレンドリクエスト承認アクション。
	 *
	 * @param requestId フレンドリクエストのID
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/approve/{requestId}")
	public String approveFriendRequest(@PathVariable Long requestId, RedirectAttributes redirectAttributes,
			@RequestParam Long usersId,
			HttpServletRequest request) {
		log.info("フレンドリクエスト承認のアクションが呼ばれました。：requestId={}", requestId);

		// フレンドリクエストを承認
		friendsService.approveFriendRequest(requestId);

		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty()) {
			return "redirect:" + referer;
		}

		redirectAttributes.addFlashAttribute("isSuccess", "true");
		return "redirect:/friend/list/" + usersId;
	}

	/**
	 * [POST]フレンドリクエスト拒否アクション。
	 *
	 * @param requestId フレンドリクエストのID
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/reject/{requestId}")
	public String rejectFriendRequest(@PathVariable Long requestId, RedirectAttributes redirectAttributes,
			@RequestParam Long usersId,
			HttpServletRequest request) {
		log.info("フレンドリクエスト拒否のアクションが呼ばれました。：requestId={}", requestId);

		// フレンドリクエストを拒否または削除
		friendsService.rejectFriendRequest(requestId);

		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty()) {
			return "redirect:" + referer;
		}

		redirectAttributes.addFlashAttribute("isSuccess", "true");
		return "redirect:/friend/list/" + usersId;
	}

	/**
	 * [POST]フレンド解除アクション。
	 *
	 * @param usersId 自分のユーザーID
	 * @param friendUsersId フレンドを解除する相手のユーザーID
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 */
	@PostMapping("/remove")
	public String removeFriend(@RequestParam Long usersId, @RequestParam Long friendUsersId,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {
		log.info("フレンド解除のアクションが呼ばれました：from={}, to={}", usersId, friendUsersId);

		friendsService.removeFriendship(usersId, friendUsersId);

		redirectAttributes.addFlashAttribute("isSuccess", "true");

		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty()) {
			return "redirect:" + referer;
		}

		return "redirect:/friend/list/" + usersId;
	}

	/**
	 * 自分のフレンド申請状態チェック
	 *
	 * @param status IDからフレンド申請状態
	 * @return フレンド申請の状態
	 */
	private int getApprovalStatusForMyRequest(int status) {
		return switch (status) {
		case 0 -> 0; // 自分が申請 → 申請中
		case 1 -> 1; // 承認済み
		default -> 4; // 却下されたが履歴がある
		};
	}

	/**
	 * 相手からのフレンド申請状態チェック
	 *
	 * @param status IDからフレンド申請状態
	 * @return フレンド申請の状態
	 */
	private int getApprovalStatusForTheirRequest(int status) {
		return switch (status) {
		case 0 -> 2; // 相手から申請あり → 承認/却下可能
		case 1 -> 1; // 承認済み
		default -> 4; // 却下されたが履歴がある
		};
	}
}
