# 学习笔记-加密BCryptPasswordEncoder
 
------

首先介绍下本章学习的内容：
 
> * BCryptPasswordEncoder加密配合security的导入
> * BCryptPasswordEncoder是否有解密
> * BCryptPasswordEncoder如何加密个原文密码

## BCryptPasswordEncoder加密配合security的导入
    /**
     * security加密使用的算法
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
     /**
     * security配置加密的方式和登录处理类
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(
                passwordEncoder());
    }
## BCryptPasswordEncoder是否有解密
BCryptPasswordEncoder不存在解密的说法，是会把你的钥匙放进去，按自己的规则进行匹配破解
所以不存在解密，也不会容易被黑客破解

## BCryptPasswordEncoder如何加密个原文密码
  ```
  public static void main(String args[]){
        String encodeStr = DigestUtils.md5DigestAsHex("abc1233".getBytes());
        System.out.println(BCrypt.hashpw(encodeStr, BCrypt.gensalt()));
   }
   ```
   在security中，会把密码进行md5加密，再进行bcript加密
 