import java.util.List;

interface IShellService {
    void exec(
        String pluginDirectory,
        String pluginEntryPoint,
        boolean enableMonitor,
        IShellCallback callback
    );
    void command(
        String commandContent,
        IShellCallback callback,
        boolean jumpToDirectory
    );
    boolean kill(int progressPid);
    boolean installShellPlugin(
        String shellPluginStagingFilePath,
        String pluginPackageName,
        String entryPoint
    );
    boolean uninstallShellPlugin(
        String pluginPackageName
    );
    boolean exportShellPlugin(
        String pluginPackageName,
        String shellPluginExportZipPath
    );
}