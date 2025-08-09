"use client"

import { useState, useEffect } from "react"
import { useSearchParams } from "react-router-dom"
import PostList from "../components/post-list"
import { mockPosts } from "../lib/mock-data"

export default function HomePage() {
  const [searchParams] = useSearchParams()
  const [searchQuery, setSearchQuery] = useState(searchParams.get("search") || "")

  useEffect(() => {
    const search = searchParams.get("search") || ""
    setSearchQuery(search)
  }, [searchParams])

  const filteredPosts = mockPosts.filter((post) => {
    const matchesSearch =
      !searchQuery ||
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase())
    return matchesSearch
  })

  return (
    <PostList posts={filteredPosts} selectedCategory={null} isSearchResult={!!searchQuery} searchQuery={searchQuery} />
  )
}
