"use client"

import { useState, useEffect } from "react"
import { ThemeProvider } from "@/components/theme-provider"
import Header from "@/components/header"
import Sidebar from "@/components/sidebar"
import PostList from "@/components/post-list"
import PostDetail from "@/components/post-detail"
import { mockCategories, mockPosts } from "@/lib/mock-data"
import type { Post } from "@/lib/types"
import { useRouter, useSearchParams } from "next/navigation"
import { redirect } from "next/navigation"

export default function BlogPage() {
  const router = useRouter()
  const searchParams = useSearchParams()

  // URL에서 초기 상태 읽기
  const [selectedCategory, setSelectedCategory] = useState<string | null>(searchParams.get("category"))
  const [selectedPost, setSelectedPost] = useState<Post | null>(null)
  const [searchQuery, setSearchQuery] = useState(searchParams.get("search") || "")
  const [sidebarOpen, setSidebarOpen] = useState(true)

  // URL에서 postId가 있으면 해당 글 찾기
  useEffect(() => {
    const postId = searchParams.get("post")
    if (postId) {
      const post = mockPosts.find((p) => p.id === postId)
      if (post) {
        setSelectedPost(post)
        setSelectedCategory(post.categoryId)
      }
    }
  }, [searchParams])

  // URL 업데이트 함수
  const updateURL = (params: { category?: string | null; post?: string | null; search?: string }) => {
    const newParams = new URLSearchParams()

    if (params.category) newParams.set("category", params.category)
    if (params.post) newParams.set("post", params.post)
    if (params.search) newParams.set("search", params.search)

    const queryString = newParams.toString()
    const newURL = queryString ? `/?${queryString}` : "/"

    router.push(newURL, { scroll: false })
  }

  const filteredPosts = mockPosts.filter((post) => {
    const matchesCategory = !selectedCategory || post.categoryId === selectedCategory
    const matchesSearch =
      !searchQuery ||
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase())
    return matchesCategory && matchesSearch
  })

  const handleCategorySelect = (categoryId: string | null) => {
    setSelectedCategory(categoryId)
    setSelectedPost(null)
    updateURL({ category: categoryId, search: searchQuery })
  }

  const handlePostSelect = (post: Post) => {
    setSelectedPost(post)
    updateURL({ category: selectedCategory, post: post.id, search: searchQuery })
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

  useEffect(() => {
    // 홈페이지 접속 시 /blog로 리다이렉트
    redirect("/blog")
  }, [router])

  return (
    <ThemeProvider attribute="class" defaultTheme="light" enableSystem>
      <div className="min-h-screen bg-background">
        <Header
          onHome={handleHome}
          searchQuery={searchQuery}
          onSearchChange={handleSearchChange}
          onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
        />

        <div className="flex">
          <Sidebar
            categories={mockCategories}
            selectedCategory={selectedCategory}
            onCategorySelect={handleCategorySelect}
            isOpen={sidebarOpen}
          />

          <main className={`flex-1 transition-all duration-300 ${sidebarOpen ? "ml-80" : "ml-0"}`}>
            <div className="p-6">
              {selectedPost ? (
                <PostDetail post={selectedPost} onBack={() => setSelectedPost(null)} />
              ) : (
                <PostList posts={filteredPosts} onPostSelect={handlePostSelect} selectedCategory={selectedCategory} />
              )}
            </div>
          </main>
        </div>
      </div>
    </ThemeProvider>
  )
}
