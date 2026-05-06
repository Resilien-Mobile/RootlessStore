import java.util.List;

interface IShellService {
    void exec(
        String pluginExecuteEntryPoint,
        String pluginPackageDirectory,
        IShellCallback callback,
        String environmentPATH,
        String environmentLDPATH,
        in List<String> environmentConfigKeyList,
        in List<String> environmentConfigValueList
    );
    boolean kill(int progressPid);
    void command(
        String commandContent,
        String environmentPATH,
        String environmentLDPATH,
        in List<String> environmentConfigKeyList,
        in List<String> environmentConfigValueList,
        IShellCallback callback,
        boolean useRunAs
    );
}