# qy-framework-ai

## 结构
- qy-framework-ai-helper: 基础对话包，集成多种大模型，支持流式输出
- qy-framework-ai-mcp-client: MCP Client扩展包
- qy-framework-ai-mcp-server: MCP Server扩展包
- qy-framework-ai-example: 对话示例（含MCP Client示例）
- qy-framework-ai-mcp-server-example: MCP Server示例
## 配置说明
### qy-framework-ai-helper
```yaml
quanyu:
  ai:
    # 策略: openai / ollama
    strategy: ollama
    config:
      # deepseek API示例
      #      chat-url: https://api.deepseek.com/chat/completions
      #      api-key: <你的api key>
      #      model: deepseek-chat
      # qwen3 API示例
      #      chat-url: https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
      #      api-key: <你的api key>
      #      model: qwen3-235b-a22b
      # ollama示例
      chat-url: http://localhost:11434/api/chat
      model: qwen3:8b
```