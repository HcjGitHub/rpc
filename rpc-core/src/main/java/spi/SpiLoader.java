package spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.anyan.rpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器
 *
 * @author anyan
 * DateTime: 2024/6/6
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类：接口名=>(key=>实现类)
     */
    public static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例存储：类路径=>实例 单例模式
     */
    public static Map<String, Object> instanceMap = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    public static String SPI_PATH = "META-INF/rpc/system/";

    /**
     * 自定义SPI目录
     */
    public static String CUSTOM_SPI_PATH = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    public static final String[] SCAN_PATHS = {SPI_PATH, CUSTOM_SPI_PATH};


    /**
     * 动态加载类列表
     */
    public static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 获取某个接口实例
     *
     * @param interfaceClass 接口类
     * @param key            实现类别名
     * @param <T>            接口类型
     */
    public static <T> T getInstance(Class<T> interfaceClass, String key) {
        log.info("获取接口{}的{}实现类", interfaceClass, key);
        String className = interfaceClass.getName();
        Map<String, Class<?>> classMap = loaderMap.get(className);
        if (classMap == null) {
            throw new RuntimeException("SpiLoader 未加载" + interfaceClass.getName() + "的实现类");
        }
        if (!classMap.containsKey(key)) {
            throw new IllegalArgumentException("SpiLoader 未加载" + key + "="
                    + interfaceClass.getName() + "的" + key + "实现类");
        }
        // 获取具体实现类
        Class<?> clazz = classMap.get(key);
        // 类的具体路径
        String classPath = clazz.getName();
        if (!instanceMap.containsKey(classPath)) {
            try {
                instanceMap.put(classPath, clazz.newInstance());
            } catch (Exception e) {
                throw new RuntimeException("实例化失败：" + e.getMessage());
            }
        }
        return (T) instanceMap.get(classPath);
    }


    /**
     * 加载所有SPI接口
     */
    public static void loadAll() {
        {
            log.info("加载所有SPI接口");
            for (Class<?> loadClass : LOAD_CLASS_LIST) {
                load(loadClass);
            }
        }
    }

    /**
     * 加载某个SPI接口
     *
     * @param loadClass 接口类
     */
    public static <T> Map<String, Class<?>> load(Class<T> loadClass) {
        log.info("开始加载SPI接口：{}", loadClass.getName());
        String interfaceName = loadClass.getName();
        Map<String, Class<?>> classMap = new HashMap<>();
        for (String path : SCAN_PATHS) {
            String fullPath = path + interfaceName;
            List<URL> resources = ResourceUtil.getResources(fullPath);
            for (URL resource : resources) {
                try {
                    InputStreamReader isr = new InputStreamReader(resource.openStream());
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length != 2) {
                            throw new RuntimeException("SPI配置文件格式错误："+line);
                        }
                        String key = split[0].trim();
                        String className = split[1].trim();
                        Class<?> value = Class.forName(className);
                        classMap.put(key, value);
                    }
                } catch (Exception e) {
                    log.error("文件加载失败：{}", e.getMessage());
                }
            }
        }
        loaderMap.put(interfaceName, classMap);
        return classMap;
    }
}

