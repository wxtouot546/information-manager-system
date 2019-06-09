package com.xingkaichun.information.controller;

import com.xingkaichun.information.dto.category.CategoryDTO;
import com.xingkaichun.information.dto.article.ArticleDTO;
import com.xingkaichun.information.dto.article.request.AddArticleRequest;
import com.xingkaichun.information.dto.article.request.DeleteArticleRequest;
import com.xingkaichun.information.dto.article.request.QueryArticleRequest;
import com.xingkaichun.information.dto.article.request.UpdateArticleRequest;
import com.xingkaichun.information.dto.article.response.QueryArticleResponse;
import com.xingkaichun.information.dto.base.FreshServiceResult;
import com.xingkaichun.information.dto.base.PageInformation;
import com.xingkaichun.information.dto.base.ServiceResult;
import com.xingkaichun.information.dto.category.request.QueryCategoryRequest;
import com.xingkaichun.information.service.ArticleService;
import com.xingkaichun.information.service.CategoryService;
import com.xingkaichun.information.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(value = "/Article")
public class ArticleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @ResponseBody
    @PostMapping("/AddArticle")
    public FreshServiceResult addArticle(@RequestBody AddArticleRequest addArticleRequest){
        try{
            if(CommonUtils.isNUllOrEmpty(addArticleRequest.getTitle())){
                return FreshServiceResult.createFailFreshServiceResult("文章标题不能为空");
            }
            if(!CommonUtils.isNUllOrEmpty(addArticleRequest.getArticleId())){
                return FreshServiceResult.createFailFreshServiceResult("系统自动分配文章ID，不允许赋值");
            }
            if(CommonUtils.isNUllOrEmpty(addArticleRequest.getCategoryId())){
                return FreshServiceResult.createFailFreshServiceResult("文章类别不能为空");
            }else{
                QueryCategoryRequest queryCategoryRequest = new QueryCategoryRequest();
                queryCategoryRequest.setCategoryId(addArticleRequest.getCategoryId());
                List<CategoryDTO> categoryDTOList = categoryService.queryCategoryReturnList(queryCategoryRequest);
                if(CommonUtils.isNUllOrEmpty(categoryDTOList)){
                    return FreshServiceResult.createFailFreshServiceResult("文章类别不存在");
                }
                addArticleRequest.setArticleId(String.valueOf(UUID.randomUUID()));
            }
            addArticleRequest.setLastEditTime(new Date(System.currentTimeMillis()));
            articleService.addArticle(addArticleRequest);
            return FreshServiceResult.createSuccessFreshServiceResult("新增文章成功");
        } catch (Exception e){
            String message = "新增文章失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ResponseBody
    @PostMapping("/DeleteArticle")
    public FreshServiceResult deleteArticle(@RequestBody DeleteArticleRequest deleteArticleRequest){
        String articleId = deleteArticleRequest.getArticleId();
        if(CommonUtils.isNUllOrEmpty(articleId)){
            return FreshServiceResult.createFailFreshServiceResult("ArticleId不能为空");
        }
        try{
            articleService.deleteArticle(deleteArticleRequest);
            return FreshServiceResult.createSuccessFreshServiceResult("删除文章成功");
        } catch (Exception e){
            String message = "删除文章失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ResponseBody
    @PostMapping("/QueryArticle")
    public ServiceResult<QueryArticleResponse> queryArticle(@RequestBody QueryArticleRequest queryArticleRequest){
        try{
            QueryArticleResponse queryArticleResponse = new QueryArticleResponse();
            PageInformation<ArticleDTO> articleDTOPageInformation = articleService.queryArticle(queryArticleRequest);
            queryArticleResponse.setArticleDTOPageInformation(articleDTOPageInformation);
            return ServiceResult.createSuccessServiceResult(queryArticleResponse);
        } catch (Exception e){
            String message = "查询文章失败";
            LOGGER.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    @ResponseBody
    @PostMapping("/UpdateArticle")
    public FreshServiceResult updateArticle(@RequestBody UpdateArticleRequest updateArticleRequest){
        String articleId = updateArticleRequest.getArticleId();
        if(CommonUtils.isNUllOrEmpty(articleId)){
            return FreshServiceResult.createFailFreshServiceResult("ArticleId不能为空");
        }
        try{
            if(CommonUtils.isNUllOrEmpty(updateArticleRequest.getCategoryId())){
                return FreshServiceResult.createFailFreshServiceResult("文章类别不能为空");
            }else{
                QueryCategoryRequest queryCategoryRequest = new QueryCategoryRequest();
                queryCategoryRequest.setCategoryId(updateArticleRequest.getCategoryId());
                List<CategoryDTO> categoryDTOList = categoryService.queryCategoryReturnList(queryCategoryRequest);
                if(CommonUtils.isNUllOrEmpty(categoryDTOList)){
                    return FreshServiceResult.createFailFreshServiceResult("文章类别不存在");
                }
            }

            articleService.updateArticle(updateArticleRequest);
            return FreshServiceResult.createSuccessFreshServiceResult("更新文章成功");
        } catch (Exception e){
            String message = "更新文章失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }
}