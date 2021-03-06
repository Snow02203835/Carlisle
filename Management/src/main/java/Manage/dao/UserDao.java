package Manage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Repository
public class UserDao {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 判断是否重复请求验证码
     * @author snow create 2021/01/17 23:06
     * @param ipAddress
     * @return
     */
    public Boolean isAllowRequestForVerifyCode(String ipAddress){
        String key = "ip_" + ipAddress;
        if(redisTemplate.hasKey(key)){
            return false;
        }
        redisTemplate.opsForValue().set(key, ipAddress);
        redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        return true;
    }

    /**
     * 将验证码放入Redis
     * @author snow create 2021/01/17 23:17
     * @param verifyCode
     * @param studentId
     */
    public void putVerifyCodeIntoRedis(String verifyCode, String studentId){
        String key = "cp_" + verifyCode;
        redisTemplate.opsForValue().set(key, studentId);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    /**
     * 从验证码中取出id
     * @author snow create 2021/01/17 23:58
     * @param verifyCode
     * @return
     */
    public String getUserIdByVerifyCode(String verifyCode){
        String key = "cp_" + verifyCode;
        if(!redisTemplate.hasKey(key)){
            return null;
        }
        return redisTemplate.opsForValue().get(key).toString();
    }

    /**
     * 将邮箱验证密匙放入Redis
     * @author snow create 2021/02/28 15:27
     * @param adminId
     * @return
     */
    public void putEmailVerifyKeyIntoRedis(Long adminId, String value){
        String key = "Email_" + adminId;
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    }

    /**
     * 从验证码中取出邮箱验证密匙
     * @author snow create 2021/02/28 15:28
     * @param adminId
     * @return
     */
    public String getEmailVerifyKey(Long adminId){
        String key = "Email_" + adminId;
        System.out.println(key);
        if(!redisTemplate.hasKey(key)){
            return null;
        }
        System.out.println(redisTemplate.opsForValue().get(key).toString());
        return redisTemplate.opsForValue().get(key).toString();
    }

    /**
     * 修改密码成功之后让验证码失效
     * @author snow create 2021/01/18 10:32
     * @param verifyCode
     */
    public void disableVerifyCodeAfterSuccessfullyModifyPassword(String verifyCode){
        String key = "cp_" + verifyCode;
        if(redisTemplate.hasKey(key)){
            redisTemplate.expire(key, 1, TimeUnit.MILLISECONDS);
        }
    }
}
