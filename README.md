### 什么的aar
在Java开发中库的形式一般会以jar包的形式提供，但是jar包只能包含代码文件。在Android开发中经常会涉及到图片、布局等资源，这个时候aar文件可以把这些资源统一打包进来。
### 使用aar的场景
- 在开发过程中有某几个项目特别频繁的使用到某些类或者资源的时候，可以将这些频繁使用的抽取出来放在aar里面，方便管理和使用。
- 在需要集成如地图，支付等等第三方项目时由于jar过多放在主项目中不美观又不好升级维护，所以此时可以通过另外一个project生成aar的方式进行引用，便于管理和维护升级jar。
