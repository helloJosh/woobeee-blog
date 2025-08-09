"use client"

import { useParams } from "react-router-dom"
import PostDetail from "../components/post-detail"
import { mockPosts } from "../lib/mock-data"
import NotFoundPage from "./not-found-page"

export default function PostPage() {
  const { postId } = useParams<{ postId: string }>()

  const post = mockPosts.find((p) => p.id === postId)

  if (!post) {
    return <NotFoundPage />
  }

  return <PostDetail post={post} />
}
