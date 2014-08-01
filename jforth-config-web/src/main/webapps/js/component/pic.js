/**
 用于图片加载，实时显示

 (1)jsp/ftl call example:
 <li>
 <input type="hidden" class="picImg_1" name="picImg"/>
 <input class="pic_1" type="file" name="pic" style="display: none;" onchange="PreviewImage(this,'img_1','imgspan_1')"/>
 <div class="caption">
 <button type="button" class="btn btn-primary btn-xs" onclick="uploadPic(1)">
 <span class="glyphicon glyphicon-arrow-up"></span>图片上传
 </button>
 </div>
 <span id="imgspan_1"><img id="img_1" style="width:150px;height:250px;border:1px solid #cccccc"" src=""></span>
 <div class="caption">
 <button type="button" class="btn btn-danger btn-xs" onclick="removePic(1)">
 <span class="glyphicon glyphicon-remove"></span>删除图片
 </button>
 </div>
 </li>
 */
//js本地图片预览，兼容ie[6-8]、火狐、Chrome17+、Opera11+、Maxthon3
function PreviewImage(fileObj,imgPreviewId,divPreviewId){
    var allowExtention='.jpg,.png,.jpeg,.bmp';//.jpg,.bmp,.gif,.png,允许上传文件的后缀名
    var extention=fileObj.value.substring(fileObj.value.lastIndexOf(".")+1).toLowerCase();            
    var browserVersion= window.navigator.userAgent.toUpperCase();
    if(allowExtention.indexOf(extention)>-1){               
        if (browserVersion.indexOf("MSIE")>-1){
            if(browserVersion.indexOf("MSIE 6.0")>-1){//ie6
                document.getElementById(imgPreviewId).setAttribute("src",fileObj.value);
            }else{//ie[7-8]、ie9未测试
                fileObj.select();
                var newPreview =document.getElementById(divPreviewId+"New");
                if(newPreview==null){
                    newPreview =document.createElement("div");
                    newPreview.setAttribute("id",divPreviewId+"New");
                    newPreview.style.width = document.getElementById(imgPreviewId).style.width;
                    newPreview.style.height = document.getElementById(imgPreviewId).style.height;
                    newPreview.style.border="solid 1px #d2e2e2";
                }
                newPreview.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + document.selection.createRange().text + "')";                            
                var tempDivPreview=document.getElementById(divPreviewId);
                tempDivPreview.parentNode.insertBefore(newPreview,tempDivPreview);
                tempDivPreview.style.display="none";                    
            }
        }else if(browserVersion.indexOf("FIREFOX")>-1){//firefox
            var firefoxVersion= parseFloat(browserVersion.toLowerCase().match(/firefox\/([\d.]+)/)[1]);
            if(firefoxVersion<7){//firefox7以下版本
                document.getElementById(imgPreviewId).setAttribute("src",fileObj.files[0].getAsDataURL());
            }else{//firefox7.0+                    
                document.getElementById(imgPreviewId).setAttribute("src",window.URL.createObjectURL(fileObj.files[0]));
            }
        }else if(fileObj.files){               
            //兼容chrome、火狐等，HTML5获取路径                   
            if(typeof FileReader !== "undefined"){
                var reader = new FileReader(); 
                reader.onload = function(e){
                    document.getElementById(imgPreviewId).setAttribute("src",e.target.result);
                }  
                reader.readAsDataURL(fileObj.files[0]);
            }else if(browserVersion.indexOf("SAFARI")>-1){
                alert("暂时不支持Safari浏览器!");
            }
        }else{
            document.getElementById(divPreviewId).setAttribute("src",fileObj.value);
        }         
    }else{
        alert("仅支持"+allowExtention+"为后缀名的文件!");
        fileObj.value="";//清空选中文件
        if(browserVersion.indexOf("MSIE")>-1){                        
            fileObj.select();
            document.selection.clear();
        }                
        fileObj.outerHTML=fileObj.outerHTML;
    }              
}