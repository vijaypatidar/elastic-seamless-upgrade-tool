import json
from ansible.plugins.callback import CallbackBase
import requests

# # Listen for debugger to attach
# import debugpy
# debugpy.listen(("localhost", 5678))
# print("🛠 Waiting for debugger to attach...")
# debugpy.wait_for_client()
# debugpy.breakpoint()  # optional: pause here when debugger connects


class CallbackModule(CallbackBase):
    CALLBACK_VERSION = 2.0
    CALLBACK_TYPE = 'stdout'
    CALLBACK_NAME = 'progress_tracker'

    def __init__(self):
        super(CallbackModule, self).__init__()
        self.task_current_by_host_ip = {}
        self._display.banner("🔧 Progress Tracker Initialized")

    def v2_playbook_on_start(self, playbook):
        self.playbook_run_id = playbook._entries[0]._variable_manager.extra_vars.get('playbook_run_id', None)
        self.playbook_run_type = playbook._entries[0]._variable_manager.extra_vars.get('playbook_run_type', "UPGRADE")
        self.cluster_type = playbook._entries[0]._variable_manager.extra_vars.get('cluster_type', None)
        self.task_count_per_host_group = self.get_total_task_count_per_host_group(playbook)
        self._display.banner(f"Playbook started")

   
    def v2_playbook_on_play_start(self, play):
        self._display.display(f"📋 Play '{play.get_name()}' started.")

    def v2_playbook_on_task_start(self, task, is_conditional):
        play = task.get_play()
        host_info_list = self.get_host_info_from_play(play)
        self._display.display(f"⚙️  Starting Task {task.get_name()} for hosts {host_info_list}")

    def v2_runner_on_failed(self, result, ignore_errors=False):
        self._display.display(f"Failed: {result.task_name or result._task.name} Host: {result._host.get_name()}")
        host_info = self.get_host_info_from_result(result)
        payload = {
            'host': host_info,
            'status': 'FAILED',
        }
        self.post_progress(payload)

    def v2_runner_on_unreachable(self, result):
        self._display.display(f"Unreachable:  Host: {result._host.get_name()} Task: {result.task_name}")
        host_info = self.get_host_info_from_result(result)
        payload = {
            'host': host_info,
            'status': 'FAILED',
            "error": "Host is unreachable. Please verify the SSH key and ensure the host IP is accessible from the system running the seamless upgrade tool."
        }
        self.post_progress(payload)

    def v2_runner_on_ok(self, result):
        self._display.display(f"Completed:  Host: {result._host.get_name()} Task: {result._task.name}")
        host_info = self.get_host_info_from_result(result)
        payload = {
            'host': host_info,
            'status': 'STARTED' if host_info['progress'] < 100 else 'SUCCESS',
        }
        self.post_progress(payload)

    def v2_runner_on_skipped(self, result):
        host_vars = result._host.get_vars()
        self._display.display(f"Skipped:  Host: {result._host.get_name()} Task: {result._task.name}")
        host_info = self.get_host_info_from_result(result)
        payload = {
            'host': host_info,
            'status': 'STARTED' if host_info['progress'] < 100 else 'SUCCESS',
            "skip":"true",
        }
        self.post_progress(payload)


    def v2_playbook_on_stats(self, stats):
        self._display.display(f"Playbook completed")
      

    def get_host_info_from_play(self, play):
        inventory = play.get_variable_manager()._inventory
        host_list = inventory.get_hosts(play.hosts)
        precheck_id = play.vars.get("precheck_id", None)

        host_info_list = []

        for host in host_list:
            host_vars = play.get_variable_manager().get_vars(host=host)
            ip = host_vars.get("ansible_host", host.name)
            host_info_list.append({
                    "name": host.name,
                    "ip": ip,
                    "precheckId": precheck_id
                })
            
        return host_info_list
    
    def post_progress(self, payload):
        try:
            url = "http://localhost:3000/webhook/clusters/cluster-id/update-status"
            headers = {
            'Content-Type': 'application/json'
            }
            
            payload["clusterType"] = self.cluster_type
            payload["playbookRunId"] = self.playbook_run_id
            payload["type"] = self.playbook_run_type

            data = json.dumps(payload)
            response = requests.request("POST", url, headers=headers, data=data)
            if response.status_code != 200:
                print(f"Failed to post progress: {response.status_code} - {response.text}")
        except Exception as e:
            self._display.display(f"error posting progress: {str(e)}")

    def get_total_task_count_per_host_group(self, playbook):
        task_count_per_host_group = {}
        for play in playbook.get_plays():
            for block in play.compile():
                for task in block.block:
                    if task.get_name() and 'meta' not in task.get_name().lower():
                        hosts = play.hosts
                        if isinstance(hosts, str) and hosts.startswith('{{') and hosts.endswith('}}'):
                            hosts = play.vars.get(hosts.strip('{{ }}').strip(), hosts)
                        if hosts not in task_count_per_host_group:
                            task_count_per_host_group[hosts] = 0
                        task_count_per_host_group[hosts] += 1
        return task_count_per_host_group

    def get_host_info_from_result(self, result):
        host_vars = result._host.get_vars()
        play = result._task.get_play()
        precheck_id = play.vars.get("precheck_id", None)
        ip = host_vars.get('ansible_host', 'N/A')
        total_tasks = self.get_total_task_count_by_host_group(result._host.groups);
        if result._task.run_once:
            for ip in self.task_current_by_host_ip:
                self.incr_and_current_task_by_host_ip(ip)
        elif result._task.name != 'Gathering Facts':
            self.incr_and_current_task_by_host_ip(ip)
        current_task = self.get_task_current_by_host_ip(ip)
        progress = int((current_task / total_tasks) * 100 if total_tasks else 0)
        stdout = result._result.get('stdout', '')
        stderr = result._result.get('stderr', '')
        self._display.display(f"IP: {ip}, Task Name: {result._task.name}, Total Tasks: {total_tasks}, Current Task: {current_task}")
        return {
            "name": result._host.get_name(),
            "ip": ip,
            "precheckId": precheck_id,
            "progress": progress,
            "totalTasks": total_tasks,
            "currentTask": current_task,
            "task": result._task.name,
            "logs":{
                "stdout": stdout,
                "stderr": stderr
            }
        }


    def get_total_task_count_by_host_group(self, host_groups):
        total_tasks = 0
        for group in host_groups:
            if group.name in self.task_count_per_host_group:
                total_tasks += self.task_count_per_host_group[group.name]
        return total_tasks
    
    def get_task_current_by_host_ip(self, host_ip):
        return self.task_current_by_host_ip[host_ip]
    
    def incr_and_current_task_by_host_ip(self, host_ip):
        if host_ip not in self.task_current_by_host_ip:
            self.task_current_by_host_ip[host_ip] = 1
        else:
            self.task_current_by_host_ip[host_ip] += 1
        return self.task_current_by_host_ip[host_ip]