# Notify Spring Boot Starter 使用指南

## 1. 添加依赖

在你的项目 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.gushan</groupId>
    <artifactId>notify-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 2. NotifyUtils 工具类使用

### 2.1 消息模板格式化

```java
String template = "用户{username}在{time}登录失败";
String message = NotifyUtils.formatMessage(template, 
    "username", "admin",
    "time", "2024-01-01 12:00:00");
// 输出：用户admin在2024-01-01 12:00:00登录失败
```

### 2.2 邮箱格式验证

```java
boolean isValid = NotifyUtils.isValidEmail("test@example.com");
if (!isValid) {
    throw new IllegalArgumentException("邮箱格式不正确");
}
```

### 2.3 手机号格式验证

```java
boolean isValid = NotifyUtils.isValidPhoneNumber("13800138000");
if (!isValid) {
    throw new IllegalArgumentException("手机号格式不正确");
}
```

### 2.4 异常忽略判断

```java
if (NotifyUtils.shouldIgnore(exception, notifyProperties)) {
    return; // 忽略该异常
}
```

## 3. 配置通知参数

在 `application.yml` 中添加配置：

```yaml
notify:
  # 配置需要监控的包路径
  scan-packages: 
    - com.example.controller
    - com.example.service
  
  # 配置通知阈值和频率
  threshold: 3        # 异常发生3次后才通知
  frequency: 60       # 同一异常60秒内只通知一次
  
  # 配置需要排除的异常
  exclude-exceptions:
    - org.springframework.web.bind.MethodArgumentNotValidException
    - com.example.exception.BusinessException
  
  # 配置至少一个通知渠道
  dingtalk:
    enabled: true
    webhook: https://oapi.dingtalk.com/robot/send?access_token=xxx
    secret: your-secret
```

## 4. 使用方式

### 4.1 自动异常通知

starter 会自动捕获配置的包路径下的异常并发送通知，无需额外代码：

```java
@RestController
@RequestMapping("/api")
public class UserController {
    
    @GetMapping("/users")
    public List<User> getUsers() {
        throw new RuntimeException("数据库连接失败");  // 这个异常会被自动捕获并通知
    }
}
```

### 4.2 SQL超时监控

starter 提供了SQL执行超时监控功能，可以自动捕获并通知执行时间超过阈值的SQL语句：

```yaml
notify:
  sql-monitor:
    enabled: true                # 启用SQL监控
    timeout: 3000               # SQL执行超时阈值（毫秒）
    include-full-sql: true      # 是否在通知中包含完整SQL语句
    include-params: true        # 是否在通知中包含SQL参数
```

当SQL执行时间超过配置的阈值时，会自动触发通知：

```java
@Service
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<User> getUsers() {
        // 如果这个查询超过3000ms，将自动触发通知
        return jdbcTemplate.query("SELECT * FROM users", new UserRowMapper());
    }
}
```

## 5. 最佳实践

### 5.1 合理使用工具类

```java
// 在发送通知前验证接收者信息
if (!NotifyUtils.isValidEmail(recipientEmail)) {
    throw new IllegalArgumentException("无效的邮箱地址");
}

// 格式化通知内容
String message = NotifyUtils.formatMessage(
    "用户{username}在{time}登录失败",
    "username", username,
    "time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
);
```

### 5.2 自定义异常处理

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleException(Exception ex) {
    if (NotifyUtils.shouldIgnore(ex, notifyProperties)) {
        return ResponseEntity.ok().build();
    }
    
    // 其他处理逻辑
}
```

## 6. 常见问题

### 6.1 如何测试工具类？

```java
@Test
void testFormatMessage() {
    String result = NotifyUtils.formatMessage("Hello {name}", "name", "World");
    assertEquals("Hello World", result);
}

@Test
void testIsValidEmail() {
    assertTrue(NotifyUtils.isValidEmail("test@example.com"));
    assertFalse(NotifyUtils.isValidEmail("invalid-email"));
}
```

### 6.2 如何处理国际化消息？

```java
String template = messageSource.getMessage("login.failed", null, locale);
String message = NotifyUtils.formatMessage(template, 
    "username", username,
    "time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
```

## 7. 缓存统计

启用缓存统计功能后，可以监控以下指标：
- 缓存命中率
- 缓存加载时间
- 缓存淘汰数量
- 缓存大小

配置示例：
```yaml
notify:
  template-cache:
    enabled: true
    max-size: 1000
    expire-after-write: 3600
    stats-enabled: true  # 启用缓存统计
```

## 8. 监控建议

1. 监控通知发送状态
2. 设置异常数量告警
3. 定期分析异常趋势
4. 关注通知延迟情况
5. 监控缓存命中率和性能
