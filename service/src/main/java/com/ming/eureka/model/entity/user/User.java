package com.ming.eureka.model.entity.user;

import com.google.common.collect.Lists;
import com.ming.eureka.model.entity.IdTimeTenantEntity;
import com.ming.eureka.model.entity.role.Role;
import com.ming.eureka.model.entity.userinfo.UserInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 */
@Getter
@Setter
@Entity
@Table(name = "b_user", indexes = {
        @Index(columnList = "loginName"),
        @Index(columnList = "state")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"loginName", "tenant_id"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends IdTimeTenantEntity implements IUser<User> {

    private static final long serialVersionUID = -5779100277511563780L;
    // 登录名
    @NotBlank
    @Column(length = 100)
    private String loginName;
    // 昵称
    private String alias;
    //md5明文密码，方便聊天客服端进行登录密码校验
    private String plainPassword;
    // 密码
    private String password;
    private String salt;
    // 状态
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private State state;

    //所属角色
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "role_id")
    private Role role;

    //上次登录时间
    private Date lastLoginDate;

    //上次登录IP
    private String lastLoginIp;

    //上次登录地址
    private String lastLoginAddress;

    // 用户基础信息 0-未设置 1-男 2-女
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private UserInfo userinfo = new UserInfo();

    // 用户使用协议
    private boolean eulaAgreed;

    public User() {
        super();
    }

    public User(long id) {
        super();
        this.id = id;
    }


    @Override
    public void prepareForInsert() {
        this.userinfo.setUser(this);
        this.state = State.NORMAL;
    }

    @Override
    public String[] getPermissionList() {

        String roles = this.getRole().getRights();
        // use for manage admin
        if (roles.equals("ALL_MENU")) {
            roles = "roleManage,userManage,taskManage,iosTaskManage,messageManage,dialogMessage,materialManage,"
                    + "materialDownload,graphicsMaterial,videoMaterial,linkMaterial,officialaccountMaterial,"
                    + "contactMaterial,searchfriendMaterial,platformAccount,textMaterial,imageMaterial,deviceManage,adddeviceGroup,"
                    + "deviceGroup,adddevice,deviceList,iosDeviceManage,"
                    + "iosAdddeviceGroup,iosDeviceGroup,iosAdddevice,iosDeviceList,iosDddWXGroup,"
                    + "iosWxGroupList,iosAccountList,dataStat,friendStat,officialaccountStat,"
                    + "updateManage,updatePush,versionInfo,actionLog,sniffer,autotasksetting,autotasksettingbatch,iosListUid,snstasklist,snstaskaddList,"
                    + "autoreplyaddList,autotReplyaddbatch,voiceMaterial,nickname,signMaterial,cityMaterial,portraitMaterial,backgroundMaterial,wechatAccount,"
                    + "identityMaterial,QRcodeMaterial,readMaterial,autoreplyMaterial,goodsMaterial,appletMaterial,snifferFriend,portrait,background,intervalsetting,validationmessage,friendTodayStat,detectStat,AmoyDevice,"
                    + "SendBillDevice,sendMsgSetting,aso,appleIdMaterial,appCommentMaterial,mobileParam,asoStatistics,groupMaterial,IMAccount,imContactMaterial,payMaterial,receiveInfoMaterial,pddAccountList,"
                    + "pddComment,addPddAccountInfoGroup,pddAccountinfoGroupList,QQAccountInfoList,addQQAccountInfoGroup,QQAccountInfoGroupList,nameAuthenticationMaterialList,"
                    + "pddOrderList,QQKSongAccountInfoList,addQQKSongAccountInfoGroup,QQKSongAccountInfoGroupList,qqLoginMaterial,birthdayMaterial,qqKSongMaterial,supplementOrderList,collectGoodsList,"
                    + "qqDetectedList,qqAccountList,qqKSongQQLoginMaterial,qqKSongFriendStat,qqKsongProductionMaterial,qqKSongTitleMaterial,qqKSongCommentMaterial,qqKSongForwardMaterial,DouYinAccountInfoList,addDouYinAccountInfoGroup,DouYinAccountInfoGroupList,douYinAccountInfoMaterial,zfbLoginMaterial,qqKSongSendMessage";
        }

        String[] permissionList = StringUtils.split(roles, ",");
        if (this.eulaAgreed) {
            List<String> list = Lists.newArrayList(permissionList);
            list.add("AGREE_EULA");
            permissionList = new String[list.size()];
            permissionList = list.toArray(permissionList);
        }
        return permissionList;
    }

    /**
     * 判断是否为超级用户
     */
    public boolean isSuperUser() {
        return this.getLoginName().equals("admin");
    }
}
