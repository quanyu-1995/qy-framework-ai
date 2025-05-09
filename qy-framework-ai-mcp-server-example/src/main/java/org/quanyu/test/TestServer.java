package org.quanyu.test;

import org.quanyu.ai.mcp.server.Tool;
import org.springframework.stereotype.Service;

@Service
public class TestServer {

    @Tool(desc = "加法")
    public int add(int a, String b) {
        return a + Integer.parseInt(b);
    }
}
