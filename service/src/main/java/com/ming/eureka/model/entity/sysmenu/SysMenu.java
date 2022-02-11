package com.ming.eureka.model.entity.sysmenu;

import com.ming.eureka.model.entity.IdEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "b_menu_sys")
public class SysMenu extends IdEntity implements Serializable {

    private static final long serialVersionUID = -4410164389105625568L;

    /**
     * 菜单状态:0-隐藏
     */
    public static final int STATUS_HIDE = 0;

    /**
     * 菜单状态:1-启用
     */
    public static final int STATUS_ALLOW = 1;

    /**
     * 菜单状态:-1 -禁用
     */
    public static final int STATUS_FORBIDDEN = -1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    @NotFound(action = NotFoundAction.IGNORE)
    private SysMenu parentMenu;

    private int level;

    private String name;

    private String cssClass;

    private String uri;

    private int sort;

    private Date addTime;

    private int status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parentMenu", targetEntity = SysMenu.class)
    @OrderBy("sort asc")
    private List<SysMenu> childMenus;

    @Override
    public void prepareForInsert() {
        // TODO Auto-generated method stub

    }

}
