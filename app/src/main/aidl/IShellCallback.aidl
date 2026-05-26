oneway interface IShellCallback {
    void onExecute(String session);
    void onError(String error);
    void onProcessExited(int exitCode);
}