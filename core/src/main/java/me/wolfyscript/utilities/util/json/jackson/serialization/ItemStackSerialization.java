/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

public class ItemStackSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(JavaPlugin.class, new com.fasterxml.jackson.databind.ser.std.StdSerializer<>(JavaPlugin.class) {
            @Override
            public void serialize(JavaPlugin value, com.fasterxml.jackson.core.JsonGenerator gen, com.fasterxml.jackson.databind.SerializerProvider provider) throws java.io.IOException {
                gen.writeNull();
            }
        });

        JacksonUtil.addSerializerAndDeserializer(module, ItemStack.class, (itemStack, gen, serializerProvider) -> {
            if (itemStack != null) {
                var yaml = new Yaml();
                var config = new YamlConfiguration();
                config.set("i", itemStack);
                Map<String, Object> map = yaml.load(config.saveToString());
                writeSafeValue(gen, map.get("i"));
            }
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isValueNode()) {
                //Old Serialization Methods. like Base64 or NMS serialization
                String value = node.asText();
                if (!value.startsWith("{")) {
                    return WolfyUtilities.getWUCore().getNmsUtil().getItemUtil().getBase64ItemStack(value);
                }
                return value.equals("empty") ? null : WolfyUtilities.getWUCore().getNmsUtil().getItemUtil().getJsonItemStack(value);
            }
            var config = new YamlConfiguration();
            //Loads the Map from the JsonNode && Sets the Map to YamlConfig
            config.set("i", p.getCodec().readValue(node.traverse(p.getCodec()), new TypeReference<Map<String, Object>>() {
            }));
            try {
                    /*
                    Load new YamlConfig from just saved string.
                    That will convert the Map to an ItemStack!
                     */
                config.loadFromString(config.saveToString());
                return config.getItemStack("i");
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static void writeSafeValue(com.fasterxml.jackson.core.JsonGenerator gen, Object value) throws java.io.IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value instanceof String s) {
            gen.writeString(s);
        } else if (value instanceof Boolean b) {
            gen.writeBoolean(b);
        } else if (value instanceof Integer i) {
            gen.writeNumber(i);
        } else if (value instanceof Long l) {
            gen.writeNumber(l);
        } else if (value instanceof Double d) {
            gen.writeNumber(d);
        } else if (value instanceof Float f) {
            gen.writeNumber(f);
        } else if (value instanceof Map m) {
            gen.writeStartObject();
            for (var entry : ((Map<String, Object>) m).entrySet()) {
                gen.writeFieldName(entry.getKey());
                writeSafeValue(gen, entry.getValue());
            }
            gen.writeEndObject();
        } else if (value instanceof List list) {
            gen.writeStartArray();
            for (Object item : list) {
                writeSafeValue(gen, item);
            }
            gen.writeEndArray();
        } else {
            gen.writeString(value.toString());
        }
    }
}
