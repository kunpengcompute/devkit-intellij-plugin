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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.sysperf;

import com.huawei.kunpeng.hyper.tuner.action.panel.LogAction;
import com.huawei.kunpeng.hyper.tuner.action.sysperf.ImpAndExpTaskAction;
import com.huawei.kunpeng.hyper.tuner.common.SettingCommonConfigPanel;
import com.huawei.kunpeng.hyper.tuner.common.constant.TuningUserManageConstant;
import com.huawei.kunpeng.hyper.tuner.common.constant.sysperf.ImpAndExpTaskContent;
import com.huawei.kunpeng.hyper.tuner.model.sysperf.ImpAndExpTaskBean;
import com.huawei.kunpeng.hyper.tuner.toolview.renderer.sysperf.SysperfTableRenderer;
import com.huawei.kunpeng.intellij.common.UserInfoContext;
import com.huawei.kunpeng.intellij.common.action.IDETableMoveAction;
import com.huawei.kunpeng.intellij.common.util.BaseIntellijIcons;
import com.huawei.kunpeng.intellij.common.util.CommonUtil;
import com.huawei.kunpeng.intellij.ui.utils.CommonTableUtil;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * 预约任务设置面板
 *
 * @since 2012-10-12
 */
public class ImpAndExpTaskPanel extends SettingCommonConfigPanel {
    /**
     * 下载按钮图标
     */
    private static final String DOWNLOAD_PATH = "/assets/img/settings/download.svg";

    // 删除按钮
    private static final String DELETE_PATH = "/assets/img/delete.png";
    /**
     * 表格列索引-编号
     */
    private static final int ID_INDEX = 0;
    /**
     * 表格列索引-任务名称
     */
    private static final int TASK_NAME_INDEX = 1;
    /**
     * 表格列索引-工程名称
     */
    private static final int PROJECT_NAME_INDEX = 2;
    /**
     * 表格列索引-任务类型（入/出）
     */
    private static final int TYPE_INDEX = 3;
    /**
     * 表格列索引-状态索引
     */
    private static final int STATUS_INDEX = 4;
    /**
     * 表格列索引-状态详情
     */
    private static final int STATUS_DETAIL_INDEX = 5;
    /**
     * 表格列索引-文件大小
     */
    private static final int SIZE_INDEX = 6;
    /**
     * 表格列索引-开始时间
     */
    private static final int START_TIME_INDEX = 7;
    /**
     * 表格列索引-结束时间
     */
    private static final int END_TIME_INDEX = 8;
    /**
     * 表格列索引-创建用户编号
     */
    private static final int USER_ID_INDEX = 9;

    /**
     * 用户本地日志下载路径参数key。
     */
    private static final String OPERA_LOG_NAME = "/ImpAndExpTask.csv";

    private static final int BUTTON_LENGTH = 2;

    private static final long serialVersionUID = -1582031749154967508L;

    // 判断当前用户是否为管理员
    private boolean isAdmin = TuningUserManageConstant.USER_ROLE_ADMIN.equals(UserInfoContext.getInstance().getRole());
    private String loginUserId;
    private JPanel panel1;

    private JPanel mainPanel;

    private ImpAndExpTaskAction impAndExpTaskAction;

    private List<ImpAndExpTaskBean> impAndExpTaskBeans;

    private JTable impAndExpTaskTable;

    private JPanel ImpAndExpTaskPanel;

    private JPanel runPanel;

    private DefaultTableModel tableModel;

    private Project project;

    private Icon icon;

    /**
     * 表格工具栏
     */
    private ToolbarDecorator toolbarForImpAndExpTaskTable;

    private AnActionButton reTryBtn;
    /**
     * 创建表头
     */
    private Vector<String> columnNamesList;

    private Object[][] tableData;

    /**
     * 构造函数
     */
    public ImpAndExpTaskPanel() {
        project = CommonUtil.getDefaultProject();
        initPanel();
        // 初始化面板内组件事件
        registerComponentAction();
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        loginUserId = UserInfoContext.getInstance().getLoginId();
        super.initPanel(mainPanel);
        if (impAndExpTaskAction == null) {
            impAndExpTaskAction = new ImpAndExpTaskAction();
        }
        impAndExpTaskBeans = impAndExpTaskAction.getTaskList();
        impAndExpTaskTable = new JBTable();

        // 添加滚动面板到内容面板
        createOperaTableColName();

        // 创建 表格模型，指定 所有行数据 和 表头
        tableModel =
            new DefaultTableModel() {
                private static final long serialVersionUID = -2579019475997889830L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        initTable();
        addListenerToTable(); // 添加监听，禁用非当前用户 创建的导出到处任务的删除按钮
        toolbarForImpAndExpTaskTable = ToolbarDecorator.createDecorator(impAndExpTaskTable);
        addActionToToolBarImpAndExpTaskTable(toolbarForImpAndExpTaskTable, impAndExpTaskTable);
        toolbarForImpAndExpTaskTable.setPreferredSize(new Dimension(300, 300));
        ImpAndExpTaskPanel.removeAll();
        ImpAndExpTaskPanel.add(toolbarForImpAndExpTaskTable.createPanel());
    }

    /**
     * 设置和更新表格数据
     */
    public void updateTable() {
        impAndExpTaskBeans.clear();
        impAndExpTaskBeans = impAndExpTaskAction.getTaskList();
        initTable();
    }

    /**
     * 给 导入导出 表格添加工具栏,若当前用户为管理员。则额外添加删除按钮
     *
     * @param toolbarDecorator 工具栏
     * @param table            操作日志表格
     */
    private void addActionToToolBarImpAndExpTaskTable(ToolbarDecorator toolbarDecorator, JTable table) {
        reTryBtn =
            new AnActionButton() {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    reTryTask();
                }
            };
        reTryBtn.setEnabled(false);
        toolbarDecorator.addExtraAction(reTryBtn);
        toolbarDecorator.setAddActionName(ImpAndExpTaskContent.DOWNLOAD_ALL);
        toolbarDecorator.setAddAction(anActionButton -> downloadTaskItemFile(impAndExpTaskTable));
        toolbarDecorator.setAddIcon(BaseIntellijIcons.load(DOWNLOAD_PATH));
        toolbarDecorator.setRemoveActionName(ImpAndExpTaskContent.DELETE_ITEM);
        toolbarDecorator.setRemoveAction(anA -> deleteImpAndExpTask(impAndExpTaskTable));
        toolbarDecorator.setMoveUpAction(new IDETableMoveAction(table, true));
        toolbarDecorator.setMoveDownAction(new IDETableMoveAction(table, false));
    }

    /**
     * 添加表格监视事件。
     */
    private void addListenerToTable() {
        impAndExpTaskTable
            .getSelectionModel()
            .addListSelectionListener(
                impAndExp -> {
                    int row = impAndExpTaskTable.getSelectedRow();
                    final CommonActionsPanel actionsPanel = toolbarForImpAndExpTaskTable.getActionsPanel();
                    Integer userIdInt = getIntegerValueAtTable(row, USER_ID_INDEX);
                    String status = getStringValueAtTable(row, STATUS_INDEX);
                    if (!loginUserId.equals(userIdInt + "")) {
                        // 若导入导出任务不是当前用户创建，则将删除按钮禁用，置为灰色
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, false);
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, false); // 下载按钮
                        reTryBtn.setEnabled(false);
                    } else {
                        // 当前用户创建的，导入失败 可以重试
                        boolean isCheckFail = status.equals(ImpAndExpTaskContent.STATUS_IMP_UPLOAD_CHECK_FAIL);
                        reTryBtn.setEnabled(isCheckFail);

                        // 只能导出成功才能下载
                        boolean isExpSuccess = status.equals(ImpAndExpTaskContent.STATUS_EXP_SUCCESS);
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, isExpSuccess);
                    }
                    if (isAdmin) {
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.REMOVE, true);
                        // 当前用户创建的，导入失败 可以重试
                        boolean isCheckFail = status.equals(ImpAndExpTaskContent.STATUS_IMP_UPLOAD_CHECK_FAIL);
                        reTryBtn.setEnabled(isCheckFail);

                        // 只能导出成功才能下载
                        boolean isExpSuccess = status.equals(ImpAndExpTaskContent.STATUS_EXP_SUCCESS);
                        actionsPanel.setEnabled(CommonActionsPanel.Buttons.ADD, isExpSuccess);
                    }
                });
    }

    /**
     * 取消操作按钮置灰。
     */
    public void cancelGrayed() {
    }

    /**
     * 监听事件
     */
    @Override
    protected void registerComponentAction() {
        if (action == null) {
            action = new LogAction();
        }
    }

    private void reTryTask() {
        int row = impAndExpTaskTable.getSelectedRow();
        int idInt = getIntegerValueAtTable(row, ID_INDEX);
        String taskNameStr = getStringValueAtTable(row, TASK_NAME_INDEX);
        String projectNameStr = getStringValueAtTable(row, PROJECT_NAME_INDEX);
        impAndExpTaskAction.reTryTask(projectNameStr, taskNameStr, idInt);
        updateTable();
    }

    private void downloadTaskItemFile(JTable impAndExpTaskTable) {
        int row = impAndExpTaskTable.getSelectedRow();
        int idInt = getIntegerValueAtTable(row, ID_INDEX);
        int fileSectionQty = 1;
        String fileName = "";
        String fileSize = "";
        for (ImpAndExpTaskBean bean : impAndExpTaskBeans) {
            if (bean.getId() == idInt) {
                fileSectionQty = bean.getFileSectionQty();
                fileName = bean.getFileName();
                fileSize = bean.getTaskFilesize();
            }
        }
        impAndExpTaskAction.downloadTaskItemFile(idInt, fileSectionQty, fileName, fileSize);
    }

    private void deleteImpAndExpTask(JTable impAndExpTaskTable) {
        int row = impAndExpTaskTable.getSelectedRow();
        int taskIdInt = getIntegerValueAtTable(row, ID_INDEX);
        impAndExpTaskAction.deleteTaskItem(taskIdInt);
        updateTable();
    }

    /**
     * 从表中获取
     *
     * @param row row
     * @param col col
     * @return 表格大小
     */
    private Integer getIntegerValueAtTable(int row, int col) {
        Object intObj = impAndExpTaskTable.getValueAt(row, col);
        int intInt = 0;
        if (intObj instanceof String) {
            intInt = Integer.parseInt((String) intObj);
        }
        if (intObj instanceof Integer) {
            intInt = (Integer) intObj;
        }
        return intInt;
    }

    private String getStringValueAtTable(int row, int col) {
        Object strObj = impAndExpTaskTable.getValueAt(row, col);
        String strStr = "";
        if (strObj instanceof String) {
            strStr = (String) strObj;
        }
        return strStr;
    }

    /**
     * 获取对应面板
     *
     * @return mainPanel
     */
    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * 初始化 导入导出任务列表
     */
    private void initTable() {
        createOperaTableDate(impAndExpTaskBeans);
        tableModel.setDataVector(tableData, columnNamesList.toArray());
        impAndExpTaskTable.setModel(tableModel);

        // 这只表格只可以监听一行。
        impAndExpTaskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 不可以重排列。
        impAndExpTaskTable.getTableHeader().setReorderingAllowed(false);

        // 给状态列设置渲染器。
        impAndExpTaskTable.getColumnModel().getColumn(STATUS_INDEX).setCellRenderer(new SysperfTableRenderer(
            "ImpAndExpTable"));

        // 设置列表 宽度
        impAndExpTaskTable.getColumnModel().getColumn(TYPE_INDEX).setPreferredWidth(80); // 类型
        impAndExpTaskTable.getColumnModel().getColumn(STATUS_INDEX).setPreferredWidth(100); // 状态
        impAndExpTaskTable.getColumnModel().getColumn(STATUS_DETAIL_INDEX).setPreferredWidth(100); // 状态 详情
        impAndExpTaskTable.getColumnModel().getColumn(SIZE_INDEX).setPreferredWidth(50); // 文件大小
        impAndExpTaskTable.getColumnModel().getColumn(START_TIME_INDEX).setPreferredWidth(120); // 开始时间
        impAndExpTaskTable.getColumnModel().getColumn(END_TIME_INDEX).setPreferredWidth(120); // 结束时间
        CommonTableUtil.hideColumn(impAndExpTaskTable, ID_INDEX);
        CommonTableUtil.hideColumn(impAndExpTaskTable, USER_ID_INDEX);
    }

    private void createOperaTableColName() {
        // 创建表头
        columnNamesList = new Vector<>();
        columnNamesList.add(ImpAndExpTaskContent.ID);
        columnNamesList.add(ImpAndExpTaskContent.TASK_NAME);
        columnNamesList.add(ImpAndExpTaskContent.PROJECT_NAME);
        columnNamesList.add(ImpAndExpTaskContent.OPERATE_TYPE);
        columnNamesList.add(ImpAndExpTaskContent.PROCESS_STATUS);
        columnNamesList.add(ImpAndExpTaskContent.DETAIL_INFO);
        columnNamesList.add(ImpAndExpTaskContent.FILE_SIZE);
        columnNamesList.add(ImpAndExpTaskContent.START_TIME);
        columnNamesList.add(ImpAndExpTaskContent.END_TIME);
        columnNamesList.add("ownerId");
    }

    /**
     * 创建表格数据二维数组
     *
     * @param schTaskBeanList 表格所有行数据
     * @return Object[][] 二位数组
     */
    private void createOperaTableDate(List<ImpAndExpTaskBean> schTaskBeanList) {
        tableData = new Object[schTaskBeanList.size()][];
        for (int i = 0; i < schTaskBeanList.size(); i++) {
            ImpAndExpTaskBean bean = schTaskBeanList.get(i);
            List<Object> itemList = new ArrayList<>();
            itemList.add(bean.getId() + ""); // 编号
            itemList.add(bean.getTaskname()); // 任务名称
            itemList.add(bean.getProjectname()); // 工程名称
            itemList.add(bean.getOperationType()); // 任务类型
            itemList.add(bean.getProcessStatus()); // 状态
            itemList.add(bean.getDetailInfo()); // 状态详情
            itemList.add(bean.getTaskFilesize()); // 文件大小
            itemList.add(bean.getStartTime()); // 开始时间
            itemList.add(bean.getEndTime()); // 结束时间
            itemList.add(bean.getOwnerId()); // 创建者用户编号
            tableData[i] = itemList.toArray();
        }
    }
}
