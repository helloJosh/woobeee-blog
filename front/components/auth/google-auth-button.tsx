"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Loader2 } from "lucide-react"
import { useAuth } from "@/hooks/use-auth"

interface GoogleAuthButtonProps {
    mode: "signin" | "signup"
    className?: string
}

declare global {
    interface Window {
        google: any
    }
}

export default function GoogleAuthButton({ mode, className }: GoogleAuthButtonProps) {
    const [isLoading, setIsLoading] = useState(false)
    const { googleLogin } = useAuth()

    const handleGoogleAuth = async () => {
        try {
            setIsLoading(true)

            // Google OAuth 초기화 (실제 구현에서는 Google OAuth 라이브러리 사용)
            // 여기서는 시뮬레이션을 위한 더미 토큰 사용
            const mockGoogleToken = "mock-google-token-" + Date.now()

            await googleLogin(mockGoogleToken)
        } catch (error) {
            console.error("Google auth error:", error)
            alert("로그인 중 오류가 발생했습니다. 다시 시도해주세요.")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <Button onClick={handleGoogleAuth} disabled={isLoading} className={className} variant="outline" size="lg">
            {isLoading ? (
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
            ) : (
                <svg className="h-4 w-4 mr-2" viewBox="0 0 24 24">
                    <path
                        fill="currentColor"
                        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                    />
                    <path
                        fill="currentColor"
                        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                    />
                    <path
                        fill="currentColor"
                        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                    />
                    <path
                        fill="currentColor"
                        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                    />
                </svg>
            )}
            {isLoading ? "로그인 중..." : mode === "signin" ? "Google로 로그인" : "Google로 회원가입"}
        </Button>
    )
}
