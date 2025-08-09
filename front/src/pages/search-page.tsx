"use client"

import { useState, useEffect } from "react"
import { useSearchParams } from "react-router-dom"
import PostList from "../components/post-list"
import { mockPosts } from "../lib/mock-data"

export default function SearchPage() {
  const [searchParams] = useSearchParams()
  const [searchQuery, setSearchQuery] = useState(searchParams.get("q") || "")

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

  return <PostList posts={filteredPosts} selectedCategory={null} isSearchResult={true} searchQuery={searchQuery} />
}
