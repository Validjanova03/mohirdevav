package com.company.management.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @Around("execution(* com.company.management.controller.*.*(..))")
    public Object auditControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String username   = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String httpMethod = getHttpMethod();
        String endpoint   = getEndpoint();
        String args       = formatArgs(joinPoint.getArgs());

        String crudType = resolveCrudType(httpMethod, methodName);
        String table    = resolveTable(className);

        log.info("[AUDIT] user={} | table={} | endpoint={} | http={} | method={} | crud={} | args={} | time={}",
                username, table, endpoint, httpMethod, methodName, crudType, args, LocalDateTime.now());

        Object result = joinPoint.proceed();

        log.info("[AUDIT] user={} | method={} | COMPLETED successfully", username, methodName);

        return result;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "anonymous";
        return auth.getName();
    }

    private String getHttpMethod() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attrs.getRequest().getMethod();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String getEndpoint() {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return request.getRequestURI();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        // Don't log password or sensitive raw objects fully
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String str = arg.toString();
                    // Truncate very long strings
                    return str.length() > 200 ? str.substring(0, 200) + "..." : str;
                })
                .toList()
                .toString();
    }

    private String resolveCrudType(String httpMethod, String methodName) {
        return switch (httpMethod.toUpperCase()) {
            case "GET"    -> "READ";
            case "POST"   -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> methodName.toUpperCase();
        };
    }

    private String resolveTable(String className) {
        if (className.contains("Employee"))      return "employees";
        if (className.contains("Customer"))      return "customers";
        if (className.contains("Advertisement")) return "advertisements";
        if (className.contains("Auth"))          return "auth";
        return className.toLowerCase();
    }
}
