package com.xingkaichun.information.service.impl;

import com.xingkaichun.common.dto.base.FreshServiceResult;
import com.xingkaichun.common.dto.base.ServiceCode;
import com.xingkaichun.common.dto.base.ServiceResult;
import com.xingkaichun.information.dao.BookChapterDao;
import com.xingkaichun.information.dao.BookDao;
import com.xingkaichun.information.dto.book.BookDTO;
import com.xingkaichun.information.dto.book.request.AddBookRequest;
import com.xingkaichun.information.dto.book.request.PhysicsDeleteBookByBookIdRequest;
import com.xingkaichun.information.dto.book.request.QueryBookListRequest;
import com.xingkaichun.information.dto.book.request.UpdateBookRequest;
import com.xingkaichun.information.model.BookChapterDomain;
import com.xingkaichun.information.model.BookDomain;
import com.xingkaichun.information.model.UserDomain;
import com.xingkaichun.information.service.BookService;
import com.xingkaichun.utils.CommonUtils;
import com.xingkaichun.utils.CommonUtilsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service(value = "bookService")
public class BookServiceImpl implements BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    private BookDao bookDao;
    @Autowired
    private BookChapterDao bookChapterDao;


    @Override
    public ServiceResult<BookDTO> addBook(HttpServletRequest request, AddBookRequest addBookRequest) {
        try{
            ServiceResult seoUrlLegalServiceResult = isBookSeoUrlLegal(addBookRequest.getSeoUrl());
            if(seoUrlLegalServiceResult.getServiceCode() == ServiceCode.FAIL){
                return FreshServiceResult.createFailFreshServiceResult(seoUrlLegalServiceResult.getMessage());
            }

            addBookRequest.setAuthorId(CommonUtilsSession.getUser(request).getUserId());
            if(!CommonUtils.isNUllOrEmpty(addBookRequest.getBookId())){
                return FreshServiceResult.createFailFreshServiceResult("系统自动分配书籍ID，请不要填写书籍ID");
            } else {
                addBookRequest.setBookId(String.valueOf(UUID.randomUUID()));
            }
            if(CommonUtils.isNUllOrEmpty(addBookRequest.getBookName())){
                return FreshServiceResult.createFailFreshServiceResult("书籍名称不能为空");
            }
            BookDomain bookDomain = classCast2(addBookRequest);
            bookDao.addBook(bookDomain);

            BookDomain dbBookDomain = bookDao.queryBookByBookId(addBookRequest.getBookId());
            BookDTO bookDTO = classCast(dbBookDomain);
            return FreshServiceResult.createSuccessServiceResult("新增书籍成功",bookDTO);
        } catch (Exception e){
            String message = "新增书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @Override
    public ServiceResult<BookDTO> updateBook(UpdateBookRequest updateBookRequest) {
        try{
            if(CommonUtils.isNUllOrEmpty(updateBookRequest.getBookId())){
                return FreshServiceResult.createFailFreshServiceResult("书籍ID不能为空");
            }
            BookDomain bookDomain = bookDao.queryBookByBookId(updateBookRequest.getBookId());
            if(bookDomain==null){
                return FreshServiceResult.createFailFreshServiceResult("书籍不存在");
            }
            if(!bookDomain.getSeoUrl().equals(updateBookRequest.getSeoUrl())){
                ServiceResult seoUrlLegalServiceResult = isBookSeoUrlLegal(updateBookRequest.getSeoUrl());
                if(seoUrlLegalServiceResult.getServiceCode() == ServiceCode.FAIL){
                    return FreshServiceResult.createFailFreshServiceResult(seoUrlLegalServiceResult.getMessage());
                }
            }
            bookDao.updateBook(updateBookRequest);

            BookDTO bookDTO = queryBookByBookId(updateBookRequest.getBookId());

            return ServiceResult.createSuccessServiceResult("更新书籍成功",bookDTO);
        } catch (Exception e){
            String message = "更新书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @Override
    public ServiceResult<BookDTO> physicsDeleteBookByBookId(PhysicsDeleteBookByBookIdRequest physicsDeleteBookByBookIdRequest) {
        try{
            if(CommonUtils.isNUllOrEmpty(physicsDeleteBookByBookIdRequest.getBookId())){
                return FreshServiceResult.createFailFreshServiceResult("书籍ID不能为空");
            }
            //校验软删除标识
            BookDomain bookDomain = bookDao.queryBookByBookId(physicsDeleteBookByBookIdRequest.getBookId());
            if(bookDomain==null){
                return FreshServiceResult.createFailFreshServiceResult("书籍不存在");
            }

            BookDTO bookDTO = classCast(bookDomain);
/*            if(!bookDomain.isSoftDelete()){
                return FreshServiceResult.createFailFreshServiceResult("书籍软删除标识为不可删");
            }*/
            //校验 书籍下不能有章节
            List<BookChapterDomain> bookChapterDomainList = bookChapterDao.queryBookChapterListByBookId(physicsDeleteBookByBookIdRequest.getBookId());
            if(!CommonUtils.isNUllOrEmpty(bookChapterDomainList)){
                return FreshServiceResult.createFailFreshServiceResult("书籍下不能有章节");
            }

            bookDao.physicsDeleteBookByBookId(physicsDeleteBookByBookIdRequest.getBookId());

            return ServiceResult.createSuccessServiceResult("删除书籍成功",bookDTO);
        } catch (Exception e){
            String message = "删除书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @Override
    public ServiceResult<List<BookDTO>> queryBookList(QueryBookListRequest request) {
        try{
            List<BookDomain> bookDomainList = bookDao.queryBookList(request);
            List<BookDTO> bookDTOList = classCast(bookDomainList);
            
            return FreshServiceResult.createSuccessServiceResult("查询书籍列表成功",bookDTOList);
        } catch (Exception e){
            String message = "删除书籍失败";
            LOGGER.error(message,e);
            return FreshServiceResult.createFailFreshServiceResult(message);
        }
    }

    @Override
    public BookDTO queryBookByBookId(String bookId) {
        BookDomain bookDomain = bookDao.queryBookByBookId(bookId);
        return classCast(bookDomain);
    }

    @Override
    public boolean isHasOperateRight(HttpServletRequest httpServletRequest, String bookId) {
        BookDomain bookDomain = bookDao.queryBookByBookId(bookId);
        UserDomain userDomain = CommonUtilsSession.getUser(httpServletRequest);
        return bookDomain.getAuthorId().equals(userDomain.getUserId());
    }

    private List<BookDTO> classCast(List<BookDomain> bookDomainList) {
        if(CommonUtils.isNUllOrEmpty(bookDomainList)){
            return null;
        }
        List<BookDTO> dtoList = new ArrayList<>();
        for (BookDomain bookDomain:bookDomainList){
            dtoList.add(classCast(bookDomain));
        }
        return dtoList;
    }

    private BookDTO classCast(BookDomain domain) {
        BookDTO dto = new BookDTO();
        dto.setId(domain.getId());
        dto.setBookId(domain.getBookId());
        dto.setAuthorId(domain.getAuthorId());
        dto.setBookName(domain.getBookName());
        dto.setBookDescription(domain.getBookDescription());

        dto.setCreateTime(domain.getCreateTime());
        dto.setLastEditTime(domain.getLastEditTime());
        dto.setSoftDelete(domain.isSoftDelete());

        dto.setSeoUrl(domain.getSeoUrl());
        dto.setSeoTitle(domain.getSeoTitle());
        dto.setSeoKeywords(domain.getSeoKeywords());
        dto.setSeoDescription(domain.getSeoDescription());

        dto.setAuditStatus(domain.getAuditStatus());
        return dto;
    }

    private BookDomain classCast2(AddBookRequest addBookRequest) {
        return classCast(addBookRequest);
    }

    private BookDomain classCast(BookDTO dto) {
        BookDomain domain = new BookDomain();
        domain.setBookId(dto.getBookId());
        domain.setAuthorId(dto.getAuthorId());
        domain.setBookName(dto.getBookName());
        domain.setBookDescription(dto.getBookDescription());

        domain.setCreateTime(dto.getCreateTime());
        domain.setLastEditTime(dto.getLastEditTime());
        domain.setSoftDelete(dto.isSoftDelete());

        domain.setSeoUrl(dto.getSeoUrl());
        domain.setSeoTitle(dto.getSeoTitle());
        domain.setSeoKeywords(dto.getSeoKeywords());
        domain.setSeoDescription(dto.getSeoDescription());

        domain.setAuditStatus(dto.getAuditStatus());
        return domain;
    }

    /**
     * seourl网址不能为空，不能重复
     */
    private ServiceResult isBookSeoUrlLegal(String seoUrl){
        if(seoUrl == null || "".equals(seoUrl)){
            return ServiceResult.createFailServiceResult("Seo网址不能为空，请修改。");
        }
        if(isSeoUrlExist(seoUrl)){
            return ServiceResult.createFailServiceResult("Seo网址已存在，请修改。");
        }
        return ServiceResult.createSuccessServiceResult("Seo网址合法，该Seo网址暂未有人使用。",null);
    }

    private boolean isSeoUrlExist(String seoUrl){
        QueryBookListRequest queryBookListRequest = new QueryBookListRequest();
        queryBookListRequest.setSeoUrl(seoUrl);
        List<BookDomain> bookDomains = bookDao.queryBookList(queryBookListRequest);
        if(bookDomains!=null&&bookDomains.size()>0){
            return true;
        }
        return false;
    }
}
