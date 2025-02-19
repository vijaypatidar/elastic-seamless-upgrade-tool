export const taskProgressMap: Record<string, number> = {
  'Validate elasticsearch Version': 10,
  'Get elasticsearch current version': 10,
  'Ensure elasticsearch service is running': 5,
  'Wait for elasticsearch node to come back up if it was stopped': 10,
  'Check current version': 10,
  'Disable shard allocation for the cluster': 5,
  'Perform a synced flush': 10,
  'Shutdown elasticsearch node': 5,
  'Update elasticsearch': 5,
  'Wait for all shards to be reallocated': 5,
  'Start elasticsearch': 5,
  'Wait for elasticsearch node to come back up': 5,
  'Wait for elasticsearch HTTP to come back up': 5,
  'Wait for cluster health to return to yellow or green': 5,
  'Wait for the node to recover': 5,
};


export const taskProgressMapKibana:  Record<string, number> = {
  'Validate kibana Version': 5,
  'Get kibana current version': 5,
  'Pre-download kibana install package': 5,
  "Validate ELK Version": 5,
   "Update kibana": 5,
   "Restart kibana": 20,
   "Wait for kibana to start listening": 20,
   "Wait for kibana to be ready": 20,
   "Set Default Index": 10,
   "Playbook executed successfully": 5
}