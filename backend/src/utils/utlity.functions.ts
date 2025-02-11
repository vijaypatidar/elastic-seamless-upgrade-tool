export const normalizeNodeUrl = (inputUrl: string): string=>{
    try {
        let trimmedUrl = inputUrl.trim(); // Remove leading & trailing spaces
        let urlObj = new URL(trimmedUrl);
        let cleanUrl = urlObj.origin + urlObj.pathname.replace(/\/+$/, '') + urlObj.search + urlObj.hash;

        return cleanUrl;
    } catch (error) {
        throw new Error('Invalid URL');
    }
}


