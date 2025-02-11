export const normalizeUrl = (inputUrl: string): string=>{
    try {
        let trimmedUrl = inputUrl.trim(); // Remove leading & trailing spaces
        let urlObj = new URL(trimmedUrl);
        urlObj.pathname = urlObj.pathname.replace(/\/+$/, ''); // Remove trailing slashes
        return urlObj.toString();
    } catch (error) {
        throw new Error('Invalid URL');
    }
}