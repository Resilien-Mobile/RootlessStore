package com.baidaidai.rootless_store.data.shizuku.server

class ShizukuEndpointCallback(
    private val onExecuteCallback:(session:String?)-> Unit,
    private val onErrorCallback: (error:String?)-> Unit,
    private val onProcessExitedCallback: (exitCode: Int) -> Unit
):IShellCallback.Stub() {
    override fun onExecute(session: String?) {
        onExecuteCallback(session)
    }

    override fun onError(error: String?) {
        onErrorCallback(error)
    }

    override fun onProcessExited(exitCode: Int){
        onProcessExitedCallback(exitCode)
    }
}