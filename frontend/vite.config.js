import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        proxy: {
            '/api': {
                target: 'http://localhost:8080/is-labs-1.0',
                changeOrigin: true,
                secure: false,
            },
            '/ws': {
                target: 'ws://localhost:8080/is-labs-1.0',
                ws: true,
                changeOrigin: true,
                secure: false,
            }
        }
    }
})