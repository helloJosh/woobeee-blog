"use client"

import type React from "react"

import { Search, Menu, Home, Sun, Moon, Github, Mail, LogIn } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useTheme } from "next-themes"
import { useState, useEffect } from "react"
import { useRouter, usePathname, useSearchParams } from "next/navigation"
import Link from "next/link"
import UserMenu from "@/components/auth/user-menu"
import { useAuth } from "@/hooks/use-auth" // Updated import

interface HeaderProps {
  onToggleSidebar: () => void
  /** 상위에서 관리하는 검색어(있으면 표시됨) */
  searchQuery?: string | null
  /** 상위에 검색어 변경/제출을 알리고 싶을 때 사용 */
  onSearchChange?: (q: string) => void
  /** 선택사항: 홈 버튼 눌렀을 때 */
  onHome?: () => void
}

// 디바운싱을 위한 커스텀 훅
function useDebounce(value: string, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value)

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)

    return () => {
      clearTimeout(handler)
    }
  }, [value, delay])

  return debouncedValue
}
export default function Header({
                                 onToggleSidebar,
                                 searchQuery: searchQueryProp,
                                 onSearchChange,
                                 onHome,
                               }: HeaderProps) {
  const { theme, setTheme } = useTheme()
  const [mounted, setMounted] = useState(false)
  const [searchQuery, setSearchQuery] = useState("")
  const router = useRouter()
  const pathname = usePathname()
  const searchParams = useSearchParams()
  const debouncedSearchQuery = useDebounce(searchQuery, 300)
  const { user, loading } = useAuth() // Updated usage

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    // URL에서 검색어 읽기

    // @ts-ignore
    const query = searchParams.get("q")
    // @ts-ignore
    setSearchQuery(query)
  }, [searchParams])

  useEffect(() => {
    if (typeof searchQueryProp === "string") {
      setSearchQuery(searchQueryProp)
    }
  }, [searchQueryProp])

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const q = searchQuery.trim()

    if (onSearchChange) {
      // 상위가 관리하는 방식: 상위에 위임
      console.log("[Header] 검색어 변경 감지:", q)
      onSearchChange(q)
    }
  }

  if (!mounted) {
    return null
  }

  return (
      <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="flex h-16 items-center px-4">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" onClick={onToggleSidebar} className="md:hidden">
              <Menu className="h-5 w-5" />
            </Button>

            <Button variant="ghost" asChild className="flex items-center gap-2 font-semibold">
              <Link href="/">
                <Home className="h-5 w-5" />
                <span className="hidden sm:inline">HOME</span>
              </Link>
            </Button>
          </div>

          <div className="flex-1 flex items-center justify-end gap-4">
            <form onSubmit={handleSearchSubmit} className="relative max-w-sm w-full">
              <button
                  type="submit"
                  className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground"
              >
                <Search className="w-4 h-4" />
              </button>

              <Input
                  placeholder="검색..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
              />
            </form>

            <Button variant="ghost" size="icon" onClick={() => setTheme(theme === "dark" ? "light" : "dark")}>
              {theme === "dark" ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
            </Button>

            <Button variant="ghost" size="icon" asChild>
              <a href="https://github.com/helloJosh" target="_blank" rel="noopener noreferrer">
                <Github className="h-5 w-5" />
              </a>
            </Button>

            <Button variant="ghost" size="icon" asChild>
              <a href="mailto:contact@example.com">
                <Mail className="h-5 w-5" />
              </a>
            </Button>

            {/* 로그인/사용자 메뉴 */}
            {loading ? (
                <div className="h-8 w-8 rounded-full bg-muted animate-pulse" />
            ) : user ? (
                <UserMenu />
            ) : localStorage.getItem("accessToken") ? (
                <Button variant="ghost" asChild className="flex items-center gap-2">
                  <Link href="/logout">
                    <LogIn className="h-4 w-4" />
                    <span className="hidden sm:inline">로그아웃</span>
                  </Link>
                </Button>
            ) : (
                <Button variant="ghost" asChild className="flex items-center gap-2">
                  <Link href="/login">
                    <LogIn className="h-4 w-4" />
                    <span className="hidden sm:inline">로그인</span>
                  </Link>
                </Button>
            )}
          </div>
        </div>
      </header>
  )
}
