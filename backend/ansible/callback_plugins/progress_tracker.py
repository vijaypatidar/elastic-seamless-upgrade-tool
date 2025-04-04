from ansible.plugins.callback import CallbackBase

class CallbackModule(CallbackBase):
    CALLBACK_VERSION = 2.0
    CALLBACK_TYPE = 'stdout'
    CALLBACK_NAME = 'progress_tracker'

    def __init__(self):
        super(CallbackModule, self).__init__()
        self.task_total = 0
        self.task_current = 0
        self._display.banner("🔧 Progress Tracker Initialized")

    def v2_playbook_on_start(self, playbook):
        self.task_total = 0
        self.task_current = 0
        self._display.banner("🚀 Playbook started")

    def v2_playbook_on_task_start(self, task, is_conditional):
        self.task_current += 1
        self._display.display(f"⚙️  Starting Task {self.task_current}/{self.task_total or '?'}: {task.get_name()}")

    def v2_playbook_on_stats(self, stats):
        self._display.banner("✅ Playbook completed")
        self._display.display(f"🧮 Final Task Progress: {self.task_current}/{self.task_total or '?'}")

    def v2_playbook_on_play_start(self, play):
        # Count total tasks in this play
        self.task_total += len(play.tasks)
        self._display.display(f"📋 Play '{play.get_name()}' has {len(play.tasks)} tasks.")

    def v2_runner_on_ok(self, result):
        self._display.display(f"✅ Completed: {result.task_name or result._task.name}")
