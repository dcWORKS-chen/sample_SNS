// WebAPIを呼び出す関数
function callWebAPI() {
	var button = document.getElementById('btn-more');
	var sinceId = button.getAttribute('data-sinceid');


	// WebAPIのエンドポイントやリクエストを記述
	fetch('http://localhost:8080/api/getTopics?sinceId=' + sinceId)
		.then(response => response.json())
		.then(ret => {
			// WebAPIからのレスポンスを処理
			console.log(ret);

			if (!ret.page_info.has_next) {
				var moreSection = document.getElementById('btn-more-section');
				moreSection.remove();
			} else {
				var button = document.getElementById('btn-more');
				button.setAttribute('data-sinceid', ret.page_info.since_id);
			}

			for (let topic of ret.data) {
				// テンプレート要素を取得
				var templateElement = document.getElementById('template');
				var clonedTemplate = templateElement.cloneNode(true);

				// IDは重複するので外す
				clonedTemplate.removeAttribute('id');
				clonedTemplate.style.display = '';

				// ユーザー名・プロフィールリンク
				var userNameElem = clonedTemplate.querySelector('[data-id="topic-user-name"]');
				userNameElem.textContent = topic.user_name;
				userNameElem.setAttribute('href', '/profile/view/' + topic.users_id);

				// アイコン画像
				var iconImg = clonedTemplate.querySelector('[data-id="icon"]');
				if (topic.icon_uri) {
					iconImg.setAttribute('src', topic.icon_uri);
				} else {
					iconImg.setAttribute('src', '/assets/img/profile-dummy.png');
				}

				// 投稿画像
				let imageContainer = clonedTemplate.querySelector('[data-id="topic-images"]');
				if (topic.image_urls && topic.image_urls.length > 0 && imageContainer) {
					for (let imageUrl of topic.image_urls) {
						const img = document.createElement('img');
						img.src = imageUrl;
						//img.className = 'card-img-top';
						img.alt = 'ポスト画像';
						imageContainer.appendChild(img);
					}
				}

				// 投稿日時
				clonedTemplate.querySelector('[data-id="topic-created"]').textContent = topic.formated_created;

				// タイトル
				clonedTemplate.querySelector('[data-id="topic-title"]').textContent = topic.title;

				// 本文（改行を<br>に）
				clonedTemplate.querySelector('[data-id="topic-body"]').innerHTML = topic.body.replace(/\n/g, '<br>');

				// コメントフォームの action 属性を設定（必要に応じて）
				var commentForm = clonedTemplate.querySelector('.comment-form');
				commentForm.setAttribute('action', '/topic/comment/regist/' + topic.id);

				// コメント表示
				if (topic.comments && topic.comments.length > 0) {
					const commentList = clonedTemplate.querySelector('[data-id="comment-list"]');
					for (let comment of topic.comments) {
						const commentWrapper = document.createElement('div');
						commentWrapper.className = 'd-flex align-items-start mb-3';

						const profileImg = document.createElement('img');
						profileImg.src = comment.iconUri || '/assets/img/profile-dummy.png';
						profileImg.alt = 'Profile';
						profileImg.className = 'rounded-circle mt-2';
						profileImg.style = 'height: 60px; width: 60px; object-fit: cover;';

						const commentContent = document.createElement('div');
						commentContent.className = 'ms-3';

						const userLink = document.createElement('a');
						userLink.className = 'fw-bold';
						userLink.href = '/profile/view/' + comment.usersId;
						userLink.textContent = comment.userName;

						const commentBody = document.createElement('p');
						commentBody.innerHTML = comment.body.replace(/\n/g, '<br>');

						commentContent.appendChild(userLink);
						commentContent.appendChild(commentBody);

						commentWrapper.appendChild(profileImg);
						commentWrapper.appendChild(commentContent);
						commentList.appendChild(commentWrapper);
					}
				}


				document.getElementById('topic-tbody').appendChild(clonedTemplate);

			}

		})
		.catch(error => {
			console.error('Error fetching data:', error);
		});
}