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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sourceporting;

import com.huawei.kunpeng.hyper.tuner.action.JavaPerfTreeAction;
import com.huawei.kunpeng.hyper.tuner.action.javaperf.GuardianManagerAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningIDEConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.GuardianMangerConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.javaperf.JavaperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.common.utils.DateUtil;
import com.huawei.kunpeng.hyper.tuner.common.utils.WebViewUtil;
import com.huawei.kunpeng.hyper.tuner.http.JavaProjectServer;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.GcLog;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.Members;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.MemoryDumpReprots;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.Owner;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.SamplingTaskInfo;
import com.huawei.kunpeng.hyper.tuner.model.javaperf.ThreadDumpReports;
import com.huawei.kunpeng.hyper.tuner.webview.tuning.handler.JavaPerfHandler;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.js2java.bean.MessageBean;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;

import org.apache.commons.lang3.StringUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * java左侧树面板
 *
 * @since 2020-04-13
 */
public class JavaPerfToolWindowPanel extends IDEBasePanel {
    /**
     * 当前选择的目标环境
     */
    public static Members selectMember;
    /**
     * 当前选择的采样任务
     */
    public static SamplingTaskInfo selectSamplingTask;
    /**
     * 当前选择的采样任务
     */
    public static MemoryDumpReprots selectMemoryDump;
    /**
     * 当前选择的采样任务
     */
    public static GcLog selectGcLog;
    /**
     * 当前选择的采样任务
     */
    public static MessageBean profilingMessage;
    /**
     * 当前选择的采样任务-线程
     */
    public static ThreadDumpReports selectThreadDump;
    /**
     * 在线分析任务状态
     */
    public static String stopProfiling;
    /**
     * 是否更新目标环境表格
     */
    public static boolean isNeedUpdate = true;
    /**
     * 是否新建采样分析
     */
    public static boolean isCreateSampling = false;
    /**
     * 导入类型
     */
    public static JavaPerfTreeAction.ImportType type;
    /**
     * 用户信息Map
     */
    private static final Map<String, Owner> USERMAP = new HashMap<>();
    /**
     * 内存转储Map
     */
    private static final Map<String, MemoryDumpReprots> MEMORY_DUMP_MAP = new HashMap<>();
    /**
     * gc log Map
     */
    private static final Map<String, GcLog> GC_LOG_MAP = new HashMap<>();
    /**
     * 线程转储Map
     */
    private static final Map<String, ThreadDumpReports> THREAD_DUMP_MAP = new HashMap<>();
    /**
     * 采样信息Map
     */
    private static final Map<String, List<SamplingTaskInfo>> USER_TASK = new HashMap<>();
    /**
     * 目标环境列表
     */
    private static final List<Members> USER_ENVIRONMENT_LIST = new ArrayList<>();
    private static DefaultMutableTreeNode profilingRecord;
    private static Tree javaTree;

    /**
     * timer
     */
    public Timer timer = new Timer();

    /**
     * timer2
     */
    public Timer timer2 = new Timer();

    /**
     * timer3
     */
    public Timer timer3 = new Timer();

    private GuardianManagerAction guardianManagerAction;
    private boolean isAdmin;
    private JPanel mainPanel;
    private JPanel treePanel;
    private JPanel decoratorPanel;
    private TreePath pathForRow;
    private AnActionButton exportTask;
    private AnActionButton importTask;
    private String seleteTreePath;
    private String dataType;
    private String id;
    private String name;
    private String uId;
    private TreeNode rootNode;
    private boolean isExpansion = false;
    private boolean isFirstLoad = true;
    private boolean isFirstSlamplingLoad = true;

    /**
     * 左侧树登录面板构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     * @param project    当前的项目
     */
    public JavaPerfToolWindowPanel(ToolWindow toolWindow, String panelName, Project project) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LEFT_TREE_SYSPERF.panelName() : panelName;
        guardianManagerAction = new GuardianManagerAction();
        // 初始化面板
        initPanel(mainPanel);

        // 初始化面板内组件事件
        registerComponentAction();

        // 初始化content实例
        createContent(mainPanel, null, false);
    }

    /**
     * 反射调用的构造函数
     *
     * @param toolWindow 工具窗口
     * @param project    当前的项目
     */
    public JavaPerfToolWindowPanel(ToolWindow toolWindow, Project project) {
        this(toolWindow, null, project);
    }

    /**
     * 获取展示的  环境列表
     *
     * @return 环境列表
     */
    public static List<Members> getUserEnvironmentList() {
        return USER_ENVIRONMENT_LIST;
    }

    /**
     * 获取用户Map
     * key为用户名。value为用户对象
     *
     * @return 户Map
     */
    public static Map<String, Owner> getUserMap() {
        return USERMAP;
    }

    public static Map<String, List<SamplingTaskInfo>> getUserTask() {
        return USER_TASK;
    }

    private static void setSelectSamplingTask(String name, String key) {
        List<SamplingTaskInfo> list = USER_TASK.get(name);
        for (SamplingTaskInfo samplingTaskInfo : list) {
            if (key.equals(
                    samplingTaskInfo.getName()
                            + JavaperfContent.DATA_LIST_CREATE_TIME
                            + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()))) {
                selectSamplingTask = samplingTaskInfo;
                return;
            }
            if (key.equals(
                    samplingTaskInfo.getName()
                            + JavaperfContent.DATA_LIST_IMPORT_TIME
                            + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()))) {
                selectSamplingTask = samplingTaskInfo;
                return;
            }
        }
    }

    /**
     * 刷新在线任务
     *
     * @param nodeName nodeName
     * @param isAdd    isAdd
     */
    public static void refreshProfilingNode(String nodeName, boolean isAdd) {
        DefaultMutableTreeNode childRecord = new DefaultMutableTreeNode();
        String showNodeName = nodeName.replaceAll(":", "-");
        childRecord.setUserObject(showNodeName);
        if (profilingRecord == null) {
            return;
        }
        if (isAdd) {
            profilingRecord.removeAllChildren();
            profilingRecord.add(childRecord);
            javaTree.expandRow(profilingRecord.getParent().getChildAt(0).getChildCount() + 1);
            refreshJavaTreePanel();
        } else {
            if (!showNodeName.isEmpty()
                    && profilingRecord.getChildCount() > 0) {
                String treeNode = profilingRecord.getChildAt(0).toString().replaceAll(" ", "-");
                if (treeNode.contains(TuningI18NServer.toLocale(
                        "plugins_hyper_tuner_profiling_import_time"))) {
                    treeNode = treeNode + ".kpht";
                } else {
                    treeNode = treeNode + "_pro.kpht";
                }
                if (!treeNode.contains(showNodeName.replaceAll(" ", "-"))) {
                    return;
                }
                JavaPerfToolWindowPanel.stopProfiling = "stopping";
                if (JavaPerfToolWindowPanel.profilingMessage != null) {
                    JavaPerfHandler.stopProfilingIntellij();
                }
                profilingRecord.removeAllChildren();
                return;
            }
            if (showNodeName.isEmpty() && JavaPerfToolWindowPanel.profilingMessage != null) {
                JavaPerfToolWindowPanel.stopProfiling = "stopping";
                JavaPerfHandler.stopProfilingIntellij();
                profilingRecord.removeAllChildren();
            }
        }
    }

    /**
     * 刷新在线任务状态
     */
    public static void refreshProfilingNode() {
        isCreateSampling = true;
        JavaPerfToolWindowPanel.stopProfiling = "stopping";
        profilingRecord.removeAllChildren();
    }

    /**
     * 是否导入的在线分析
     *
     * @return 结果
     */
    public static boolean isImporting() {
        return profilingRecord != null && profilingRecord.getChildCount() > 0 &&
                profilingRecord.getChildAt(0).toString().contains(JavaperfContent.DATA_LIST_IMPORT_TIME);
    }

    /**
     * 检查在线分析状态
     *
     * @return 在线分析状态
     */
    public static boolean checkProfilingStata() {
        return profilingRecord.getChildCount() > 0;
    }

    @Override
    protected void initPanel(JPanel panel) {
        isAdmin = UserInfoContext.getInstance().getRole().equals(TuningUserManageConstant.USER_ROLE_ADMIN);
        // 初始化项目树
        initTree();
        addActionButton();
        importTask.setEnabled(false);
        exportTask.setEnabled(false);
        treePanel.add(javaTree);
        treeEvent();
        mouseEvent();
        initTimerTask();
        refreshSamplingUser(rootNode.getChildAt(2), true);
        refreshDataList();
    }

    private void initTree() {
        treePanel.setLayout(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root Node");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        javaTree = new Tree(treeModel);
        javaTree.setCellRenderer(new LeftTreeCellRenderJava());
        javaTree.setExpandableItemsEnabled(true);
        javaTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        javaTree.setShowsRootHandles(true);
        pathForRow = javaTree.getPathForRow(0);
        loadTreeContant();
        javaTree.expandPath(javaTree.getPathForRow(0));
        javaTree.setRootVisible(false);
    }

    private void loadTreeContant() {
        DefaultMutableTreeNode sourceRoot;
        if (pathForRow.getLastPathComponent() instanceof DefaultMutableTreeNode) {
            sourceRoot = (DefaultMutableTreeNode) pathForRow.getLastPathComponent();
            loadEnvironment(sourceRoot);
            loadOnline(sourceRoot);
            loadSampling(sourceRoot);
            loadData(sourceRoot);
        }
    }

    /**
     * 加载目标环境
     *
     * @param sourceRoot 节点
     */
    private void loadEnvironment(DefaultMutableTreeNode sourceRoot) {
        DefaultMutableTreeNode environmentRecord = new DefaultMutableTreeNode();
        environmentRecord.setUserObject(JavaperfContent.MEMBERS_LIST);
        List<Members> membersList = JavaProjectServer.getAllGuardian();
        inflateTreeNode(environmentRecord, membersList);
        sourceRoot.add(environmentRecord);
        USER_ENVIRONMENT_LIST.addAll(membersList);
    }

    /**
     * 加载在线分析
     *
     * @param sourceRoot 节点
     */
    private void loadOnline(DefaultMutableTreeNode sourceRoot) {
        DefaultMutableTreeNode onlineRecord = new DefaultMutableTreeNode();
        onlineRecord.setUserObject(JavaperfContent.ONLINE_ANALYSIS);
        sourceRoot.add(onlineRecord);
        profilingRecord = onlineRecord;
    }

    private void loadSampling(DefaultMutableTreeNode sourceRoot) {
        DefaultMutableTreeNode samplingRecord = new DefaultMutableTreeNode();
        samplingRecord.setUserObject(JavaperfContent.SAMPLING_ANALYSIS);
        sourceRoot.add(samplingRecord);
    }

    private void loadSamplingResp(DefaultMutableTreeNode childRecord, List<SamplingTaskInfo> samplingTaskInfoList) {
        for (SamplingTaskInfo samplingTaskInfo : samplingTaskInfoList) {
            DefaultMutableTreeNode taskRecord = new DefaultMutableTreeNode();
            if ("UPLOAD".equalsIgnoreCase(samplingTaskInfo.getSource())) {
                taskRecord.setUserObject(
                        samplingTaskInfo.getName()
                                + JavaperfContent.DATA_LIST_IMPORT_TIME
                                + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()));
            } else {
                taskRecord.setUserObject(
                        samplingTaskInfo.getName()
                                + JavaperfContent.DATA_LIST_CREATE_TIME
                                + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()));
            }
            childRecord.add(taskRecord);
        }
    }

    private void loadData(DefaultMutableTreeNode sourceRoot) {
        DefaultMutableTreeNode dataRecord = new DefaultMutableTreeNode();
        dataRecord.setUserObject(JavaperfContent.DATA_LIST);
        loadMemory(dataRecord);
        loadThread(dataRecord);
        loadGc(dataRecord);
        sourceRoot.add(dataRecord);
    }

    private void loadMemory(DefaultMutableTreeNode dataRecord) {
        DefaultMutableTreeNode memoryRecord = new DefaultMutableTreeNode();
        memoryRecord.setUserObject(JavaperfContent.DATA_LIST_MEMORY_DUMP);
        dataRecord.add(memoryRecord);
    }

    private void loadThread(DefaultMutableTreeNode dataRecord) {
        DefaultMutableTreeNode threadRecord = new DefaultMutableTreeNode();
        threadRecord.setUserObject(JavaperfContent.DATA_LIST_THREAD_DUMP);
        dataRecord.add(threadRecord);
    }

    private void loadGc(DefaultMutableTreeNode dataRecord) {
        DefaultMutableTreeNode gcRecord = new DefaultMutableTreeNode();
        gcRecord.setUserObject(JavaperfContent.DATA_LIST_GC_LOGS);
        dataRecord.add(gcRecord);
    }

    /*
     * 节点被选中的监听器
     */
    private void treeEvent() {
        javaTree.addTreeSelectionListener(
                event -> {
                    isExpansion = false;
                    // 获取被选中的相关节点
                    TreePath path = event.getPath();
                    seleteTreePath = path.toString();
                    seleteTreePath = seleteTreePath.substring(1, seleteTreePath.length() - 1);
                    String[] nodes = seleteTreePath.split(",");
                    String firstNode = nodes[1].trim();
                    // 选中节点名称
                    Object lastSelectComp = javaTree.getLastSelectedPathComponent();
                    if (!(lastSelectComp instanceof DefaultMutableTreeNode)) {
                        return;
                    }
                    name = lastSelectComp.toString().trim();
                    // 判断目录树层级
                    if (nodes.length == 2) {
                        importTask.setEnabled(name.equals(JavaperfContent.ONLINE_ANALYSIS)
                                || name.equals(JavaperfContent.SAMPLING_ANALYSIS));
                        exportTask.setEnabled(false);
                        type =
                                name.equals(JavaperfContent.ONLINE_ANALYSIS)
                                        ? JavaPerfTreeAction.ImportType.ONLINE_ANALYSIS
                                        : JavaPerfTreeAction.ImportType.SAMPLING_ANALYSIS;
                    } else if (nodes.length == 3) {
                        treeThreeNodeChange(nodes, firstNode);
                    } else if (nodes.length == 4) {
                        treeFourNodeChange(nodes, firstNode);
                    } else {
                        importTask.setEnabled(false);
                        exportTask.setEnabled(false);
                    }
                });
        treeExpanListen();
    }

    private void treeExpanListen() {
        /*
         * 节点展开/折叠监听器
         */
        javaTree.addTreeExpansionListener(
                new TreeExpansionListener() {
                    @Override
                    public void treeExpanded(TreeExpansionEvent event) {
                        isExpansion = true;
                    }

                    @Override
                    public void treeCollapsed(TreeExpansionEvent event) {
                        isExpansion = true;
                    }
                });
    }

    private void treeFourNodeChange(String[] nodes, String firstNode) {
        if (firstNode.equals(JavaperfContent.SAMPLING_ANALYSIS)) {
            setSelectSamplingTask(nodes[2].trim(), name);
            if (selectSamplingTask
                    .getCreatedBy()
                    .equals(UserInfoContext.getInstance().getLoginId())) {
                type = JavaPerfTreeAction.ImportType.SAMPLING_ANALYSIS;
                exportTask.setEnabled(true);
            } else {
                exportTask.setEnabled(false);
            }
            importTask.setEnabled(false);
        }
        if (firstNode.equals(JavaperfContent.DATA_LIST)) {
            importTask.setEnabled(false);
            exportTask.setEnabled(!isAdmin);
            String nodeParent = nodes[nodes.length - 2].trim();
            if (nodeParent.equals(JavaperfContent.DATA_LIST_MEMORY_DUMP)) {
                type = JavaPerfTreeAction.ImportType.REPORT_LIST_MEMORY_DUMP;
                selectMemoryDump = MEMORY_DUMP_MAP.get(name);
            } else if (nodeParent.equals(JavaperfContent.DATA_LIST_GC_LOGS)) {
                type = JavaPerfTreeAction.ImportType.REPORT_LIST_GC_LOGS;
                selectGcLog = GC_LOG_MAP.get(name);
            } else {
                type = JavaPerfTreeAction.ImportType.REPORT_LIST_THREAD_DUMP;
                selectThreadDump = THREAD_DUMP_MAP.get(name);
            }
        }
    }

    private void treeThreeNodeChange(String[] nodes, String firstNode) {
        if (firstNode.equals(JavaperfContent.MEMBERS_LIST)) {
            exportTask.setEnabled(false);
            importTask.setEnabled(false);
        } else if (firstNode.equals(JavaperfContent.ONLINE_ANALYSIS)) {
            type = JavaPerfTreeAction.ImportType.ONLINE_ANALYSIS;
            importTask.setEnabled(false);
            exportTask.setEnabled(true);
            if (name.contains(JavaperfContent.DATA_LIST_IMPORT_TIME)) {
                exportTask.setEnabled(false);
            }
        } else if (firstNode.equals(JavaperfContent.SAMPLING_ANALYSIS)) {
            type = JavaPerfTreeAction.ImportType.SAMPLING_ANALYSIS;
            importTask.setEnabled(false);
            exportTask.setEnabled(false);
            if (!UserInfoContext.getInstance().getRole().equals(TuningUserManageConstant.USER_ROLE_ADMIN)) {
                exportTask.setEnabled(true);
                setSelectSamplingTask(UserInfoContext.getInstance().getUserName(), name);
            }
        } else if (name.equals(JavaperfContent.DATA_LIST_MEMORY_DUMP)) {
            type = JavaPerfTreeAction.ImportType.REPORT_LIST_MEMORY_DUMP;
            importTask.setEnabled(true);
            exportTask.setEnabled(false);
        } else if (name.equals(JavaperfContent.DATA_LIST_GC_LOGS)) {
            type = JavaPerfTreeAction.ImportType.REPORT_LIST_GC_LOGS;
            importTask.setEnabled(true);
            exportTask.setEnabled(false);
        } else if (name.equals(JavaperfContent.DATA_LIST_THREAD_DUMP)) {
            type = JavaPerfTreeAction.ImportType.REPORT_LIST_THREAD_DUMP;
            importTask.setEnabled(true);
            exportTask.setEnabled(false);
        } else {
            importTask.setEnabled(false);
            exportTask.setEnabled(false);
        }
    }

    private void mouseEvent() {
        javaTree.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent event) {
                        final Component component = event.getComponent();
                        if (component instanceof Tree) {
                            if (seleteTreePath == null) {
                                return;
                            }
                            String[] nodes = seleteTreePath.split(",");
                            String firstNode = nodes[1].trim();
                            if (isExpansion) {
                                isExpansion = false;
                                return;
                            }
                            // 判断目录树层级
                            excuteMouseClick(firstNode, event, nodes);
                        }
                    }
                });
    }

    private void excuteMouseClick(String firstNode, MouseEvent event, String[] nodes) {
        switch (nodes.length) {
            case 2:
                nodeLength2ShowPopupMenu(firstNode, event);
                break;
            case 3:
                clickTreeNodeThree(firstNode, event, nodes);
                break;
            case 4:
                clickSamplingNode(firstNode, event, nodes);
                if (firstNode.equals(JavaperfContent.DATA_LIST)) {
                    String nodeParent = nodes[nodes.length - 2].trim();
                    if (!isAdmin) {
                        respDataList(nodeParent, nodes, event);
                    }
                }
                break;
            case 5:
                String nodeParent = nodes[nodes.length - 3].trim();
                respDataList(nodeParent, nodes, event);
                break;
            default:
                break;
        }
    }

    private void nodeLength2ShowPopupMenu(String firstNode, MouseEvent event) {
        if (firstNode.equals(JavaperfContent.MEMBERS_LIST) && event.isMetaDown()) {
            showGuardManagePopupMenu(event.getComponent(), event.getX(), event.getY());
        } else if (firstNode.equals(JavaperfContent.ONLINE_ANALYSIS) && event.isMetaDown()) {
            showOnlineManageMenu(event.getComponent(), event.getX(), event.getY());
        } else if (firstNode.equals(JavaperfContent.SAMPLING_ANALYSIS) && event.isMetaDown()) {
            showSamplingManageMenu(event.getComponent(), event.getX(), event.getY());
        } else {
            return;
        }
    }

    private void clickSamplingNode(String firstNode, MouseEvent event, String[] nodes) {
        if (firstNode.equals(JavaperfContent.SAMPLING_ANALYSIS)) {
            if (event.isMetaDown()) { // 如果是鼠标右键，则显示弹出菜单
                showSamplingPopupMenu(event.getComponent(), event.getX(), event.getY(), nodes);
            } else {
                // 展示采样分析内容
                if (selectSamplingTask.getCreatedBy().equals(UserInfoContext.getInstance().getLoginId())) {
                    JavaPerfTreeAction.instance().showSamplingTask(false);
                    return;
                }
                leftMouseClickTip("", event, "sampling", nodes[nodes.length - 1].trim());
            }
        }
    }

    private void clickTreeNodeThree(String firstNode, MouseEvent event, String[] nodes) {
        if (firstNode.equals(JavaperfContent.MEMBERS_LIST)) {
            setSelectMember(nodes[nodes.length - 1].trim());
            if (event.isMetaDown()) { // 如果是鼠标右键，则显示弹出菜单
                showGuardPopupMenu(event.getComponent(), event.getX(), event.getY());
            } else {
                if (selectMember.getOwner().getUsername().equals(UserInfoContext.getInstance().getUserName())) {
                    JavaPerfTreeAction.instance().showGuardianProcess();
                } else {
                    leftMouseClickTip("", event, "", "");
                }
            }
        }
        if (!isAdmin) {
            clickSamplingNode(firstNode, event, nodes);
        }
        if (firstNode.equals(JavaperfContent.ONLINE_ANALYSIS) && event.isMetaDown()) {
            showOnlinePopupMenu(nodes[nodes.length - 1].trim(), event.getComponent(), event.getX(), event.getY());
        }
        if (firstNode.equals(JavaperfContent.DATA_LIST) && event.isMetaDown()) {
            String nodeParent = nodes[nodes.length - 1].trim();
            if (nodeParent.equals(JavaperfContent.DATA_LIST_MEMORY_DUMP)) {
                dataType = "memoryGump";
                selectMemoryDump = MEMORY_DUMP_MAP.get(nodes[nodes.length - 1].trim());
            } else if (nodeParent.equals(JavaperfContent.DATA_LIST_GC_LOGS)) {
                dataType = "gcLog";
                selectGcLog = GC_LOG_MAP.get(nodes[nodes.length - 1].trim());
            } else {
                dataType = "threadGump";
                selectThreadDump = THREAD_DUMP_MAP.get(nodes[nodes.length - 1].trim());
            }
            dataListImportMenu(event.getComponent(), event.getX(), event.getY());
        }
    }

    private void dataListImportMenu(Component invoker, int xCoordinates, int yCoordinates) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem importDataList = new JMenuItem(JavaperfContent.IMPORT_DATA_LIST);
        importDataList.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.IMPORT_ICON_PATH));
        popupMenu.add(importDataList);
        // 导入数据
        importDataList.addActionListener(
                event -> {
                    if (("memoryGump").equals(dataType)) {
                        type = JavaPerfTreeAction.ImportType.REPORT_LIST_MEMORY_DUMP;
                        excuteRefreshDataList(dataType);
                    } else if (("gcLog").equals(dataType)) {
                        type = JavaPerfTreeAction.ImportType.REPORT_LIST_GC_LOGS;
                        excuteRefreshDataList(dataType);
                    } else {
                        type = JavaPerfTreeAction.ImportType.REPORT_LIST_THREAD_DUMP;
                        excuteRefreshDataList(dataType);
                    }
                    JavaPerfTreeAction.instance().importFile(JavaPerfToolWindowPanel.type);
                });
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showGuardManagePopupMenu(Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem guardManageItem = new JMenuItem(JavaperfContent.MEMBERS_MANAGE);
        guardManageItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.GUARDIAN_MANAGEMENT_ICON_PATH));
        // 添加 一级菜单 到 弹出菜单
        popupMenu.add(guardManageItem);

        // 目标环境管理
        guardManageItem.addActionListener(
                event -> ShowSettingsUtil.getInstance()
                        .showSettingsDialog(
                                CommonUtil.getDefaultProject(), GuardianMangerConstant.DISPLAY_NAME));
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showOnlineManageMenu(Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu profilingPopupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem importProfilingItem = new JMenuItem(JavaperfContent.IMPORT_ANALYSIS_RECORDS);
        JMenuItem manageProfilingItem = new JMenuItem(JavaperfContent.ANALYSIS_RECORD_MANAGEMENT);
        importProfilingItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.IMPORT_ICON_PATH));
        manageProfilingItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.RECORD_MANAGE_ICON_PATH));
        // 添加 一级菜单 到 弹出菜单
        profilingPopupMenu.add(importProfilingItem);

        // 导入在线分析记录
        importProfilingItem.addActionListener(
                event -> {
                    type = JavaPerfTreeAction.ImportType.ONLINE_ANALYSIS;
                    if ("running".equals(JavaPerfToolWindowPanel.stopProfiling)) {
                        JavaPerfTreeAction.instance().openCommonDialog("ONLINE_ANALYSIS", null, null, null);
                    } else {
                        JavaPerfTreeAction.instance().importOpenFileDialog(JavaPerfToolWindowPanel.type);
                    }
                });
        // 分析记录管理
        manageProfilingItem.addActionListener(
                event -> {
                });
        // 在指定位置显示弹出菜单
        profilingPopupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showSamplingManageMenu(Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem importAnalysisItem = new JMenuItem(JavaperfContent.IMPORT_ANALYSIS_RECORDS);
        JMenuItem manageAnalysisItem = new JMenuItem(JavaperfContent.ANALYSIS_RECORD_MANAGEMENT);
        importAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.IMPORT_ICON_PATH));
        manageAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.RECORD_MANAGE_ICON_PATH));
        // 添加 一级菜单 到 弹出菜单
        popupMenu.add(importAnalysisItem);

        // 导入采样分析记录
        importAnalysisItem.addActionListener(
                event -> {
                    type = JavaPerfTreeAction.ImportType.SAMPLING_ANALYSIS;
                    JavaPerfTreeAction.instance().importFile(type);
                });
        // 导出采样分析记录
        JMenuItem exportAnalysisItem = new JMenuItem(JavaperfContent.EXPORT_ANALYSIS_RECORDS);
        exportAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.EXPORT_ICON_PATH));
        exportAnalysisItem.addActionListener(
                event -> {
                    String fileId = JavaPerfToolWindowPanel.selectSamplingTask.getId();
                    String fileName = JavaPerfToolWindowPanel.selectSamplingTask.getName();
                    JavaPerfTreeAction.instance().downloadSamplingAnalysisFile(fileId, fileName);
                });
        // 采样分析记录管理
        manageAnalysisItem.addActionListener(
                event -> {
                });
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showGuardPopupMenu(Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem deleteGuardItem = new JMenuItem(JavaperfContent.DELETE);
        deleteGuardItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.DELETE_ICON_PATH));
        JMenuItem restartGuardItem = new JMenuItem(JavaperfContent.RESTART);
        restartGuardItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.RESTART_ICON_PATH));
        // 添加 一级菜单 到 弹出菜单
        if ("CONNECTED".equals(selectMember.getState())) {
            popupMenu.add(deleteGuardItem);
        } else {
            popupMenu.add(deleteGuardItem);
            if (selectMember.getOwner().getUsername().equals(UserInfoContext.getInstance().getUserName())) {
                popupMenu.add(restartGuardItem);
            }
        }
        // 删除目标环境
        deleteGuardItem.addActionListener(
                event -> {
                    isNeedUpdate = false;
                    guardianManagerAction.deleteGuardian(
                            selectMember.getName(),
                            selectMember.getState(),
                            selectMember.getId(),
                            selectMember.getOwner().getUsername());
                    WebViewUtil.closePage(selectMember.getName() + "." + TuningIDEConstant.TUNING_KPHT);
                });
        // 重启目标环境
        restartGuardItem.addActionListener(
                event -> {
                    isNeedUpdate = false;
                    guardianManagerAction.restartGuardian(
                            selectMember.getId(),
                            selectMember.getIp(),
                            String.valueOf(selectMember.getSshPort()),
                            selectMember.getName());
                });
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showDataListPopupMenu(Component invoker, int xCoordinates, int yCoordinates, String[] nodes) {
        {
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem deleteDataList = new JMenuItem(JavaperfContent.DELETE);
            deleteDataList.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.DELETE_ICON_PATH));
            JMenuItem exportDataList = new JMenuItem(JavaperfContent.EXPORT_DATA_LIST);
            exportDataList.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.EXPORT_ICON_PATH));
            if (("memoryGump").equals(dataType)) {
                id = selectMemoryDump.getId();
                name = selectMemoryDump.getAlias();
                uId = selectMemoryDump.getCreatedBy();
                type = JavaPerfTreeAction.ImportType.REPORT_LIST_MEMORY_DUMP;
            } else if (("gcLog").equals(dataType)) {
                id = selectGcLog.getId();
                name = selectGcLog.getLogName();
                uId = selectGcLog.getCreatedBy();
                type = JavaPerfTreeAction.ImportType.REPORT_LIST_GC_LOGS;
            } else {
                id = selectThreadDump.getId();
                name = selectThreadDump.getReportName();
                uId = selectThreadDump.getCreatedBy();
                type = JavaPerfTreeAction.ImportType.REPORT_LIST_THREAD_DUMP;
            }
            String userName = getUserName(uId);
            if (UserInfoContext.getInstance().getLoginId().equals(uId)) {
                popupMenu.add(exportDataList);
            }
            popupMenu.add(deleteDataList);
            // 删除数据列表
            deleteDataList.addActionListener(
                    event -> {
                        JavaPerfTreeAction.instance()
                                .openCommonDialog(dataType, id, nodes[nodes.length - 1], userName);
                        exportTask.setEnabled(false);
                    });
            exportDataList.addActionListener(
                    event -> {
                        JavaPerfTreeAction.instance().exportDataListFile(id, name, type);
                    });
            // 在指定位置显示弹出菜单
            popupMenu.show(invoker, xCoordinates, yCoordinates);
        }
    }

    private void excuteRefreshDataList(String type) {
        switch (type) {
            case "memoryGump":
                refreshData(rootNode.getChildAt(3).getChildAt(0), "memory");
                break;
            case "gcLog":
                refreshData(rootNode.getChildAt(3).getChildAt(2), "gclog");
                break;
            default:
                refreshData(rootNode.getChildAt(3).getChildAt(1), "thread");
        }
    }

    private void showOnlinePopupMenu(String node, Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem exportAnalysisItem = new JMenuItem(JavaperfContent.EXPORT_ANALYSIS_RECORDS);
        JMenuItem stopAnalysisItem = new JMenuItem(JavaperfContent.STOP_ANALYZING_RECORDS);
        exportAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.EXPORT_ICON_PATH));
        stopAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.STOP_ICON_PATH));
        // 添加 一级菜单 到 弹出菜单
        if (!node.contains(JavaperfContent.DATA_LIST_IMPORT_TIME)) {
            popupMenu.add(exportAnalysisItem);
            popupMenu.add(stopAnalysisItem);
        }
        // 导出在线分析记录
        exportAnalysisItem.addActionListener(
                event -> JavaPerfTreeAction.instance().exportProfiling());
        // 停止在线分析记录
        stopAnalysisItem.addActionListener(
                event -> {
                JavaPerfTreeAction.instance().stopProfilingTask();
                exportTask.setEnabled(false);});
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showSamplingPopupMenu(Component invoker, int xCoordinates, int yCoordinates, String[] nodes) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem exportAnalysisItem = new JMenuItem(JavaperfContent.EXPORT_ANALYSIS_RECORDS);
        JMenuItem deleteAnalysisItem = new JMenuItem(JavaperfContent.DELETE);
        exportAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.EXPORT_ICON_PATH));
        deleteAnalysisItem.setIcon(BaseIntellijIcons.load(LeftTreeCellRenderJava.DELETE_ICON_PATH));
        // 添加 一级菜单 到 弹出菜单
        if (selectSamplingTask.getCreatedBy().equals(UserInfoContext.getInstance().getLoginId())) {
            popupMenu.add(exportAnalysisItem);
        }
        popupMenu.add(deleteAnalysisItem);

        // 导出采样分析记录
        exportAnalysisItem.addActionListener(
                export -> {
                    String fileId = JavaPerfToolWindowPanel.selectSamplingTask.getId();
                    String fileName = JavaPerfToolWindowPanel.selectSamplingTask.getName();
                    // 处理带有目录的文件
                    if (StringUtils.contains(fileName, "/")) {
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    }
                    JavaPerfTreeAction.instance().downloadSamplingAnalysisFile(fileId, fileName);
                });
        // 删除采样分析记录
        deleteAnalysisItem.addActionListener(
                delete -> {
                    id = JavaPerfToolWindowPanel.selectSamplingTask.getId();
                    uId = JavaPerfToolWindowPanel.selectSamplingTask.getCreatedBy();
                    String userName = getUserName(uId);
                    JavaPerfTreeAction.instance()
                            .openCommonDialog("sampling", id, nodes[nodes.length - 1], userName);
                    // 刷新左侧树
                    refreshSamplingUser(rootNode.getChildAt(2), false);
                    exportTask.setEnabled(false);
                });
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void addActionButton() {
        // 添加目标环境
        AnActionButton addTargetEnvironment =
                AnActionButton.fromAction(ActionManager.getInstance().getAction("javaperf.environment"));
        addTargetEnvironment.getTemplatePresentation().setText(JavaperfContent.ADD_TARGET_ENVIRONMENT);
        // 目标环境管理
        AnActionButton onlineAnalysis =
                AnActionButton.fromAction(ActionManager.getInstance().getAction("javaperf.environmentmanage"));
        onlineAnalysis.getTemplatePresentation().setText(GuardianMangerConstant.DISPLAY_NAME);
        // 分析记录管理
        AnActionButton samplingAnalysis =
                AnActionButton.fromAction(ActionManager.getInstance().getAction("javaperf.sampling"));
        samplingAnalysis.getTemplatePresentation().setText(JavaperfContent.SAMPLING_ANALYSIS);
        samplingAnalysis.setVisible(false); // 暂时不做展示
        // 导入
        importTask = AnActionButton.fromAction(ActionManager.getInstance().getAction("javaperf.import"));
        importTask.setEnabled(false);
        importTask.getTemplatePresentation().setText(JavaperfContent.IMPORT_ANALYSIS_RECORDS);
        // 导出
        exportTask = AnActionButton.fromAction(ActionManager.getInstance().getAction("javaperf.export"));
        exportTask.getTemplatePresentation().setText(JavaperfContent.EXPORT_ANALYSIS_RECORDS);

        ToolbarDecorator toolbarDecorator =
                ToolbarDecorator.createDecorator(javaTree)
                        .initPosition()
                        .addExtraAction(addTargetEnvironment)
                        .addExtraAction(onlineAnalysis)
                        .addExtraAction(samplingAnalysis)
                        .addExtraAction(importTask)
                        .addExtraAction(exportTask);
        decoratorPanel = toolbarDecorator.createPanel();
        treePanel.add(decoratorPanel, BorderLayout.BEFORE_FIRST_LINE);
    }

    private void setSelectMember(String nodeName) {
        String nodeIp;
        String userName;
        String[] nodeNameArr = nodeName.split("\\(");
        if (nodeNameArr.length > 1) {
            userName = nodeNameArr[1].split("\\)")[0];
        } else {
            userName = UserInfoContext.getInstance().getUserName();
        }
        nodeIp = nodeNameArr[0];
        List<Members> newMembersList = JavaProjectServer.getAllGuardian();
        for (Members member : newMembersList) {
            if (nodeIp.equals(member.getName()) && userName.equals(member.getOwner().getUsername())) {
                selectMember = member;
                return;
            }
        }
    }

    /**
     * 定时刷新左侧树
     */
    private void initTimerTask() {
        Object pathRowComp = pathForRow.getLastPathComponent();
        if (!(pathRowComp instanceof TreeNode)) {
            return;
        }
        rootNode = ((TreeNode) pathRowComp);
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (UserInfoContext.getInstance().getUserName() == null) {
                            timer.cancel();
                        }
                        refreshEnvironment(rootNode.getChildAt(0));
                        refreshJavaTreePanel();
                    }
                },
                0,
                1500);
        timer2.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (UserInfoContext.getInstance().getUserName() == null) {
                            timer2.cancel();
                        }
                        excuteTimer();
                        refreshJavaTreePanel();
                    }
                },
                0,
                1500);
        timer3.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (UserInfoContext.getInstance().getUserName() == null) {
                            timer3.cancel();
                        }
                        JavaPerfHandler.refreshGuardinsByIDE();
                        JavaPerfHandler.sendDiscAlarm();
                        if ("running".equals(stopProfiling)) {
                            JavaPerfHandler.queryGuardinsByIDE();
                        }
                    }
                },
                0,
                10000);
    }

    private void excuteTimer() {
        if (JavaPerfTreeAction.isImport) {
            String refreshType = getDataType(JavaPerfTreeAction.instance().importTypeStr);
            if ("sampling".equals(type)) {
                refreshSamplingUser(rootNode.getChildAt(2), false);
            } else {
                excuteRefreshDataList(refreshType);
            }
            JavaPerfTreeAction.isImport = false;
        }
        if (JavaPerfTreeAction.isDeleteDataList) {
            JavaPerfTreeAction.isDeleteDataList = false;
            excuteRefreshDataList(dataType);
            JavaPerfHandler.updateReportConfig(dataType, "delete");
        }
        if (JavaPerfHandler.isSave) {
            refreshDataList();
            JavaPerfHandler.isSave = false;
        }
        if (isCreateSampling) {
            isCreateSampling = false;
            refreshSamplingUser(rootNode.getChildAt(2), false);
        }
    }

    private String getDataType(String myType) {
        String refreshtype;
        switch (myType) {
            case "SAMPLING_ANALYSIS":
                refreshtype = "sampling";
                break;
            case "REPORT_LIST_THREAD_DUMP":
                refreshtype = "thread";
                break;
            case "REPORT_LIST_MEMORY_DUMP":
                refreshtype = "memoryGump";
                break;
            default:
                refreshtype = "gcLog";
        }
        return refreshtype;
    }

    private void refreshDataList() {
        refreshData(rootNode.getChildAt(3).getChildAt(0), "memory");
        refreshData(rootNode.getChildAt(3).getChildAt(2), "gclog");
        refreshData(rootNode.getChildAt(3).getChildAt(1), "thread");
    }

    private static void refreshJavaTreePanel() {
        if (javaTree != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                javaTree.updateUI();
                javaTree.validate();
                javaTree.repaint();
            });
        }
    }

    private void refreshEnvironment(TreeNode environRoot) {
        // 根据接口重新获取环境列表
        List<Members> newMembersList = JavaProjectServer.getAllGuardian();
        USER_ENVIRONMENT_LIST.clear();
        USER_ENVIRONMENT_LIST.addAll(newMembersList);
        if (!(environRoot instanceof DefaultMutableTreeNode)) {
            return;
        }
        DefaultMutableTreeNode environDMTRoot = (DefaultMutableTreeNode) environRoot;
        // 与已有的环境进行比对
        Enumeration children = environDMTRoot.children();
        while (children.hasMoreElements()) {
            Object nextElement = children.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode childMTNode = (DefaultMutableTreeNode) nextElement;
            boolean environExistFlag = false;
            String newName;
            for (Members newMembers : newMembersList) {
                if (isAdmin) {
                    if (newMembers.getOwner() == null) {
                        return;
                    }
                    newName =
                            "tunadmin".equals(newMembers.getOwner().getUsername())
                                    ? newMembers.getName()
                                    : newMembers.getName() + '(' + newMembers.getOwner().getUsername() + ')';
                } else {
                    newName = newMembers.getName();
                }
                if (newName.equals(childMTNode.getUserObject())) {
                    environExistFlag = true;
                    break;
                }
            }
            if (!environExistFlag) {
                // 新的环境中已不存在该环境节点。从树中删除该节点
                environDMTRoot.remove(childMTNode);
            }
        }
        // 将新增的环境添加到树中
        Enumeration<TreeNode> addChildren = environDMTRoot.children();
        List<Members> addMembersList = addMembers(addChildren, newMembersList);
        inflateTreeNode(environDMTRoot, addMembersList);
    }

    private void inflateTreeNode(DefaultMutableTreeNode environDMTRoot, List<Members> addMembersList) {
        for (Members members : addMembersList) {
            DefaultMutableTreeNode childRecord = new DefaultMutableTreeNode();
            if (isAdmin) {
                if ("tunadmin".equals(members.getOwner().getUsername())) {
                    childRecord.setUserObject(members.getName());
                } else {
                    childRecord.setUserObject(members.getName() + '(' + members.getOwner().getUsername() + ')');
                }
            } else {
                childRecord.setUserObject(members.getName());
            }
            environDMTRoot.add(childRecord);
        }
    }

    private List<Members> addMembers(Enumeration<?> taskChildren, List<Members> newUserList) {
        List<Members> list = new ArrayList<>();
        List<String> oldListKey = obtainNodeFrom(taskChildren);
        String newMembersKey;
        for (Members members : newUserList) {
            if (isAdmin) {
                if ("tunadmin".equals(members.getOwner().getUsername())) {
                    newMembersKey = members.getName();
                } else {
                    newMembersKey = members.getName() + '(' + members.getOwner().getUsername() + ')';
                }
            } else {
                newMembersKey = members.getName();
            }
            if (!oldListKey.contains(newMembersKey)) {
                list.add(members);
            }
        }
        return list;
    }

    private void refreshSamplingUser(TreeNode samplingNode, boolean isFirstLoad) {
        if (!(samplingNode instanceof DefaultMutableTreeNode)) {
            return;
        }
        DefaultMutableTreeNode samplingRecord = (DefaultMutableTreeNode) samplingNode;
        Owner selfUser = new Owner();
        selfUser.setUsername(UserInfoContext.getInstance().getUserName());
        selfUser.setUid(UserInfoContext.getInstance().getLoginId());
        if (isAdmin) {
            refreshSamplingUserAdmin(samplingRecord, JavaProjectServer.getAllUser());
        } else {
            refreshSamplingTask(samplingRecord, selfUser);
        }
        refreshJavaTreePanel();
    }

    private void refreshSamplingUserAdmin(DefaultMutableTreeNode samplingRecord, List<Owner> newUserList) {
        // 管理员。获取所有人的
        for (Owner newUser : newUserList) {
            USERMAP.put(newUser.getUsername(), newUser);
        }
        Enumeration<TreeNode> children = samplingRecord.children();
        List<DefaultMutableTreeNode> needRemovechildMTNode = new ArrayList<>();
        while (children.hasMoreElements()) {
            // 便利采样分析-子节点：每个用户
            Object nextElement = children.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode childMTNode = (DefaultMutableTreeNode) nextElement;
            boolean userExistFlag = false;
            userExistFlag = isUserExistFlag(newUserList, needRemovechildMTNode, childMTNode, userExistFlag);
            if (!userExistFlag) {
                // 删除不存在用户及其任务列表
                samplingRecord.remove(childMTNode);
                USER_TASK.remove(childMTNode.getUserObject().toString());
            }
        }
        for (DefaultMutableTreeNode child : needRemovechildMTNode) {
            samplingRecord.remove(child);
        }
        Enumeration<TreeNode> addChildren = samplingRecord.children();
        List<Owner> addUserList = isAddUser(addChildren, newUserList);
        // 将新增的用户添加到树中
        for (Owner newUser : addUserList) {
            List<SamplingTaskInfo> samplingTaskInfoList = JavaProjectServer.getUserRecord(newUser.getUid());
            DefaultMutableTreeNode childRecord = new DefaultMutableTreeNode();
            childRecord.setUserObject(JavaperfContent.DATA_LIST_USER + "(" + newUser.getUsername() + ")");
            // 获取新添加用户的任务
            if (samplingTaskInfoList.size() > 0) {
                samplingRecord.add(childRecord);
                loadSamplingResp(childRecord, samplingTaskInfoList);
                USER_TASK.put(JavaperfContent.DATA_LIST_USER + "(" + newUser.getUsername() + ")", samplingTaskInfoList);
            }
        }
        isFirstSlamplingLoad = false;
    }

    private boolean isUserExistFlag(List<Owner> newUserList, List<DefaultMutableTreeNode> needRemovechildMTNode,
        DefaultMutableTreeNode childMTNode, boolean userExistFlag) {
        boolean isUserExistFlag = userExistFlag;
        for (Owner newUser : newUserList) {
            if ((JavaperfContent.DATA_LIST_USER + "(" + newUser.getUsername() + ")")
                    .equals(childMTNode.getUserObject())) {
                isUserExistFlag = true;
                boolean idExpanded = javaTree.isExpanded(new TreePath(childMTNode.getPath()));
                if (idExpanded || childMTNode.getChildCount() == 0 || isFirstSlamplingLoad) {
                    if (refreshSamplingTask(childMTNode, newUser)) {
                        needRemovechildMTNode.add(childMTNode);
                    }
                }
                break;
            }
        }
        return isUserExistFlag;
    }

    private List<Owner> isAddUser(Enumeration<?> taskChildren, List<Owner> newUserList) {
        List<Owner> list = new ArrayList<>();
        List<String> oldListKey = obtainNodeFrom(taskChildren);
        for (Owner owner : newUserList) {
            if (!oldListKey.contains(JavaperfContent.DATA_LIST_USER + "(" + owner.getUsername() + ")")) {
                list.add(owner);
            }
        }
        return list;
    }

    private void updataSamplingStata(List<SamplingTaskInfo> taskList) {
        for (SamplingTaskInfo samplingTaskInfo : taskList) {
            if (!"FINISHED".equals(samplingTaskInfo.getState())) {
                isCreateSampling = true;
            }
        }
    }

    /**
     * 刷新单个用户的采样任务列表,获取该用户的所有任务列表，对已有的节点进行比对，删除失效，添加新增
     *
     * @param userNode 待刷新的用户节点
     * @param user     待刷新的用户对象
     * @return 是否刪除节点
     */
    protected boolean refreshSamplingTask(TreeNode userNode, Owner user) {
        if (!(userNode instanceof DefaultMutableTreeNode)) {
            return false;
        }
        DefaultMutableTreeNode userDMTNode = (DefaultMutableTreeNode) userNode;
        List<SamplingTaskInfo> newTaskList;
        if (isAdmin) {
            newTaskList = JavaProjectServer.getUserRecord(user.getUid());
            USER_TASK.put(JavaperfContent.DATA_LIST_USER + "(" + user.getUsername() + ")", newTaskList);
        } else {
            newTaskList = JavaProjectServer.getUserRecord();
            USER_TASK.put(user.getUsername(), newTaskList);
        }
        if (newTaskList.size() == 0) {
            ((DefaultMutableTreeNode) userNode).removeAllChildren();
            return true;
        }
        // 采样任务未完成，则一直刷新
        updataSamplingStata(newTaskList);
        List<SamplingTaskInfo> deleteList = new ArrayList<>(newTaskList);
        List<DefaultMutableTreeNode> removeNode = new ArrayList<>();
        Enumeration<TreeNode> children = userDMTNode.children();
        while (children.hasMoreElements()) {
            if (refreshSamplingChild(deleteList, removeNode, children)) {
                return false;
            }
        }
        for (DefaultMutableTreeNode node : removeNode) {
            // 删除不存在任务节点
            userDMTNode.remove(node);
        }
        Enumeration<TreeNode> addChildren = userDMTNode.children();
        List<SamplingTaskInfo> addSamplingTask = isAddSamplingTask(addChildren, newTaskList);
        // 将新增的任务添加到树中
        for (SamplingTaskInfo newTask : addSamplingTask) {
            DefaultMutableTreeNode childRecord = new DefaultMutableTreeNode();
            String createType = "UPLOAD".equalsIgnoreCase(newTask.getSource())
                    ? JavaperfContent.DATA_LIST_IMPORT_TIME : JavaperfContent.DATA_LIST_CREATE_TIME;
            childRecord.setUserObject(newTask.getName() + createType +
                    DateUtil.getInstance().createTimeStr(newTask.getCreateTime()));
            userDMTNode.add(childRecord);
        }
        return false;
    }

    private boolean refreshSamplingChild(List<SamplingTaskInfo> deleteList, List<DefaultMutableTreeNode> removeNode,
        Enumeration<TreeNode> children) {
        Object nextElement = children.nextElement();
        if (!(nextElement instanceof DefaultMutableTreeNode)) {
            return true;
        }
        DefaultMutableTreeNode childMTNode = (DefaultMutableTreeNode) nextElement;
        boolean taskExistFlag = false;
        for (SamplingTaskInfo newTask : deleteList) {
            if (childMTNode.getUserObject().equals(newTask.getName() + JavaperfContent.DATA_LIST_CREATE_TIME
                    + DateUtil.getInstance().createTimeStr(newTask.getCreateTime()))
                    || childMTNode.getUserObject().equals(newTask.getName() + JavaperfContent.DATA_LIST_IMPORT_TIME
                    + DateUtil.getInstance().createTimeStr(newTask.getCreateTime()))) {
                taskExistFlag = true;
                break;
            }
        }
        if (!taskExistFlag) {
            removeNode.add(childMTNode);
        }
        return false;
    }

    private List<SamplingTaskInfo> isAddSamplingTask(Enumeration taskChildren, List<SamplingTaskInfo> newTaskList) {
        List<SamplingTaskInfo> list = new ArrayList<>();
        List<String> oldListKey = obtainNodeFrom(taskChildren);
        for (SamplingTaskInfo samplingTaskInfo : newTaskList) {
            if (!oldListKey.contains(
                    samplingTaskInfo.getName()
                            + JavaperfContent.DATA_LIST_CREATE_TIME
                            + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()))
                    && !oldListKey.contains(
                    samplingTaskInfo.getName()
                            + JavaperfContent.DATA_LIST_IMPORT_TIME
                            + DateUtil.getInstance().createTimeStr(samplingTaskInfo.getCreateTime()))) {
                list.add(samplingTaskInfo);
            }
        }
        return list;
    }

    private List<String> obtainNodeFrom(Enumeration<?> taskChildren) {
        List<String> oldListKey = new ArrayList<>();
        while (taskChildren.hasMoreElements()) {
            Object nextElement = taskChildren.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                continue;
            }
            DefaultMutableTreeNode taskChild = (DefaultMutableTreeNode) nextElement;
            String taskNodeName = taskChild.getUserObject().toString().trim();
            oldListKey.add(taskNodeName);
        }
        return oldListKey;
    }

    private void refreshData(TreeNode dataNode, String type) {
        if (!(dataNode instanceof DefaultMutableTreeNode)) {
            return;
        }
        DefaultMutableTreeNode parentNode = null;
        if (dataNode instanceof DefaultMutableTreeNode) {
            parentNode = (DefaultMutableTreeNode) dataNode;
        }
        if (isAdmin) {
            List<Owner> newUserList = JavaProjectServer.getAllUser();
            // 內存转储
            refreshAdmin(parentNode, newUserList, type);
        } else {
            switch (type) {
                case "memory":
                    // 內存转储
                    refreshMemory(parentNode, JavaProjectServer.getUserMemoryDumpReports());
                    break;
                case "thread":
                    // 內存转储
                    refreshThread(parentNode, JavaProjectServer.getUserThreadDumpReports());
                    break;
                default:
                    refreshGcLog(parentNode, JavaProjectServer.getUserDcLogsReports());
                    break;
            }
        }
        refreshJavaTreePanel();
    }

    private void refreshAdmin(DefaultMutableTreeNode threadRoot, List<Owner> newUserList, String thread2) {
        refreshDataListUser(threadRoot, newUserList, thread2);
    }

    private void refreshDataListUser(DefaultMutableTreeNode node, List<Owner> newUserList, String type) {
        Enumeration<TreeNode> childrenNode = node.children();
        List<String> oldUserList = new ArrayList<>();
        List<DefaultMutableTreeNode> needRemoveChild = new ArrayList<>();
        // 删除用户
        while (childrenNode.hasMoreElements()) {
            Object nextElement = childrenNode.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nextElement;
            String childName = child.getUserObject().toString();
            String userId = "";
            for (Owner owner : newUserList) {
                if (childName.equals(JavaperfContent.DATA_LIST_USER + "(" + owner.getUsername() + ")")) {
                    userId = owner.getUid();
                    oldUserList.add(childName);
                    break;
                }
            }
            if (!javaTree.isExpanded(new TreePath(child.getPath())) && child.getChildCount() > 0 ||
                    isFirstLoad || userId.isEmpty()) {
                continue;
            }
            if (refreshUserChild(child, userId, type)) {
                needRemoveChild.add(child);
            }
        }
        for (DefaultMutableTreeNode child : needRemoveChild) {
            node.remove(child);
        }
        // 新增用户
        for (Owner owner : newUserList) {
            if (!oldUserList.contains(JavaperfContent.DATA_LIST_USER + "(" + owner.getUsername() + ")")) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
                newNode.setUserObject(JavaperfContent.DATA_LIST_USER + "(" + owner.getUsername() + ")");
                refreshUserAddChild(newNode, owner.getUid(), type, node);
            }
        }
        isFirstLoad = false;
    }

    private boolean refreshUserChild(DefaultMutableTreeNode child, String userId, String type) {
        if ("thread".equals(type)) {
            List<ThreadDumpReports> list = JavaProjectServer.getUserThreadDumpReports(userId);
            if (list.size() > 0) {
                refreshThread(child, list);
                return false;
            }
        } else if ("memory".equals(type)) {
            List<MemoryDumpReprots> list = JavaProjectServer.getUserMemoryDumpReports(userId);
            if (list.size() > 0) {
                refreshMemory(child, list);
                return false;
            }
        } else {
            List<GcLog> list = JavaProjectServer.getUserDcLogsReports(userId);
            if (list.size() > 0) {
                refreshGcLog(child, list);
                return false;
            }
        }
        child.removeAllChildren();
        return true;
    }

    private void refreshUserAddChild(DefaultMutableTreeNode child, String userId, String type,
        DefaultMutableTreeNode parentNode) {
        if ("thread".equals(type)) {
            List<ThreadDumpReports> list = JavaProjectServer.getUserThreadDumpReports(userId);
            if (list.size() > 0) {
                refreshThread(child, list);
                parentNode.add(child);
            }
        } else if ("memory".equals(type)) {
            List<MemoryDumpReprots> list = JavaProjectServer.getUserMemoryDumpReports(userId);
            if (list.size() > 0) {
                refreshMemory(child, list);
                parentNode.add(child);
            }
        } else {
            List<GcLog> list = JavaProjectServer.getUserDcLogsReports(userId);
            if (list.size() > 0) {
                refreshGcLog(child, list);
                parentNode.add(child);
            }
        }
    }

    private void refreshMemory(DefaultMutableTreeNode node, List<MemoryDumpReprots> newMemoryList) {
        Enumeration<TreeNode> childrenNode = node.children();
        List<String> oldMemory = new ArrayList<>();
        List<DefaultMutableTreeNode> removeNode = new ArrayList<>();
        // 删除內存转储节点
        while (childrenNode.hasMoreElements()) {
            Object nextElement = childrenNode.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nextElement;
            String childName = child.getUserObject().toString();
            boolean isDeleteNode = true;
            for (MemoryDumpReprots memoryDumpReprots : newMemoryList) {
                if (childName.equals(
                        memoryDumpReprots.getAlias()
                                + JavaperfContent.DATA_LIST_CREATE_TIME
                                + DateUtil.getInstance().getLongToTime(memoryDumpReprots.getCreateTime()))) {
                    isDeleteNode = false;
                }
                if (childName.equals(
                        memoryDumpReprots.getAlias()
                                + JavaperfContent.DATA_LIST_IMPORT_TIME
                                + DateUtil.getInstance().getLongToTime(memoryDumpReprots.getCreateTime()))) {
                    isDeleteNode = false;
                }
            }
            if (isDeleteNode) {
                removeNode.add(child);
                MEMORY_DUMP_MAP.remove(childName);
            }
            oldMemory.add(childName);
        }
        // 删除节点
        for (DefaultMutableTreeNode child : removeNode) {
            node.remove(child);
        }
        addNodes(newMemoryList, oldMemory, node);
    }

    private void addNodes(List<MemoryDumpReprots> newMemoryList, List<String> oldMemory, DefaultMutableTreeNode node) {
        // 新增节点
        String creatRecord;
        String importRecord;
        for (MemoryDumpReprots memoryDumpReprots : newMemoryList) {
            creatRecord =
                    memoryDumpReprots.getAlias()
                            + JavaperfContent.DATA_LIST_CREATE_TIME
                            + DateUtil.getInstance().getLongToTime(memoryDumpReprots.getCreateTime());
            importRecord =
                    memoryDumpReprots.getAlias()
                            + JavaperfContent.DATA_LIST_IMPORT_TIME
                            + DateUtil.getInstance().getLongToTime(memoryDumpReprots.getCreateTime());
            if (!oldMemory.contains(creatRecord) && !oldMemory.contains(importRecord)) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
                if ("IMPORT".equalsIgnoreCase(memoryDumpReprots.getSource())) {
                    newNode.setUserObject(importRecord);
                    MEMORY_DUMP_MAP.put(importRecord, memoryDumpReprots);
                } else {
                    newNode.setUserObject(creatRecord);
                    MEMORY_DUMP_MAP.put(creatRecord, memoryDumpReprots);
                }
                node.add(newNode);
            }
        }
    }

    private void refreshThread(DefaultMutableTreeNode node, List<ThreadDumpReports> newThreadList) {
        Enumeration<TreeNode> childrenNode = node.children();
        List<String> oldThread = new ArrayList<>();
        List<DefaultMutableTreeNode> removeNode = new ArrayList<>();
        // 删除线程转储节点
        while (childrenNode.hasMoreElements()) {
            Object nextElement = childrenNode.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nextElement;
            String childName = child.getUserObject().toString();
            boolean isDeleteNode = true;
            for (ThreadDumpReports threadDumpReports : newThreadList) {
                if (childName.equals(
                        threadDumpReports.getReportName()
                                + JavaperfContent.DATA_LIST_CREATE_TIME
                                + DateUtil.getInstance().getLongToTime(threadDumpReports.getCreateTime()))) {
                    isDeleteNode = false;
                }
                if (childName.equals(
                        threadDumpReports.getReportName()
                                + JavaperfContent.DATA_LIST_IMPORT_TIME
                                + DateUtil.getInstance().getLongToTime(threadDumpReports.getCreateTime()))) {
                    isDeleteNode = false;
                }
            }
            if (isDeleteNode) {
                removeNode.add(child);
                THREAD_DUMP_MAP.remove(childName);
            }
            oldThread.add(childName);
        }
        // 删除节点
        for (DefaultMutableTreeNode child : removeNode) {
            node.remove(child);
        }
        // 新增线程转储
        addThreadDump(newThreadList, oldThread, node);
    }

    private void addThreadDump(
            List<ThreadDumpReports> newThreadList, List<String> oldThread, DefaultMutableTreeNode node) {
        String creatRecord;
        String importRecord;
        for (ThreadDumpReports threadDumpReports : newThreadList) {
            creatRecord =
                    threadDumpReports.getReportName()
                            + JavaperfContent.DATA_LIST_CREATE_TIME
                            + DateUtil.getInstance().getLongToTime(threadDumpReports.getCreateTime());
            importRecord =
                    threadDumpReports.getReportName()
                            + JavaperfContent.DATA_LIST_IMPORT_TIME
                            + DateUtil.getInstance().getLongToTime(threadDumpReports.getCreateTime());
            if (!oldThread.contains(creatRecord) && !oldThread.contains(importRecord)) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
                if ("IMPORT".equalsIgnoreCase(threadDumpReports.getReportSource())) {
                    newNode.setUserObject(importRecord);
                    THREAD_DUMP_MAP.put(importRecord, threadDumpReports);
                } else {
                    newNode.setUserObject(creatRecord);
                    THREAD_DUMP_MAP.put(creatRecord, threadDumpReports);
                }
                node.add(newNode);
            }
        }
    }

    private void refreshGcLog(DefaultMutableTreeNode node, List<GcLog> newGcLogList) {
        Enumeration<TreeNode> childrenNode = node.children();
        List<String> oldGc = new ArrayList<>();
        List<DefaultMutableTreeNode> removeNode = new ArrayList<>();
        // 删除內存转储节点
        while (childrenNode.hasMoreElements()) {
            Object nextElement = childrenNode.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nextElement;
            String childName = child.getUserObject().toString();
            boolean isDeleteNode = true;
            for (GcLog gcLog : newGcLogList) {
                if (childName.equals(
                        gcLog.getLogName()
                                + JavaperfContent.DATA_LIST_CREATE_TIME
                                + DateUtil.getInstance().getLongToTime(gcLog.getCreateTime()))) {
                    isDeleteNode = false;
                }
                if (childName.equals(
                        gcLog.getLogName()
                                + JavaperfContent.DATA_LIST_IMPORT_TIME
                                + DateUtil.getInstance().getLongToTime(gcLog.getCreateTime()))) {
                    isDeleteNode = false;
                }
            }
            if (isDeleteNode) {
                removeNode.add(child);
                GC_LOG_MAP.remove(childName);
            }
            oldGc.add(childName);
        }
        // 删除节点
        for (DefaultMutableTreeNode child : removeNode) {
            node.remove(child);
        }
        // 添加GC日志树节点
        addGcNodes(newGcLogList, oldGc, node);
    }

    private void addGcNodes(List<GcLog> newGcLogList, List<String> oldMemory, DefaultMutableTreeNode node) {
        String creatRecord;
        String importRecord;
        for (GcLog gcLog : newGcLogList) {
            creatRecord =
                    gcLog.getLogName()
                            + JavaperfContent.DATA_LIST_CREATE_TIME
                            + DateUtil.getInstance().getLongToTime(gcLog.getCreateTime());
            importRecord =
                    gcLog.getLogName()
                            + JavaperfContent.DATA_LIST_IMPORT_TIME
                            + DateUtil.getInstance().getLongToTime(gcLog.getCreateTime());
            // 新增
            if (!oldMemory.contains(creatRecord) && !oldMemory.contains(importRecord)) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
                if ("IMPORT".equalsIgnoreCase(gcLog.getReportSource())) {
                    newNode.setUserObject(importRecord);
                    GC_LOG_MAP.put(importRecord, gcLog);
                } else {
                    newNode.setUserObject(creatRecord);
                    GC_LOG_MAP.put(creatRecord, gcLog);
                }
                node.add(newNode);
            }
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    private void respDataList(String nodeParent, String[] nodes, MouseEvent event) {
        if (nodeParent.equals(JavaperfContent.DATA_LIST_MEMORY_DUMP)) {
            type = JavaPerfTreeAction.ImportType.REPORT_LIST_MEMORY_DUMP;
            dataType = "memoryGump";
            selectMemoryDump = MEMORY_DUMP_MAP.get(nodes[nodes.length - 1].trim());
            if (selectMemoryDump == null) {
                return;
            }
            uId = selectMemoryDump.getCreatedBy();
        } else if (nodeParent.equals(JavaperfContent.DATA_LIST_GC_LOGS)) {
            type = JavaPerfTreeAction.ImportType.REPORT_LIST_GC_LOGS;
            dataType = "gcLog";
            selectGcLog = GC_LOG_MAP.get(nodes[nodes.length - 1].trim());
            if (selectGcLog == null) {
                return;
            }
            uId = selectGcLog.getCreatedBy();
        } else {
            type = JavaPerfTreeAction.ImportType.REPORT_LIST_THREAD_DUMP;
            dataType = "threadGump";
            selectThreadDump = THREAD_DUMP_MAP.get(nodes[nodes.length - 1].trim());
            if (selectThreadDump == null) {
                return;
            }
            uId = selectThreadDump.getCreatedBy();
        }
        if (UserInfoContext.getInstance().getLoginId().equals(uId)) {
            importTask.setEnabled(false);
            exportTask.setEnabled(true);
        }
        leftMouseClickTip(uId, event, dataType, nodes[nodes.length - 1].trim());
        if (event.isPopupTrigger()) { // 如果是鼠标右键，则显示弹出菜单
            showDataListPopupMenu(event.getComponent(), event.getX(), event.getY(), nodes);
        } else {
            if (UserInfoContext.getInstance().getLoginId().equals(uId)) {
                JavaPerfTreeAction.instance().showDataDetail(dataType);
            }
        }
    }

    private String getUserName(String id) {
        String userName = "";
        for (Owner owner : getUserMap().values()) {
            if (id.equals(owner.getUid())) {
                userName = owner.getUsername();
                break;
            }
        }
        return userName;
    }

    private void leftMouseClickTip(String uId, MouseEvent event, String dataType, String nodeName) {
        if (UserInfoContext.getInstance().getLoginId().equals(uId) && !event.isMetaDown()) {
            JavaPerfTreeAction.instance().showDataDetail(dataType);
            return;
        }
        if (!StringUtils.isEmpty(dataType) && !event.isMetaDown()) {
            String deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_open_report_user_tip");
            deleteContent = MessageFormat.format(deleteContent, nodeName);
            IDENotificationUtil.notificationCommon(new NotificationBean("", deleteContent, NotificationType.WARNING));
            return;
        }
        if (StringUtils.isEmpty(dataType)) {
            String deleteContent = TuningI18NServer.toLocale("plugins_hyper_tuner_javaperf_open_report_member_tip");
            IDENotificationUtil.notificationCommon(new NotificationBean("", deleteContent, NotificationType.WARNING));
        }
    }

    /**
     * 目标环境添加成功后，展开左侧树，打野进程页面
     *
     * @param guardiansId guardiansId
     */
    public static void addGuardiansFinally(String guardiansId) {
        List<Members> list = JavaProjectServer.getAllGuardian();
        for (Members members : list) {
            if (guardiansId.equals(members.getId())) {
                selectMember = members;
                JavaPerfTreeAction.instance().showGuardianProcess();
                USER_ENVIRONMENT_LIST.add(members);
                javaTree.expandRow(0);
                refreshJavaTreePanel();
            }
        }
    }
}
