# 学习笔记-ControllerAdvice的使用
 
------

首先介绍下本章学习的内容：
 
> * 学习笔记-ControllerAdvice的使用

## 学习笔记-ControllerAdvice的使用
    这是个全局参数的增强类，美其名曰控制器增强
    
    /**
     * 全局参数sysName
     * @return
     */
    @ModelAttribute("sysName")
    public String getSysName() {
        return this.sysName;
    }
 这里写了@ModelAttribute("sysName")，代表前端在任何地方都可以直接使用 ${sysName}
