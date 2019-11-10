package com.xingkaichun.information.controller;

import com.xingkaichun.common.dto.base.FreshServiceResult;
import com.xingkaichun.common.dto.base.ServiceResult;
import com.xingkaichun.information.dto.book.BookDTO;
import com.xingkaichun.information.dto.book.request.*;
import com.xingkaichun.information.dto.book.response.AddBookResponse;
import com.xingkaichun.information.dto.book.response.QueryBookDetailsByBookIdResponse;
import com.xingkaichun.information.dto.book.response.QueryBookListResponse;
import com.xingkaichun.information.dto.bookChapter.BookChapterDTO;
import com.xingkaichun.information.dto.bookChapter.request.*;
import com.xingkaichun.information.dto.bookChapter.response.AddBookChapterResponse;
import com.xingkaichun.information.dto.bookChapter.response.QueryBookChapterListByBookIdResponse;
import com.xingkaichun.information.dto.bookSection.BookSectionDTO;
import com.xingkaichun.information.dto.bookSection.request.AddBookSectionRequest;
import com.xingkaichun.information.dto.bookSection.request.PhysicsDeleteBookSectionByBookSectionIdRequest;
import com.xingkaichun.information.dto.bookSection.request.QueryBookSectionListBybookChapterIdRequest;
import com.xingkaichun.information.dto.bookSection.request.UpdateBookSectionRequest;
import com.xingkaichun.information.dto.bookSection.response.AddBookSectionResponse;
import com.xingkaichun.information.dto.bookSection.response.QueryBookSectionListBybookChapterIdResponse;
import com.xingkaichun.information.model.UserDomain;
import com.xingkaichun.information.service.BookChapterService;
import com.xingkaichun.information.service.BookSectionService;
import com.xingkaichun.information.service.BookService;
import com.xingkaichun.information.service.ComplexBookService;
import com.xingkaichun.utils.CommonUtilsSession;
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
import java.util.List;

@Api(value="书籍controller",tags={"书籍操作接口"})
@Controller
@RequestMapping(value = "/Book")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private BookChapterService bookChapterService;
    @Autowired
    private BookSectionService bookSectionService;
    @Autowired
    private ComplexBookService complexBookServiceImpl;


    @ApiOperation(value="新增书籍", notes="新增书籍")
    @ResponseBody
    @PostMapping("/Addbook")
    public ServiceResult<AddBookResponse> addbook(@RequestBody AddBookRequest request, HttpServletRequest httpServletRequest){
        try{
            ServiceResult<BookDTO> serviceResult = bookService.addBook(httpServletRequest,request);
            if(!ServiceResult.isSuccess(serviceResult)){
                return FreshServiceResult.createFailFreshServiceResult(serviceResult.getMessage());
            }
            AddBookResponse response = new AddBookResponse();
            response.setBookDTO(serviceResult.getResult());
            return ServiceResult.createSuccessServiceResult("新增书籍成功",response);
        } catch (Exception e){
            String message = "新增书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="更新书籍", notes="更新书籍")
    @ResponseBody
    @PostMapping("/UpdateBook")
    public FreshServiceResult updateBook(@RequestBody UpdateBookRequest request, HttpServletRequest httpServletRequest){
        try{
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,request.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult freshServiceResult = bookService.updateBook(request);
            complexBookServiceImpl.createHtmlPage(request.getBookId());
            return freshServiceResult;
        } catch (Exception e){
            String message = "更新书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="查询书籍详情", notes="查询书籍详情")
    @ResponseBody
    @PostMapping("/QueryBookDetailsByBookId")
    public ServiceResult<QueryBookDetailsByBookIdResponse> queryBookDetailsByBookIdRequest(@RequestBody QueryBookDetailsByBookIdRequest request, HttpServletRequest httpServletRequest){
        try{
            BookDTO bookDTO = complexBookServiceImpl.queryBookDetailsByBookIdRequest(request);

            QueryBookDetailsByBookIdResponse response = new QueryBookDetailsByBookIdResponse();
            response.setBookDTO(bookDTO);

            return ServiceResult.createSuccessServiceResult("查询书籍详情成功",response);
        } catch (Exception e){
            String message = "查询书籍详情失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="物理删除书籍", notes="物理删除书籍")
    @ResponseBody
    @PostMapping("/PhysicsDeleteBookByBookId")
    public FreshServiceResult physicsDeleteBookByBookId(@RequestBody PhysicsDeleteBookByBookIdRequest request, HttpServletRequest httpServletRequest){
        try{
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,request.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult freshServiceResult = bookService.physicsDeleteBookByBookId(request);
            return freshServiceResult;
        } catch (Exception e){
            String message = "物理删除书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="查询全部书籍", notes="查询全部书籍")
    @ResponseBody
    @PostMapping("/QueryBookList")
    public ServiceResult<QueryBookListResponse> queryBookList(@RequestBody QueryBookListRequest request, HttpServletRequest httpServletRequest){
        try{
            UserDomain userDomain = CommonUtilsSession.getUser(httpServletRequest);
            request.setAuthorId(userDomain.getUserId());
            ServiceResult<List<BookDTO>> serviceResult = bookService.queryBookList(request);
            if(!ServiceResult.isSuccess(serviceResult)){
                return FreshServiceResult.createFailFreshServiceResult(serviceResult.getMessage());
            }
            QueryBookListResponse response = new QueryBookListResponse();
            response.setBookDTOList(serviceResult.getResult());
            return ServiceResult.createSuccessServiceResult("[查询全部书籍]成功",response);
        } catch (Exception e){
            String message = "[查询全部书籍]失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }




    @ApiOperation(value="新增书籍章节", notes="新增书籍章节")
    @ResponseBody
    @PostMapping("/AddBookChapter")
    public ServiceResult<AddBookChapterResponse> addBookChapter(@RequestBody AddBookChapterRequest request, HttpServletRequest httpServletRequest){
        try{
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,request.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            ServiceResult<BookChapterDTO> serviceResult = bookChapterService.addBookChapter(request);
            if(!ServiceResult.isSuccess(serviceResult)){
                return FreshServiceResult.createFailFreshServiceResult(serviceResult.getMessage());
            }
            AddBookChapterResponse response = new AddBookChapterResponse();
            response.setBookChapterDTO(serviceResult.getResult());
            return ServiceResult.createSuccessServiceResult("新增书籍章节成功",response);
        } catch (Exception e){
            String message = "新增书籍章节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="更新书籍章节", notes="更新书籍章节")
    @ResponseBody
    @PostMapping("/UpdateBookChapter")
    public FreshServiceResult updateBookChapter(@RequestBody UpdateBookChapterRequest request, HttpServletRequest httpServletRequest){
        try{
            BookChapterDTO bookChapterDTO = bookChapterService.queryBookChapterByBookChapterId(request.getBookChapterId());
            if(bookChapterDTO==null){
                return FreshServiceResult.createFailFreshServiceResult("书籍不存在");
            }
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,bookChapterDTO.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult serviceResult = bookChapterService.updateBookChapter(request);
            return serviceResult;
        } catch (Exception e){
            String message = "更新书籍章节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="更新书籍章节", notes="更新书籍章节")
    @ResponseBody
    @PostMapping("/SwapBookChapterOrder")
    public FreshServiceResult swapBookChapterOrder(@RequestBody SwapBookChapterOrderRequest request, HttpServletRequest httpServletRequest){
        try{
            BookChapterDTO bookAChapterDTO = bookChapterService.queryBookChapterByBookChapterId(request.getBookChapterAId());
            if(bookAChapterDTO==null){
                return FreshServiceResult.createFailFreshServiceResult("书籍不存在");
            }
            BookChapterDTO bookBChapterDTO = bookChapterService.queryBookChapterByBookChapterId(request.getBookChapterBId());
            if(bookBChapterDTO==null){
                return FreshServiceResult.createFailFreshServiceResult("书籍不存在");
            }
            if(!bookAChapterDTO.getBookId().equals(bookBChapterDTO.getBookId())){
                return FreshServiceResult.createFailFreshServiceResult("不是同一本书籍");
            }
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,bookAChapterDTO.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult serviceResult = bookChapterService.swapBookChapterOrder(request);
            return serviceResult;
        } catch (Exception e){
            String message = "更新书籍章节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="物理删除书籍章节", notes="物理删除书籍章节")
    @ResponseBody
    @PostMapping("/PhysicsDeleteBookChapterByBookChapterId")
    public FreshServiceResult physicsDeleteBookChapterByBookChapterId(@RequestBody PhysicsDeleteBookChapterByBookChapterIdRequest request, HttpServletRequest httpServletRequest){
        try{
            BookChapterDTO bookChapterDTO = bookChapterService.queryBookChapterByBookChapterId(request.getBookChapterId());
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,bookChapterDTO.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult freshServiceResult = bookChapterService.physicsDeleteBookChapterByBookChapterId(request);
            return freshServiceResult;
        } catch (Exception e){
            String message = "物理删除书籍章节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="查询书籍章节", notes="查询书籍章节")
    @ResponseBody
    @PostMapping("/QueryBookChapterListByBookId")
    public ServiceResult<QueryBookChapterListByBookIdResponse> queryBookChapterListByBookId(@RequestBody QueryBookChapterListByBookIdRequest request, HttpServletRequest httpServletRequest){
        try{
            ServiceResult<List<BookChapterDTO>> serviceResult = bookChapterService.queryBookChapterListByBookId(request);
            if(!ServiceResult.isSuccess(serviceResult)){
                return FreshServiceResult.createFailFreshServiceResult(serviceResult.getMessage());
            }
            QueryBookChapterListByBookIdResponse response = new QueryBookChapterListByBookIdResponse();
            response.setBookChapterDTOList(serviceResult.getResult());
            return ServiceResult.createSuccessServiceResult("查询书籍章节列表成功",response);
        } catch (Exception e){
            String message = "查询书籍章节列表失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }








    @ApiOperation(value="新增书籍小节", notes="新增书籍小节")
    @ResponseBody
    @PostMapping("/AddBookSection")
    public ServiceResult<AddBookSectionResponse> addBookSection(@RequestBody AddBookSectionRequest request, HttpServletRequest httpServletRequest){
        try{
            BookChapterDTO bookChapterDTO = bookChapterService.queryBookChapterByBookChapterId(request.getBookChapterId());
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,bookChapterDTO.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            ServiceResult<BookSectionDTO> serviceResult = bookSectionService.addBookSection(httpServletRequest,request);
            if(!ServiceResult.isSuccess(serviceResult)){
                return FreshServiceResult.createFailFreshServiceResult(serviceResult.getMessage());
            }
            AddBookSectionResponse response = new AddBookSectionResponse();
            response.setBookSectionDTO(serviceResult.getResult());
            return ServiceResult.createSuccessServiceResult("新增书籍小节成功",response);
        } catch (Exception e){
            String message = "新增书籍小节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="更新书籍小节", notes="更新书籍小节")
    @ResponseBody
    @PostMapping("/UpdateBookSection")
    public FreshServiceResult updateBookSection(@RequestBody UpdateBookSectionRequest request, HttpServletRequest httpServletRequest){
        try{
            BookSectionDTO bookSectionDTO = bookSectionService.queryBookSectionDTOBySectionId(request.getBookSectionId());
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,bookSectionDTO.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult serviceResult = bookSectionService.updateBookSection(request);
            return serviceResult;
        } catch (Exception e){
            String message = "更新书籍小节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="物理删除书籍小节", notes="物理删除书籍小节")
    @ResponseBody
    @PostMapping("/PhysicsDeleteBookSectionByBookSectionId")
    public FreshServiceResult physicsDeleteBookSectionByBookSectionId(@RequestBody PhysicsDeleteBookSectionByBookSectionIdRequest request, HttpServletRequest httpServletRequest){
        try{
            BookSectionDTO bookSectionDTO = bookSectionService.queryBookSectionDTOBySectionId(request.getBookSectionId());
            boolean isHasOperateRight = bookService.isHasOperateRight(httpServletRequest,bookSectionDTO.getBookId());
            if(!isHasOperateRight){
                return FreshServiceResult.createFailFreshServiceResult("你没有权限操作该书籍");
            }
            FreshServiceResult freshServiceResult = bookSectionService.physicsDeleteBookSectionByBookSectionId(request);
            return freshServiceResult;
        } catch (Exception e){
            String message = "物理删除书籍小节失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @ApiOperation(value="查询书籍小节", notes="查询书籍小节")
    @ResponseBody
    @PostMapping("/QueryBookSectionListBybookChapterId")
    public ServiceResult<QueryBookSectionListBybookChapterIdResponse> queryBookSectionListBybookChapterId(@RequestBody QueryBookSectionListBybookChapterIdRequest request, HttpServletRequest httpServletRequest){
        try{
            ServiceResult<List<BookSectionDTO>> serviceResult = bookSectionService.queryBookSectionListBybookChapterId(request);
            if(!ServiceResult.isSuccess(serviceResult)){
                return FreshServiceResult.createFailFreshServiceResult(serviceResult.getMessage());
            }
            QueryBookSectionListBybookChapterIdResponse response = new QueryBookSectionListBybookChapterIdResponse();
            response.setBookSectionDTOList(serviceResult.getResult());
            return ServiceResult.createSuccessServiceResult("查询书籍小节列表成功",response);
        } catch (Exception e){
            String message = "查询书籍小节列表失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }


}
