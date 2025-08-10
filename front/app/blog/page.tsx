"use client"

import { useState, useEffect } from "react"
import { useSearchParams } from "next/navigation"
import PostList from "@/components/post-list"
import InfiniteScrollSettings from "@/components/infinite-scroll-settings"
import { mockPosts } from "@/lib/mock-data"

export default function BlogPage() {
  const searchParams = useSearchParams()
  const [searchQuery, setSearchQuery] = useState(searchParams.get("search") || "")
  const [enableInfiniteScroll, setEnableInfiniteScroll] = useState(true)
  const [itemsPerPage, setItemsPerPage] = useState(5)

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
      <div className="space-y-4">
        {/* 설정 버튼 */}
        {/*<div className="flex justify-end">*/}
        {/*  <InfiniteScrollSettings*/}
        {/*      enableInfiniteScroll={enableInfiniteScroll}*/}
        {/*      onToggleInfiniteScroll={setEnableInfiniteScroll}*/}
        {/*      itemsPerPage={itemsPerPage}*/}
        {/*      onItemsPerPageChange={setItemsPerPage}*/}
        {/*  />*/}
        {/*</div>*/}

        <PostList
            posts={filteredPosts}
            selectedCategory={null}
            isSearchResult={!!searchQuery}
            searchQuery={searchQuery}
            enableInfiniteScroll={enableInfiniteScroll}
            itemsPerPage={itemsPerPage}
        />
      </div>
  )
}
