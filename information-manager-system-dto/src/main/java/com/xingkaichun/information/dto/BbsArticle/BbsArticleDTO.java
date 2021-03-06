package com.xingkaichun.information.dto.BbsArticle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xingkaichun.information.dto.BbsArticleComment.BbsArticleCommentDTO;
import com.xingkaichun.information.dto.user.UserInfo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class BbsArticleDTO {
    @JsonProperty("BbsArticleId")
    private String bbsArticleId;
    @JsonProperty("UserId")
    private String userId;
    @JsonProperty("UserInfo")
    private UserInfo userInfo;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Content")
    private String content;
    @JsonProperty("CreateTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonProperty("LastEditTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastEditTime;
    @JsonProperty("IsSoftDelete")
    private boolean isSoftDelete;

    @JsonProperty("BbsArticleCommentDTOList")
    private List<BbsArticleCommentDTO> bbsArticleCommentDTOList;
}
