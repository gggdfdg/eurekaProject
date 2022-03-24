package com.ming.eureka.restcontroller;

import com.ming.eureka.BaseService;
import com.ming.eureka.CommonResult;
import com.ming.eureka.DateUtil;
import com.ming.eureka.dto.LoginInfo;
import com.ming.eureka.model.dao.config.SysConfigDao;
import com.ming.eureka.model.entity.config.SysConfig;
import com.ming.eureka.model.entity.user.User;
import com.ming.eureka.security.TokenUtils;
import com.ming.eureka.util.SecureUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("/api")
@Slf4j
@Api(tags = "api认证")
public class RestAuthController extends RestBaseController {

    private String CHECK_KEY = "!@#$#$$$23234$$fa11111111134343453452456547sdfads$";

    private @Autowired FindByIndexNameSessionRepository<? extends ExpiringSession> sessionRepository;

    @Autowired
    private LoadBalancerClient discoveryClient;

    @Autowired
    private SysConfigDao sysConfigDao;
    
    /**
     * 认证成功返回token
     *
     * @param session
     * @return
     */
    @ApiOperation(value = "登录成功后跳转到该接口", response = LoginInfo.class)
    @PostMapping(value = "token")
    CommonResult authSucc(HttpSession session, HttpServletResponse response) {
        LoginInfo info = new LoginInfo();
        info.setSocketHost(BaseService.HOSTNAME);
        User currentUser = this.getCurrentUser();
        //生成token（用户拿着这个token+请求返回给服务端，服务端匹配token，如果匹配上了则处理用户发来的请求，如果匹配不上则用户验证失败不处理请求）
        String jwt = TokenUtils.createToken(currentUser.getLoginName());
        info.setToken(jwt);
        return CommonResult.succ().withResult(info);
    }

    /**
     * 登录成功
     * @return
     */
    @ApiIgnore
    @GetMapping(value = "logoutsucc")
    CommonResult logoutSucc() {
        return CommonResult.succ();
    }


    /**
     * 返回服务器时间
     *
     *
     * @return
     */
    @ApiOperation(value = "返回服务器时间", response = long.class)
    @GetMapping(value = "time")
    long servicetime(HttpSession session, HttpServletResponse response) {
        return System.currentTimeMillis();
    }

    /**
     * 服务器状态配置
     * @param key 检测字符串
     * @return
     */
    @ApiOperation(value = "服务器状态配置")
    @PostMapping(value = "server")
    CommonResult serverConfig(String key,long otime) throws Exception {
        //时间检测超过3S
        if(System.currentTimeMillis() - otime > 3000){
            return CommonResult.commError("链接已超时");
        }

        MessageDigest md5=MessageDigest.getInstance("MD5");
        String digestStr = otime + CHECK_KEY;
        String calKey = Base64.encodeBase64String(md5.digest(digestStr.getBytes("utf-8")));
        if(!calKey.equals(key)){
            return CommonResult.commError("key校验失败");
        }
        SysConfig sysConfig = sysConfigDao.findById(1L).orElse(null);
        if(sysConfig==null){
            return CommonResult.notFoundError();
        }
        sysConfig.setLockStatus(0);
        sysConfigDao.save(sysConfig);

        return CommonResult.succ();
    }
}
