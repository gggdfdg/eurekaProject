package com.ming.eureka.model.entity.user;


public interface IUser<T> {
    public enum State {
        NORMAL("正常"), BLOCKED("冻结"), DELETED("删除");
        public String desc;

        private State(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return this.desc;
        }
    }

    Long getId();

    void setId(Long id);

    String getLoginName();

    void setLoginName(String loginName);

    String getPlainPassword();

    String getPassword();

    void setPassword(String password);

    String getSalt();

    void setSalt(String salt);

    State getState();

    String[] getPermissionList();

    default boolean isEnable() {
        return getState() == State.NORMAL;
    }

}
