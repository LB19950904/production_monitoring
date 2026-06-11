@echo off
:: 进入上级目录，也就是项目根目录
cd ..
:: 创建日志目录
if not exist "liblog4phoenix" mkdir "liblog4phoenix"
:: 使用 PowerShell 同时输出到屏幕和文件
powershell -Command "mvn javadoc:aggregate -DskipTests=true \"-Djavadoc.failOnError=false\" 2>&1 | Tee-Object -FilePath 'liblog4phoenix/javadoc.log'"
:: 切换到 UTF-8 编码，避免中文乱码
chcp 65001 >nul
echo 日志已保存到: liblog4phoenix/javadoc.log
@pause