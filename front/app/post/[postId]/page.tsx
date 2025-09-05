"use client"

import { notFound } from "next/navigation"
import PostDetail from "@/components/post-detail"
interface PostPageProps {
  params: {
    postId: string
  }
}

export default function PostPage({ params }: PostPageProps) {
  const { postId } = params


  return <PostDetail post={post} />
}
