var url = "";
var parameters = {};

//通过URL获取小节ID
function getSectionIdByUrl() {
    var args = {};
    var query = location.search.substring(1);
    var pairs = query.split("&");
    for (var i = 0; i < pairs.length; i++) {
        var pos = pairs[i].indexOf("=");
        if (pos == -1) continue;
        var name = pairs[i].substring(0, pos);
        var value = pairs[i].substring(pos + 1);
        value = decodeURIComponent(value);
        args[name] = value;
    }
    return args;
}

//获取小节内容
function getSectionContent() {
    var section_content = {};
    $.ajax({
        type: "post",
        url: url + "/Book/QueryBookSectionListBybookChapterId",
        contentType: "application/json",
        data: `{
                "BookChapterId": "${getSectionIdByUrl().BookChapterId}"
        }`,
        dataType: "json",
        async: false,
        success: function (data) {
            var section_list = data.Result.BookSectionDTOList;
            for (var i=0; i<section_list.length; i++){
                if (section_list[i].BookSectionId == getSectionIdByUrl().BookSectionId){
                    section_content.Section = section_list[i];
                    parameters.section_content = section_list[i].BookSectionContent;
                    parameters.section_url = section_list[i].SeoUrl;
                    parameters.bookid = section_list[i].BookId;
                }
            }
        },
        error: function (e) {
        }
    });
    var section_content_html = template("course_list_template",section_content);
    $("#section_content").html(section_content_html);
}
getSectionContent();

//获取用户输入信息
function userInputInfo() {
    var user = {};
    user.name = $("#section_info input[name=name]").val();
    user.description = $("#section_info input[name=section_description]").val();
    user.seo_url = $("#section_info input[name=seo_url]").val();
    user.seo_title = $("#section_info input[name=seo_title]").val();
    user.seo_keywords = $("#section_info input[name=seo_keywords]").val();
    user.seo_description = $("#section_info input[name=seo_description]").val();
    return user;
}

//获取书籍URL，书籍名称
function getBookUrl() {
    var book = {};
    $.ajax({
        type: "post",
        url: url + "/Book/QueryBookDetailsByBookId",
        contentType: "application/json",
        data: `{
                "BookId": "${parameters.bookid}"
        }`,
        dataType: "json",
        async: false,
        success: function (data) {
            book.book_url = data.Result.BookDTO.SeoUrl;
            book.book_name = data.Result.BookDTO.BookName;
            book.chapter = data.Result.BookDTO.BookChapterDTOList;
            for (var i=0; i<book.chapter.length; i++){
                if (book.chapter[i].BookChapterId == getSectionIdByUrl().BookChapterId){
                    book.chapter_name = book.chapter[i].BookChapterName;
                }
            }
        },
        error: function (e) {
        }
    });
    return book;
}



//修改小节
function modifySection() {
    var str =  getContent();
    str = str.replace(/"/g,'\\"');
    $.ajax({
            type: "post",
            url: url+"/Book/UpdateBookSection",
            contentType:"application/json",
            data:`{
                    "BookSectionId": "${getSectionIdByUrl().BookSectionId}",
                    "BookSectionName": "${userInputInfo().name}",
                    "BookSectionDescription": "${userInputInfo().description}",
                    "BookSectionContent": "${str}",
                    "SeoUrl": "${userInputInfo().seo_url}",
                    "SeoTitle": "${userInputInfo().seo_title}",
                    "SeoKeywords": "${userInputInfo().seo_keywords}",
                    "SeoDescription": "${userInputInfo().seo_description}"
            }`,
            dataType: "json",
            async:false,
            success: function(data){
                popBox("提交成功，等待管理员审核",0);
                // location.href="/jiaocheng/"+getBookUrl().book_url+"/"+parameters.section_url+".html";
            },
            error:function(e){
            }
        });
}

//插入图片
$("input[name='upload_img']").change(function(){
    console.log(this.files);
    for(var i=0,len = this.files.length;i<len;i++){
        var dataFrom = new FormData();
        dataFrom.append("file",  this.files[i])
        $.ajax({
            url:  url+"/File/FileUpload",
            type: "post",
            processData: false,
            contentType: false,
            traditional: true,
            dataType: "json",
            data:dataFrom,
            async: false,
            success: function success(data) {
                $(".edui-body-container").append(`<img style='display:block;' src='${data.Result.FileDto.FilePath}'/>`)
            }
        })
    }

})

//标题
var bookName = document.getElementById("book_name");
var chapterkName = document.getElementById("chapter_name");
bookName.innerHTML = getBookUrl().book_name;
chapterkName.innerHTML = getBookUrl().chapter_name;



//编辑器内容展示

var um = UM.getEditor('myEditor');
// UE.getEditor('editor').setContent(parameters.section_content);
function setContent() {
    UM.getEditor('myEditor').setContent(parameters.section_content);
}
function getContent() {
     var a = UM.getEditor('myEditor').getContent();
     return a;
}
window.onload = function(){
    setContent();
}



