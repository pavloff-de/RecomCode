package de.pavloff.recomcode.core.ipnb;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.components.JBList;
import com.jetbrains.python.run.PyRunConfigurationFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ipnb.run.IpnbConfigurationEditor;
import org.jetbrains.plugins.ipnb.run.IpnbRunConfiguration;
import org.jetbrains.plugins.ipnb.run.IpnbRunConfigurationType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Ipnb {

    /**
     * Copy of the method hyperlinkActivated from
     * {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager.IpnbRunAdapter}
     */
    public static void start(Project myProject) {
        final List<RunnerAndConfigurationSettings> configurationsList =
                RunManager.getInstance(myProject).getConfigurationSettingsList(IpnbRunConfigurationType.getInstance());

        if (configurationsList.isEmpty()) {
            final RunnerAndConfigurationSettings configurationSettings = PyRunConfigurationFactory.getInstance()
                    .createRunConfiguration(ModuleManager.getInstance(myProject).getModules()[0],
                            IpnbRunConfigurationType.getInstance().getConfigurationFactories()[0]);
            final IpnbRunConfiguration configuration = (IpnbRunConfiguration)configurationSettings.getConfiguration();
            configuration.setHost(IpnbConfigurationEditor.DEFAULT_HOST);
            configuration.setPort(IpnbConfigurationEditor.DEFAULT_PORT);
            configurationSettings.setSingleton(true);

            ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance());
        }
        else {
            if (configurationsList.size() == 1) {
                ExecutionUtil.runConfiguration(configurationsList.get(0), DefaultRunExecutor.getRunExecutorInstance());
            }
            else {
                final JList<RunnerAndConfigurationSettings> list = new JBList<>(configurationsList);
                list.setCellRenderer(new ColoredListCellRenderer<RunnerAndConfigurationSettings>() {
                    @Override
                    protected void customizeCellRenderer(@NotNull JList<? extends RunnerAndConfigurationSettings> list,
                                                         RunnerAndConfigurationSettings value, int index,
                                                         boolean selected, boolean hasFocus) {
                        append(value.getName());
                    }
                });
                final PopupChooserBuilder builder = new PopupChooserBuilder(list);
                builder.setTitle("Choose Jupyter Notebook Server");
                builder.setItemChoosenCallback(() -> {
                    final RunnerAndConfigurationSettings configuration = list.getSelectedValue();
                    ExecutionUtil.runConfiguration(configuration, DefaultRunExecutor.getRunExecutorInstance());
                });
                final JBPopup popup = builder.createPopup();
                final PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                if (pointerInfo == null) return;
                final Point point = pointerInfo.getLocation();
                popup.showInScreenCoordinates(WindowManagerEx.getInstanceEx().getMostRecentFocusedWindow(), point);
            }
        }
    }
}
