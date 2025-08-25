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
import remarkGfm from "remark-gfm"
import remarkBreaks from "remark-breaks";

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
          <div
              className="
                max-w-none font-sans text-base leading-7 break-words
                /* 제목 공통 */
                [&_h1]:font-bold [&_h1]:leading-tight [&_h1]:mt-6 [&_h1]:mb-3 [&_h1]:text-3xl
                [&_h2]:font-bold [&_h2]:leading-tight [&_h2]:mt-6 [&_h2]:mb-3 [&_h2]:text-2xl
                [&_h3]:font-bold [&_h3]:leading-tight [&_h3]:mt-5 [&_h3]:mb-2 [&_h3]:text-xl
                [&_h4]:font-semibold [&_h4]:mt-5 [&_h4]:mb-2

                /* 문단 */
                [&_p]:my-3

                /* 리스트 */
                [&_ul]:list-disc [&_ul]:pl-4 [&_ul]:ml-6 [&_ul]:my-3
                [&_ol]:list-decimal [&_ol]:pl-4 [&_ol]:ml-6 [&_ol]:my-3
                [&_li]:my-1
                [&_li>ul]:mt-1 [&_li>ol]:mt-1

                /* 인라인 코드 */
                [&_code]:bg-gray-100 [&_code]:rounded [&_code]:px-1.5 [&_code]:py-0.5
                [&_code]:font-mono [&_code]:text-sm

                /* 코드블록 */
                [&_pre]:bg-gray-100 [&_pre]:p-4 [&_pre]:rounded-lg [&_pre]:overflow-x-auto [&_pre]:my-4
                [&_pre_code]:bg-transparent [&_pre_code]:p-0

                /* 인용문 */
                [&_blockquote]:my-4 [&_blockquote]:pl-4 [&_blockquote]:border-l-4
                [&_blockquote]:border-slate-200 [&_blockquote]:bg-slate-50 [&_blockquote]:text-slate-700

                /* 구분선 */
                [&_hr]:my-6 [&_hr]:border-0 [&_hr]:border-t [&_hr]:border-slate-200

                /* 이미지/테이블 */
                [&_img]:max-w-full [&_img]:h-auto
                [&_table]:w-full [&_table]:border-collapse [&_table]:my-4
                [&_th]:border [&_th]:border-slate-200 [&_th]:px-3 [&_th]:py-2 [&_th]:text-left
                [&_td]:border [&_td]:border-slate-200 [&_td]:px-3 [&_td]:py-2

                /* 라이트/다크 모드 색상 (전역 theme 변수 안 쓰는 순정 유틸 버전) */
                text-slate-800
                dark:text-slate-200
                dark:[&_pre]:bg-slate-900
                dark:[&_code]:bg-slate-900
                dark:[&_blockquote]:bg-slate-900 dark:[&_blockquote]:border-slate-600 dark:[&_blockquote]:text-slate-300
                dark:[&_hr]:border-slate-600
                dark:[&_th]:border-slate-600 dark:[&_td]:border-slate-600
              "
          >
            <ReactMarkdown remarkPlugins={[remarkGfm, remarkBreaks]}>{post.content}</ReactMarkdown>
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
