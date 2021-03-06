package com.xingkaichun.information.filter;

import com.xingkaichun.information.service.UserService;
import com.xingkaichun.utils.CommonUtils;
import com.xingkaichun.utils.CommonUtilsSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@WebFilter(filterName="UserTokenFilter", urlPatterns="/*")
public class UserTokenFilter implements Filter {

	@Autowired
	private UserService userService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		//尝试登录
		if(CommonUtils.isNUll(CommonUtilsSession.getUser(httpServletRequest))){
			userService.updateUserSession(httpServletRequest);
		}

		if(CommonUtils.isNUll(CommonUtilsSession.getUser(httpServletRequest))){
			String uri = httpServletRequest.getRequestURI();

			boolean needAccess = false;
			if (uri.contains("/Add")||uri.contains("/Delete")||uri.contains("/Update")){
				needAccess = true ;
			}
			if (uri.contains("/QueryBbsArticleByUserId")||uri.contains("/User/GetUserInfo")){
				needAccess = true ;
			}
			if(uri.contains("/User/AddUser")){
				needAccess = false;
			}

			if(needAccess){
				if(CommonUtils.isNUll(CommonUtilsSession.getUser(httpServletRequest))){
					HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					httpServletResponse.sendRedirect("/Error/Auth");
					return;
				}
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
}