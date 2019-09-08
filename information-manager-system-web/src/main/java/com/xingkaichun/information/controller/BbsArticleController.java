package com.xingkaichun.information.controller;

import com.xingkaichun.information.dto.BbsArticle.BbsArticleDTO;
import com.xingkaichun.information.dto.BbsArticle.request.AddBbsArticleRequest;
import com.xingkaichun.information.dto.BbsArticle.request.QueryBbsArticleByRandRequest;
import com.xingkaichun.information.dto.BbsArticle.request.QueryBbsArticleByUserIdRequest;
import com.xingkaichun.information.dto.BbsArticle.request.QueryBbsArticleDetailByBbsArticleIdRequest;
import com.xingkaichun.information.dto.BbsArticle.response.AddBbsArticleResponse;
import com.xingkaichun.information.dto.BbsArticle.response.QueryBbsArticleByRandResponse;
import com.xingkaichun.information.dto.BbsArticle.response.QueryBbsArticleByUserIdResponse;
import com.xingkaichun.information.dto.BbsArticle.response.QueryBbsArticleDetailByBbsArticleIdResponse;
import com.xingkaichun.information.dto.BbsArticleComment.request.AddBbsArticleCommentRequest;
import com.xingkaichun.information.dto.base.FreshServiceResult;
import com.xingkaichun.information.dto.base.ServiceResult;
import com.xingkaichun.information.service.BbsArticleCommentService;
import com.xingkaichun.information.service.BbsArticleService;
import com.xingkaichun.utils.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Api(value="论坛帖子controller",tags={"论坛帖子接口"})
@Controller
@RequestMapping(value = "/BbsArticle")
public class BbsArticleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BbsArticleController.class);

    @Autowired
    private BbsArticleService bbsArticleService;
    @Autowired
    private BbsArticleCommentService bbsArticleCommentService;

    //region 帖子相关
    @ApiOperation(value="发帖", notes="发帖")
    @ResponseBody
    @PostMapping("/AddBbsArticle")
    public ServiceResult<AddBbsArticleResponse> addBbsArticle(HttpServletRequest request, HttpServletResponse response, @RequestBody AddBbsArticleRequest addBbsArticleRequest){

        try {
            if(CommonUtils.isNUllOrEmpty(addBbsArticleRequest.getTitle())){
                return FreshServiceResult.createFailFreshServiceResult("帖子标题不能为空");
            }
            if(CommonUtils.isNUllOrEmpty(addBbsArticleRequest.getContent())){
                return FreshServiceResult.createFailFreshServiceResult("帖子内容不能为空");
            }
            if(!CommonUtils.isNUllOrEmpty(addBbsArticleRequest.getBbsArticleId())){
                return FreshServiceResult.createFailFreshServiceResult("系统自动分配帖子Id,请不要填写");
            }
            addBbsArticleRequest.setBbsArticleId(String.valueOf(UUID.randomUUID()));

            bbsArticleService.addBbsArticle(addBbsArticleRequest);
            BbsArticleDTO bbsArticleDTO = bbsArticleService.queryBbsArticleDetailByBbsArticleId(addBbsArticleRequest.getBbsArticleId());

            AddBbsArticleResponse addBbsArticleResponse = new AddBbsArticleResponse();
            addBbsArticleResponse.setBbsArticleDTO(bbsArticleDTO);

            return FreshServiceResult.createSuccessServiceResult("发帖成功",addBbsArticleResponse);
        } catch (Exception e) {
            String message = "发帖出错";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        } finally {
        }
    }

    @ApiOperation(value="随机获取帖子", notes="随机获取帖子")
    @ResponseBody
    @PostMapping("/QueryBbsArticleByRand")
    public ServiceResult<QueryBbsArticleByRandResponse> queryBbsArticleByRand(@RequestBody QueryBbsArticleByRandRequest request){

        try {
            List<BbsArticleDTO> bbsArticleDTOList = bbsArticleService.queryBbsArticleByRand();

            QueryBbsArticleByRandResponse queryBbsArticleByRandResponse = new QueryBbsArticleByRandResponse();
            queryBbsArticleByRandResponse.setBbsArticleDTOList(bbsArticleDTOList);

            return ServiceResult.createSuccessServiceResult("随机获取帖子成功",queryBbsArticleByRandResponse);
        } catch (Exception e) {
            String message = "随机获取帖子出错";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailServiceResult(message);
        } finally {
        }
    }

    @ApiOperation(value="获取用户帖子", notes="获取用户帖子")
    @ResponseBody
    @PostMapping("/QueryBbsArticleByUserId")
    public ServiceResult<QueryBbsArticleByUserIdResponse> queryBbsArticleByUserId(@RequestBody QueryBbsArticleByUserIdRequest queryBbsArticleByUserIdRequest){

        try {
            List<BbsArticleDTO> bbsArticleDTOList = bbsArticleService.queryBbsArticleByUserId(queryBbsArticleByUserIdRequest.getUserId());

            QueryBbsArticleByUserIdResponse queryBbsArticleByUserIdResponse = new QueryBbsArticleByUserIdResponse();
            queryBbsArticleByUserIdResponse.setBbsArticleDTOList(bbsArticleDTOList);

            return ServiceResult.createSuccessServiceResult("获取用户帖子成功",queryBbsArticleByUserIdResponse);
        } catch (Exception e) {
            String message = "获取用户帖子出错";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailServiceResult(message);
        } finally {
        }
    }

    @ApiOperation(value="获取帖子详情", notes="获取帖子详情")
    @ResponseBody
    @PostMapping("/QueryBbsArticleDetailByBbsArticleId")
    public ServiceResult<QueryBbsArticleDetailByBbsArticleIdResponse> queryBbsArticleDetailByBbsArticleId(@RequestBody QueryBbsArticleDetailByBbsArticleIdRequest queryBbsArticleDetailByBbsArticleIdRequest){

        try {
            BbsArticleDTO bbsArticleDTO = bbsArticleService.queryBbsArticleDetailByBbsArticleId(queryBbsArticleDetailByBbsArticleIdRequest.getBbsArticleId());

            QueryBbsArticleDetailByBbsArticleIdResponse queryBbsArticleDetailByBbsArticleIdResponse = new QueryBbsArticleDetailByBbsArticleIdResponse();
            queryBbsArticleDetailByBbsArticleIdResponse.setBbsArticleDTO(bbsArticleDTO);

            return ServiceResult.createSuccessServiceResult("获取帖子详情成功",queryBbsArticleDetailByBbsArticleIdResponse);
        } catch (Exception e) {
            String message = "获取帖子详情出错";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailServiceResult(message);
        } finally {
        }
    }
    //endregion

    //region 帖子评论相关
    @ApiOperation(value="评论帖子", notes="评论帖子")
    @ResponseBody
    @PostMapping("/AddBbsArticleComment")
    public FreshServiceResult AddBbsArticleComment(HttpServletRequest request, HttpServletResponse response,@RequestBody AddBbsArticleCommentRequest addBbsArticleCommentRequest){

        return bbsArticleCommentService.AddBbsArticleComment(addBbsArticleCommentRequest);
    }
    //endregion
}
