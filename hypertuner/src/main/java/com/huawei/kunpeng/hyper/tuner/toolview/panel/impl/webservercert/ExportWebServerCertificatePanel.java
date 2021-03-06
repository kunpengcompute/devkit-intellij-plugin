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

package com.huawei.kunpeng.hyper.tuner.toolview.panel.impl.webservercert;

import com.huawei.kunpeng.hyper.tuner.action.panel.webservercert.TuningWebServerCertificateAction;
import com.huawei.kunpeng.hyper.tuner.common.i18n.TuningI18NServer;
import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.ToolWindow;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * ExportWebServerCertificatePanel
 *
 * @since 2020-10-07
 */
public class ExportWebServerCertificatePanel extends IDEBasePanel {
    private JPanel mainPanel;
    private JLabel countryLabel;
    private JTextField countryTextField;
    private JLabel provinceLabel;
    private JTextField provinceTextField;
    private JLabel cityLabel;
    private JTextField cityTextField;
    private JLabel orgLabel;
    private JTextField orgTextField;
    private JLabel departmentLabel;
    private JTextField departmentTextField;
    private JLabel commonNameLabel;
    private JTextField commonNameTextField;
    private JLabel countryStarLabel;
    private JLabel commonNameStarLabel;
    private JPanel descriptionPanel;
    private JLabel iconLabel;
    private JLabel dscLabel;

    /**
     * ??????????????????
     *
     * @param toolWindow  ????????????
     * @param panelName   ????????????
     * @param displayName ????????????
     * @param isLockable  ????????????
     */
    public ExportWebServerCertificatePanel(
            ToolWindow toolWindow, String panelName, String displayName, boolean isLockable) {
        // ??????????????????????????????
        registerComponentAction();

        // ???????????????
        initPanel(mainPanel);

        // ?????????content??????
        createContent(mainPanel, displayName, isLockable);
    }

    /**
     * ???toolWindow???displayName???????????????
     *
     * @param toolWindow  toolWindow
     * @param displayName ??????????????????
     * @param isLockable  isLockable
     */
    public ExportWebServerCertificatePanel(ToolWindow toolWindow, String displayName, boolean isLockable) {
        this(toolWindow, null, displayName, isLockable);
    }

    /**
     * ???toolWindow???????????????,????????????????????????
     *
     * @param toolWindow toolWindow
     */
    public ExportWebServerCertificatePanel(ToolWindow toolWindow) {
        this(toolWindow, null, null, false);
    }

    /**
     * ??????????????????
     *
     * @param panel ??????
     */
    @Override
    protected void initPanel(JPanel panel) {
        super.initPanel(this.mainPanel);
        this.countryLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_country"));
        this.provinceLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_province"));
        this.cityLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_city"));
        this.orgLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_organization"));
        this.departmentLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_department"));
        this.commonNameLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_commonName"));
        this.dscLabel.setText(TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_desc"));
    }

    /**
     * ??????????????????
     */
    @Override
    protected void registerComponentAction() {
        if (this.action == null) {
            this.action = new TuningWebServerCertificateAction();
        }
    }

    @Override
    public void setAction(IDEPanelBaseAction action) {
        if (action instanceof TuningWebServerCertificateAction) {
            this.action = action;
        }
        registerComponentAction();
    }

    /**
     * ?????????????????????
     *
     * @return ????????????
     */
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        // ??????????????????
        String country = countryTextField.getText();
        country = Normalizer.normalize(country, Normalizer.Form.NFKC); // ????????????????????????????????????
        if (ValidateUtils.isEmptyString(country)) {
            vi =
                    new ValidationInfo(
                            TuningI18NServer.toLocale(
                                    "plugins_hyper_tuner_certificate_cannot_empty",
                                    TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_country")),
                            countryTextField);
        } else if (!country.matches("^[a-zA-Z]{2}$")) {
            vi =
                    new ValidationInfo(
                            TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_country_verification_tips"),
                            countryTextField);
        } else {
            Logger.info("The input country is validate.");
        }
        return vi;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> validationInfos = new ArrayList<>();
        if (vi != null) {
            validationInfos.add(vi);
        }
        // ???????????????
        String province = provinceTextField.getText();
        province = Normalizer.normalize(province, Normalizer.Form.NFKC); // ????????????????????????????????????
        if (ValidateUtils.isNotEmptyString(province) && !province.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            validationInfos.add(new ValidationInfo(
                    TuningI18NServer.toLocale(
                            "plugins_hyper_tuner_certificate_province_verification_tips", province),
                    provinceTextField));
        }
        // ???????????????
        String city = cityTextField.getText();
        city = Normalizer.normalize(city, Normalizer.Form.NFKC); // ????????????????????????????????????
        if (ValidateUtils.isNotEmptyString(city) && !city.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            validationInfos.add(new ValidationInfo(
                    TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_city_verification_tips", city),
                    cityTextField));
        }
        // ??????????????????
        String org = orgTextField.getText();
        org = Normalizer.normalize(org, Normalizer.Form.NFKC); // ????????????????????????????????????
        if (ValidateUtils.isNotEmptyString(org) && !org.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            validationInfos.add(new ValidationInfo(TuningI18NServer.toLocale(
                    "plugins_hyper_tuner_certificate_organization_verification_tips", org), orgTextField));
        }
        // ??????????????????
        String department = departmentTextField.getText();
        department = Normalizer.normalize(department, Normalizer.Form.NFKC); // ????????????????????????????????????
        if (ValidateUtils.isNotEmptyString(department) && !department.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            validationInfos.add(new ValidationInfo(TuningI18NServer.toLocale(
                    "plugins_hyper_tuner_certificate_department_verification_tips", department),
                    departmentTextField));
        }
        // ?????????????????????
        String commonName = commonNameTextField.getText();
        commonName = Normalizer.normalize(commonName, Normalizer.Form.NFKC); // ????????????????????????????????????
        if (ValidateUtils.isEmptyString(commonName)) {
            validationInfos.add(new ValidationInfo(TuningI18NServer.toLocale(
                    "plugins_hyper_tuner_certificate_cannot_empty",
                    TuningI18NServer.toLocale("plugins_hyper_tuner_certificate_commonName")),
                    commonNameTextField));
        } else if (!commonName.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            validationInfos.add(new ValidationInfo(TuningI18NServer.toLocale(
                    "plugins_hyper_tuner_certificate_commonName_verification_tips", commonName),
                    commonNameTextField));
        } else {
            Logger.info("The input common name is validate.");
        }
        return validationInfos;
    }

    public JTextField getCountryTextField() {
        return countryTextField;
    }

    public JTextField getCityTextField() {
        return cityTextField;
    }

    public JTextField getProvinceTextField() {
        return provinceTextField;
    }

    public JTextField getOrgTextField() {
        return orgTextField;
    }

    public JTextField getCommonNameTextField() {
        return commonNameTextField;
    }

    public JTextField getDepartmentTextField() {
        return departmentTextField;
    }
}
