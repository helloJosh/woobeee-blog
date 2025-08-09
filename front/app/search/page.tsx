"use client"

import { useState, useEffect } from "react"
import { useSearchParams } from "next/navigation"
import Header from "@/components/header"
import Sidebar from "@/components/sidebar"
import PostList from "@/components/post-list"
import { mockCategories, mockPosts } from "@/lib/mock-data"

export default function SearchPage() {
  const searchParams = useSearchParams()
  const [searchQuery, setSearchQuery] = useState(searchParams.get("q") || "")
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [sidebarWidth, setSidebarWidth] = useState(320)

  useEffect(() => {
    const query = searchParams.get("q") || ""
    setSearchQuery(query)
  }, [searchParams])

  const filteredPosts = mockPosts.filter((post) => {
    const matchesSearch =
      !searchQuery ||
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase())
    return matchesSearch
  })

  return (
    <div className="min-h-screen bg-background">
      <Header onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} sidebarWidth={sidebarWidth} />

      <div className="flex">
        <Sidebar
          categories={mockCategories}
          selectedCategory={null}
          isOpen={sidebarOpen}
          width={sidebarWidth}
          onWidthChange={setSidebarWidth}
        />

        <main
          className={`flex-1 transition-all duration-300`}
          style={{
            marginLeft: sidebarOpen ? `${sidebarWidth}px` : "0px",
          }}
        >
          <div className="p-6">
            <PostList posts={filteredPosts} selectedCategory={null} isSearchResult={true} searchQuery={searchQuery} />
          </div>
        </main>
      </div>
    </div>
  )
}
