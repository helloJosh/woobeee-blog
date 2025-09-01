"use client"

import { useState, useEffect, createContext, useContext } from "react"
import type { ReactNode } from "react"
import { authAPI, tokenManager } from "@/lib/api"
import type { User } from "@/lib/types"

interface AuthContextType {
    user: User | null
    loading: boolean
    googleLogin: (googleToken: string) => Promise<void>
    googleSignIn: (googleToken: string) => Promise<void>
    logout: () => Promise<void>
    isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function useAuth() {
    const context = useContext(AuthContext)
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider")
    }
    return context
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const initAuth = async () => {
            const token = tokenManager.getToken()
            // if (token) {
            //     try {
            //         const userData = await authAPI.getProfile()
            //         setUser(userData)
            //     } catch (error) {
            //         console.error("Failed to get user profile:", error)
            //         tokenManager.removeToken()
            //     }
            // }
            setLoading(false)
        }

        initAuth()
    }, [])

    const googleLogin = async (googleToken: string) => {
        try {
            const response = await authAPI.googleLogin(googleToken)
            tokenManager.setToken(response.data)
            //tokenManager.setRefreshToken(response.refreshToken)
            //setUser(response.user)
        } catch (error) {
            console.error("Google login failed:", error)
            throw error
        }
    }

    const googleSignIn = async (googleToken: string) => {
        try {
            const response = await authAPI.googleSignIn(googleToken)

            tokenManager.setToken(response.data)
            //tokenManager.setRefreshToken(response.refreshToken)
            setUser(response.user)
        } catch (error) {
            console.error("Google login failed:", error)
            throw error
        }
    }

    const logout = async () => {
        try {
            await authAPI.logout()
        } catch (error) {
            console.error("Logout failed:", error)
        } finally {
            tokenManager.removeToken()
            setUser(null)
        }
    }

    const value: AuthContextType = {
        user,
        loading,
        googleLogin,
        googleSignIn,
        logout,
        isAuthenticated: !!user,
    }

    return <AuthContext.Provider value={value}>
        {children}
        </AuthContext.Provider> ;
}
