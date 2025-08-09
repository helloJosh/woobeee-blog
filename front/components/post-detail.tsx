"use client"

import { useState } from "react"
import { formatDistanceToNow } from "date-fns"
import { ko } from "date-fns/locale"
import { ArrowLeft, Eye, Heart, MessageCircle, Share2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import ReactMarkdown from "react-markdown"
import CommentSection from "@/components/comment-section"
import { useRouter } from "next/navigation"
import type { Post } from "@/lib/types"

interface PostDetailProps {
  post: Post
}

export default function PostDetail({ post }: PostDetailProps) {
  const router = useRouter()
  const [likes, setLikes] = useState(post.likes)
  const [isLiked, setIsLiked] = useState(false)

  const handleLike = () => {
    if (isLiked) {
      setLikes(likes - 1)
    } else {
      setLikes(likes + 1)
    }
    setIsLiked(!isLiked)
  }

  const handleBack = () => {
    router.back()
  }

  const handleShare = async () => {
    const url = `${window.location.origin}/blog/post/${post.id}`

    if (navigator.share) {
      try {
        await navigator.share({
          title: post.title,
          text: post.title,
          url: url,
        })
      } catch (err) {
        console.log("공유 취소됨")
      }
    } else {
      try {
        await navigator.clipboard.writeText(url)
        alert("링크가 클립보드에 복사되었습니다!")
      } catch (err) {
        console.error("클립보드 복사 실패:", err)
      }
    }
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <Button variant="ghost" onClick={handleBack} className="flex items-center gap-2">
        <ArrowLeft className="h-4 w-4" />
        뒤로가기
      </Button>

      <Card>
        <CardHeader>
          <div className="space-y-4">
            <Badge variant="outline">{post.category}</Badge>
            <h1 className="text-3xl font-bold">{post.title}</h1>

            <div className="flex items-center justify-between text-sm text-muted-foreground">
              <div className="flex items-center gap-4">
                <div className="flex items-center gap-1">
                  <Eye className="h-4 w-4" />
                  <span>{post.views.toLocaleString()}</span>
                </div>
                <div className="flex items-center gap-1">
                  <Heart className="h-4 w-4" />
                  <span>{likes}</span>
                </div>
                <div className="flex items-center gap-1">
                  <MessageCircle className="h-4 w-4" />
                  <span>{post.comments.length}</span>
                </div>
              </div>
              <span>
                {formatDistanceToNow(post.createdAt, {
                  addSuffix: true,
                  locale: ko,
                })}
              </span>
            </div>
          </div>
        </CardHeader>

        <CardContent>
          <div className="prose prose-gray dark:prose-invert max-w-none">
            <ReactMarkdown>{post.content}</ReactMarkdown>
          </div>

          <div className="flex items-center gap-4 mt-8 pt-6 border-t">
            <Button variant={isLiked ? "default" : "outline"} onClick={handleLike} className="flex items-center gap-2">
              <Heart className={`h-4 w-4 ${isLiked ? "fill-current" : ""}`} />
              좋아요 {likes}
            </Button>

            <Button variant="outline" onClick={handleShare} className="flex items-center gap-2 bg-transparent">
              <Share2 className="h-4 w-4" />
              공유하기
            </Button>
          </div>
        </CardContent>
      </Card>

      <CommentSection comments={post.comments} />
    </div>
  )
}
