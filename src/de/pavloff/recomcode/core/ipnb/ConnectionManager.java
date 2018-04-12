package de.pavloff.recomcode.core.ipnb;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.ExecutionManagerImpl;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.components.JBList;
import com.intellij.util.TimeoutUtil;
import com.jetbrains.python.run.PyRunConfigurationFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ipnb.IpnbUtils;
import org.jetbrains.plugins.ipnb.format.cells.output.IpnbErrorOutputCell;
import org.jetbrains.plugins.ipnb.format.cells.output.IpnbOutputCell;
import org.jetbrains.plugins.ipnb.protocol.IpnbConnection;
import org.jetbrains.plugins.ipnb.protocol.IpnbConnectionListenerBase;
import org.jetbrains.plugins.ipnb.protocol.IpnbConnectionV3;
import org.jetbrains.plugins.ipnb.run.IpnbConfigurationEditor;
import org.jetbrains.plugins.ipnb.run.IpnbRunConfiguration;
import org.jetbrains.plugins.ipnb.run.IpnbRunConfigurationType;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class ConnectionManager {
    private static final Logger LOG = Logger.getInstance(ConnectionManager.class);

    private final Map<String, IpnbConnection> kernels = new HashMap<>();
    private final Map<String, OutputCell> outputs = new HashMap<>();
    private Project openedProject;

    public ConnectionManager(Project project) {
        openedProject = project;
    }

    public static ConnectionManager getInstance(Project project) {
        return project.getComponent(ConnectionManager.class);
    }

    public void initConnection(VirtualFile file) {
        if (getConfiguration() == null) {
            openConfiguration();

        } else if (!kernels.containsKey(file.getPath())) {
            IpnbUtils.runCancellableProcessUnderProgress(openedProject, () -> setupConnection(file),
                    "Connecting to Jupyter Notebook Server");
        }
    }

    public void execute(VirtualFile file, String code, OutputCell outputCell) {
        initConnection(file);

        if (kernels.containsKey(file.getPath())) {
            IpnbConnection conn = kernels.get(file.getPath());

            if (conn.isAlive()) {
                String messageId = conn.execute(code);
                outputs.put(messageId, outputCell);

            } else {
                kernels.remove(file.getPath());
                initConnection(file);
            }
        }
    }

    /**
     * Copy of the method setupConnection from
     * {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager}
     */
    private boolean setupConnection(VirtualFile file) {
        try {
            Ref<Boolean> connectionOpened = new Ref<>(false);
            final IpnbConnectionListenerBase listener = new IpnbConnectionListenerBase() {
                @Override
                public void onOpen(@NotNull IpnbConnection c) {
                    connectionOpened.set(true);
                }

                @Override
                public void onOutput(@NotNull IpnbConnection c, @NotNull String m) {
                    if (!outputs.containsKey(m)) {
                        return;
                    }

                    IpnbOutputCell out = c.getOutput();
                    if (out == null) {
                        return;
                    }

                    if (out instanceof IpnbErrorOutputCell) {
                        IpnbErrorOutputCell errOut = (IpnbErrorOutputCell) out;
                        outputs.get(m).onError(errOut.getEname(), errOut.getEvalue(), errOut.getText());
                        return;
                    }

                    List<String> output = out.getText();
                    if (output == null || output.size() == 0) {
                        return;
                    }

                    outputs.get(m).onOutput(output);
                }

                @Override
                public void onPayload(@Nullable String p, @NotNull String m) {
                    if (!outputs.containsKey(m)) {
                        return;
                    }

                    if (p == null) {
                        return;
                    }

                    outputs.get(m).onPayload(p);
                    outputs.remove(m);
                }

                @Override
                public void onFinished(@NotNull IpnbConnection c, @NotNull String m) {
                    if (!outputs.containsKey(m)) {
                        return;
                    }

                    outputs.remove(m);
                }
            };

            Pair<String, String> conf = getConfiguration();
            if (conf == null) {
                return false;
            }

            final IpnbConnection connection = new IpnbConnectionV3(conf.getFirst(), listener, conf.getSecond(), openedProject, file.getPath());

            int countAttempt = 0;
            while (!connectionOpened.get() && countAttempt < 10) {
                countAttempt += 1;
                TimeoutUtil.sleep(1000);
            }

            if (connection.isAlive()) {
                kernels.put(file.getPath(), connection);
            }
        }
        catch (URISyntaxException e) {
            LOG.warn("Jupyter Notebook connection refused: " + e.getMessage());
            return false;
        }
        catch (UnsupportedOperationException e) {
            LOG.warn("Jupyter Notebook connection warning: " + e.getMessage());
        }
        catch (UnknownHostException e) {
            LOG.warn("Jupyter Notebook connection error: " + e.getMessage());
            return false;
        }
        catch (IOException e) {
            LOG.warn("Jupyter Notebook login failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Copy of the method hyperlinkActivated from
     * {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager.IpnbRunAdapter}
     */
    private void openConfiguration() {
        final List<RunnerAndConfigurationSettings> configurationsList =
                RunManager.getInstance(openedProject).getConfigurationSettingsList(IpnbRunConfigurationType.getInstance());

        if (configurationsList.isEmpty()) {
            final RunnerAndConfigurationSettings configurationSettings = PyRunConfigurationFactory.getInstance()
                    .createRunConfiguration(ModuleManager.getInstance(openedProject).getModules()[0],
                            IpnbRunConfigurationType.getInstance().getConfigurationFactories()[0]);
            final IpnbRunConfiguration configuration = (IpnbRunConfiguration) configurationSettings.getConfiguration();
            configuration.setHost(IpnbConfigurationEditor.DEFAULT_HOST);
            configuration.setPort(IpnbConfigurationEditor.DEFAULT_PORT);
            configurationSettings.setSingleton(true);

            ExecutionUtil.runConfiguration(configurationSettings, DefaultRunExecutor.getRunExecutorInstance());
        } else {
            if (configurationsList.size() == 1) {
                ExecutionUtil.runConfiguration(configurationsList.get(0), DefaultRunExecutor.getRunExecutorInstance());
            } else {
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

    /**
     * Copy of the methods startConnection and getUrlTokenByDescriptor from
     * {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager}
     */
    private Pair<String, String> getConfiguration() {
        Pair<String, String> conn = null;

        final List<RunContentDescriptor> descriptors = ExecutionManagerImpl.getInstance(openedProject).getRunningDescriptors(
                settings -> settings.getConfiguration() instanceof IpnbRunConfiguration);

        if (descriptors.size() == 1) {
            final RunContentDescriptor descriptor = descriptors.get(0);
            final Set<RunnerAndConfigurationSettings> configurations = ExecutionManagerImpl.getInstance(openedProject).getConfigurations(descriptor);

            for (RunnerAndConfigurationSettings configuration : configurations) {
                final RunConfiguration runConfiguration = configuration.getConfiguration();
                if (runConfiguration instanceof IpnbRunConfiguration) {
                    final String token = ((IpnbRunConfiguration) runConfiguration).getToken();
                    if (token != null) {
                        conn = Pair.create(((IpnbRunConfiguration) runConfiguration).getUrl(), token);
                    }
                }
            }
        }

        return conn;
    }
}
