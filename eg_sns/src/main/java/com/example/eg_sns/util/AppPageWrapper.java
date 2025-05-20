package com.example.eg_sns.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

/**
 * ページャークラス.
 *
 * @author tomo-sato
 */
@Getter
public class AppPageWrapper<T> {

	/** ページングで表示するページアイテムの最大表示件数 */
	public static final int MAX_PAGE_ITEM_DISPLAY = 3;

	/** SpringBoot Pageクラス */
	private Page<T> page;
	/** ページングで表示するページアイテム */
	private List<PageItem> pageItem;
	/** 現在のページ番号 */
	private int currentNumber;
	/** ベースURL */
	private String baseUrl;
	/** 1ページ目であるか否か */
	private boolean isFirstPage;
	/** 最後のページであるか否か */
	private boolean isLastPage;
	/** 前の省略表示が必要か否か */
	private boolean isStartPageOmitted;
	/** 後の省略表示が必要か否か */
	private boolean isEndPageOmitted;

	/**
	 * コンストラクタ.
	 *
	 * @param page SpringBoot Pageクラスを指定。
	 */
	public AppPageWrapper(Page<T> page) {
		this(page, "");
	}

	/**
	 * コンストラクタ.
	 *
	 * @param page SpringBoot Pageクラスを指定。
	 * @param url ベースURLを指定。
	 */
	public AppPageWrapper(Page<T> page, String baseUrl) {
		this.page = page;
		this.baseUrl = baseUrl;
		pageItem = new ArrayList<PageItem>();

		currentNumber = page.getNumber() + 1;

		// 表示するページアイテムの開始と、表示個数。
		int start = 1;
		int size = MAX_PAGE_ITEM_DISPLAY;
		// 省略表示の有無。
		this.isFirstPage = false;
		this.isLastPage = false;
		this.isStartPageOmitted = false;
		this.isEndPageOmitted = false;

		if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY + 1) {
			// ページ数が、定数のページ数以下の場合。

			size = page.getTotalPages();

		} else {
			// ページ数が、定数のページ数より大きい場合。
			// 現在ページにより開始と、表示個数の制御。

			if (currentNumber <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) {
				// currentNumberが、1 ～ 中央未満。（※前の方。）

				// 省略表示ON。
				this.isLastPage = true;
				this.isEndPageOmitted = true;

			} else if (currentNumber >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY / 2) {
				// currentNumberが、n ～ 中央以上かつ、最大ページ表示時。（※後の方。）

				start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY + 1;

				// 省略表示ON。
				this.isFirstPage = true;
				this.isStartPageOmitted = true;

			} else if (page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY == 2
					&& currentNumber == (MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) + 1) {
				// 表示数がページ総数より2少ない、且つcurrentNumberが中央値
				start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;

				// 省略表示ON。
				this.isFirstPage = true;
				this.isLastPage = true;

			} else {
				// currentNumberが、n ～ 中央以上。（※真ん中。）

				if (currentNumber == (MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) + 1) {

					start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;

					// 省略表示ON。
					this.isFirstPage = true;
					this.isLastPage = true;
					this.isEndPageOmitted = true;

				} else if (currentNumber == (page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY / 2) - 1) {

					start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;

					// 省略表示ON。
					this.isFirstPage = true;
					this.isLastPage = true;
					this.isStartPageOmitted = true;

				} else {

					start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;

					// 省略表示ON。
					this.isFirstPage = true;
					this.isLastPage = true;
					this.isStartPageOmitted = true;
					this.isEndPageOmitted = true;
				}
			}
		}

		// 表示するページアイテムをリストに追加。
		for (int i = 0; i < size; i++) {
			pageItem.add(new PageItem((start + i), ((start + i) == currentNumber)));
		}
	}

	/**
	 * 前の省略表示が必要か否か。
	 * @return
	 */
	public boolean isStartPageOmitted() {
		return this.isStartPageOmitted;
	}

	/**
	 * 後の省略表示が必要か否か。
	 * @return
	 */
	public boolean isEndPageOmitted() {
		return this.isEndPageOmitted;
	}

	/**
	 * ページに表示するコンテンツ。
	 * @return
	 */
	public List<T> getContent() {
		return page.getContent();
	}

	/**
	 * 1ページに表示する件数。
	 * @return
	 */
	public int getSize() {
		return page.getSize();
	}

	/**
	 * 全ページ数。
	 * @return
	 */
	public int getTotalPages() {
		return page.getTotalPages();
	}

	/**
	 * 検索結果の合計件数。
	 * @return
	 */
	public long getTotalElements() {
		return page.getTotalElements();
	}

	/**
	 * 現在のページの開始件数。
	 * @return
	 */
	public int getCurrentStartCount() {
		int contentCount = page.getContent().size();
		int currentStartCount = (contentCount == 0) ? 0 : 1 + ((currentNumber - 1) * this.getSize());
		return currentStartCount;
	}

	/**
	 * 現在のページの終了件数。
	 * @return
	 */
	public int getCurrentEndCount() {
		// 現在ページで表示する件数。
		int contentCount = page.getContent().size();
		int currentEndCount = (contentCount == 0) ? 0 : getCurrentStartCount() + contentCount - 1;
		return currentEndCount;
	}

	/**
	 * 1ページ目であるか否か。
	 * @return
	 */
	public boolean isFirstPage() {
		return this.isFirstPage;
	}

	/**
	 * 最後のページであるか否か。
	 * @return
	 */
	public boolean isLastPage() {
		return this.isLastPage;
	}

	/**
	 * 前のページがあるか否か。
	 * @return
	 */
	public boolean isHasPreviousPage() {
		return page.hasPrevious();
	}

	/**
	 * 次のページがあるか否か。
	 * @return
	 */
	public boolean isHasNextPage() {
		return page.hasNext();
	}

	/**
	 * ページアイテム.
	 */
	@Getter
	public class PageItem {
		private int number;
		private boolean current;

		public PageItem(int number, boolean current) {
			this.number = number;
			this.current = current;
		}

		public boolean isCurrent() {
			return this.current;
		}
	}

}