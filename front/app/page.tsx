"use client"

import { useState, useEffect } from "react"
import { ThemeProvider } from "@/components/theme-provider"
import Header from "@/components/header"
import Sidebar from "@/components/sidebar"
import PostList from "@/components/post-list"
import { useCategories } from "@/hooks/use-categories"
import type { Post } from "@/lib/types"
import { useRouter, useSearchParams } from "next/navigation"

export default function BlogPage() {
  const { categories, loading, error, refresh } = useCategories()
  const router = useRouter()
  const searchParams = useSearchParams()

  // @ts-ignore
  const [selectedCategory, setSelectedCategory] = useState<number | null>(searchParams.get("category"))
  // @ts-ignore
  const [selectedCategoryName, setSelectedCategoryName] = useState<String | null>(searchParams.get("categoryName"))
  const [selectedPost, setSelectedPost] = useState<Post | null>(null)
  // @ts-ignore
  const [searchQuery, setSearchQuery] = useState<String | null>(searchParams.get("search"))
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [sidebarWidth, setSidebarWidth] = useState(320) // ✅ 추가
  const [post,setPost] = useState()

  // URL ↔ 상태 동기화 (뒤/앞으로가기 대응)
  useEffect(() => {
    // @ts-ignore
    const cat = searchParams.get("category")
    // @ts-ignore
    const q = searchParams.get("search")
    setSelectedCategory(cat ? Number(cat) : null)
    setSearchQuery(q ?? "")
  }, [searchParams])

  // URL 업데이트 함수
  const updateURL = (params: { category?: number | null; search?: string | null}) => {
    const newParams = new URLSearchParams()

    if (params.category) newParams.set("category", String(params.category))
    if (params.search) newParams.set("search", params.search)

    const queryString = newParams.toString()
    const newURL = queryString ? `/?${queryString}` : "/"

    router.push(newURL, { scroll: false })
  }

  const handleCategorySelect = (categoryId: number | null, categoryName: String) => {
    setSelectedCategory(categoryId)
    setSelectedCategoryName(categoryName)
    // @ts-ignore
    updateURL({ category: categoryId, search: searchQuery })
  }

  const handlePostSelect = (post: Post) => {
    setSelectedPost(post)
    router.push(`/post/${post.id}`)
  }

  const handleHome = () => {
    setSelectedCategory(null)
    setSelectedPost(null)
    setSearchQuery("")
    router.push("/")
  }

  const handleSearchChange = (query: string) => {
    setSearchQuery(query)
    updateURL({ category: selectedCategory, search: query })

  }

  return (
    <ThemeProvider attribute="class" defaultTheme="light" enableSystem>
      <div className="min-h-screen bg-background">
        <Header
          onHome={handleHome}
            // @ts-ignore
          searchQuery={searchQuery}
          onSearchChange={handleSearchChange}
          onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
        />

        <div className="flex">
          <Sidebar
              categories={categories}
              isOpen={sidebarOpen}
              width={sidebarWidth}
              onWidthChange={setSidebarWidth}
              onCategorySelect={handleCategorySelect}
          />

          <main className={`flex-1 transition-all duration-300 ${sidebarOpen ? "ml-80" : "ml-0"}`}>
            <div className="p-6">
              <PostList
                  selectedCategoryId={selectedCategory ? Number(selectedCategory) : undefined}
                  selectedCategoryName={selectedCategoryName ? String(selectedCategoryName) : undefined}
                  // @ts-ignore
                  searchQuery={searchQuery || undefined}
                  onPostSelect={handlePostSelect || undefined}
              />
            </div>
          </main>
        </div>
      </div>
    </ThemeProvider>
  )
}
