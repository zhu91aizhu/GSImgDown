package com.jpycrgo.gsimgdown;

/**
 * 代理设置类
 * @author mengzx
 * @date 2016/4/28
 * @since 1.0.0
 */
public class ProxyConfig {

    private final String host;
    private final int port;
    private final String proxyType;
    private final String username;
    private final String password;

    private ProxyConfig(Builder builder) {
        host = builder.host;
        port = builder.port;
        proxyType = builder.proxyType;
        username = builder.username;
        password = builder.password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProxyType() {
        return proxyType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {
        private String host;
        private int port = 80;
        private String proxyType = "http";
        private String username;
        private String password;

        public Builder(String host) {
            this.host = host;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder proxyType(String proxyType) {
            this.proxyType = proxyType;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public ProxyConfig build() {
            return new ProxyConfig(this);
        }
    }

}
