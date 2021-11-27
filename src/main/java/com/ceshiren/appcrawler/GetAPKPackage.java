package com.ceshiren.appcrawler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 获取APK信息
public class GetAPKPackage {

    // 获取SDK目录下最高版本的文件夹名称
    public static String getHighestVersion(String[] subDirs) {
        String result = "";
        for (String dirName : subDirs) {
            if (result.equals("")) {
                result = dirName;
            }
            int dirVersion = Integer.parseInt(dirName.split("\\.", 2)[0]);
            int resultVersion = Integer.parseInt(result.split("\\.", 2)[0]);
            if (dirVersion > resultVersion) {
                result = dirName;
            }
        }
        return result;
    }

    // 获取SDK下的AAPT应用路径
    public static String getAAPTPath() throws NoSuchFieldException {
        if(System.getenv("ANDROID_HOME")==null){
            return "";
        }
        String buildToolsBasePath = System.getenv("ANDROID_HOME") + "/build-tools";
        File buildToolsPath = new File(buildToolsBasePath);
        String highestVersionDirName = getHighestVersion(Objects.requireNonNull(buildToolsPath.list()));
        return buildToolsBasePath + "/" + highestVersionDirName + "/" + "aapt";
    }

    // 获取APK的package信息，如果返回为null说明未获取到数据
    public static String getPackageName(String apkFilePath) throws NoSuchFieldException {
        // 如果AAPT位置未获取到，直接返回null
        String aaptPath = getAAPTPath();
        if(aaptPath.equals("")){
            return null;
        }
        String cmd =  aaptPath+ " dump badging " + apkFilePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(cmd);
            byte[] b = new byte[1000];
            int num = 0;
            while ((num = process.getInputStream().read(b)) != -1) {
                String infoStr = new String(b, StandardCharsets.UTF_8);
                Pattern splitPattern = Pattern.compile("package: name=\\'(.*?)\\'");
                if (infoStr.contains("package")) {
                    Matcher res = splitPattern.matcher(infoStr);
                    if (res.find()) {
                        return res.group(1);
                    }
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

