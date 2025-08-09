"use client"

import { useState } from "react"
import { formatDistanceToNow } from "date-fns"
import { ko } from "date-fns/locale"
import { MessageCircle, Reply } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { Input } from "@/components/ui/input"
import type { Comment } from "@/lib/types"

interface CommentSectionProps {
  comments: Comment[]
}

export default function CommentSection({ comments: initialComments }: CommentSectionProps) {
  const [comments, setComments] = useState<Comment[]>(initialComments)
  const [newComment, setNewComment] = useState("")
  const [newAuthor, setNewAuthor] = useState("")
  const [replyingTo, setReplyingTo] = useState<string | null>(null)
  const [replyContent, setReplyContent] = useState("")
  const [replyAuthor, setReplyAuthor] = useState("")

  const handleAddComment = () => {
    if (!newComment.trim() || !newAuthor.trim()) return

    const comment: Comment = {
      id: Date.now().toString(),
      author: newAuthor,
      content: newComment,
      createdAt: new Date(),
      replies: [],
    }

    setComments([...comments, comment])
    setNewComment("")
    setNewAuthor("")
  }

  const handleAddReply = (parentId: string) => {
    if (!replyContent.trim() || !replyAuthor.trim()) return

    const reply: Comment = {
      id: Date.now().toString(),
      author: replyAuthor,
      content: replyContent,
      createdAt: new Date(),
      replies: [],
    }

    setComments(
      comments.map((comment) =>
        comment.id === parentId ? { ...comment, replies: [...comment.replies, reply] } : comment,
      ),
    )

    setReplyContent("")
    setReplyAuthor("")
    setReplyingTo(null)
  }

  const renderComment = (comment: Comment, isReply = false) => (
    <div key={comment.id} className={`${isReply ? "ml-8 mt-4" : ""}`}>
      <Card className={isReply ? "bg-muted/50" : ""}>
        <CardHeader className="pb-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="font-medium">{comment.author}</span>
              {isReply && <Reply className="h-3 w-3 text-muted-foreground" />}
            </div>
            <span className="text-xs text-muted-foreground">
              {formatDistanceToNow(comment.createdAt, {
                addSuffix: true,
                locale: ko,
              })}
            </span>
          </div>
        </CardHeader>
        <CardContent className="pt-0">
          <p className="text-sm whitespace-pre-wrap">{comment.content}</p>
          {!isReply && (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setReplyingTo(comment.id)}
              className="mt-2 h-8 px-2 text-xs"
            >
              <Reply className="h-3 w-3 mr-1" />
              답글
            </Button>
          )}
        </CardContent>
      </Card>

      {replyingTo === comment.id && (
        <Card className="ml-8 mt-2">
          <CardContent className="pt-4">
            <div className="space-y-3">
              <Input
                placeholder="익명"
                value={replyAuthor}
                onChange={(e) => setReplyAuthor(e.target.value)}
                className="h-8"
              />
              <Textarea
                placeholder="답글을 입력하세요..."
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                className="min-h-[80px] resize-none"
              />
              <div className="flex gap-2">
                <Button
                  size="sm"
                  onClick={() => handleAddReply(comment.id)}
                  disabled={!replyContent.trim() || !replyAuthor.trim()}
                >
                  답글 작성
                </Button>
                <Button variant="outline" size="sm" onClick={() => setReplyingTo(null)}>
                  취소
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {comment.replies.map((reply) => renderComment(reply, true))}
    </div>
  )

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <h3 className="font-semibold flex items-center gap-2">
            <MessageCircle className="h-5 w-5" />
            댓글 {comments.length}개
          </h3>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <Input placeholder="익명" value={newAuthor} onChange={(e) => setNewAuthor(e.target.value)} />
            <Textarea
              placeholder="댓글을 입력하세요..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              className="min-h-[100px] resize-none"
            />
            <Button onClick={handleAddComment} disabled={!newComment.trim() || !newAuthor.trim()}>
              댓글 작성
            </Button>
          </div>
        </CardContent>
      </Card>

      <div className="space-y-4">{comments.map((comment) => renderComment(comment))}</div>

      {comments.length === 0 && (
        <div className="text-center py-8">
          <p className="text-muted-foreground">첫 번째 댓글을 작성해보세요!</p>
        </div>
      )}
    </div>
  )
}
