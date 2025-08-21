// API 기본 설정
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/api"

// 토큰 관리
export const tokenManager = {
    getToken: () => {
        if (typeof window !== "undefined") {
            return localStorage.getItem("accessToken")
        }
        return null
    },

    setToken: (token: string) => {
        if (typeof window !== "undefined") {
            localStorage.setItem("accessToken", token)
        }
    },

    removeToken: () => {
        if (typeof window !== "undefined") {
            localStorage.removeItem("accessToken")
            localStorage.removeItem("refreshToken")
        }
    },

    getRefreshToken: () => {
        if (typeof window !== "undefined") {
            return localStorage.getItem("refreshToken")
        }
        return null
    },

    setRefreshToken: (token: string) => {
        if (typeof window !== "undefined") {
            localStorage.setItem("refreshToken", token)
        }
    },
}

// API 요청 함수
export const apiRequest = async (endpoint: string, options: RequestInit = {}) => {
    const url = `${API_BASE_URL}${endpoint}`
    const token = tokenManager.getToken()

    const config: RequestInit = {
        headers: {
            "Content-Type": "application/json",
            ...(token && { Authorization: `Bearer ${token}` }),
            ...options.headers,
        },
        ...options,
    }

    try {
        const response = await fetch(url, config)

        // 토큰 만료 시 리프레시 시도
        if (response.status === 401 && token) {
            const refreshed = await refreshAccessToken()
            if (refreshed) {
                // 새 토큰으로 재시도
                config.headers = {
                    ...config.headers,
                    Authorization: `Bearer ${tokenManager.getToken()}`,
                }
                return fetch(url, config)
            } else {
                // 리프레시 실패 시 로그아웃
                tokenManager.removeToken()
                window.location.href = "/login"
                throw new Error("Authentication failed")
            }
        }

        return response
    } catch (error) {
        console.error("API request failed:", error)
        throw error
    }
}

// 토큰 리프레시
const refreshAccessToken = async (): Promise<boolean> => {
    const refreshToken = tokenManager.getRefreshToken()
    if (!refreshToken) return false

    try {
        const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ refreshToken }),
        })

        if (response.ok) {
            const data = await response.json()
            tokenManager.setToken(data.accessToken)
            if (data.refreshToken) {
                tokenManager.setRefreshToken(data.refreshToken)
            }
            return true
        }
    } catch (error) {
        console.error("Token refresh failed:", error)
    }

    return false
}

// 인증 API
export const authAPI = {
    // 로그인
    login: async (email: string, password: string) => {
        const response = await apiRequest("/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, password }),
        })

        if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || "로그인에 실패했습니다.")
        }

        return response.json()
    },

    // 회원가입
    register: async (email: string, password: string, name: string) => {
        const response = await apiRequest("/auth/register", {
            method: "POST",
            body: JSON.stringify({ email, password, name }),
        })

        if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || "회원가입에 실패했습니다.")
        }

        return response.json()
    },

    // Google OAuth 로그인
    googleLogin: async (googleToken: string) => {
        const response = await apiRequest("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({ idToken: googleToken }),
        })

        if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || "Google 로그인에 실패했습니다.")
        }

        return response.json()
    },

    // 로그아웃
    logout: async () => {
        const response = await apiRequest("/auth/logout", {
            method: "POST",
        })

        tokenManager.removeToken()
        return response.ok
    },

    // 사용자 정보 조회
    getProfile: async () => {
        const response = await apiRequest("/auth/profile")

        if (!response.ok) {
            throw new Error("사용자 정보를 가져올 수 없습니다.")
        }

        return response.json()
    },
}
