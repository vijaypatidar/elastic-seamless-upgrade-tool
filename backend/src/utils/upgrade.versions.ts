const possibleUpgrades: Record<string, string[]> = {
    "7.10.0": ["7.11.0", "7.12.0", "7.13.0", "7.14.0", "7.15.0", "7.16.0", "7.17.0"],
    "7.11.0": ["7.12.0", "7.13.0", "7.14.0", "7.15.0", "7.16.0", "7.17.0"],
    "7.12.0": ["7.13.0", "7.14.0", "7.15.0", "7.16.0", "7.17.0"],
    "7.13.0": ["7.14.0", "7.15.0", "7.16.0", "7.17.0"],
    "8.0.0": ["8.1.0", "8.2.0", "8.3.0"],
    "8.3.0": ["8.3.2", "8.3.5", "8.4.1"],
    "8.10.0": ["8.11.1", "8.11.3"]
  };
  
  export const getPossibleUpgrades = (version: string): string[] | null => {
    const versions = Object.keys(possibleUpgrades)
      .sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));
  
   
    const foundVersion = versions.find(v => compareVersions(v, version) >= 0);
  
    return foundVersion ? possibleUpgrades[foundVersion] : [];
  };
  
  
  const compareVersions = (a: string, b: string): number => {
    const aParts = a.split('.').map(Number);
    const bParts = b.split('.').map(Number);
  
    for (let i = 0; i < Math.max(aParts.length, bParts.length); i++) {
      const aVal = aParts[i] || 0;
      const bVal = bParts[i] || 0;
      if (aVal > bVal) return 1;
      if (aVal < bVal) return -1;
    }
    return 0;
  };
  
  