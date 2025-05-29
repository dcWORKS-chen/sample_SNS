document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".comment-submit").forEach(button => {
    button.addEventListener("click", async (e) => {
      const postDiv = e.target.closest(".post");
      const postId = postDiv.dataset.postId;
      const textarea = postDiv.querySelector(".comment-input");
      const commentBody = textarea.value.trim();

      if (!commentBody) {
        alert("コメントを入力してください。");
        return;
      }

      const formData = new FormData();
      formData.append("body", commentBody);

      try {
        const response = await fetch(`/post/comment/regist/${postId}`, {
          method: "POST",
          body: formData,
        });

        if (response.ok) {
          const result = await response.json();

          // コメント一覧に追加するHTMLを構築（ここは必要に応じて整形）
          const commentList = postDiv.querySelector(".comment-list");
          const newCommentHtml = `
            <div class="d-flex justify-content-between align-items-center mb-4">
              <div class="d-flex align-items-center">
                <img src="${result.iconUri}" alt="Profile"
                     class="rounded-circle mt-2"
                     style="height: 80px; width: 80px; object-fit: cover;">
                <a class="ms-4 fw-bold" style="margin-top: -16px;" href="/profile/view/${result.usersId}">
                  ${escapeHtml(result.userName)}
                </a>
              </div>
            </div>
            <p class="mb-0" style="margin-left: 82px; margin-top: -46px; padding-left: 20px;">
              ${nl2br(escapeHtml(result.comment.body))}
            </p>
          `;
          commentList.insertAdjacentHTML("beforeend", newCommentHtml);
          textarea.value = "";
        } else {
          alert("コメントの送信に失敗しました。");
        }
      } catch (error) {
        console.error("送信エラー:", error);
        alert("エラーが発生しました。");
      }
    });
  });

  function escapeHtml(text) {
    const div = document.createElement("div");
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
  }

  function nl2br(str) {
    return str.replace(/\n/g, "<br>");
  }
});
