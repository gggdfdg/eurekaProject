package com.ming.eureka;

/**
 * 返回结果值
 * 
 * @author wengzc 2015年1月5日
 */
public class ResultCode {
	
	/** 操作成功 */
	public static final int CODE_SUCC = 0;
	public final static int CODE_CREATE_ERROR = 9001;	//增加失败
	public final static int CODE_UPDATE_ERROR = 9002;	//修改失败
	public final static int CODE_DELE_ERROR = 9003;		//删除失败
	public final static int CODE_NOTFOUND_ERROR = 9004;	//找不到
	public final static int CODE_COMM_ERROR = 9005;		//其他自定义
	public final static int CODE_REPEAT_ERROR = 9006;	//重复操作
	public final static int CODE_VALITE_ERROR = 9007;	//参数状态校验错误
	public final static int CODE_NIL_ERROR = 9008; // 空对象	
	
	public final static String MSG_CREATE_ERROR = "增加失败";
	public final static String MSG_UPDATE_ERROR = "修改失败";
	public final static String MSG_DELE_ERROR = "删除失败";
	public final static String MSG_NOTFOUND_ERROR = "查询失败";
	public final static String MSG_COMM_ERROR = "未知错误";
	public final static String MSG_REPEAT_ERROR = "请勿重复操作";
	public final static String MSG_VALITE_ERROR = "参数状态校验错误";
	public final static String MSG_NIL_ERROR = "空对象";
	
	/**
	 * 权限相关错误号
	 */
	public final static int CODE_PERMISSION_ERROR = 2001;
	public final static String MSG_PERMISSION_ERROR = "非法操作";
	/** 认证失败，密码错误*/
	public final static int CODE_PERMISSION_PASS_ERROR = 2002;
	
	//登录
	/** 密码错误 */
	public static final int CODE_COMMON_LOGIN_PWD_ERROR = 1101;
	/** 账号被禁用 */
	public static final int CODE_USER_FORBIDDEN = 1102;
	/** 登录失效 重新登录 */
	public static final int CODE_LOGIN_TRYAGAIN = 1105;
	
}
