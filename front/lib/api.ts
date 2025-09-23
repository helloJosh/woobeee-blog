"use client"

import {
    ApiResponse,
    GetCommentResponse,
    GetPostResponse,
    GetPostsResponse,
    PostCommentRequest,
    PostsParams
} from "./types"
import {getFriendlyErrorMessage} from "@/lib/errors/error-utils";

// API 기본 설정
//const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8000"
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "https://woobeee.com"

export class HttpError extends Error {
    status: number
    data: any
    constructor(status: number, message: string, data?: any) {
        super(message)
        this.status = status
        this.data = data
    }
}

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

        // 응답 자체는 왔지만 HTTP 에러인 경우
        if (!response.ok) {
            if (response.status === 401) {
                tokenManager.removeToken()
                window.location.reload()
                throw new Error("인증이 만료되었습니다. 다시 로그인해 주세요.")
            }
            let code = "unknown"
            let description = "요청에 실패했습니다."

            try {
                const errorData = await response.json()
                code = errorData?.header?.successful === false
                    ? errorData?.header?.message ?? "unknown"
                    : "unknown"
                description = getFriendlyErrorMessage(code)

                console.log(description)
                alert(description)
            } catch {
            }

            throw new Error(description)
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
        try {
            const response = await apiRequest("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ idToken: googleToken }),
            })
            return response.json()
        } catch (e) {
            if (e instanceof HttpError) {
                switch (e.status) {
                    case 400:
                        throw new Error(e.message || "잘못된 요청입니다.")
                    case 401:
                        // 토큰 만료/위조 등 → 로그인 재시도 안내
                        throw new Error(e.message || "인증이 만료되었어요. 다시 로그인 해주세요.")
                    case 403:
                        throw new Error(e.message || "권한이 없습니다.")
                    case 500:
                        throw new Error("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                    default:
                        throw new Error(e.message || "로그인에 실패했습니다.")
                }
            }
            throw new Error("네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.")
        }

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

        const response = await apiRequest(`/api/back/posts?${searchParams.toString()}`, {
            method: "GET"
        })

        if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || "포스트를 가져오는데 실패했습니다.")
        }

        const apiResponse: ApiResponse<GetPostsResponse> = await response.json()

        if (!apiResponse.header.successful) {
            throw new Error(apiResponse.header.message || "포스트를 가져오는데 실패했습니다.")
        }

        return apiResponse.data
    },

    getPost: async (postId: number): Promise<GetPostResponse> => {
        const res = await apiRequest(`/api/back/posts/${postId}`, {
            method: "GET"
        })

        if (!res.ok) {
            throw new Error("Failed to fetch post")
        }

        const data: ApiResponse<GetPostResponse> = await res.json()
        return data.data
    }
}


type Method = "POST" | "DELETE";
async function call(method: Method, postId: number, userId?: string) {
    const res = await apiRequest(`/api/back/likes/${postId}`, {
        method: method
    })
    // const res = await fetch(`/api/back/likes/${postId}`, {
    //     method,
    //     headers: {
    //         "Content-Type": "application/json",
    //         ...(userId ? { userId } : {}), // 선택 헤더
    //     },
    // });

    if (!res.ok) {
        const msg = await res.text().catch(() => "");
        throw new Error(msg || `Like API ${method} failed (${res.status})`);
    }

    if (res.headers.get("content-type")?.includes("application/json")) {
        return (await res.json()) as ApiResponse<null | void>;
    }

    const fallback: ApiResponse<null> = {
        header: { successful: true, message: "OK", resultCode: res.status },
        data: null,
    };
    return fallback;
}

export const likeAPI = {
    addLike(postId: number, userId?: string) {
        return call("POST", postId, userId);
    },

    deleteLike(postId: number, userId?: string) {
        return call("DELETE", postId, userId);
    },
}


export type GetCommentsApiResponse = ApiResponse<GetCommentResponse[]>
export const commentAPI = {
    async getAllFromPost(
        postId: number,
        userId?: string
    ): Promise<GetCommentsApiResponse> {

        const res = await apiRequest(`/api/back/comments/${postId}`, {
            method: "GET"
        })
        // const res = await fetch(`/api/back/comments/${postId}`, {
        //     method: "GET",
        //     headers: {
        //         Accept: "application/json",
        //         ...(userId ? { userId } : {}),
        //     },
        //     cache: "no-store", // 최신 댓글 보장 (필요시 제거)
        // })

        if (!res.ok) {
            const msg = await res.text().catch(() => "")
            throw new Error(msg || `Comments GET failed (${res.status})`)
        }

        return (await res.json()) as GetCommentsApiResponse
    },
    /**
     * 댓글 저장
     * - POST /api/back/comments
     * - 헤더: userId (선택)
     */
    async saveComment(
        request: PostCommentRequest,
        userId?: string
    ): Promise<ApiResponse<void>> {
        const res = await apiRequest(`/api/back/comments`, {
            method: "POST",
            body: JSON.stringify(request),
        })

        if (!res.ok) {
            const msg = await res.text().catch(() => "")
            throw new Error(msg || `Comments POST failed (${res.status})`)
        }
        return (await res.json()) as ApiResponse<void>
    },

    /**
     * 댓글 삭제
     * - DELETE /api/back/comments/{commentId}
     * - 헤더: userId (선택)
     */
    async deleteComment(
        commentId: number,
        userId?: string
    ): Promise<ApiResponse<void>> {
        const res = await apiRequest(`/api/back/comments/${commentId}`, {
            method: "DELETE"
        })

        if (!res.ok) {
            const msg = await res.text().catch(() => "")
            throw new Error(msg || `Comments DELETE failed (${res.status})`)
        }
        return (await res.json()) as ApiResponse<void>
    },
}