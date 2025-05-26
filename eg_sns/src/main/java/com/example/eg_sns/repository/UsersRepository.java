package com.example.eg_sns.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.eg_sns.entity.Users;

/**
 * ユーザー関連リポジトリインターフェース。
 *
 * @author chink
 */
public interface UsersRepository extends PagingAndSortingRepository<Users, Long>, CrudRepository<Users, Long> {

	/**
	 * ユーザー検索を行う。
	 * ログインIDを指定し、ユーザーを検索する。
	 *
	 * @param loginId ログインID
	 * @return ユーザー情報を返す。
	 */
	Users findByLoginId(String loginId);

	/**
	 * ユーザー検索を行う。
	 * ログインID、パスワードを指定し、ユーザーを検索する。
	 *
	 * @param loginId ログインID
	 * @param password パスワード
	 * @return ユーザー情報を返す。
	 */
	Users findByLoginIdAndPassword(String loginId, String password);
	


	/**
	 * ユーザー検索を行う。
	 * IDを指定し、それ以外のユーザーを検索する。
	 *
	 * @param Id ユーザーID
	 * @return ユーザー情報を返す。
	 */
	List<Users> findByIdNot(Long id);
}
