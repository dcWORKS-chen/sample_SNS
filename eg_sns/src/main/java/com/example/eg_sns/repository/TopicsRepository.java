package com.example.eg_sns.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.eg_sns.entity.Topics;

/**
 * トピック関連リポジトリインターフェース。
 *
 * @author tomo-sato
 */
public interface TopicsRepository extends PagingAndSortingRepository<Topics, Long>, CrudRepository<Topics, Long> {

	/**
	 * トピック検索を行う。
	 * トピックIDを指定し、トピックを検索する。
	 *
	 * @param id トピックID
	 * @return トピック情報を返す。
	 */
	Optional<Topics> findById(Long id);

	/**
	 * トピック検索を行う。
	 * トピックID、ユーザーIDを指定し、トピックを検索する。
	 *
	 * @param id トピックID
	 * @param usersId ユーザーID
	 * @return トピック情報を返す。
	 */
	Optional<Topics> findByIdAndUsersId(Long id, Long usersId);

	/**
	 * トピック一覧を取得する。
	 * トピックIDの降順。
	 * @return トピック一覧を返す。
	 */
	List<Topics> findAllByOrderByIdDesc();
	
	/**
	 * トピック一一覧を取得する。
	 * トピック一IDの昇順。
	 * @return トピック一一覧を返す。
	 */
	Page<Topics> findAll(Pageable pageable);
	
	/**
	 * トピック一一覧を取得する。
	 * トピック一IDの昇順。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return トピック一一覧を返す。
	 */
	Page<Topics> findByIdLessThan(@Param("id") Long sinceId, Pageable pageable);


	/**
	 * トピック一一覧を取得する。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return 指定したIDよりも新しいデータを10件取得する。
	 */
	List<Topics> findFirst10ByIdGreaterThan(@Param("id") Integer sinceId);
	
	/**
	 * 指定なユーザーのトピック一覧を取得する。
	 * トピックIDの降順。
	 * @return 指定なユーザーのトピック一覧を返す。
	 */
	List<Topics> findByUsersIdOrderByCreatedDesc(Long usersId);
	
}
