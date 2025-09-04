import type {ApiResponse, GetPostsResponse, PostsParams} from "./types"

// API 기본 설정
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8000"

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
            //localStorage.removeItem("refreshToken")
        }
    },

    // getRefreshToken: () => {
    //     if (typeof window !== "undefined") {
    //         return localStorage.getItem("refreshToken")
    //     }
    //     return null
    // },
    //
    // setRefreshToken: (token: string) => {
    //     if (typeof window !== "undefined") {
    //         localStorage.setItem("refreshToken", token)
    //     }
    // },
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
            //const refreshed = await refreshAccessToken()
            const refreshed = false
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
// const refreshAccessToken = async (): Promise<boolean> => {
//     const refreshToken = tokenManager.getRefreshToken()
//     if (!refreshToken) return false
//
//     try {
//         const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json",
//             },
//             body: JSON.stringify({ refreshToken }),
//         })
//
//         if (response.ok) {
//             const data = await response.json()
//             tokenManager.setToken(data.accessToken)
//             if (data.refreshToken) {
//                 tokenManager.setRefreshToken(data.refreshToken)
//             }
//             return true
//         }
//     } catch (error) {
//         console.error("Token refresh failed:", error)
//     }
//
//     return false
// }

// 인증 API
export const authAPI = {
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

    // Google OAuth 회원가입
    googleSignIn: async (googleToken: string) => {
        const response = await apiRequest("/api/auth/signIn", {
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
        // const response = await apiRequest("/auth/logout", {
        //     method: "POST",
        // })

        tokenManager.removeToken()
        //return response.ok
    }
}

export const categoryAPI = {
    categories: async () => {
        const response = await apiRequest("/api/back/categories", {
            method: "GET"
        })

        if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || "카테고리 조회 실패.")
        }

        const json = await response.json()
        return json.data
    }
}



export const postsAPI = {
    getPosts: async (params: PostsParams = {}): Promise<GetPostsResponse> => {
        const searchParams = new URLSearchParams()

        if (params.page !== undefined) searchParams.append("page", String(params.page))
        if (params.size !== undefined) searchParams.append("size", String(params.size))
        if (params.q && params.q.trim() !== "") {
            searchParams.append("q", params.q)
        }
        if (params.categoryId !== undefined && params.categoryId !== null) {
            searchParams.append("categoryId", String(params.categoryId))
        }

        const response = await apiRequest(`/api/back/posts?${searchParams.toString()}`)

        if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || "포스트를 가져오는데 실패했습니다.")
        }

        const apiResponse: ApiResponse<GetPostsResponse> = await response.json()

        if (!apiResponse.header.successful) {
            throw new Error(apiResponse.header.message || "포스트를 가져오는데 실패했습니다.")
        }

        return apiResponse.data
    }
}


//
// // 검색 API
// export const searchAPI = {
//     // 검색 제안 가져오기 (자동완성)
//     getSuggestions: async (query: string, limit = 10): Promise<SearchSuggestion[]> => {
//         try {
//             const response = await apiRequest(`/search/suggestions?q=${encodeURIComponent(query)}&limit=${limit}`)
//
//             if (!response.ok) {
//                 // 백엔드 API가 없는 경우 mock 데이터 반환
//                 return generateMockSuggestions(query, limit)
//             }
//
//             const apiResponse: ApiResponse<SearchSuggestion[]> = await response.json()
//
//             if (!apiResponse.success) {
//                 throw new Error(apiResponse.message || "검색 제안을 가져오는데 실패했습니다.")
//             }
//
//             return apiResponse.data
//         } catch (error) {
//             console.warn("Search suggestions API not available, using mock data:", error)
//             return generateMockSuggestions(query, limit)
//         }
//     },
//
//     // 인기 검색어 가져오기
//     getPopularSearches: async (limit = 10): Promise<SearchSuggestion[]> => {
//         try {
//             const response = await apiRequest(`/search/popular?limit=${limit}`)
//
//             if (!response.ok) {
//                 return getMockPopularSearches(limit)
//             }
//
//             const apiResponse: ApiResponse<SearchSuggestion[]> = await response.json()
//
//             if (!apiResponse.success) {
//                 throw new Error(apiResponse.message || "인기 검색어를 가져오는데 실패했습니다.")
//             }
//
//             return apiResponse.data
//         } catch (error) {
//             console.warn("Popular searches API not available, using mock data:", error)
//             return getMockPopularSearches(limit)
//         }
//     },
//
//     // 검색 히스토리 가져오기 (로그인한 사용자)
//     getSearchHistory: async (limit = 10): Promise<SearchSuggestion[]> => {
//         try {
//             const userId = userManager.getUserId()
//             if (!userId) {
//                 // 로그인하지 않은 경우 로컬 스토리지에서 가져오기
//                 return getLocalSearchHistory(limit)
//             }
//
//             const response = await apiRequest(`/search/history?limit=${limit}`)
//
//             if (!response.ok) {
//                 return getLocalSearchHistory(limit)
//             }
//
//             const apiResponse: ApiResponse<SearchSuggestion[]> = await response.json()
//
//             if (!apiResponse.success) {
//                 throw new Error(apiResponse.message || "검색 히스토리를 가져오는데 실패했습니다.")
//             }
//
//             return apiResponse.data
//         } catch (error) {
//             console.warn("Search history API not available, using local storage:", error)
//             return getLocalSearchHistory(limit)
//         }
//     },
//
//     // 검색 히스토리 저장
//     saveSearchHistory: async (query: string): Promise<void> => {
//         try {
//             const userId = userManager.getUserId()
//
//             if (userId) {
//                 // 로그인한 사용자는 서버에 저장
//                 await apiRequest("/search/history", {
//                     method: "POST",
//                     body: JSON.stringify({ query }),
//                 })
//             }
//
//             // 로컬 스토리지에도 저장 (백업용)
//             saveToLocalSearchHistory(query)
//         } catch (error) {
//             console.warn("Failed to save search history to server, saving locally:", error)
//             saveToLocalSearchHistory(query)
//         }
//     },
//
//     // 검색 히스토리 삭제
//     clearSearchHistory: async (): Promise<void> => {
//         try {
//             const userId = userManager.getUserId()
//
//             if (userId) {
//                 await apiRequest("/search/history", {
//                     method: "DELETE",
//                 })
//             }
//
//             clearLocalSearchHistory()
//         } catch (error) {
//             console.warn("Failed to clear search history on server, clearing locally:", error)
//             clearLocalSearchHistory()
//         }
//     },
// }
//
// // Mock 데이터 생성 함수들
// const generateMockSuggestions = (query: string, limit: number): SearchSuggestion[] => {
//     if (!query.trim()) return []
//
//     const mockSuggestions = [
//         `${query} 튜토리얼`,
//         `${query} 가이드`,
//         `${query} 예제`,
//         `${query} 설명`,
//         `${query} 방법`,
//         `${query} 팁`,
//         `${query} 트릭`,
//         `${query} 최신`,
//     ]
//
//     return mockSuggestions.slice(0, limit).map((suggestion, index) => ({
//         query: suggestion,
//         count: Math.floor(Math.random() * 100) + 1,
//         type: "suggestion" as const,
//     }))
// }
//
// const getMockPopularSearches = (limit: number): SearchSuggestion[] => {
//     const popularSearches = [
//         { query: "React", count: 1250 },
//         { query: "Next.js", count: 980 },
//         { query: "TypeScript", count: 856 },
//         { query: "JavaScript", count: 742 },
//         { query: "Node.js", count: 634 },
//         { query: "CSS", count: 523 },
//         { query: "HTML", count: 445 },
//         { query: "Vue.js", count: 387 },
//         { query: "Python", count: 298 },
//         { query: "Java", count: 234 },
//     ]
//
//     return popularSearches.slice(0, limit).map((item) => ({
//         ...item,
//         type: "popular" as const,
//     }))
// }
//
// // 로컬 스토리지 검색 히스토리 관리
// const SEARCH_HISTORY_KEY = "blog_search_history"
//
// const getLocalSearchHistory = (limit: number): SearchSuggestion[] => {
//     try {
//         const history = localStorage.getItem(SEARCH_HISTORY_KEY)
//         if (!history) return []
//
//         const parsed: SearchSuggestion[] = JSON.parse(history)
//         return parsed.slice(0, limit)
//     } catch (error) {
//         console.error("Failed to get local search history:", error)
//         return []
//     }
// }
//
// const saveToLocalSearchHistory = (query: string): void => {
//     try {
//         const trimmedQuery = query.trim()
//         if (!trimmedQuery) return
//
//         const existing = getLocalSearchHistory(50) // 최대 50개 유지
//
//         // 중복 제거
//         const filtered = existing.filter((item) => item.query !== trimmedQuery)
//
//         // 새 검색어를 맨 앞에 추가
//         const updated = [{ query: trimmedQuery, count: 1, type: "history" as const }, ...filtered].slice(0, 50) // 최대 50개로 제한
//
//         localStorage.setItem(SEARCH_HISTORY_KEY, JSON.stringify(updated))
//     } catch (error) {
//         console.error("Failed to save to local search history:", error)
//     }
// }
//
// const clearLocalSearchHistory = (): void => {
//     try {
//         localStorage.removeItem(SEARCH_HISTORY_KEY)
//     } catch (error) {
//         console.error("Failed to clear local search history:", error)
//     }
// }
export class categoriyAPI {
}