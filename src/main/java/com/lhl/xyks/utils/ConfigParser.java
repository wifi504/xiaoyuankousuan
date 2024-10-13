package com.lhl.xyks.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhl.xyks.pojo.Point;
import com.lhl.xyks.pojo.Symbol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 配置文件解析工具类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time: 2024/10/14_2:25
 */
public class ConfigParser {

    /**
     * 自定义 Point 类的反序列化器 (内部类)
     */
    public static class PointDeserializer extends JsonDeserializer<Point> {
        @Override
        public Point deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            List<?> list = jsonParser.readValueAs(List.class);
            return new Point((int) list.get(0), (int) list.get(1), (boolean) list.get(2));
        }
    }

    /**
     * 从 symbol-mapper.json 加载绘制符号映射
     *
     * @return 绘制符号映射 Map
     */
    public static HashMap<Character, ArrayList<Point>> loadSymbolMap() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Symbol> symbols = objectMapper.readValue(
                new File("symbol-mapper.json"),
                new TypeReference<>() {
                }
        );
        HashMap<Character, ArrayList<Point>> map = new HashMap<>();
        symbols.forEach(symbol -> map.put(symbol.getCharacter(), symbol.getPoints()));
        return map;
    }
}
