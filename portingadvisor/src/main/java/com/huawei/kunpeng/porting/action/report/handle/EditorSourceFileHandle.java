/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.kunpeng.porting.action.report.handle;

import com.huawei.kunpeng.intellij.common.bean.RequestDataBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.IDEConstant;
import com.huawei.kunpeng.intellij.common.enums.HttpMethod;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.FileUtil;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.JsonUtil;
import com.huawei.kunpeng.intellij.js2java.fileditor.IDEFileEditorManager;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.porting.bean.SourceFileBean;
import com.huawei.kunpeng.porting.common.constant.PortingIDEConstant;
import com.huawei.kunpeng.porting.common.constant.SuggestionConstant;
import com.huawei.kunpeng.porting.common.constant.enums.RespondStatus;
import com.huawei.kunpeng.porting.common.constant.enums.TaskType;
import com.huawei.kunpeng.porting.http.PortingHttpsServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 编辑区文件相关操作类
 *
 * @since 2020-11-13
 */
public class EditorSourceFileHandle extends IDEPanelBaseAction {
    /**
     * 任务id
     */
    public static final String TASK_ID = "taskId";

    /**
     * 任务类型
     */
    public static final String TASK_TYPE = "taskType";

    /**
     * 编辑区当前正在编辑的porting数据
     */
    public static JSONObject portingJson = null;

    private static final JSONArray JSON_ARRAY_EMPTY = new JSONArray();
    private static volatile EditorSourceFileHandle editorSourceFileHandle = null;

    // 存储已经打开的源码文件信息
    private static Map<String, JSONObject> portInfoMap = new ConcurrentHashMap<>();

    private static Map<String, Boolean> sourceFileMap = new ConcurrentHashMap<>();

    /**
     * 双检索单例
     *
     * @return SuggestHandle
     */
    public static EditorSourceFileHandle getEditorSourceFileHandle() {
        if (editorSourceFileHandle == null) {
            synchronized (EditorSourceFileHandle.class) {
                if (editorSourceFileHandle == null) {
                    editorSourceFileHandle = new EditorSourceFileHandle();
                }
            }
        }
        return editorSourceFileHandle;
    }

    /**
     * 返回建议报告的portingitems的JSONArray数据格式
     *
     * @return JSONArray
     */
    public JSONArray getPortingItems() {
        if (portingJson == null) {
            return JSON_ARRAY_EMPTY;
        }
        Object portingItems = portingJson.get(SuggestionConstant.PORTING_ITEMS_KEY);
        if (portingItems instanceof JSONArray) {
            return (JSONArray) portingItems;
        }
        return JSON_ARRAY_EMPTY;
    }

    /**
     * 打开需要修改的源代码文件
     *
     * @param sourceFileBean 源码文件建议查看数据参数体
     */
    public void openSourceFile(SourceFileBean sourceFileBean) {
        Optional<File> fileOptional = FileUtil.getFile(sourceFileBean.getLocalFilePath(), true);
        if (!fileOptional.isPresent()) {
            return;
        }
        File sourceFile = fileOptional.get();
        if (!portInfoMap.containsKey(sourceFile.getPath())) {
            JSONObject jsonMessage =
                requestAndSavePortInfo(sourceFileBean, sourceFile);
            if (jsonMessage == null || jsonMessage.isEmpty()) {
                return;
            }
            portInfoMap.put(sourceFile.getPath(), jsonMessage);
            printPortingLog(jsonMessage, sourceFile.getName());
        }
        if (!sourceFileMap.containsKey(sourceFile.getPath())) {
            sourceFileMap.put(sourceFile.getPath(), sourceFileBean.isLocalFile());
        }

        // 打开源码文件名放在textmate或者C++的filetype的pattern中
        FileType[] registeredFileTypes = FileTypeManager.getInstance().getRegisteredFileTypes();
        String fileTypeName = "PortingText";
        for (FileType fileType : registeredFileTypes) {
            if (fileType.getName().equals(fileTypeName)) {
                // 将文件name放入到C++或者textmate的类型中
                ApplicationManager.getApplication().runWriteAction(() ->
                    FileTypeManagerEx.getInstanceEx().associatePattern(fileType, sourceFile.getName()));
            }
        }

        VirtualFile resourceCode = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(sourceFile);

        // 将源文件存放在列表中
        IDEFileEditorManager.getInstance().addSourceFileIfAbsent(resourceCode);
        portingJson = portInfoMap.get(sourceFile.getPath());
        IDEFileEditorManager.getInstance().openFile(resourceCode, true); // 在编辑区打开文件
    }

    /**
     * 打印日志方便测试
     *
     * @param jsonMessage 请求返回数据
     * @param fileName    文件名
     */
    private void printPortingLog(JSONObject jsonMessage, String fileName) {
        if (jsonMessage == null) {
            return;
        }
        Object portingObject = jsonMessage.get(SuggestionConstant.PORTING_ITEMS_KEY);
        Logger.info("fileName: {}.", fileName);
        if (portingObject instanceof JSONArray) {
            JSONArray portingItems = (JSONArray) portingObject;
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject portingItem = portingItems.getJSONObject(i);
                Logger.info("begin: {};end: {};suggestiontype: {}.",
                    portingItem.get(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY),
                    portingItem.get(SuggestionConstant.PORTING_ITEM_LOC_END_KEY),
                    portingItem.get(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY));
            }
        }
    }

    private JSONObject requestAndSavePortInfo(SourceFileBean sourceFileBean, File sourceFile) {
        final JSONObject jsonMessage = getPortingInfo(sourceFileBean);
        if (jsonMessage != null && !jsonMessage.isEmpty()) {
            if (!sourceFileBean.isLocalFile()) {
                String content = jsonMessage.getString("content");
                try (FileOutputStream outStream = new FileOutputStream(sourceFile)) {
                    byte[] sourceByte = content.getBytes(StandardCharsets.UTF_8);

                    // 文件输出流用于将数据写入文件
                    outStream.write(sourceByte);
                } catch (IOException ex) {
                    Logger.error("Can not write source code into file.");
                }
            }
            jsonMessage.put("isLocalFile", sourceFileBean.isLocalFile());
            jsonMessage.put(TASK_ID, sourceFileBean.getTaskId());
            jsonMessage.put(TASK_TYPE, sourceFileBean.getTaskType());
            jsonMessage.put("remoteFilePath", sourceFileBean.getRemoteFilePath());
        }
        return jsonMessage;
    }

    /**
     * 返回缓存的源码文件数据
     *
     * @return 缓存的源码文件数据
     */
    public static Map<String, JSONObject> getPortInfo() {
        return portInfoMap;
    }

    /**
     * 返回缓存的源码文件
     *
     * @return 缓存的源码文件
     */
    public static Map<String, Boolean> getSourceFile() {
        return sourceFileMap;
    }

    /**
     * 将修改后的数据上传到服务器
     *
     * @param vFile    虚拟文件
     * @param isClosed 该文件是否关闭
     */
    public void saveSourceFile(VirtualFile vFile, boolean isClosed) {
        if (!IDEFileEditorManager.getInstance().isSourceFile(vFile)) {
            return;
        }
        File sourceFile = new File(vFile.getPath());
        if (!sourceFile.exists()) {
            return;
        }
        final Document document = FileDocumentManager.getInstance().getDocument(vFile);
        if (document == null) {
            return;
        }
        JSONObject portingSuggestion = portInfoMap.get(sourceFile.getPath());
        Map<String, Object> dataMap = getSavePortingItems(portingSuggestion);
        String url = "/portadv/tasks/" + portingSuggestion.getString(TASK_ID) + "/originfile/";
        if (TaskType.WEAK_CHECK.value().equals(portingSuggestion.getString(TASK_TYPE))) {
            url = "/portadv/weakconsistency/tasks/" + portingSuggestion.getString(TASK_ID) + "/originfile/";
        }
        dataMap.put("origincontent", document.getText());
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            url, HttpMethod.POST.vaLue(), null);
        message.setNeedToken(true);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(dataMap));
        message.setCharset(IDEConstant.CHARSET_UTF8);
        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        if (responseBean == null) {
            return;
        }
        String responseStatus = responseBean.getStatus();
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseStatus)) {
            responseStatus = RespondStatus.PROCESS_STATUS_ERROR.value();
        }
        IDENotificationUtil.notifyCommonForResponse("", responseStatus, responseBean);
        if (isClosed && RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            boolean isLocalFile = portingSuggestion.getBooleanValue("isLocalFile");
            if (!isLocalFile) {
                portInfoMap.remove(sourceFile.getPath());
                if (!sourceFile.delete()) {
                    Logger.error("delete file fail when save source file.");
                }
            }
        }
    }

    /**
     * 关闭增强功能源码文件
     */
    public void closeEnhancedSourceFile() {
        List<VirtualFile> files = new ArrayList<>();
        for (VirtualFile file : IDEFileEditorManager.getInstance().getOpenSourceFiles()) {
            String fileName = new File(file.getPath()).getPath();
            if (portInfoMap.containsKey(fileName) &&
                (portInfoMap.get(fileName).getString(TASK_TYPE).equals(TaskType.MIGRATION_PRE_CHECK.value()) ||
                    portInfoMap.get(fileName).getString(TASK_TYPE).equals(TaskType.WEAK_CHECK.value()))) {
                files.add(file);
            }
        }
        IDEFileEditorManager.getInstance().closeFiles(files);
    }

    private JSONObject getPortingInfo(SourceFileBean sourceFileBean) {
        HashMap<Object, Object> dataMap = new HashMap<>();
        final boolean isCacheLineOrMigrationPreCheckTask =
            TaskType.MIGRATION_PRE_CHECK.value().equals(sourceFileBean.getTaskType()) ||
                TaskType.CACHE_LINE_ALIGNMENT.value().equals(sourceFileBean.getTaskType());
        if (isCacheLineOrMigrationPreCheckTask) {
            dataMap.put("file_path", sourceFileBean.getRemoteFilePath());
            dataMap.put("task_name", sourceFileBean.getTaskId());
        } else {
            dataMap.put("filepath", sourceFileBean.getRemoteFilePath());
        }
        // 拼接请求体
        RequestDataBean message = new RequestDataBean(PortingIDEConstant.TOOL_NAME_PORTING,
            sourceFileBean.getUrl(), HttpMethod.POST.vaLue(), null);
        message.setNeedToken(true);
        message.setBodyData(JsonUtil.getJsonStrFromJsonObj(dataMap));
        // 获取接口返回信息

        ResponseBean responseBean = PortingHttpsServer.INSTANCE.requestData(message);
        JSONObject jsonMessage = null;
        if (responseBean == null) {
            return jsonMessage;
        }
        if (!RespondStatus.PROCESS_STATUS_NORMAL.value().equals(responseBean.getStatus())) {
            IDENotificationUtil.notifyCommonForResponse("", RespondStatus.PROCESS_STATUS_ERROR.value(), responseBean);
            return jsonMessage;
        }
        jsonMessage = JSON.parseObject(responseBean.getData());
        if (TaskType.MIGRATION_PRE_CHECK.value().equals(sourceFileBean.getTaskType())) {
            jsonMessage.put(SuggestionConstant.PORTING_ITEMS_KEY, transPreCheckLinesToItems(jsonMessage));
            return jsonMessage;
        }

        if (TaskType.CACHE_LINE_ALIGNMENT.value().equals(sourceFileBean.getTaskType())) {
            jsonMessage.put(SuggestionConstant.PORTING_ITEMS_KEY, transCacheLineLinesToItems(jsonMessage));
            return jsonMessage;
        }

        if (TaskType.WEAK_CHECK.value().equals(sourceFileBean.getTaskType())) {
            transWeakCheckItemsToItems(jsonMessage);
            return jsonMessage;
        }
        return jsonMessage;
    }

    private JSONArray transPreCheckLinesToItems(final JSONObject jsonMessage) {
        JSONArray portingItems = new JSONArray();
        Object obj = jsonMessage.get(SuggestionConstant.LINE_KEY);
        if (obj instanceof JSONArray) {
            JSONArray lines = (JSONArray) obj;
            for (int i = 0; i < lines.size(); i++) {
                JSONObject item = new JSONObject();
                String[] line = lines.getString(i).split(":");
                if (line.length != 2) {
                    continue;
                }
                int row = Integer.parseInt(line[0]);
                int col = Integer.parseInt(line[1]);
                item.put(SuggestionConstant.PORTING_COL_KEY, col);
                item.put(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY, row);
                item.put(SuggestionConstant.PORTING_ITEM_LOC_END_KEY, row);
                item.put(SuggestionConstant.PORTING_ITEM_KEYWORD_KEY, "");
                item.put(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY,
                    I18NServer.toLocale("plugins_porting_pre_check_suggestion"));
                item.put(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY,
                    I18NServer.toLocale("plugins_porting_pre_check_suggestion"));
                item.put(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY,
                    SuggestionConstant.ENHANCED_NO_QUICK_FIX_SUGGESTION_TYPE);
                portingItems.add(item);
            }
        }
        return portingItems;
    }

    private JSONArray transCacheLineLinesToItems(final JSONObject jsonMessage) {
        JSONArray portingItems = new JSONArray();
        Object obj = jsonMessage.get(SuggestionConstant.LINE_KEY);
        if (obj instanceof JSONArray) {
            JSONArray lines = (JSONArray) obj;
            for (int i = 0; i < lines.size(); i++) {
                JSONObject item = new JSONObject();
                final JSONArray line = lines.getJSONArray(i);
                if (line.size() != 2) {
                    continue;
                }
                int rowStart = line.getInteger(0);
                int rowEnd = line.getInteger(1);
                item.put(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY, rowStart);
                item.put(SuggestionConstant.PORTING_ITEM_LOC_END_KEY, rowEnd);
                item.put(SuggestionConstant.PORTING_ITEM_KEYWORD_KEY, "");
                item.put(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY,
                    jsonMessage.getString(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY));
                item.put(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY,
                    jsonMessage.getString("suggestion"));
                item.put(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY,
                    SuggestionConstant.BYTE_ALIGN_NO_QUICK_FIX_SUGGESTION_TYPE);
                portingItems.add(item);
            }
        }
        return portingItems;
    }

    private void transWeakCheckItemsToItems(final JSONObject jsonMessage) {
        Object obj = jsonMessage.get(SuggestionConstant.PORTING_ITEMS_KEY);
        if (obj instanceof JSONArray) {
            JSONArray items = (JSONArray) obj;
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                item.put(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY, item.getIntValue("line"));
                item.put(SuggestionConstant.PORTING_ITEM_LOC_END_KEY, item.getIntValue("line"));
                item.put(SuggestionConstant.PORTING_ITEM_KEYWORD_KEY, "");
                item.put(SuggestionConstant.PORTING_ITEM_DESCRIPTION_KEY,
                    I18NServer.toLocale("plugins_porting_weak_check_suggestion"));
                if (item.getBooleanValue("quick_fix")) {
                    item.put(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY,
                        I18NServer.toLocale("plugins_porting_weak_check_suggestion"));
                    item.put(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY,
                        SuggestionConstant.ENHANCED_QUICK_FIX_SUGGESTION_TYPE);
                } else {
                    item.put(SuggestionConstant.PORTING_ITEM_STRATEGY_KEY,
                        I18NServer.toLocale("plugins_porting_weak_check_multi_suggestion"));
                    item.put(SuggestionConstant.PORTING_ITEM_SUGGESTION_TYPE_KEY,
                        SuggestionConstant.ENHANCED_NO_QUICK_FIX_SUGGESTION_TYPE);
                }
            }
        }
        jsonMessage.put(SuggestionConstant.PORTING_ITEMS_KEY, obj);
    }

    private Map<String, Object> getSavePortingItems(JSONObject portingSuggestion) {
        HashMap<String, Object> dataMap = new HashMap<>();
        JSONArray portingItems = portingSuggestion.getJSONArray(SuggestionConstant.PORTING_ITEMS_KEY);
        if (TaskType.MIGRATION_PRE_CHECK.value().equals(portingSuggestion.getString(TASK_TYPE))) {
            List<String> migrationItems = new ArrayList<>();
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject item = portingItems.getJSONObject(i);
                if (!item.getBooleanValue(SuggestionConstant.PORTING_ITEM_REPLACED_KEY)) {
                    String line = item.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY) + ":" +
                        item.getIntValue(SuggestionConstant.PORTING_COL_KEY);
                    migrationItems.add(line);
                }
            }
            dataMap.put("migrationitems", migrationItems);
        } else if (TaskType.WEAK_CHECK.value().equals(portingSuggestion.getString(TASK_TYPE))) {
            List<JSONObject> locs = new ArrayList<>();
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject item = portingItems.getJSONObject(i);
                if (!item.getBooleanValue(SuggestionConstant.PORTING_ITEM_REPLACED_KEY)) {
                    JSONObject loc = new JSONObject();
                    loc.put("line", item.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY));
                    loc.put("col", item.getIntValue(SuggestionConstant.PORTING_COL_KEY));
                    locs.add(loc);
                }
            }
            dataMap.put("locs", locs);
        } else if (TaskType.CACHE_LINE_ALIGNMENT.value().equals(portingSuggestion.getString(TASK_TYPE))) {
            List<JSONArray> migrations = new ArrayList<>();
            for (int i = 0; i < portingItems.size(); i++) {
                JSONObject item = portingItems.getJSONObject(i);
                if (!item.getBooleanValue(SuggestionConstant.PORTING_ITEM_REPLACED_KEY)) {
                    JSONArray loc = new JSONArray();
                    loc.add(item.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_BEGIN_KEY));
                    loc.add(item.getIntValue(SuggestionConstant.PORTING_ITEM_LOC_END_KEY));
                    migrations.add(loc);
                }
            }
            dataMap.put("migrationitems", migrations);
        } else {
            dataMap.put("portingitems", portingItems);
        }
        dataMap.put("filepath", portingSuggestion.getString("remoteFilePath"));
        return dataMap;
    }
}
