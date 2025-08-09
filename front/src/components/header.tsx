"use client"

import type React from "react"

import { Search, Menu, Home, Sun, Moon, Github, Mail } from "lucide-react"
import { Button } from "./ui/button"
import { Input } from "./ui/input"
import { useTheme } from "next-themes"
import { useState, useEffect } from "react"
import { useNavigate, useLocation } from "react-router-dom"
import { Link } from "react-router-dom"

interface HeaderProps {
  onToggleSidebar: () => void
  sidebarWidth: number
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

export default function Header({ onToggleSidebar, sidebarWidth }: HeaderProps) {
  const { theme, setTheme } = useTheme()
  const [mounted, setMounted] = useState(false)
  const [searchQuery, setSearchQuery] = useState("")
  const navigate = useNavigate()
  const location = useLocation()
  const debouncedSearchQuery = useDebounce(searchQuery, 300)

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    // URL에서 검색어 읽기
    const params = new URLSearchParams(location.search)
    const query = params.get("q") || params.get("search") || ""
    setSearchQuery(query)
  }, [location])

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery)}`)
    } else {
      navigate("/blog")
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
            <Link to="/blog">
              <Home className="h-5 w-5" />
              <span className="hidden sm:inline">HOME</span>
            </Link>
          </Button>
        </div>

        <div className="flex-1 flex items-center justify-end gap-4">
          <form onSubmit={handleSearchSubmit} className="relative max-w-sm w-full">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
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
            <a href="https://github.com" target="_blank" rel="noopener noreferrer">
              <Github className="h-5 w-5" />
            </a>
          </Button>

          <Button variant="ghost" size="icon" asChild>
            <a href="mailto:contact@example.com">
              <Mail className="h-5 w-5" />
            </a>
          </Button>
        </div>
      </div>
    </header>
  )
}
