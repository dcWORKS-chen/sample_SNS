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
				let imageContainer = clonedTemplate.querySelector('#post-image');
				imageContainer.innerHTML = '';
				if (post.post_images_uri && post.post_images_uri.length > 0 && imageContainer) {
					for (let imageUrl of post.post_images_uri) {
						const img = document.createElement('img');
						img.src = imageUrl;
						img.className = "card-img-top";
						imageContainer.appendChild(img);
					}
				}

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

				// コメントフォームの action 属性を設定（必要に応じて）
				var commentForm = clonedTemplate.querySelector('.comment-form');
				commentForm.setAttribute('action', '/post/comment/regist/' + post.id);
				// コメント表示
				let commentContainer = clonedTemplate.querySelector('#post-comments');
				commentContainer.innerHTML = '';

				if (post.comments && post.comments.length > 0 && commentContainer) {
					for (let comment of post.comments) {
						const commentWrapper = document.createElement('div');
						commentWrapper.classList.add('mb-3');

						// コメント上部（画像・名前）
						const topRow = document.createElement('div');
						topRow.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-2');

						const userInfo = document.createElement('div');
						userInfo.classList.add('d-flex', 'align-items-center');

						// プロフィール画像
						const userImg = document.createElement('img');
						userImg.src = comment.user_icon_uri;
						userImg.alt = "Profile";
						userImg.classList.add('rounded-circle', 'mt-2');
						userImg.style.height = "80px";
						userImg.style.width = "80px";
						userImg.style.objectFit = "cover";

						// ユーザー名リンク
						const userLink = document.createElement('a');
						userLink.href = `/view/${comment.user_id}`;
						userLink.textContent = comment.user_name;
						userLink.classList.add('ms-4', 'fw-bold');
						userLink.style.marginTop = "-16px";

						// 要素の組み立て（上部）
						userInfo.appendChild(userImg);
						userInfo.appendChild(userLink);
						topRow.appendChild(userInfo);

						// コメント本文
						const commentBody = document.createElement('p');
						commentBody.classList.add('mb-0');
						commentBody.style.marginLeft = "82px";
						commentBody.style.marginTop = "-46px";
						commentBody.style.paddingLeft = "20px";

						// 改行対応（<br>に変換）
						const bodyLines = comment.body.split('\n');
						bodyLines.forEach((line, index) => {
							commentBody.appendChild(document.createTextNode(line));
							if (index !== bodyLines.length - 1) {
								commentBody.appendChild(document.createElement('br'));
							}
						});

						// 全体を組み立て
						commentWrapper.appendChild(topRow);
						commentWrapper.appendChild(commentBody);
						commentContainer.appendChild(commentWrapper);
					}
				}


				document.getElementById('post-tbody').appendChild(clonedTemplate);
			}
		})
		.catch(error => {
			console.error('Error fetching data:', error);
		});
}