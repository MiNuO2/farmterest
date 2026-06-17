package com.farmterest.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DBCP 커넥션 풀에서 커넥션을 빌려주는 유틸리티.
 * context.xml 의 jdbc/farmterest 자원을 JNDI 로 1회 조회해 재사용한다.
 */
public class DBManager {

    private static DataSource dataSource;

    static {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/farmterest");
        } catch (NamingException e) {
            throw new RuntimeException("DataSource(jdbc/farmterest) 조회 실패", e);
        }
    }

    /** 풀에서 커넥션 대여 (close 시 풀로 반환). */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /** ResultSet/Statement/Connection 등을 한 번에 안전하게 닫는다. */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignore) {
                    // 닫기 실패는 무시
                }
            }
        }
    }
}
