"use client"

import { useState, useEffect, useCallback, useRef } from "react"
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

// export default function GoogleAuthButton({ mode, className }: GoogleAuthButtonProps) {
//     const [isLoading, setIsLoading] = useState(false)
//     const [isScriptLoaded, setIsScriptLoaded] = useState(false)
//     const { googleLogin, googleSignIn } = useAuth()
//     const router = useRouter()
//     const buttonContainerRef = useCallback(
//         (node: HTMLDivElement) => {
//             if (node && isScriptLoaded && window.google) {
//                 // Google 버튼 렌더링
//                 window.google.accounts.id.renderButton(node, {
//                     type: "standard",
//                     theme: "outline",
//                     size: "large",
//                     text: mode === "login" ? "signin_with" : "signup_with",
//                     shape: "rectangular",
//                     logo_alignment: "left",
//                     width: node.offsetWidth,
//                 })
//             }
//         },
//         [isScriptLoaded, mode],
//     )
//
//     // Google OAuth 초기화
//     useEffect(() => {
//         // 이미 스크립트가 로드되어 있는지 확인
//         if (document.querySelector("script#google-identity-services")) {
//             setIsScriptLoaded(true)
//             return
//         }
//
//         // Google Identity Services 스크립트 로드
//         const script = document.createElement("script")
//         script.id = "google-identity-services"
//         script.src = "https://accounts.google.com/gsi/client"
//         script.async = true
//         script.defer = true
//         script.onload = () => {
//             setIsScriptLoaded(true)
//
//             // Google OAuth 초기화
//             window.google.accounts.id.initialize({
//                 client_id: GOOGLE_CLIENT_ID,
//                 callback: handleCredentialResponse,
//                 auto_select: false,
//                 cancel_on_tap_outside: true,
//             })
//         }
//
//         document.body.appendChild(script)
//
//         return () => {
//             // 컴포넌트 언마운트 시 스크립트 제거 (선택사항)
//             const existingScript = document.querySelector("script#google-identity-services")
//             if (existingScript) {
//                 // document.body.removeChild(existingScript);
//             }
//         }
//     }, [])
//
//     // Google 인증 응답 처리
//     const handleCredentialResponse = async (response: any) => {
//         try {
//             setIsLoading(true)
//
//             // Google에서 받은 ID 토큰
//             const idToken = response.credential
//             console.log("Google ID Token:", idToken)
//
//             // 백엔드로 토큰 전송하여 인증
//             if (mode === "signin") {
//                 await googleSignIn(idToken)
//             } else {
//                 await googleLogin(idToken)
//             }
//             router.replace("/")
//         } catch (error) {
//             console.error("Google auth error:", error)
//             alert("로그인 중 오류가 발생했습니다. 다시 시도해주세요.")
//         } finally {
//             setIsLoading(false)
//         }
//     }
//
//     return (
//         <>
//             {/* Google 버튼 컨테이너 */}
//             <div ref={buttonContainerRef} className={`${className} h-10 flex justify-center`}></div>
//
//         </>
//     )
// }
export default function GoogleAuthButton({ mode, className }: GoogleAuthButtonProps) {
    const containerRef = useRef<HTMLDivElement | null>(null)
    const renderedRef = useRef(false) // 버튼 중복 렌더 방지
    const [scriptReady, setScriptReady] = useState(false)
    const { googleLogin, googleSignIn } = useAuth()
    const router = useRouter()

    // 1) 스크립트 로드
    useEffect(() => {
        if (window.google?.accounts?.id) {
            setScriptReady(true)
            return
        }
        const exist = document.querySelector<HTMLScriptElement>("#google-identity-services")
        if (exist) {
            exist.addEventListener("load", () => setScriptReady(true))
            return
        }
        const s = document.createElement("script")
        s.id = "google-identity-services"
        s.src = "https://accounts.google.com/gsi/client"
        s.async = true
        s.defer = true
        s.onload = () => setScriptReady(true)
        document.body.appendChild(s)
    }, [])

    // 2) 초기화 + 버튼 렌더 (한 번만)
    useEffect(() => {
        const el = containerRef.current
        if (!scriptReady || !window.google || !el || renderedRef.current) return

        // 레이아웃이 잡히기 전에 호출되면 offsetWidth=0일 수 있으니 방지
        const w = el.offsetWidth
        try {
            window.google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: handleCredentialResponse,
                auto_select: false,
                cancel_on_tap_outside: true,
            })

            window.google.accounts.id.renderButton(el, {
                type: "standard",
                theme: "outline",
                size: "large",
                text: mode === "login" ? "signin_with" : "signup_with",
                shape: "rectangular",
                logo_alignment: "left",
                ...(w > 0 ? { width: w } : {}), // 0이면 width 옵션 생략
            })

            renderedRef.current = true
        } catch (e) {
            console.error("Google button render failed:", e)
        }
    }, [scriptReady, mode])

    const handleCredentialResponse = async (res: any) => {
        try {
            const idToken = res?.credential
            if (!idToken) throw new Error("No credential")
            if (mode === "signin") await googleSignIn(idToken)
            else await googleLogin(idToken)
            router.replace("/")
        } catch (e) {
            console.error("Google auth error:", e)
            alert("로그인 중 오류가 발생했습니다. 다시 시도해주세요.")
        }
    }

    return (
        <div
            ref={containerRef}
            className={`${className ?? ""} h-10 flex justify-center`}
            // 최소 폭을 주면 0px 이슈 줄어듭니다
            style={{ minWidth: 200 }}
        />
    )
}