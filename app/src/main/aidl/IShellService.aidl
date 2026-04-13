interface IShellService {
    void exec(String pluginExecuteEntryPoint, String pluginPackageDirectory, IShellCallback callback);
    boolean kill(int progressPid);
    void command(String commandContent, IShellCallback callback);
}