package com.xingkaichun.information.service;

import com.xingkaichun.common.dto.base.ServiceResult;
import com.xingkaichun.common.dto.base.page.PageInformation;
import com.xingkaichun.information.dto.BbsArticle.BbsArticleDTO;
import com.xingkaichun.information.dto.BbsArticle.BbsArticleDTOForDetailsPage;
import com.xingkaichun.information.dto.BbsArticle.BbsArticleDTOForHomeShowListPage;
import com.xingkaichun.information.dto.BbsArticle.BbsArticleDTOForShowListPage;
import com.xingkaichun.information.dto.BbsArticle.request.*;
import com.xingkaichun.information.dto.BbsArticleComment.BbsArticleCommentDTOForBbsShowList;
import com.xingkaichun.information.dto.BbsArticleComment.BbsArticleCommentDTOForHomeShowList;

import java.util.List;

public interface BbsArticleService {

    int addBbsArticle(AddBbsArticleRequest addBbsArticleRequest);

    List<BbsArticleDTO> queryBbsArticleByRand();

    PageInformation<BbsArticleDTOForHomeShowListPage> queryBbsArticleByUserId(QueryBbsArticleByUserIdRequest request);

    ServiceResult<BbsArticleDTOForDetailsPage> queryBbsArticleDetailByBbsArticleId(String bbsArticleId);

    PageInformation<BbsArticleDTOForShowListPage> queryBbsArticle(QueryBbsArticleRequest request);

    PageInformation<BbsArticleCommentDTOForHomeShowList> queryBbsArticleCommentByUser(QueryBbsArticleCommentByUserRequest request);

    PageInformation<BbsArticleCommentDTOForBbsShowList> queryBbsArticleCommentByBbsArticleId(QueryBbsArticleCommentByBbsArticleIdRequest request);

    PageInformation<BbsArticleCommentDTOForBbsShowList> queryBbsArticleCommentByForBbsArticleCommentId(QueryBbsArticleCommentByForBbsArticleCommentIdRequest request);

    List<BbsArticleCommentDTOForBbsShowList> queryTwoUserBbsArticleComment(QueryTwoUserBbsArticleCommentRequest request);
}
