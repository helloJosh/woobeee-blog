"use client"

import { useState, useEffect } from "react"
import { useSearchParams } from "next/navigation"
import { notFound } from "next/navigation"
import PostList from "@/components/post-list"
import InfiniteScrollSettings from "@/components/infinite-scroll-settings"
import { mockCategories, mockPosts } from "@/lib/mock-data"
import {categoryAPI} from "@/lib/api";

interface CategoryPageProps {
  params: {
    categoryId: string
  }
}

export default function CategoryPage({ params }: CategoryPageProps) {
  const { categoryId } = params
  //const searchParams = useSearchParams()
  //const [searchQuery, setSearchQuery] = useState(searchParams.get("search") || "")
  const [enableInfiniteScroll, setEnableInfiniteScroll] = useState(true)
  const [itemsPerPage, setItemsPerPage] = useState(5)


  // const filteredPosts = mockPosts.filter((post) => {
  //   const matchesCategory = post.categoryId === categoryId
  //   const matchesSearch =
  //       !searchQuery ||
  //       post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
  //       post.content.toLowerCase().includes(searchQuery.toLowerCase())
  //   return matchesCategory && matchesSearch
  // })

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
            posts={null}
            selectedCategory={categoryId}
            enableInfiniteScroll={enableInfiniteScroll}
            itemsPerPage={itemsPerPage}
        />
      </div>
  )
}
