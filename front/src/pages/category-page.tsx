"use client"

import { useState, useEffect } from "react"
import { useParams, useSearchParams } from "react-router-dom"
import PostList from "../components/post-list"
import { mockCategories, mockPosts } from "../lib/mock-data"
import NotFoundPage from "./not-found-page"

export default function CategoryPage() {
  const { categoryId } = useParams<{ categoryId: string }>()
  const [searchParams] = useSearchParams()
  const [searchQuery, setSearchQuery] = useState(searchParams.get("search") || "")

  useEffect(() => {
    const search = searchParams.get("search") || ""
    setSearchQuery(search)
  }, [searchParams])

  // 카테고리 존재 확인 (재귀적으로 모든 카테고리 검색)
  const findCategory = (categories: any[], id: string): any => {
    for (const category of categories) {
      if (category.id === id) return category
      if (category.children) {
        const found = findCategory(category.children, id)
        if (found) return found
      }
    }
    return null
  }

  const currentCategory = findCategory(mockCategories, categoryId!)

  if (!currentCategory) {
    return <NotFoundPage />
  }

  const filteredPosts = mockPosts.filter((post) => {
    const matchesCategory = post.categoryId === categoryId
    const matchesSearch =
      !searchQuery ||
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase())
    return matchesCategory && matchesSearch
  })

  return <PostList posts={filteredPosts} selectedCategory={categoryId!} categoryName={currentCategory.name} />
}
