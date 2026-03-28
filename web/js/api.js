const API_BASE_URL = 'https://kindra-venulose-innoxiously.ngrok-free.dev/api';

const Api = {
    async request(method, endpoint, data = null, token = null) {
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const options = {
            method: method,
            headers: headers,
        };
        if (data) options.body = JSON.stringify(data);

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
            
            const contentType = response.headers.get("content-type");
            let result;
            if (contentType && contentType.includes("application/json")) {
                result = await response.json();
            } else {
                const text = await response.text();
                throw new Error(`Server returned non-JSON response (${response.status}): ${text.substring(0, 50)}...`);
            }

            if (!response.ok) throw new Error(result.error || result.detail || 'API request failed');
            return result;
        } catch (error) {
            console.error(`API ${method} Error:`, error);
            throw error;
        }
    },

    async get(endpoint, token = null) { return this.request('GET', endpoint, null, token); },
    async post(endpoint, data, token = null) { return this.request('POST', endpoint, data, token); },
    async put(endpoint, data, token = null) { return this.request('PUT', endpoint, data, token); },
    async patch(endpoint, data, token = null) { return this.request('PATCH', endpoint, data, token); },
    async delete(endpoint, token = null) { return this.request('DELETE', endpoint, null, token); }
};
