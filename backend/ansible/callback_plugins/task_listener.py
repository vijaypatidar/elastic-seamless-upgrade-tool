from ansible.plugins.callback import CallbackBase

class CallbackModule(CallbackBase):
    CALLBACK_VERSION = 2.0
    CALLBACK_TYPE = 'stdout'  # or 'aggregate', 'json', etc.
    CALLBACK_NAME = 'task_listener'

    def v2_runner_on_ok(self, result, **kwargs):
        task_name = result.task_name or result._task.name
        host = result._host.get_name()
        print(f"[TASK COMPLETE] Task '{task_name}' completed successfully on {host}")
