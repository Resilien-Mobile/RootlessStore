package com.baidaidai.rootless_store.data.shizuku.server

class ShizukuEndpointCallback(
    private val onOutput: (output: String?) -> Unit,
    private val onErrors: (error: String?) -> Unit,
    private val onProcessExit: (exitCode: Int) -> Unit
):IShellCallback.Stub() {
    override fun onExecute(session: String?) {
        onOutput(session)
    }

    override fun onError(error: String?) {
        onErrors(error)
    }

    override fun onProcessExited(exitCode: Int){
        onProcessExit(exitCode)
    }
}
