package de.pavloff.pycharm.plugin.ipnb;

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

/** Plugin code which simulates the {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager}
 * It creates connection to Jupyter Notebook,
 *      sends python code for executing and
 *      runs callback on output
 */
public class ConnectionManager {

    private final Map<String, IpnbConnection> kernels = new HashMap<>();

    private final Map<String, OutputCell> outputs = new HashMap<>();

    private Project openedProject;

    private static Logger logger = Logger.getInstance(ConnectionManager.class);

    public ConnectionManager(Project project) {
        openedProject = project;
    }

    public static ConnectionManager getInstance(Project project) {
        return project.getComponent(ConnectionManager.class);
    }

    /**
     * initializes connection to Jupyter Notebook
     * opens the configuration popup if Jupyter Notebook not configured
     */
    public void initConnection(VirtualFile file) {
        logger.debug("initialize connection to Jupyter Notebook..");
        if (getConfiguration() == null) {
            openConfiguration();

        } else if (!kernels.containsKey(file.getPath())) {
            IpnbUtils.runCancellableProcessUnderProgress(openedProject, () -> setupConnection(file),
                    "Connecting to Jupyter Notebook Server");
        }
        logger.debug("connection to Jupyter Notebook initialized");
    }

    /**
     * checks connection to Jupyter Notebook than
     * - reinitializing if connection closed
     * - code executing if connection alive
     */
    public void execute(VirtualFile file, String code, OutputCell outputCell) {
        initConnection(file);

        String filePath = file.getPath();

        if (kernels.containsKey(filePath)) {
            IpnbConnection conn = kernels.get(filePath);

            if (conn.isAlive()) {
                String messageId = conn.execute(code);
                logger.debug(String.format("executing code '%s'..", messageId));
                outputs.put(messageId, outputCell);

            } else {
                logger.debug("connection closed. reinitializing..");
                kernels.remove(filePath);
                initConnection(file);
            }
        } else {
            logger.debug(String.format("file '%s' not found in kernels", filePath));
        }
    }

    /**
     * Copy of the method setupConnection from
     * {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager}
     *
     * sets up the connection to Jupyter Notebook
     */
    private boolean setupConnection(VirtualFile file) {
        logger.debug("setting up connection..");
        try {
            Ref<Boolean> connectionOpened = new Ref<>(false);
            final IpnbConnectionListenerBase listener = new IpnbConnectionListenerBase() {
                @Override
                public void onOpen(@NotNull IpnbConnection c) {
                    logger.debug("connection opened");
                    connectionOpened.set(true);
                }

                @Override
                public void onOutput(@NotNull IpnbConnection c, @NotNull String m) {
                    logger.debug(String.format("code '%s' executed. output as result",
                            m));

                    if (!outputs.containsKey(m)) {
                        logger.debug(String.format("code '%s' not known", m));
                        return;
                    }

                    IpnbOutputCell out = c.getOutput();
                    if (out == null) {
                        logger.debug(String.format("code '%s' without output", m));
                        return;
                    }

                    if (out instanceof IpnbErrorOutputCell) {
                        logger.debug(String.format("code '%s' with error", m));
                        IpnbErrorOutputCell errOut = (IpnbErrorOutputCell) out;
                        outputs.get(m).onError(errOut.getEname(), errOut.getEvalue(), errOut.getText());
                        return;
                    }

                    List<String> output = out.getText();
                    if (output == null || output.size() == 0) {
                        return;
                    }

                    outputs.get(m).onOutput(output);
                    outputs.remove(m);
                }

                @Override
                public void onPayload(@Nullable String p, @NotNull String m) {
                    logger.debug(String.format("code '%s' executed. payload as result",
                            m));
                    if (!outputs.containsKey(m)) {
                        logger.debug(String.format("code '%s' not known", m));
                        return;
                    }

                    if (p == null) {
                        logger.debug(String.format("code '%s' without payload", m));
                        return;
                    }

                    outputs.get(m).onPayload(p);
                    outputs.remove(m);
                }

                @Override
                public void onFinished(@NotNull IpnbConnection c, @NotNull String m) {
                    logger.debug(String.format("code '%s' executed", m));}
            };

            Pair<String, String> conf = getConfiguration();
            if (conf == null) {
                return false;
            }

            final IpnbConnection connection = new IpnbConnectionV3(conf.getFirst(),
                    listener, conf.getSecond(), openedProject, file.getPath());

            int countAttempt = 0;
            while (!connectionOpened.get() && countAttempt < 10) {
                countAttempt += 1;
                TimeoutUtil.sleep(1000);
            }

            if (connection.isAlive()) {
                logger.debug("connection alive. adding to kernels..");
                kernels.put(file.getPath(), connection);
            }
        }
        catch (URISyntaxException e) {
            logger.warn("Jupyter Notebook connection refused: " + e.getMessage());
            return false;
        }
        catch (UnsupportedOperationException e) {
            logger.warn("Jupyter Notebook connection warning: " + e.getMessage());
        }
        catch (UnknownHostException e) {
            logger.warn("Jupyter Notebook connection error: " + e.getMessage());
            return false;
        }
        catch (IOException e) {
            logger.warn("Jupyter Notebook login failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Copy of the method hyperlinkActivated from
     * {@link org.jetbrains.plugins.ipnb.configuration.IpnbConnectionManager.IpnbRunAdapter}
     *
     * opens configuration popup
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
     *
     * finds configuration for Jupyter Notebook
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
