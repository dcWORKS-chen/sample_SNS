// WebAPIを呼び出す関数
function callWebAPI() {
	var button = document.getElementById('btn-more');
	var sinceId = button.getAttribute('data-sinceid');


	// WebAPIのエンドポイントやリクエストを記述
	fetch('http://localhost:8080/api/getPosts?sinceId=' + sinceId)
		.then(response => response.json())
		.then(ret => {
			// WebAPIからのレスポンスを処理


			console.log("フロントが正常に取得しました。受け取ったレスポンス:", ret);
			console.log("ページ情報:", ret.page_info);
			console.log("投稿データ一覧:", ret.data);

			if (!ret.page_info.has_next) {
				var moreSection = document.getElementById('btn-more-section');
				moreSection.remove();
			} else {
				var button = document.getElementById('btn-more');
				button.setAttribute('data-sinceid', ret.page_info.since_id);
			}

			for (let post of ret.data) {
				// テンプレート要素を取得
				var templateElement = document.getElementById('template-post');
				var clonedTemplate = templateElement.cloneNode(true);
				clonedTemplate.style.display = "block";

				// 投稿画像

				// アイコン画像
				clonedTemplate.querySelector('#post-usericon').src = post.user_icon_uri;
				// ユーザー名・プロフィールリンク
				var usernameElem = clonedTemplate.querySelector('#post-username');
				usernameElem.textContent = post.user_name;
				usernameElem.href = '/view/' + post.user_id;
				// 投稿日時
				clonedTemplate.querySelector('#post-created').textContent = post.formated_created;
				// タイトル
				clonedTemplate.querySelector('#post-title').textContent = post.title;
				// 本文（改行を<br>に）
				var bodyHtml = post.body.replace(/\n/g, '<br>');
				clonedTemplate.querySelector('#post-body').innerHTML = bodyHtml;

				// コメント表示

				document.getElementById('post-tbody').appendChild(clonedTemplate);
			}

		})
		.catch(error => {
			console.error('Error fetching data:', error);
		});
}