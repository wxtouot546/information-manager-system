$(function(){
    var url=""

    $.ajax({
        type: "post",
        url: url+"/User/GetUserInfo",
        contentType:"application/json",
        dataType: "json",
        async:false,
        success: function(data){
            console.log(data)
            var jsElement = document.getElementById("common_auto_login_js");
            var page = jsElement.getAttribute("data");
            if(data.ServiceCode=='SUCCESS'){
                var showHtml = "" ;
                if(page == "jiaocheng_page"){
                    showHtml = "<a href='/bbs/post_list.html' class='nav_li'>论坛</a>"
                             + "<a href='javascript:void(0)' class='nav_li'>"+data.Result.UserInfo.UserName+"</a>";
                }
                if(page == "post_list_page"){
                    showHtml = "<a href='/bbs/start_a_new_post.html' class='nav_li'>发帖</a>"
                             + "<a href='/bbs/post_list.html' class='nav_li active'>论坛</a>"
                             + "<a href='/bbs/my_post_list.html' class='nav_li'>我的帖子</a>"
                             + "<a href='javascript:void(0)' class='nav_li'>"+data.Result.UserInfo.UserName+"</a>";
                }
                if(page == "start_a_new_post_page"){
                    showHtml = "<a href='/bbs/start_a_new_post.html' class='nav_li active'>发帖</a>"
                             + "<a href='/bbs/post_list.html' class='nav_li'>论坛</a>"
                             + "<a href='/bbs/my_post_list.html' class='nav_li'>我的帖子</a>"
                             + "<a href='javascript:void(0)' class='nav_li'>"+data.Result.UserInfo.UserName+"</a>";
                }
                if(page == "my_post_list_page"){
                    showHtml = "<a href='/bbs/start_a_new_post.html' class='nav_li'>发帖</a>"
                             + "<a href='/bbs/post_list.html' class='nav_li'>论坛</a>"
                             + "<a href='/bbs/my_post_list.html' class='nav_li active'>我的帖子</a>"
                             + "<a href='javascript:void(0)' class='nav_li'>"+data.Result.UserInfo.UserName+"</a>";
                }
                if(page == "post_details_template_page"){
                    showHtml = "<a href='/bbs/start_a_new_post.html' class='nav_li'>发帖</a>"
                             + "<a href='/bbs/post_list.html' class='nav_li'>论坛</a>"
                             + "<a href='javascript:void(0)' class='nav_li active'>帖子</a>"
                             + "<a href='/bbs/my_post_list.html' class='nav_li'>我的帖子</a>"
                             + "<a href='javascript:void(0)' class='nav_li'>"+data.Result.UserInfo.UserName+"</a>";
                }
                $("#rigth_nav").html(showHtml)
            } else {
                if(page == "start_a_new_post_page"){
                    alert("网站只对会员提供发帖功能。请先登录。即将跳转到登录页面...");
                    location.href="/login.html"
                }
                if(page == "my_post_list_page"){
                    alert("只有会员才有'我'的帖子页面。请先登录。即将跳转到登录页面...");
                    location.href="/login.html"
                }
                if(page == "post_list_page"){
                    alert("网站只对会员提供论坛功能。请先登录。即将跳转到登录页面...");
                    location.href="/login.html"
                }
            }
        },
        error:function(e){
        }
    });
})