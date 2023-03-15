package com.tanhua.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.tanhua.api.sso.UserServiceApi;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.exception.ConsumerException;
import com.tanhua.db.pojo.User;
import com.tanhua.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    //private RedisTemplate<String,String> redisTemplate;
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    //dubbo远程调用
    @DubboReference
    private UserServiceApi userServiceApi;

    /**
     * 1、发送短信验证码
     * @param phone
     */
    public void sendCode(String phone) {
        //存储到redis中 key  value
        String redisKey="CHECK_CODE_"+phone;
        //从redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get(redisKey);
        //如果redisCode不为空，则表示验证码未失效，无需再次发送
        if (StrUtil.isNotBlank(redisCode)){
            //throw new RuntimeException("验证码还未失效！！");
            throw new ConsumerException("验证码还未失效！！");
        }
        //smsTemplate.sendSms(phone,"666666");
        String code="666666";

        //设置时效 5分钟
        redisTemplate.opsForValue().set(redisKey,code,5L, TimeUnit.MINUTES);
    }

    /**
     * 2、手机号和验证校验
     * @param params
     * @return
     */
    public Map<String, Object> loginVerification(Map<String, String> params) {
        //1、获取手机号和验证码
        String phone = params.get("phone");
        String code =  params.get("verificationCode");

        //2、获取redis中的验证码
        String redisKey="CHECK_CODE_"+phone;
        //从redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get(redisKey);
        //如果不相同则表示验证码输入错误
        if(!StrUtil.equals(code,redisCode)){
            //向客户端显示错误信息
            //throw new RuntimeException("验证输入错误！");
            throw new ConsumerException("验证码不正确，请查证后重新输入！");
        }
        //3、如果验证码验证通过，则删除redis中验证码
        redisTemplate.delete(redisKey);

        //4、校验手机号：数据库中
        User user = userServiceApi.findByPhone(phone);
        //声明一个标识，用于标记是否是新用户
        boolean isNew=false;//默认是老用户
        //5、判断user
        if (ObjectUtil.isNull(user)){//数据库当前手机号不存在
            isNew=true;//标记为新用户
            user=new User();
            //封装数据
            user.setMobile(phone);
            user.setPassword(DigestUtil.md5Hex("123456"));//md加密处理
           /* user.setCreated(new Date());
            user.setUpdated(new Date());*/
            //调用保存功能,同时返回保存公共用户id
            Long id=userServiceApi.save(user);
            user.setId(id);
            //调用环信创建用户功能
            huanXinTemplate.createUser("hx" + id,"123456");
        }
        //6、生成token
        String token = AppJwtUtil.getToken(user.getId());

        Map<String, Object> result=new HashMap<>();

        result.put("token",token);
        result.put("isNew",isNew);
        return result;
    }

    //解析token
    public Long getToken(String token){
        Claims claimsBody = AppJwtUtil.getClaimsBody(token);
        int flag = AppJwtUtil.verifyToken(claimsBody);
        //判断token是否在有效期
        if (flag==1 || flag==2){
            throw new ConsumerException("token已失效!");
        }
        return Convert.toLong(claimsBody.get("id"));
    }

}
