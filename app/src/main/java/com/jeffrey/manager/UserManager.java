package com.jeffrey.manager;

import com.jeffrey.module.user.User;
import com.jeffrey.view.fragment.home.MineFragment;

/**
 * Created by Jun.wang on 2018/10/7.
 */

//单例管理登录用户信息
public class UserManager {
    private  static UserManager userManager = null;
    private User user = null;

    public static UserManager getInstance() {
        if (userManager == null) {
            synchronized (UserManager.class) {
                if (userManager == null) {
                    userManager = new UserManager();
                }
                return userManager;
            }
        } else {
            return userManager;
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean hasLogined() {
        return user == null ? false : true;
    }

    public User getUser() {
        return this.user;
    }

    public void removeUser() {
        this.user = null;
    }
}















