"use client"

import { notFound } from "next/navigation"
import PostDetail from "@/components/post-detail"
import { mockPosts } from "@/lib/mock-data"

interface PostPageProps {
  params: {
    postId: string
  }
}

export default function PostPage({ params }: PostPageProps) {
  const { postId } = params

  const post = mockPosts.find((p) => p.id === postId)

  if (!post) {
    notFound()
  }

  return <PostDetail post={post} />
}
