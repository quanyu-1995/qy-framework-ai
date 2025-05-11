# qy-framework-ai

## 结构
- qy-framework-ai-mcp-client: 基础对话包，集成多种大模型，支持流式输出，内置MCP Client
- qy-framework-ai-mcp-server: MCP Server扩展包
- qy-framework-ai-example: 对话示例（含MCP Client示例）
- qy-framework-ai-mcp-server-example: MCP Server示例
## 配置说明
### qy-framework-ai-client
```yaml
# 端口配置
server:
  port: 8080
# qy-AI框架相关配置
quanyu:
  ai:
    # 策略: openai / ollama
    strategy: openai
    config:
      # deepseek API示例
      #      chat-url: https://api.deepseek.com/chat/completions
      #      api-key: <你的API KEY>
      #      model: deepseek-chat
      # qwen3 API示例
      chat-url: https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
      api-key: <你的API KEY>
      model: qwen3-235b-a22b
      # ollama示例
      #      chat-url: http://localhost:11434/api/chat
      #      model: qwen3:8b
    mcp:
      client:
        # mcp服务端url
        base-url: http://localhost:8090
# 默认缓存策略所需配置（DefaultCacheStrategy）
spring:
  data:
    redis:
      # 连接地址
      host: "localhost"
      # 端口
      port: 6379
      # 数据库
      database: 0
      # 用户名，如果有
      # username:
      # 密码，如果有
      # password:
      # 连接超时
      connect-timeout: 5s
      # 读超时
      timeout: 5s
      # Lettuce 客户端的配置
      lettuce:
        # 连接池配置
        pool:
          # 最小空闲连接
          min-idle: 0
          # 最大空闲连接
          max-idle: 8
          # 最大活跃连接
          max-active: 8
          # 从连接池获取连接 最大超时时间，小于等于0则表示不会超时
          max-wait: -1ms
```

### qy-framework-ai-server
```yaml
# 端口配置
server:
  port: 8080
```
