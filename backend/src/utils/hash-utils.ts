import crypto from "crypto";

export const generateHash = (data: any): string => {
	const jsonString = JSON.stringify(data, Object.keys(data).sort());
	return crypto.createHash("sha256").update(jsonString).digest("hex");
};
