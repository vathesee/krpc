package krpc.rpc.bootstrap.springboot;

import krpc.rpc.bootstrap.*;
import krpc.rpc.bootstrap.spring.RefererFactory;
import krpc.rpc.bootstrap.spring.SpringBootstrap;
import krpc.rpc.core.DumpPlugin;
import krpc.rpc.core.HealthPlugin;
import krpc.rpc.core.RefreshPlugin;
import krpc.rpc.monitor.SelfCheckHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(BootProperties.class)
@ConditionalOnClass(Bootstrap.class)
@ConditionalOnProperty(prefix = "krpc", value = "enabled", matchIfMissing = false)
public class AutoConfiguration implements ApplicationListener<ApplicationEvent> {

    static Logger log = LoggerFactory.getLogger(AutoConfiguration.class);

    @Bean
    static public BeanFactoryPostProcessor postProcessor() {
        return new BootPostProcessor();
    }

    private boolean checkBeanExisted(String beanName, ApplicationContext context) {
        try {
            Object bean = context.getBean(beanName);
            return bean != null;
        } catch (Throwable e) {
            return false;
        }
    }

    private boolean checkConsulEnabled(Environment environment, ApplicationContext context) {
        if (!checkBeanExisted("consulClient", context) || !checkBeanExisted("consulProperties", context)) return false;

        String host = environment.getProperty("spring.cloud.consul.host", "localhost");
        String port = environment.getProperty("spring.cloud.consul.port", "8500");
        String addr = host + ":" + port;

        RegistryConfig c = new RegistryConfig();
        c.setType("consul");
        c.setAddrs(addr);

        Bootstrap bootstrap = SpringBootstrap.instance.getBootstrap();
        bootstrap.addRegistry(c);
        return true;
    }

    private boolean checkZooKeeperEnabled(Environment environment, ApplicationContext context) {
        if (!checkBeanExisted("curatorFramework", context) || !checkBeanExisted("zookeeperProperties", context))
            return false;

        String addr = environment.getProperty("spring.cloud.zookeeper.connectString", "localhost:2181");

        RegistryConfig c = new RegistryConfig();
        c.setType("zookeeper");
        c.setAddrs(addr);

        Bootstrap bootstrap = SpringBootstrap.instance.getBootstrap();
        bootstrap.addRegistry(c);
        return true;
    }

    @Bean
    @ConditionalOnMissingBean(RpcApp.class)
    public RpcApp rpcApp(BootProperties bootProperties, Environment environment, ApplicationContext context) {
        SpringBootstrap.instance.spring = (ConfigurableApplicationContext) context;

        Bootstrap bootstrap = SpringBootstrap.instance.getBootstrap();
        bootstrap.setTwoPhasesBuild(true);

        if (bootProperties.application != null) {
            bootstrap.setAppConfig(bootProperties.application);
        }

        String springAppName = environment.getProperty("spring.application.name");
        if (!isEmpty(springAppName)) {
            bootstrap.getAppConfig().setName(springAppName);
        }

        if (bootProperties.monitor != null) {
            bootstrap.setMonitorConfig(bootProperties.monitor);
        }

        if (bootProperties.registry != null) {
            bootstrap.addRegistry(bootProperties.registry);
        }
        if (bootProperties.registries != null) {
            for (RegistryConfig c : bootProperties.registries)
                bootstrap.addRegistry(c);
        }

        if (bootstrap.getRegistryList().size() == 0) {
            boolean loaded = checkConsulEnabled(environment, context);
            if (!loaded) loaded = checkZooKeeperEnabled(environment, context);
        }

        if (bootProperties.server != null) {
            bootstrap.addServer(bootProperties.server);
        }
        if (bootProperties.servers != null) {
            for (ServerConfig c : bootProperties.servers)
                bootstrap.addServer(c);
        }

        if (bootProperties.client != null) {
            bootstrap.addClient(bootProperties.client);
        }
        if (bootProperties.clients != null) {
            for (ClientConfig c : bootProperties.clients)
                bootstrap.addClient(c);
        }

        if (bootProperties.webserver != null) {
            bootstrap.addWebServer(bootProperties.webserver);
        }
        if (bootProperties.webservers != null) {
            for (WebServerConfig c : bootProperties.webservers)
                bootstrap.addWebServer(c);
        }

        bootstrap.mergePlugins(SpringBootstrap.instance.loadSpiBeans());

        String profileGroup = environment.getProperty("spring.profiles.active");
        String forceGroup = environment.getProperty("krpc.registry.group");
        if( forceGroup != null ) {
            profileGroup = forceGroup;
        }
        log.info("krpc default group is " + profileGroup);

        if (bootProperties.referer != null) {
            RefererConfig c = bootProperties.referer;

            String group = c.getGroup();
            if (group == null || group.isEmpty()) {
                c.setGroup(profileGroup);
            }

            bootstrap.addReferer(c);
        }
        if (bootProperties.referers != null) {
            for (RefererConfig c : bootProperties.referers) {

                String group = c.getGroup();
                if (group == null || group.isEmpty()) {
                    c.setGroup(profileGroup);
                }

                bootstrap.addReferer(c);
            }
        }

        if (bootProperties.service != null) {
            ServiceConfig c = bootProperties.service;

            String group = c.getGroup();
            if (group == null || group.isEmpty()) {
                c.setGroup(profileGroup);
            }

            bootstrap.addService(c);
        }
        if (bootProperties.services != null) {
            for (ServiceConfig c : bootProperties.services) {

                String group = c.getGroup();
                if (group == null || group.isEmpty()) {
                    c.setGroup(profileGroup);
                }

                bootstrap.addService(c);
            }
        }

        RpcApp app = bootstrap.build();

        SpringBootstrap.instance.setRpcApp(app);

        return app;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConditionalOnMissingBean(RpcAppInitBean.class)
    public RpcAppInitBean rpcAppInitBean(RpcApp rpcApp, BootProperties bootProperties, Environment environment, ApplicationContext context) {

        Bootstrap bootstrap = SpringBootstrap.instance.getBootstrap();

        RpcAppInitBean  b = new RpcAppInitBean(bootstrap,rpcApp);

        List<ServiceConfig> cl = bootstrap.getServiceList();
        for( ServiceConfig c: cl) {
            String impl = c.getImpl() == null ? null : c.getImpl().toString();
            Object bean = loadBean(impl, c.getInterfaceName(), context);
            if (bean == null) throw new RuntimeException("bean not found for service " + c.getInterfaceName());
            c.setImpl(bean);
        }

        b.postBuild();

        startSelfCheckServer(rpcApp,context);
        return b;
    }

    public void startSelfCheckServer(RpcApp rpcApp, ApplicationContext context) {
        SelfCheckHttpServer selfCheckServer =  rpcApp.getSelfCheckHttpServer();
        if( selfCheckServer != null ) {
            Map<String, DumpPlugin> plugins1 = loadBeanByType(DumpPlugin.class, context);
            for(DumpPlugin o:plugins1.values() ) {
                selfCheckServer.addDumpPlugin(o);
            }
            Map<String, RefreshPlugin> plugins2 = loadBeanByType(RefreshPlugin.class, context);
            for(RefreshPlugin o:plugins2.values() ) {
                selfCheckServer.addRefreshPlugin(o);
            }
            Map<String, HealthPlugin> plugins3 = loadBeanByType(HealthPlugin.class, context);
            for(HealthPlugin o:plugins3.values() ) {
                selfCheckServer.addHealthPlugin(o);
            }
        }
    }

    <T> Map<String, T> loadBeanByType(Class<T> cls, ApplicationContext context) {
        return  context.getBeansOfType(cls);
    }

    public void onApplicationEvent(ApplicationEvent event) {
// System.out.println("boot onApplicationEvent called, event = " + event);
        if (event instanceof ContextRefreshedEvent) {
            int delayStart = SpringBootstrap.instance.getBootstrap().getAppConfig().getDelayStart();
            SpringBootstrap.instance.getRpcApp().start(delayStart);
        }
        if (event instanceof ContextClosedEvent) {
            SpringBootstrap.instance.getRpcApp().stop();
            log.info("krpc service port stopped");
        }
//System.out.println("boot onApplicationEvent called, event = " + event+" ended ----------- ");
    }

    Object loadBean(String impl, String interfaceName, BeanFactory beanFactory) {
        if (interfaceName == null) return null;

        String beanName;
        if (impl != null && !impl.isEmpty()) {
            beanName = impl;
        } else {
            int p = interfaceName.lastIndexOf(".");
            if (p < 0) return null;
            String name = interfaceName.substring(p + 1);
            beanName = name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        try {
            Object o = beanFactory.getBean(beanName);
            return o;
        } catch (Exception e1) {
            try {
                Object o = beanFactory.getBean(Class.forName(interfaceName));
                return o;
            } catch (Throwable e2) {
                return null;
            }
        }
    }

    boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    static class BootPostProcessor implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory0) throws BeansException {

            ServerConfig.DEFAULT_PORT = 0;
            WebServerConfig.DEFAULT_PORT = 0;

            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) beanFactory0;

            Environment environment = (Environment) beanFactory.getBean("environment");

            String s = environment.getProperty("krpc.referer.interfaceName");
            if (s != null) {
                String id = environment.getProperty("krpc.referer.id");
                registerReferer(id, s, beanFactory);
            }

            for (int i = 0; i < 10000; ++i) {
                s = environment.getProperty("krpc.referers[" + i + "].interfaceName");
                if (s != null) {
                    String id = environment.getProperty("krpc.referers[" + i + "].id");
                    registerReferer(id, s, beanFactory);
                } else break;
            }

        }

        void registerReferer(String id, String interfaceName, DefaultListableBeanFactory beanFactory) {
            String beanName = generateBeanName(id, interfaceName);
            //log.info("register referer "+interfaceName+", beanName="+beanName);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RefererFactory.class);
            beanDefinitionBuilder.addConstructorArgValue(beanName);
            beanDefinitionBuilder.addConstructorArgValue(interfaceName);
            beanDefinitionBuilder.addDependsOn("rpcApp");
            beanDefinitionBuilder.setLazyInit(true);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());

            registerAsyncReferer(beanName + "Async", interfaceName + "Async", beanFactory);
        }

        void registerAsyncReferer(String beanName, String interfaceName, DefaultListableBeanFactory beanFactory) {
            //log.info("register referer "+interfaceName+", beanName="+beanName);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RefererFactory.class);
            beanDefinitionBuilder.addConstructorArgValue(beanName);
            beanDefinitionBuilder.addConstructorArgValue(interfaceName);
            beanDefinitionBuilder.addDependsOn("rpcApp");
            beanDefinitionBuilder.setLazyInit(true);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
        }

        String generateBeanName(String id, String interfaceName) {
            if (id != null && !id.isEmpty()) return id;
            int p = interfaceName.lastIndexOf(".");
            String name = interfaceName.substring(p + 1);
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            return name;
        }

    }

}

