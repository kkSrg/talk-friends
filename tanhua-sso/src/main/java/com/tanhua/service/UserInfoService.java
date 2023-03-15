package com.tanhua.service;

import cn.hutool.core.convert.Convert;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.db.dto.UserInfoDto;
import com.tanhua.exception.ConsumerException;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @Autowired
    private UserService userService;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;


    /**
     * 3、完成个人信息
     * @param dto
     */
    public void loginReginfo(UserInfoDto dto) {
        //获取登录者id
        Long id= ThreadLocalUtil.getId();

        UserInfo userInfo=new UserInfo();
        //给userInfo补全信息
        BeanUtils.copyProperties(dto,userInfo);

        userInfo.setId(Convert.toLong(id));
        userInfoServiceApi.save(userInfo);
    }

    /**
     * 4、补充头像处理
     * @param headPhoto
     */
    public void head(MultipartFile headPhoto) {
        //1、获取登录者id
        Long id=ThreadLocalUtil.getId();
        //2、头像上传到阿里云oss   返回具体的存储地址
        try {
            String path= ossTemplate.upload(headPhoto.getOriginalFilename(),headPhoto.getInputStream());
            //对上传阿里云的图片做人像检测
//            boolean flag = aipFaceTemplate.detect(path);
//            if (!flag){
//                throw new ConsumerException("上传的头像非人像！");
//            }
            //3、封装对象：id  头像存储的地址
            UserInfo userInfo=new UserInfo();
            userInfo.setId(id);
            userInfo.setAvatar(path);
            //4、调用db实现头像的更新
            userInfoServiceApi.updateAvatar(userInfo);
        } catch (IOException e) {
            throw new ConsumerException("上传头像失败！");
        }

    }
}
