$(document).on("click", ".like-button", function() {
	const button = $(this);
	const postId = button.data("post-id");

	$.ajax({
		url: `/post/like/${postId}`,
		type: "POST",
		success: function(data) {
			// アイコンの更新
			const icon = button.find("i");
			if (data.liked) {
				icon.removeClass("bi-hand-thumbs-up").addClass("bi-hand-thumbs-up-fill");
			} else {
				icon.removeClass("bi-hand-thumbs-up-fill").addClass("bi-hand-thumbs-up");
			}

			// いいね数の更新
			button.find(".like-count").text(data.likeCount);
		},
		error: function() {
			alert("いいねの処理に失敗しました。");
		}
	});
});
