# 浏览器语言自动检测功能说明

## 功能概述

系统现在支持根据用户浏览器语言自动切换到对应的语言版本，提供更好的用户体验。

## 支持的语言

### 中文语言变体
- `zh` - 中文通用
- `zh-CN` - 简体中文（中国大陆）
- `zh-TW` - 繁体中文（台湾）
- `zh-HK` - 繁体中文（香港）
- `zh-SG` - 中文（新加坡）
- `zh-MO` - 中文（澳门）

**检测到任何中文变体时，系统将显示：中文界面 (zh_CN)**

### 英文语言变体
- `en` - 英文通用
- `en-US` - 英语（美国）
- `en-GB` - 英语（英国）
- `en-AU` - 英语（澳大利亚）
- `en-CA` - 英语（加拿大）
- `en-IN` - 英语（印度）
- `en-NZ` - 英语（新西兰）
- `en-ZA` - 英语（南非）
- `en-IE` - 英语（爱尔兰）

**检测到任何英文变体时，系统将显示：English Interface (en_US)**

### 其他语言
- 对于不属于中文或英文的其他语言，**系统默认显示：English Interface (en_US)**

## 工作原理

### 语言检测优先级
1. **Cookie中的用户选择** - 如果用户之前手动选择过语言，系统优先使用Cookie中保存的语言设置
2. **浏览器语言自动检测** - 如果没有Cookie，系统自动检测浏览器语言设置
3. **默认语言** - 如果浏览器语言不是中文或英文，系统使用默认的英文界面

### 检测流程
```
用户访问网站
    ↓
检查Cookie中是否有语言设置
    ↓
    有 → 使用Cookie中的语言设置
    ↓
    无 → 检测浏览器语言
    ↓
    检测结果判断
    ↓
    中文变体 → 显示中文界面 (zh_CN)
    英文变体 → 显示英文界面 (en_US)
    其他语言 → 显示英文界面 (en_US)
```

## 调试和测试

### 新增的调试方法

#### 1. 获取语言检测信息
```javascript
const detectionInfo = I18nConfig.getDetectionInfo();
console.log(detectionInfo);
```

返回的信息包括：
- `browserLanguage` - 检测到的浏览器语言
- `detectedLocale` - 系统检测到的语言代码
- `currentLocale` - 当前使用的语言
- `cookieLanguage` - Cookie中保存的语言设置
- `isChineseLocale` - 是否为中文语言变体
- `isEnglishLocale` - 是否为英文语言变体

#### 2. 手动重新检测语言
```javascript
I18nConfig.redetectLanguage();
```

这个方法会删除现有的语言Cookie，重新检测浏览器语言，并在检测到变化时自动切换。

#### 3. 获取当前语言显示名称
```javascript
const displayName = I18nConfig.getCurrentLocaleDisplayName();
console.log(displayName); // 输出: "中文" 或 "English"
```

### 测试页面

项目包含了专门的测试页面：`test-i18n-browser-detection.html`

在浏览器中打开这个页面可以：
- 查看当前浏览器的语言设置
- 查看语言检测结果
- 测试不同语言用户的体验
- 查看详细的系统状态信息

## 如何测试不同语言

### 修改浏览器语言进行测试

#### Chrome浏览器
1. 打开设置 → 语言
2. 添加新的语言
3. 将目标语言移动到顶部
4. 重启浏览器
5. 访问网站查看效果

#### Firefox浏览器
1. 在地址栏输入 `about:config`
2. 搜索 `intl.accept_languages`
3. 修改值为目标语言（如 `zh-CN,zh` 或 `en-US,en`）
4. 重启浏览器测试

#### Edge浏览器
1. 设置 → 语言
2. 添加语言并设为默认
3. 重启浏览器测试

### 模拟测试
使用测试页面中的按钮功能：
- "模拟中文用户" - 查看中文用户应该看到的界面
- "模拟英文用户" - 查看英文用户应该看到的界面
- "模拟法语用户" - 查看其他语言用户默认看到的英文界面

## 技术实现细节

### 核心检测逻辑
```javascript
detectBrowserLanguage() {
    const browserLang = (navigator.language || navigator.userLanguage).toLowerCase();

    if (this.isChineseLocale(browserLang)) {
        return 'zh_CN';
    }

    if (this.isEnglishLocale(browserLang)) {
        return 'en_US';
    }

    // 其他语言默认使用英文
    return 'en_US';
}
```

### 语言判断方法
```javascript
// 中文语言判断
isChineseLocale(locale) {
    const chineseLocales = ['zh', 'zh-CN', 'zh-TW', 'zh-HK', 'zh-SG', 'zh-MO'];
    return chineseLocales.some(chineseLocale =>
        locale.toLowerCase().startsWith(chineseLocale.toLowerCase())
    );
}

// 英文语言判断
isEnglishLocale(locale) {
    const englishLocales = ['en', 'en-US', 'en-GB', 'en-AU', 'en-CA', 'en-IN', 'en-NZ', 'en-ZA', 'en-IE'];
    return englishLocales.some(englishLocale =>
        locale.toLowerCase().startsWith(englishLocale.toLowerCase())
    );
}
```

## 用户语言切换

用户仍然可以手动切换语言：
1. 点击页面右上角的语言切换器
2. 选择中文或English
3. 系统会记住用户的选择（保存在Cookie中）
4. 下次访问时使用用户选择的语言，忽略浏览器语言检测

## 日志和调试

系统在控制台输出详细的检测日志：

```
Detected browser language: zh-CN
Detected Chinese locale, using zh_CN
Detected and saved browser language: zh_CN
I18n initialized with locale: zh_CN
```

这些日志有助于调试和验证语言检测功能是否正常工作。

## 注意事项

1. **Cookie优先级**：如果用户之前手动选择过语言，系统会优先使用Cookie中的设置，而不是浏览器语言检测

2. **首次访问**：只有在用户首次访问网站或清除了Cookie时，才会进行浏览器语言自动检测

3. **默认语言**：系统默认语言现在改为英文，而不是中文

4. **兼容性**：支持所有现代浏览器的语言检测功能

## 总结

现在系统能够智能地根据用户浏览器语言自动切换界面语言，为不同语言背景的用户提供更好的使用体验。中文用户看到中文界面，英文用户看到英文界面，其他语言用户看到默认的英文界面。

用户仍然保留手动选择语言的权利，系统会尊重用户的选择并优先使用。