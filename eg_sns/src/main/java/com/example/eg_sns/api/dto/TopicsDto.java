package com.example.eg_sns.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TopicsDto {
	/** ID */
	private Long id;

	/** ID */
	private Long usersId;
	
	private String iconUri;

	/** 名前 */
	private String userName;

	private String title;
	private String body;

	/** 作成日時 */
	private Date created;

	/** 更新日時 */
	private Date updated;

	/** 作成日時（yyyy-MM-dd HH:mm） */
	private String formatedCreated;
	
	private List<String> imageUrls;
	
	private List<CommentDto> comments;


	public String getFormatedCreated() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.formatedCreated = sdf.format(this.created);
		return this.formatedCreated;
	}
	
	@Data
	public static class CommentDto {
		private Long id;
		private Long usersId;
		private String userName;
		private String iconUri;
		private String body;
	}


}
