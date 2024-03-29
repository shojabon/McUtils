package com.shojabon.mcutils.Utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SConfigFile {

    private JavaPlugin plugin;

    public SConfigFile(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public static boolean saveResource(JavaPlugin plugin, String resourcePath, String path){
        InputStream input = plugin.getResource(resourcePath);
        if(input == null) return false;


        File targetFile = new File(path);
        if(targetFile.exists())return false;

        File parent = new File(targetFile.getParent());

        if(!parent.exists()){
            if(!new File(targetFile.getParent()).mkdirs()) return false;
        }

        try {
            if(!targetFile.createNewFile()) return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try{
            OutputStream output = new FileOutputStream(targetFile);
            int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int size;
            while (-1 != (size = input.read(buffer))) {
                output.write(buffer, 0, size);
            }
            input.close();
            output.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean saveResource(String resourcePath, String path){
        return saveResource(plugin, resourcePath, path);
    }

    public static YamlConfiguration getConfigFile(String path){
        File targetFile = new File(path);
        if(!targetFile.exists()) return null;
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(targetFile);
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }
        return config;
    }

    public static YamlConfiguration getResourceAsConfig(JavaPlugin plugin, String resourcePath){

        InputStream resourceStream = plugin.getResource(resourcePath);
        if(resourceStream == null) return null;
        Reader resourceReader = new InputStreamReader(resourceStream);


        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(resourceReader);
        } catch (Exception e) {
            try {
                resourceReader.close();
            } catch (Exception ignored) {
            }
            return null;
        }

        return config;
    }
    public static String getResourceAsText(JavaPlugin plugin, String resourcePath){

        InputStream resourceStream = plugin.getResource(resourcePath);
        if(resourceStream == null) return null;
        Stream<String> streamOfString= new BufferedReader(new InputStreamReader(resourceStream)).lines();
        return streamOfString.collect(Collectors.joining());
    }

    public YamlConfiguration getResourceAsConfig(String resourcePath){
        return getResourceAsConfig(plugin, resourcePath);
    }

    public static YamlConfiguration getConfigWithDefaultValues(JavaPlugin plugin, String resourcePath, String filePath){
        YamlConfiguration resource = getResourceAsConfig(plugin, resourcePath);
        if(resource == null) return null;
        YamlConfiguration file = getConfigFile(filePath);
        if(file == null)return null;

        for(String key: file.getKeys(true)){
            resource.set(key, file.get(key));
        }
        return resource;
    }

    public YamlConfiguration getConfigWithDefaultValues(String resourcePath, String filePath){
        return getConfigWithDefaultValues(plugin, resourcePath, filePath);
    }

    public static boolean saveConfig(YamlConfiguration config, String path){
        if(config == null)return false;

        File targetFile = new File(path);
        if(targetFile.exists())return false;

        File parent = new File(targetFile.getParent());

        if(!parent.exists()){
            if(!new File(targetFile.getParent()).mkdirs()) return false;
        }

        try {
            config.save(targetFile);
        } catch (IOException e) {
            return false;
        }
        return true;

    }

    public static ArrayList<File> getAllFileNameInPath(String path){
        File target = new File(path);
        if(!target.exists()) return new ArrayList<>();
        if(!target.isDirectory())return new ArrayList<>();
        File[] files = target.listFiles();
        if(files == null) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(files));


    }

    public static String base64EncodeConfig(YamlConfiguration config){
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    public static YamlConfiguration loadConfigFromBase64(String base64){
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        String decodedString = new String(decodedBytes);
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(decodedString);
            return config;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }


}
