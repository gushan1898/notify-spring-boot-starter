# 自定义异常处理示例

## 1. 自定义异常类

```java
@NotifyIgnore(reason = "业务异常无需通知")
public class BusinessException extends RuntimeException {
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
```

## 2. 自定义通知模板

```yaml
notify:
  template:
    business: |
      【业务异常通知】
      异常代码：${exception.code}
      异常信息：${exception.message}
      发生时间：${timestamp}
      环境：${env}
      
    system: |
      【系统异常通知】
      异常类型：${exception.type}
      异常信息：${exception.message}
      堆栈信息：${exception.stackTrace}
      发生时间：${timestamp}
      环境：${env}
```

## 3. 自定义通知渠道

```java
@Component
@ConditionalOnProperty(prefix = "notify.custom", name = "enabled", havingValue = "true")
public class CustomNotifySender implements NotifySender {
    private final RestTemplate restTemplate;
    private final String webhookUrl;
    
    public CustomNotifySender(RestTemplate restTemplate, 
                            @Value("${notify.custom.webhook-url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }
    
    @Override
    public void send(Exception exception) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", exception.getClass().getSimpleName());
        payload.put("message", exception.getMessage());
        payload.put("timestamp", LocalDateTime.now());
        
        restTemplate.postForEntity(webhookUrl, payload, String.class);
    }
}
```

## 4. 自定义异常过滤器

```java
@Component
public class CustomExceptionFilter implements ExceptionFilter {
    @Override
    public boolean shouldNotify(Exception exception) {
        // 忽略特定包下的异常
        if (exception.getStackTrace()[0].getClassName().startsWith("com.example.internal")) {
            return false;
        }
        
        // 忽略特定异常类型
        if (exception instanceof IllegalArgumentException) {
            return false;
        }
        
        return true;
    }
}
```

## 5. 集成到 Spring MVC

```java
@RestControllerAdvice
public class GlobalExceptionAdvice {
    private final GlobalExceptionHandler exceptionHandler;
    
    public GlobalExceptionAdvice(GlobalExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // 发送异常通知
        exceptionHandler.handleException(e);
        
        // 返回错误响应
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("服务器内部错误");
    }
}
```

## 6. 测试示例

```java
@SpringBootTest
class NotifyTest {
    @Autowired
    private GlobalExceptionHandler exceptionHandler;
    
    @Test
    void testExceptionNotification() {
        try {
            // 模拟业务操作
            throw new RuntimeException("测试异常");
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
}
```

## 7. 配置示例

```yaml
notify:
  # 基础配置
  scan-packages:
    - com.example.controller
    - com.example.service
  threshold: 3
  frequency: 60
  include-stack-trace: true
  
  # 邮件通知
  email:
    enabled: true
    host: smtp.example.com
    port: 465
    username: sender@example.com
    password: your-password
    properties:
      mail.smtp.ssl.enable: true
    from: sender@example.com
    to:
      - admin@example.com
      
  # 钉钉通知
  dingtalk:
    enabled: true
    webhook: https://oapi.dingtalk.com/robot/send?access_token=xxx
    secret: your-secret
    at-mobiles:
      - 13800138000
      
  # 自定义通知
  custom:
    enabled: true
    webhook-url: https://api.example.com/notify
``` 