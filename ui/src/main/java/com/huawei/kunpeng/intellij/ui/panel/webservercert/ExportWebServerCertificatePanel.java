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

package com.huawei.kunpeng.intellij.ui.panel.webservercert;

import com.huawei.kunpeng.intellij.common.log.Logger;
import com.huawei.kunpeng.intellij.common.util.I18NServer;
import com.huawei.kunpeng.intellij.common.util.ValidateUtils;
import com.huawei.kunpeng.intellij.ui.action.IDEPanelBaseAction;
import com.huawei.kunpeng.intellij.ui.panel.IDEBasePanel;

import com.intellij.openapi.ui.ValidationInfo;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * ExportWebServerCertificatePanel
 *
 * @since 2021-09-08
 */
public class ExportWebServerCertificatePanel extends IDEBasePanel {
    /**
     * mainPanel
     */
    protected JPanel mainPanel;
    /**
     * countryLabel
     */
    protected JLabel countryLabel;
    /**
     * countryTextField
     */
    protected JTextField countryTextField;
    /**
     * provinceLabel
     */
    protected JLabel provinceLabel;
    /**
     * provinceTextField
     */
    protected JTextField provinceTextField;
    /**
     * cityLabel
     */
    protected JLabel cityLabel;
    /**
     * cityTextField
     */
    protected JTextField cityTextField;
    /**
     * orgLabel
     */
    protected JLabel orgLabel;
    /**
     * orgTextField
     */
    protected JTextField orgTextField;
    /**
     * departmentLabel
     */
    protected JLabel departmentLabel;
    /**
     * departmentTextField
     */
    protected JTextField departmentTextField;
    /**
     * commonNameLabel
     */
    protected JLabel commonNameLabel;
    /**
     * commonNameTextField
     */
    protected JTextField commonNameTextField;
    /**
     * countryStarLabel
     */
    protected JLabel countryStarLabel;
    /**
     * commonNameStarLabel
     */
    protected JLabel commonNameStarLabel;
    /**
     * descriptionPanel
     */
    protected JPanel descriptionPanel;
    /**
     * iconLabel
     */
    protected JLabel iconLabel;
    /**
     * dscLabel
     */
    protected JLabel dscLabel;

    /**
     * 初始化主面板
     *
     * @param panel 面板
     */
    protected void initPanel(JPanel panel) {
        super.initPanel(mainPanel);
        this.countryLabel.setText(I18NServer.toLocale("plugins_porting_certificate_country"));
        this.provinceLabel.setText(I18NServer.toLocale("plugins_porting_certificate_province"));
        this.cityLabel.setText(I18NServer.toLocale("plugins_porting_certificate_city"));
        this.orgLabel.setText(I18NServer.toLocale("plugins_porting_certificate_organization"));
        this.departmentLabel.setText(I18NServer.toLocale("plugins_porting_certificate_department"));
        this.commonNameLabel.setText(I18NServer.toLocale("plugins_porting_certificate_commonName"));
        this.dscLabel.setText(I18NServer.toLocale("plugins_porting_certificate_desc"));
    }

    /**
     * 异常信息提示框
     *
     * @return 异常信息
     */
    public ValidationInfo doValidate() {
        ValidationInfo vi = null;
        // 国家验证规则
        String country = countryTextField.getText();
        country = Normalizer.normalize(country, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        if (ValidateUtils.isEmptyString(country)) {
            vi = new ValidationInfo(I18NServer.toLocale("plugins_porting_certificate_cannot_empty",
                    I18NServer.toLocale("plugins_porting_certificate_country")), countryTextField);
        } else if (!country.matches("^[a-zA-Z]{2}$")) {
            vi = new ValidationInfo(I18NServer.toLocale("plugins_porting_certificate_country_verification_tips"),
                    countryTextField);
        } else {
            Logger.info("The input country is validate.");
        }
        return vi;
    }

    /**
     * 异常信息集中处理
     *
     * @return 异常集合
     */
    public List<ValidationInfo> doValidateAll() {
        ValidationInfo vi = doValidate();
        List<ValidationInfo> result = new ArrayList<>();
        if (vi != null) {
            result.add(vi);
        }
        // 省验证规则
        String province = provinceTextField.getText();
        province = Normalizer.normalize(province, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        if (ValidateUtils.isNotEmptyString(province)
                && !province.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            result.add(new ValidationInfo(I18NServer.toLocale("plugins_porting_certificate_province_verification_tips",
                    province), provinceTextField));
        }
        // 市验证规则
        String city = cityTextField.getText();
        city = Normalizer.normalize(city, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        if (ValidateUtils.isNotEmptyString(city)
                && !city.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            result.add(new ValidationInfo(I18NServer.toLocale("plugins_porting_certificate_city_verification_tips",
                    city), cityTextField));
        }
        // 组织验证规则
        String org = orgTextField.getText();
        org = Normalizer.normalize(org, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        if (ValidateUtils.isNotEmptyString(org)
                && !org.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            result.add(new ValidationInfo(I18NServer.toLocale(
                    "plugins_porting_certificate_organization_verification_tips", org), orgTextField));
        }
        // 部门验证规则
        String department = departmentTextField.getText();
        department = Normalizer.normalize(department, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        if (ValidateUtils.isNotEmptyString(department)
                && !department.matches("^[\\s.-_a-zA-Z0-9]{0,128}$")) {
            result.add(new ValidationInfo(I18NServer.toLocale(
                    "plugins_porting_certificate_department_verification_tips", department), departmentTextField));
        }
        // 常用名验证规则
        String commonName = commonNameTextField.getText();
        commonName = Normalizer.normalize(commonName, Normalizer.Form.NFKC); // 校验前增加字符串标准化。
        if (ValidateUtils.isEmptyString(commonName)) {
            result.add(new ValidationInfo(I18NServer.toLocale("plugins_porting_certificate_cannot_empty",
                    I18NServer.toLocale("plugins_porting_certificate_commonName")), commonNameTextField));
        } else if (!commonName.matches("^[\\w\\s\\-.]{0,64}$")) {
            result.add(new ValidationInfo(I18NServer.toLocale(
                    "plugins_porting_certificate_commonName_verification_tips", commonName), commonNameTextField));
        } else {
            Logger.info("The input common name is validate.");
        }
        return result;
    }

    public JTextField getCountryTextField() {
        return countryTextField;
    }

    public JTextField getProvinceTextField() {
        return provinceTextField;
    }

    public JTextField getCityTextField() {
        return cityTextField;
    }

    public JTextField getOrgTextField() {
        return orgTextField;
    }

    public JTextField getDepartmentTextField() {
        return departmentTextField;
    }

    public JTextField getCommonNameTextField() {
        return commonNameTextField;
    }

    @Override
    protected void registerComponentAction() {
    }

    @Override
    protected void setAction(IDEPanelBaseAction action) {
    }
}
