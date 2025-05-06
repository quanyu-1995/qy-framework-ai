package org.quanyu.ai.mcp.server;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McpParameter {
    String desc() default "";
    boolean required() default true;
}