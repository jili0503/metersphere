package io.metersphere.api.jmeter;

import io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample;
import org.apache.jmeter.extractor.JSR223PostProcessor;
import org.apache.jmeter.extractor.RegexExtractor;
import org.apache.jmeter.extractor.XPath2Extractor;
import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.collections.HashTree;
import org.springframework.util.StringUtils;

import java.util.*;

public class JMeterVars {

    private JMeterVars() {
    }

    /**
     * 数据和线程变量保持一致
     */
    private static Map<Integer, JMeterVariables> variables = new HashMap<>();

    /**
     * 线程执行过程调用提取变量值
     *
     * @param testId
     * @param vars
     * @param extract
     */
    public static void addVars(Integer testId, JMeterVariables vars, String extract) {
        JMeterVariables vs = new JMeterVariables();

        if (!StringUtils.isEmpty(extract) && vars != null) {
            List<String> extracts = Arrays.asList(extract.split(";"));
            Optional.ofNullable(extracts).orElse(new ArrayList<>()).forEach(item -> {
                vs.put(item, vars.get(item) == null ? "" : vars.get(item));
            });
            vs.remove("TESTSTART.MS"); // 标示变量移除
        }

        variables.put(testId, vs);
    }

    /**
     * 处理所有请求，有提取变量的请求增加后置脚本提取变量值
     *
     * @param tree
     */
    public static void addJSR223PostProcessor(HashTree tree) {
        for (Object key : tree.keySet()) {
            HashTree node = tree.get(key);
            if (key instanceof HTTPSamplerProxy || key instanceof DubboSample || key instanceof JDBCSampler) {
                StringJoiner extract = new StringJoiner(";");
                for (Object child : node.keySet()) {
                    if (child instanceof RegexExtractor) {
                        RegexExtractor regexExtractor = (RegexExtractor) child;
                        extract.add(regexExtractor.getRefName());
                    } else if (child instanceof XPath2Extractor) {
                        XPath2Extractor regexExtractor = (XPath2Extractor) child;
                        extract.add(regexExtractor.getRefName());
                    } else if (child instanceof JSONPostProcessor) {
                        JSONPostProcessor regexExtractor = (JSONPostProcessor) child;
                        extract.add(regexExtractor.getRefNames());
                    }
                }

                if (Optional.ofNullable(extract).orElse(extract).length() > 0) {
                    JSR223PostProcessor shell = new JSR223PostProcessor();
                    shell.setEnabled(true);
                    shell.setProperty("script", "io.metersphere.api.jmeter.JMeterVars.addVars(prev.hashCode(),vars," + "\"" + extract.toString() + "\"" + ");");
                    node.add(shell);
                }
            }

            if (node != null) {
                addJSR223PostProcessor(node);
            }
        }
    }

    public static JMeterVariables get(Integer key) {
        return variables.get(key);
    }

    public static void remove(Integer key) {
        variables.remove(key);
    }

}
