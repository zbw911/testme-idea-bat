# 为什么
 testme 本身是一款足够优秀的单元测试自动生成工具，在这里感谢原作者。
 
 有一些情况，我们需要批量生成单元测试。在此基础上加入批量生成功能。 
 
 当前只支持本地硬盘安装插件方式。
 
 使用 方法 ：
 tools -> 批量生成测试

 

 注意 AGPLv3 ，不要变成所谓的自主产权，大家注意。
 
 Example：
 ![alt example](https://github.com/zbw911/testme-idea-bat/blob/master/Example.png)

下面是作者原文.

# TestMe IJ IDEA Plugin
[![Build Status](https://travis-ci.org/wrdv/testme-idea.svg?branch=master)](https://travis-ci.org/wrdv/testme-idea)
[![Coverage Status](https://coveralls.io/repos/github/wrdv/testme-idea/badge.svg?branch=master)](https://coveralls.io/github/wrdv/testme-idea?branch=master)

Auto Generate Unit Tests in Java, Groovy or Scala.
No more boilerplate!

### Main Features
- Auto generate Java, Scala or Groovy test code with JUnit 4/5, TestNG, Spock or Specs2 frameworks
- Auto generate Mockito mocks
- Generate test params and assertion statements
- Generate relevant mocked return statements
- Integrates with IDEA menus: Code->TestMe, Code->Generate

For a more detailed documentation, please refer to http://weirddev.com/testme/

### Installation/Usage
Installation available from JetBrains plugins repository:
1. From IDEA menu: `Preferences` (`Ctrl`+`Shift`+`S`) -> `Plugins` -> `Browse repositories...` -> Search: `TestMe` -> `Install Plugin`
2. Restart IDEA.


### Issue Tracking
You're welcome to report issues and raise feature requests at [TestMe project forum](http://weirddev.com/forum#!/testme)

### Contributing/Developing
Please refer to [`CONTRIBUTING.md`](./CONTRIBUTING.md) file.


### License
Copyright (c) 2017 - 2019 [WeirdDev](http://weirddev.com). Licensed for free usage under the terms and conditions of AGPLv3 - [GNU Affero General Public License v3](https://www.gnu.org/licenses/agpl-3.0.en.html).
