import { formatDistanceToNow } from "date-fns"
import { ko } from "date-fns/locale"
import { Eye, Heart, MessageCircle } from "lucide-react"
import { Card, CardContent, CardHeader } from "./ui/card"
import { Badge } from "./ui/badge"
import { Link } from "react-router-dom"
import type { Post } from "../lib/types"

interface PostListProps {
  posts: Post[]
  selectedCategory: string | null
  categoryName?: string
  isSearchResult?: boolean
  searchQuery?: string
}

export default function PostList({
  posts,
  selectedCategory,
  categoryName,
  isSearchResult,
  searchQuery,
}: PostListProps) {
  const getTitle = () => {
    if (isSearchResult) {
      return searchQuery ? `"${searchQuery}" 검색 결과` : "검색 결과"
    }
    if (categoryName) {
      return `${categoryName} 카테고리`
    }
    return "전체 글"
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">{getTitle()}</h1>
        <Badge variant="secondary">총 {posts.length}개의 글</Badge>
      </div>

      <div className="space-y-3">
        {posts.map((post, index) => (
          <Card key={post.id} className="cursor-pointer hover:shadow-md transition-shadow">
            <Link to={`/front/app/post/${post.id}`}>
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <span className="text-sm font-medium text-muted-foreground">#{index + 1}</span>
                      <Badge variant="outline" className="text-xs">
                        {post.category}
                      </Badge>
                    </div>
                    <h3 className="font-semibold text-lg hover:text-primary transition-colors">{post.title}</h3>
                  </div>
                </div>
              </CardHeader>

              <CardContent className="pt-0">
                <div className="flex items-center justify-between text-sm text-muted-foreground">
                  <div className="flex items-center gap-4">
                    <div className="flex items-center gap-1">
                      <Eye className="h-4 w-4" />
                      <span>{post.views.toLocaleString()}</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Heart className="h-4 w-4" />
                      <span>{post.likes}</span>
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
              </CardContent>
            </Link>
          </Card>
        ))}
      </div>

      {posts.length === 0 && (
        <div className="text-center py-12">
          <p className="text-muted-foreground">{isSearchResult ? "검색 결과가 없습니다." : "글이 없습니다."}</p>
        </div>
      )}
    </div>
  )
}
