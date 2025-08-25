"use client"

import { useState, useEffect, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { Loader2 } from "lucide-react"
import { useAuth } from "@/hooks/use-auth"
import { GOOGLE_CLIENT_ID } from "@/lib/constants"
import { useRouter } from "next/navigation"

interface GoogleAuthButtonProps {
    mode: "signin" | "login"
    className?: string
}

declare global {
    interface Window {
        google: {
            accounts: {
                id: {
                    initialize: (config: any) => void
                    renderButton: (element: HTMLElement, config: any) => void
                    prompt: (callback?: () => void) => void
                }
            }
        }
    }
}

export default function GoogleAuthButton({ mode, className }: GoogleAuthButtonProps) {
    const [isLoading, setIsLoading] = useState(false)
    const [isScriptLoaded, setIsScriptLoaded] = useState(false)
    const { googleLogin, googleSignIn } = useAuth()
    const router = useRouter()
    const buttonContainerRef = useCallback(
        (node: HTMLDivElement) => {
            if (node && isScriptLoaded && window.google) {
                // Google 버튼 렌더링
                window.google.accounts.id.renderButton(node, {
                    type: "standard",
                    theme: "outline",
                    size: "large",
                    text: mode === "login" ? "signin_with" : "signup_with",
                    shape: "rectangular",
                    logo_alignment: "left",
                    width: node.offsetWidth,
                })
            }
        },
        [isScriptLoaded, mode],
    )

    // Google OAuth 초기화
    useEffect(() => {
        // 이미 스크립트가 로드되어 있는지 확인
        if (document.querySelector("script#google-identity-services")) {
            setIsScriptLoaded(true)
            return
        }

        // Google Identity Services 스크립트 로드
        const script = document.createElement("script")
        script.id = "google-identity-services"
        script.src = "https://accounts.google.com/gsi/client"
        script.async = true
        script.defer = true
        script.onload = () => {
            setIsScriptLoaded(true)

            // Google OAuth 초기화
            window.google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: handleCredentialResponse,
                auto_select: false,
                cancel_on_tap_outside: true,
            })
        }

        document.body.appendChild(script)

        return () => {
            // 컴포넌트 언마운트 시 스크립트 제거 (선택사항)
            const existingScript = document.querySelector("script#google-identity-services")
            if (existingScript) {
                // document.body.removeChild(existingScript);
            }
        }
    }, [])

    // Google 인증 응답 처리
    const handleCredentialResponse = async (response: any) => {
        try {
            setIsLoading(true)

            // Google에서 받은 ID 토큰
            const idToken = response.credential
            console.log("Google ID Token:", idToken)

            // 백엔드로 토큰 전송하여 인증
            // await googleLogin(idToken)
            // 모드에 따라 로그인/회원가입 분기
            if (mode === "signin") {
                await googleSignIn(idToken)
            } else {
                await googleLogin(idToken)
            }
            router.replace("/")
        } catch (error) {
            console.error("Google auth error:", error)
            alert("로그인 중 오류가 발생했습니다. 다시 시도해주세요.")
        } finally {
            setIsLoading(false)
        }
    }
    // // 수동으로 Google 로그인 프롬프트 표시
    // const handleManualGoogleAuth = () => {
    //     if (!isScriptLoaded || !window.google) {
    //         alert("Google 로그인을 로드하는 중입니다. 잠시 후 다시 시도해주세요.")
    //         return
    //     }
    //
    //     setIsLoading(true)
    //     window.google.accounts.id.prompt((notification: any) => {
    //         if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
    //             // 프롬프트가 표시되지 않은 경우 (쿠키 문제 등)
    //             console.log("Google prompt not displayed:", notification)
    //             setIsLoading(false)
    //         }
    //     })
    // }

    return (
        <>
            {/* Google 버튼 컨테이너 */}
            <div ref={buttonContainerRef} className={`${className} h-10 flex justify-center`}></div>

        </>
    )
}
