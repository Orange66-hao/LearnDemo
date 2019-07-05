/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.boocu.web.LogConfig;
import net.boocu.web.service.LogConfigService;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * Service - 日志配置
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Service("logConfigServiceImpl")
public class LogConfigServiceImpl implements LogConfigService {

    @Override
    @SuppressWarnings("unchecked")
    @Cacheable("logConfig")
    public List<LogConfig> getAll() {
        try {
            File logConfigXmlFile = new ClassPathResource(LogConfig.XML_PATH).getFile();
            Document document = new SAXReader().read(logConfigXmlFile);
            List<org.dom4j.Element> elements = document.selectNodes("/logConfigs/logConfig");
            List<LogConfig> logConfigs = new ArrayList<LogConfig>();
            for (org.dom4j.Element element : elements) {

                // 日志配置参数
                String operation = element.attributeValue("operation");
                String urlPattern = element.attributeValue("urlPattern");
                String methodPattern = element.attributeValue("methodPattern");

                // 日志配置实例
                LogConfig logConfig = new LogConfig();
                logConfig.setOperation(operation);
                logConfig.setUrlPattern(urlPattern);
                logConfig.setMethodPattern(methodPattern);
                logConfigs.add(logConfig);
            }
            return logConfigs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}