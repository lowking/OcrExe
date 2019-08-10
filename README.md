[![License](http://img.shields.io/:license-apache-blue.svg "2.0")](http://www.apache.org/licenses/LICENSE-2.0.html)
[![JDK 1.8](https://img.shields.io/badge/JDK-1.8-green.svg "JDK 1.8")]()
[![License](http://img.shields.io/:license-Tess4j-purple.svg "")](https://github.com/nguyenq/tess4j/blob/master/LICENSE)
## OcrExe
- 使用开源的Tess4j实现简单的ocr文字识别

## 使用说明
> - 可直接启动MainFrame.main()
> - 如果想打包成exe,可以使用/src/main/resources/ocr.exe4j配置打包(本人使用exe4j,可自行选择打包工具)
> - 支持启动参数选择外观主题  
> - 支持截图识别(支持全局快捷键,默认Ctrl+Alt+E)  
>   目前支持以下几种参数:  
>   - mac: mac主题风格,使用quaqua.jar  
>   - metal: jdk自带  
>   - nimbus: jdk自带
>   - weblaf: 第三方主题包[weblaf](https://github.com/mgarin/weblaf)  
>   - beautyeye: 第三方主题包[beautyeye](https://github.com/JackJiang2011/beautyeye)