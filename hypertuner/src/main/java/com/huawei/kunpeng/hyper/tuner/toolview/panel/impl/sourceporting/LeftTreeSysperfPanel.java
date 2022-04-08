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

import com.huawei.kunpeng.hyper.tuner.action.LeftTreeAction;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.SysperfContent;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.hyper.tuner.http.SysperfProjectServer;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.project.SysperfProject;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.NodeList;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.task.Tasklist;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.bean.NotificationBean;
import com.huawei.kunpeng.intellij.common.bean.ResponseBean;
import com.huawei.kunpeng.intellij.common.constant.UserManageConstant;
import com.huawei.kunpeng.intellij.common.util.IDENotificationUtil;
import com.huawei.kunpeng.intellij.common.util.StringUtil;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.enums.Panels;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.treeStructure.Tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Intellij 左侧树加载
 *
 * @since 2020-09-25
 */
public class LeftTreeSysperfPanel extends IDEBasePanel {
    /**
     * 是否新建任务
     */
    public static boolean isCreateTask = false;

    private static final long serialVersionUID = -2996358156928536128L;
    private static SysperfProject selectProject;
    private static Tasklist selectTask;
    private static SysperfProject newProject;
    private static Tasklist newTask;
    private static NodeList newNode;
    private static NodeList selectNode;
    private static Map<String, Tasklist> taskMap = new HashMap<>();
    private static Map<String, SysperfProject> projectMap = new HashMap<>();
    private static Map<String, NodeList> nodeMap = new HashMap<>();
    private static AnActionButton exportTask;
    /**
     * timer
     */
    public Timer timer = new Timer();

    private Tree tuningTree;
    private TreePath pathForRow;
    private JPanel mainPanel;
    private JPanel treePanel;
    private JPanel decoratorPanel;
    private String seleteTreePath;
    private AnActionButton createTask;
    private boolean clickProjectNode = false;
    private boolean clickNode = false;
    private int index;

    /**
     * 左侧树登录面板构造函数
     *
     * @param toolWindow 工具窗口
     * @param panelName  面板名称
     */
    public LeftTreeSysperfPanel(ToolWindow toolWindow, String panelName) {
        setToolWindow(toolWindow);
        this.panelName = StringUtil.stringIsEmpty(panelName) ? Panels.LEFT_TREE_SYSPERF.panelName() : panelName;

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
     */
    public LeftTreeSysperfPanel(ToolWindow toolWindow) {
        this(toolWindow, null);
    }

    @Override
    protected void initPanel(JPanel panel) {
        // 初始化项目树
        initProjectTree();
        initTimerTask();
        addActionButton();
        treePanel.add(decoratorPanel, BorderLayout.BEFORE_FIRST_LINE);
        treePanel.add(tuningTree);
        treeEvent();
    }

    /*
     * 节点被选中的监听器
     */
    private void treeEvent() {
        tuningTree.addTreeSelectionListener(
                event -> {
                    TreePath path = event.getPath();
                    seleteTreePath = path.toString();
                    String[] nodes = seleteTreePath.split(",");
                    Object lastSelectComp = tuningTree.getLastSelectedPathComponent();
                    if (!(lastSelectComp instanceof DefaultMutableTreeNode)) {
                        return;
                    }
                    String name = ((DefaultMutableTreeNode) lastSelectComp).toString();
                    // 判断目录树层级
                    if (nodes.length == 2) {
                        clickProjectNode = true;
                        selectProject = projectMap.get(name);
                        createTask.setEnabled(false);
                        menuIsChecked();
                        exportTask.setEnabled(false);
                    } else if (nodes.length == 3) {
                        selectTask = taskMap.get(nodes[1].trim() + name);
                        selectTask.setProjectName((nodes[1].trim()));
                        selectProject = projectMap.get(nodes[1].trim());
                        exportTask.setEnabled(true);
                        menuIsChecked();
                        createTask.setEnabled(false);
                    } else {
                        clickNode = true;
                        Object lastSelectedComp2 = tuningTree.getLastSelectedPathComponent();
                        if (!(lastSelectedComp2 instanceof DefaultMutableTreeNode)) {
                            return;
                        }
                        DefaultMutableTreeNode lastSelectedTreeNode = (DefaultMutableTreeNode) lastSelectedComp2;
                        if (lastSelectedTreeNode.getParent() == null) {
                            return;
                        }
                        String taskName = lastSelectedTreeNode.getParent().toString();
                        String projectName = lastSelectedTreeNode.getParent().getParent().toString();
                        refreshTaskContant(projectName);
                        selectProject = projectMap.get(projectName);
                        selectTask = taskMap.get(projectName + taskName);
                        selectNode = nodeMap.get(projectName + taskName + name);
                        createTask.setEnabled(false);
                        exportTask.setEnabled(false);
                        LeftTreeAction.instance().showTaskNode();
                    }
                });
        treeExpansionListener();
        mouseEvent();
    }

    /*
     * 节点展开/折叠监听器
     */
    private void treeExpansionListener() {
        tuningTree.addTreeExpansionListener(
                new TreeExpansionListener() {
                    @Override
                    public void treeExpanded(TreeExpansionEvent event) {
                        clickProjectNode = false;
                    }

                    @Override
                    public void treeCollapsed(TreeExpansionEvent event) {
                        clickProjectNode = false;
                    }
                });
    }

    private void mouseEvent() {
        tuningTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                final Component component = event.getComponent();
                if (component instanceof Tree) {
                    if (seleteTreePath == null) {
                        return;
                    }
                    String[] nodes = seleteTreePath.split(",");
                    setIndex(nodes);
                    if (event.isMetaDown()) { // 如果是鼠标右键，则显示弹出菜单
                        metaDownShowPopupMenuInfo(event);
                        return;
                    } else {
                        leftDownShowWebViewPage();
                    }
                }
            }
        });
    }

    private void leftDownShowWebViewPage() {
        if (index == 1) {
            showProjectInfo();
        } else if (index == 2) {
            return;
        } else {
            if (clickNode) {
                LeftTreeAction.instance().showTaskNode();
                clickNode = false;
            }
        }
    }

    private void showProjectInfo() {
        if (ValidateUtils.equals(selectProject.getOwnerName(),
                UserInfoContext.getInstance().getUserName())
                || ValidateUtils.equals(UserInfoContext.getInstance().getRole(), "Admin")) {
            if (!selectProject.isImport()) {
                if (clickProjectNode) {
                    LeftTreeAction.instance().showProjectInfo();
                } else {
                    clickProjectNode = true;
                }
            }
        }
    }

    private void metaDownShowPopupMenuInfo(MouseEvent event) {
        if (index == 1) {
            showProjectPopupMenu(event.getComponent(), event.getX(), event.getY());
        }
        if (index == 2) {
            showTaskPopupMenu(event.getComponent(), event.getX(), event.getY());
        }
    }

    private void setIndex(String[] nodes) {
        if (nodes.length == 2) {
            index = 1;
        } else if (nodes.length == 3) {
            index = 2;
        } else {
            index = 9;
        }
    }

    private void menuIsChecked() {
        createTask.setEnabled(selectProject.getOwnerName().equals(UserInfoContext.getInstance().getUserName()));
        // 管理员有所有任务的导出权限
        exportTask.setEnabled(selectProject.getOwnerName().equals(UserInfoContext.getInstance().getUserName())
                || UserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole()));
        if (selectProject.isImport()) {
            createTask.setEnabled(false);
            exportTask.setEnabled(false);
        }
    }

    private void showProjectPopupMenu(Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();
        // 创建 一级菜单
        JMenuItem createTaskItem = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_new_task"));
        JMenuItem editorProjectItem =
                new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_modify_project"));
        JMenuItem deleteProjectMenu =
                new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_delete_project"));
        // 添加 一级菜单 到 弹出菜单
        if (selectProject.getOwnerName().equals(UserInfoContext.getInstance().getUserName())) {
            if (selectProject.isImport()) {
                popupMenu.add(deleteProjectMenu);
            } else {
                popupMenu.add(createTaskItem);
                popupMenu.add(editorProjectItem);
                popupMenu.add(deleteProjectMenu);
            }
        } else if (UserInfoContext.getInstance().getRole().equals(TuningUserManageConstant.USER_ROLE_ADMIN)) {
            if (selectProject.isImport()) {
                popupMenu.add(deleteProjectMenu);
            } else {
                popupMenu.add(editorProjectItem);
                popupMenu.add(deleteProjectMenu);
            }
        } else {
            return;
        }

        // 添加任务
        createTaskItem.addActionListener(
                createTask -> LeftTreeAction.instance().createTask());
        // 编辑项目
        editorProjectItem.addActionListener(
                editorProject -> LeftTreeAction.instance().modifyProject());
        // 删除项目
        deleteProjectMenu.addActionListener(
                deleteProject -> LeftTreeAction.instance().deleteProject());
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void showTaskPopupMenu(Component invoker, int xCoordinates, int yCoordinates) {
        // 创建 弹出菜单 对象
        JPopupMenu popupMenu = new JPopupMenu();

        // 创建 一级菜单
        JMenuItem reanalyzeItem = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_restart_task"));
        JMenuItem editorTaskItem = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_modify_task"));
        JMenuItem deleteTaskMenu = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_delete_task"));
        JMenuItem startTaskMenu = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_run_task"));
        JMenuItem stopTaskMenu = new JMenuItem(TuningI18NServer.toLocale("plugins_hyper_tuner_menu_stop_task"));

        // 添加 一级菜单 到 弹出菜单
        if (getSelectTaskOwner().equals(UserInfoContext.getInstance().getUserName())
                || UserInfoContext.getInstance().getRole().equals(TuningUserManageConstant.USER_ROLE_ADMIN)) {
            popupMenu.add(deleteTaskMenu);
            boolean isSchedule = SysperfProjectServer.scheduleTaskJudge(selectTask.getTaskname());
            if (!isSchedule) {
                if ("Completed".equals(selectTask.getTaskstatus())
                        || "Failed".equals(selectTask.getTaskstatus())
                        || "Cancelled".equals(selectTask.getTaskstatus())) {
                    if (!selectProject.isImport()) {
                        popupMenu.add(reanalyzeItem);
                    }
                } else if ("Created".equals(selectTask.getTaskstatus())) {
                    popupMenu.add(editorTaskItem);
                    popupMenu.add(startTaskMenu);
                } else {
                    popupMenu.add(stopTaskMenu);
                    popupMenu.remove(deleteTaskMenu);
                }
            }
        } else {
            return;
        }

        menuListener(reanalyzeItem, editorTaskItem, deleteTaskMenu, startTaskMenu, stopTaskMenu);
        // 在指定位置显示弹出菜单
        popupMenu.show(invoker, xCoordinates, yCoordinates);
    }

    private void menuListener(JMenuItem reanalyzeItem, JMenuItem editorTaskItem, JMenuItem deleteTaskMenu,
        JMenuItem startTaskMenu, JMenuItem stopTaskMenu) {
        // 启动任务
        startTaskMenu.addActionListener(startTask -> {
            ResponseBean result = SysperfProjectServer.startTask(selectTask.getId());
            if ("SysPerf.Success".equals(result.getCode())) {
                refreshTaskContant();
                LeftTreeAction.instance().showTaskNode();
            }
        });
        // 停止任务
        stopTaskMenu.addActionListener(stopTask -> {
            ResponseBean result = SysperfProjectServer.stopTask(selectTask.getId());
            String taskName = selectTask.getTaskname();
            String message;
            if ("SysPerf.Success".equals(result.getCode())) {
                refreshTaskContant();
                LeftTreeAction.instance().showTaskNode();
                message = MessageFormat.format(SysperfContent.STOP_TASK_SUCCESS, taskName);
                IDENotificationUtil.notificationCommon(new NotificationBean(
                        SysperfContent.STOP_TASK, message, NotificationType.INFORMATION));
            } else {
                message = MessageFormat.format(SysperfContent.STOP_TASK_ERROR, taskName);
                IDENotificationUtil.notificationCommon(
                        new NotificationBean(SysperfContent.STOP_TASK, message, NotificationType.ERROR));
            }
        });
        // 重新分析
        reanalyzeItem.addActionListener(reanalyze -> {
            LeftTreeAction.instance().reanalyzeTask();
            refreshTaskContant();
        });
        // 编辑任务
        editorTaskItem.addActionListener(editorTask -> LeftTreeAction.instance().modifyTask());
        // 删除任务
        deleteTaskMenu.addActionListener(deleteTask -> LeftTreeAction.instance().deleteTask());
    }

    private String getSelectTaskOwner() {
        Iterator<String> iterator = projectMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (projectMap.get(key).getProjectName().equals(selectTask.getProjectName())) {
                return projectMap.get(key).getOwnerName();
            }
        }
        return "";
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void initProjectTree() {
        treePanel.setLayout(new BorderLayout());
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root Node");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        tuningTree = new Tree(treeModel);
        tuningTree.setCellRenderer(new LeftTreeCellRender());
        tuningTree.setExpandableItemsEnabled(true);
        tuningTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tuningTree.setShowsRootHandles(true);
        pathForRow = tuningTree.getPathForRow(0);
        List<SysperfProject> projects = SysperfProjectServer.getAllSysperfProject();
        initTuningTree(projects);
        if (projects.size() == 0) {
            Object sourceRoot = pathForRow.getLastPathComponent();
            ((DefaultMutableTreeNode) sourceRoot).remove(0);
        }
    }

    /**
     * 添加工具栏按钮
     */
    private void addActionButton() {
        // 创建工程
        AnActionButton createProject =
                AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.createProject"));
        createProject.getTemplatePresentation().setText(SysperfContent.CREATE_PROJECT);
        // 创建任务
        createTask = AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.createTask"));
        createTask.setEnabled(false);
        createTask.getTemplatePresentation().setText(SysperfContent.CREATE_TASK);
        // 节点管理
        AnActionButton node = AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.node"));
        node.getTemplatePresentation().setText(SysperfContent.NODE_MANAGER_DIC);
        // 导入导出
        AnActionButton importTask = AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.import"));
        importTask.getTemplatePresentation().setText(SysperfContent.IMPORT_TASK);

        // 导入导出
        exportTask = AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.export"));
        exportTask.setEnabled(false);
        exportTask.getTemplatePresentation().setText(SysperfContent.EXPORT_TASK);

        // 模板管理
        AnActionButton taskTemplate =
                AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.taskTemplate"));
        taskTemplate.getTemplatePresentation().setText(SysperfContent.TASK_TEMPLETE_DIC);

        // 预约任务
        AnActionButton scheduledTask =
                AnActionButton.fromAction(ActionManager.getInstance().getAction("sysperf.scheduledTask"));
        scheduledTask.getTemplatePresentation().setText(SysperfContent.SCH_TASK_DIC);

        ToolbarDecorator toolbarDecorator =
                ToolbarDecorator.createDecorator(tuningTree)
                        .initPosition()
                        .addExtraAction(createProject)
                        .addExtraAction(createTask)
                        .addExtraAction(node)
                        .addExtraAction(importTask)
                        .addExtraAction(exportTask)
                        .addExtraAction(taskTemplate)
                        .addExtraAction(scheduledTask);

        decoratorPanel = toolbarDecorator.createPanel();
    }

    private void initTuningTree(List<SysperfProject> projects) {
        Object sourceRoot = pathForRow.getLastPathComponent();
        if (sourceRoot instanceof DefaultMutableTreeNode) {
            if (projects.size() == 0) {
                DefaultMutableTreeNode newRecord = new DefaultMutableTreeNode();
                newRecord.setUserObject("");
                ((DefaultMutableTreeNode) sourceRoot).add(newRecord);
            }
            for (SysperfProject sysperfProject : projects) {
                DefaultMutableTreeNode newRecord = new DefaultMutableTreeNode();
                newRecord.setUserObject(sysperfProject.getProjectName());
                setTaskTreeNode(sysperfProject.getProjectName(), newRecord);
                ((DefaultMutableTreeNode) sourceRoot).add(newRecord);
                // 将所有项目信息放入map，用于点击左侧树，展示项目信息
                projectMap.put(sysperfProject.getProjectName(), sysperfProject);
            }
        }
        tuningTree.expandPath(tuningTree.getPathForRow(0));
        tuningTree.setRootVisible(false);
    }

    private void setTaskTreeNode(String projectName, DefaultMutableTreeNode newRecord) {
        List<Tasklist> tasks = SysperfProjectServer.getAllSysperfTasks(projectName);
        if (tasks == null || tasks.size() == 0) {
            return;
        }
        for (Tasklist task : tasks) {
            DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task.getTaskname());
            List<NodeList> nodeLists = task.getNodeList();
            for (NodeList nodeList : nodeLists) {
                DefaultMutableTreeNode nodeNickName = new DefaultMutableTreeNode(nodeList.getNodeIP());
                taskNode.add(nodeNickName);
                nodeMap.put(projectName + task.getTaskname() + nodeList.getNodeIP(), nodeList);
            }
            newRecord.add(taskNode);
            task.setProjectName(projectName);
            taskMap.put(projectName + task.getTaskname(), task);
        }
    }

    /**
     * 定时刷新左侧树
     */
    private void initTimerTask() {
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (UserInfoContext.getInstance().getUserName() == null) {
                            timer.cancel();
                        }
                        refreshProject();
                        refreshTask();
                        refreshTuningTreePanel();
                    }
                },
                0,
                1000);
    }

    private void refreshProject() {
        List<SysperfProject> projects = SysperfProjectServer.getAllSysperfProject();
        // 将新增项目插入到左侧树中
        List<SysperfProject> newAddProjects = new ArrayList<>();
        if (projects == null) {
            return;
        }
        for (SysperfProject sysperfProject : projects) {
            if (null == projectMap.get(sysperfProject.getProjectName())) {
                newAddProjects.add(sysperfProject);
            } else {
                projectMap.put(sysperfProject.getProjectName(), sysperfProject);
            }
        }
        if (newAddProjects.size() > 0) {
            initTuningTree(newAddProjects);
        }
        // 将删除项目从左侧树中移除
        List<String> removeProjectName = new ArrayList<>();
        Iterator<String> iterator = projectMap.keySet().iterator();
        while (iterator.hasNext()) {
            String projectName = iterator.next();
            if (isDeleteProject(projects, projectName)) {
                Object sourceRoot = pathForRow.getLastPathComponent();
                if (!(sourceRoot instanceof DefaultMutableTreeNode)) {
                    return;
                }
                DefaultMutableTreeNode d = ((DefaultMutableTreeNode) sourceRoot);
                Enumeration children = d.children();
                while (children.hasMoreElements()) {
                    Object nextElement = children.nextElement();
                    if (!(nextElement instanceof DefaultMutableTreeNode)) {
                        return;
                    }
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) nextElement;
                    String nodeName = child.getUserObject().toString();
                    if (projectName.equals(nodeName)) {
                        removeProjectName.add(nodeName);
                        ((DefaultMutableTreeNode) sourceRoot).remove(child);
                    }
                }
            }
        }
        for (String name : removeProjectName) {
            projectMap.remove(name);
        }
    }

    private boolean isDeleteProject(List<SysperfProject> projects, String projectName) {
        for (SysperfProject sysperfProject : projects) {
            if (projectName.equals(sysperfProject.getProjectName())) {
                return false;
            }
        }
        return true;
    }

    private void refreshTask() {
        Object sourceRoot = pathForRow.getLastPathComponent();
        if (!(sourceRoot instanceof DefaultMutableTreeNode)) {
            return;
        }
        DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) sourceRoot);
        Enumeration children = rootNode.children();
        while (children.hasMoreElements()) {
            Object nextElement = children.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                return;
            }
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) nextElement;
            boolean nodeExpande = tuningTree.isExpanded(new TreePath(child.getPath()));
            String projectName = child.getUserObject().toString();
            // 对展开的项目节点和当前选中的项目进行刷新
            if (nodeExpande || (selectProject != null && selectProject.getProjectName().equals(projectName))) {
                updateNodeForProject(child, projectName);
            }
        }
    }

    private boolean updateNodeForProject(DefaultMutableTreeNode child, String projectName) {
        List<Tasklist> tasks = SysperfProjectServer.getAllSysperfTasks(projectName);
        Enumeration taskChildrenDelete = child.children();
        // 任务节点被删除后，从左侧树中移除
        while (taskChildrenDelete.hasMoreElements()) {
            Object nextElement22 = taskChildrenDelete.nextElement();
            if (!(nextElement22 instanceof DefaultMutableTreeNode)) {
                return true;
            }
            DefaultMutableTreeNode taskChild = (DefaultMutableTreeNode) nextElement22;
            String taskName = taskChild.getUserObject().toString();
            if (isDeleteTask(tasks, taskName, taskChild, projectName)) {
                child.remove(taskChild);
            }
        }
        // 新增任务节点，添加到左侧树中
        Enumeration taskChildrenAdd = child.children();
        List<String> currentList = isAddTask(taskChildrenAdd);
        for (Tasklist tasklist : tasks) {
            List<NodeList> nodeList = tasklist.getNodeList();
            if (!currentList.contains(tasklist.getTaskname())) {
                DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(tasklist.getTaskname());
                for (NodeList node : nodeList) {
                    DefaultMutableTreeNode nodeIp = new DefaultMutableTreeNode(node.getNodeIP());
                    taskNode.add(nodeIp);
                }
                child.add(taskNode);
            }
            for (NodeList node : nodeList) {
                nodeMap.put(projectName + tasklist.getTaskname() + node.getNodeIP(), node);
            }
            taskMap.put(projectName + tasklist.getTaskname(), tasklist);
        }
        return false;
    }

    private boolean isDeleteTask(List<Tasklist> tasks, String taskName, DefaultMutableTreeNode taskChild,
        String projectName) {
        for (Tasklist tasklist : tasks) {
            if (taskName.equals(tasklist.getTaskname())) {
                List<NodeList> nodeList = tasklist.getNodeList();
                taskChild.removeAllChildren();
                for (NodeList node : nodeList) {
                    DefaultMutableTreeNode nodeIp = new DefaultMutableTreeNode(node.getNodeIP());
                    taskChild.add(nodeIp);
                    nodeMap.put(projectName + tasklist.getTaskname() + node.getNodeIP(), node);
                }
                return false;
            }
        }
        return true;
    }

    private List<String> isAddTask(Enumeration taskChildren) {
        List<String> list = new ArrayList<>();
        while (taskChildren.hasMoreElements()) {
            Object nextElement = taskChildren.nextElement();
            if (!(nextElement instanceof DefaultMutableTreeNode)) {
                continue;
            }
            DefaultMutableTreeNode taskChild = (DefaultMutableTreeNode) nextElement;
            String taskNodeName = taskChild.getUserObject().toString();
            list.add(taskNodeName);
        }
        return list;
    }

    private void refreshTuningTreePanel() {
        if (tuningTree != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                tuningTree.updateUI();
                tuningTree.validate();
                tuningTree.repaint();
            });
        }
    }

    public static SysperfProject getSelectProject() {
        return selectProject;
    }

    public static SysperfProject getNewProject() {
        return newProject;
    }

    public static Tasklist getSelectTask() {
        return selectTask;
    }

    public static Tasklist getNewTask() {
        return newTask;
    }

    public static NodeList getNewNode() {
        return newNode;
    }

    public static NodeList getSelectNode() {
        return selectNode;
    }

    public static void setSelectNode(NodeList nodeList) {
        selectNode = nodeList;
    }

    /**
     * 刷新重启，停止任务状态
     */
    private static void refreshTaskContant() {
        String projectName = selectProject.getProjectName();
        List<Tasklist> tasksList = SysperfProjectServer.getAllSysperfTasks(projectName);
        for (Tasklist task : tasksList) {
            if (selectTask.getTaskname().equals(task.getTaskname())) {
                taskMap.put(projectName + task.getTaskname(), task);
                selectTask = task;
                selectNode = task.getNodeList().get(0);
                break;
            }
        }
    }

    /**
     * 刷新新建任务状态
     *
     * @param taskName    任务名
     * @param projectName 项目
     */
    public static void refreshTaskContant(String taskName, String projectName) {
        List<Tasklist> tasksList = SysperfProjectServer.getAllSysperfTasks(projectName);
        for (Tasklist task : tasksList) {
            if (taskName.equals(task.getTaskname())) {
                taskMap.put(projectName + task.getTaskname(), task);
                for (NodeList nodeList : task.getNodeList()) {
                    nodeMap.put(projectName + task.getTaskname() + nodeList.getNodeIP(), nodeList);
                }
                newTask = task;
                newNode = task.getNodeList().get(0);
                newProject = projectMap.get(projectName);
                break;
            }
        }
    }

    /**
     * 刷新新建任务状态
     *
     * @param taskId      taskId
     * @param projectName projectName
     * @param nodeIp      nodeIp
     */
    public static void refreshTaskContant(int taskId, String projectName, String nodeIp) {
        List<Tasklist> tasksList = SysperfProjectServer.getAllSysperfTasks(projectName);
        for (Tasklist task : tasksList) {
            if (taskId == task.getId()) {
                taskMap.put(projectName + task.getTaskname(), task);
                newTask = task;
                for (NodeList node : task.getNodeList()) {
                    if (node.getNodeIP().equals(nodeIp)) {
                        newNode = node;
                    }
                }
                newProject = projectMap.get(projectName);
                break;
            }
        }
    }

    /**
     * 查看任务信息时先刷新任务状态
     *
     * @param projectName projectName
     */
    public static void refreshTaskContant(String projectName) {
        List<Tasklist> tasksList = SysperfProjectServer.getAllSysperfTasks(projectName);
        for (Tasklist task : tasksList) {
            taskMap.put(projectName + task.getTaskname(), task);
            for (NodeList node : task.getNodeList()) {
                nodeMap.put(projectName + task.getTaskname() + node.getNodeIP(), node);
            }
        }
    }

    public static Map<String, SysperfProject> getProjectMap() {
        return projectMap;
    }

    public static Map<String, Tasklist> getTaskMap() {
        return taskMap;
    }

    /**
     * 设置导出按钮状态
     *
     * @param statue statue
     */
    public static void updateExportTaskButton(boolean statue) {
        exportTask.setEnabled(statue);
    }
}
