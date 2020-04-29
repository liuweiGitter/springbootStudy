package com.jshx.zq.p2p.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.lang.Nullable;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.time.Duration;
import java.util.Optional;

/**
 * @author liuwei
 * @date 2020-03-10 15:22
 * @desc redis单机客户端配置类
 * spring的JedisConnectionFactory针对redis单机的配置比较简单，很多细节不能通过现有的实现类配置
 * 自定义接口的实现，用以在单机redis中使用spring的JedisConnectionFactory进行详细配置
 * 主要用以封装jedisPoolConfig
 */
public class JsStandClientConfig implements JedisClientConfiguration {
    private boolean useSsl;
    @Nullable
    private SSLSocketFactory sslSocketFactory;
    @Nullable
    private SSLParameters sslParameters;
    @Nullable
    private HostnameVerifier hostnameVerifier;
    private boolean usePooling = true;
    private GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
    @Nullable
    private String clientName;
    private Duration readTimeout = Duration.ofMillis(2000L);
    private Duration connectTimeout = Duration.ofMillis(2000L);

    private JsStandClientConfig() {
    }

    public static JedisClientConfiguration create(JedisShardInfo shardInfo) {
        JsStandClientConfig configuration = new JsStandClientConfig();
        configuration.setShardInfo(shardInfo);
        return configuration;
    }

    public static JedisClientConfiguration create(GenericObjectPoolConfig jedisPoolConfig) {
        JsStandClientConfig configuration = new JsStandClientConfig();
        configuration.setPoolConfig(jedisPoolConfig);
        return configuration;
    }

    public boolean isUseSsl() {
        return this.useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public Optional<SSLSocketFactory> getSslSocketFactory() {
        return Optional.ofNullable(this.sslSocketFactory);
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public Optional<SSLParameters> getSslParameters() {
        return Optional.ofNullable(this.sslParameters);
    }

    public void setSslParameters(SSLParameters sslParameters) {
        this.sslParameters = sslParameters;
    }

    public Optional<HostnameVerifier> getHostnameVerifier() {
        return Optional.ofNullable(this.hostnameVerifier);
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public boolean isUsePooling() {
        return this.usePooling;
    }

    public void setUsePooling(boolean usePooling) {
        this.usePooling = usePooling;
    }

    public Optional<GenericObjectPoolConfig> getPoolConfig() {
        return Optional.ofNullable(this.poolConfig);
    }

    public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public Optional<String> getClientName() {
        return Optional.ofNullable(this.clientName);
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Duration getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setShardInfo(JedisShardInfo shardInfo) {
        this.setSslSocketFactory(shardInfo.getSslSocketFactory());
        this.setSslParameters(shardInfo.getSslParameters());
        this.setHostnameVerifier(shardInfo.getHostnameVerifier());
        this.setUseSsl(shardInfo.getSsl());
        this.setConnectTimeout(Duration.ofMillis((long) shardInfo.getConnectionTimeout()));
        this.setReadTimeout(Duration.ofMillis((long) shardInfo.getSoTimeout()));
    }
}
