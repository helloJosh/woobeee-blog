"use client"

import { formatDistanceToNow } from "date-fns"
import { ko } from "date-fns/locale"
import { Eye, Heart, MessageCircle, Loader2 } from "lucide-react"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import Link from "next/link"
import { useInfiniteScroll } from "@/hooks/use-infinite-scroll"
import type { Post } from "@/lib/types"
import MinimalScrollToTop from "@/components/minimal-scroll-to-top"

interface PostListProps {
    posts: Post[]
    selectedCategory: string | null
    categoryName?: string
    isSearchResult?: boolean
    searchQuery?: string
    enableInfiniteScroll?: boolean
    itemsPerPage?: number
}

export default function PostList({
                                     posts,
                                     selectedCategory,
                                     categoryName,
                                     isSearchResult,
                                     searchQuery,
                                     enableInfiniteScroll = true,
                                     itemsPerPage = 5,
                                 }: PostListProps) {
    const { displayedItems, isLoading, hasMoreItems, loadMoreRef, loadMore } = useInfiniteScroll({
        items: posts,
        itemsPerPage,
        hasMore: true,
    })

    const getTitle = () => {
        if (isSearchResult) {
            return searchQuery ? `"${searchQuery}" 검색 결과` : "검색 결과"
        }
        if (categoryName) {
            return `${categoryName} 카테고리`
        }
        return "전체 글"
    }

    const postsToShow = enableInfiniteScroll ? displayedItems : posts

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold">{getTitle()}</h1>
                <div className="flex items-center gap-2">
                    <Badge variant="secondary">
                        {enableInfiniteScroll ? `${displayedItems.length}/${posts.length}` : `총 ${posts.length}개의 글`}
                    </Badge>

                </div>
            </div>

            <div className="space-y-3">
                {postsToShow.map((post, index) => (
                    <Card key={post.id} className="cursor-pointer hover:shadow-md transition-shadow">
                        <Link href={`/blog/post/${post.id}`}>
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
                                        {/* 검색 결과일 때 미리보기 텍스트 표시 */}
                                        {isSearchResult && searchQuery && (
                                            <p className="text-sm text-muted-foreground mt-2 line-clamp-2">
                                                {post.content.substring(0, 150)}...
                                            </p>
                                        )}
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

            {/* 무한 스크롤 로딩 영역 */}
            {enableInfiniteScroll && hasMoreItems && (
                <div ref={loadMoreRef} className="flex flex-col items-center py-8 space-y-4">
                    {isLoading ? (
                        <div className="flex items-center gap-2 text-muted-foreground">
                            <Loader2 className="h-4 w-4 animate-spin" />
                            <span>더 많은 글을 불러오는 중...</span>
                        </div>
                    ) : (
                        <Button variant="outline" onClick={loadMore} className="w-full max-w-xs bg-transparent">
                            더 보기 ({posts.length - displayedItems.length}개 남음)
                        </Button>
                    )}
                </div>
            )}

            {/* TOP 버튼 - 글 목록 중간에 위치 */}
            <MinimalScrollToTop />

            {/* 모든 글을 다 보여준 경우 */}
            {enableInfiniteScroll && !hasMoreItems && displayedItems.length > 0 && (
                <div className="text-center py-8">
                    <p className="text-muted-foreground">모든 글을 확인했습니다! 🎉</p>
                </div>
            )}

            {/* 글이 없는 경우 */}
            {posts.length === 0 && (
                <div className="text-center py-12">
                    <p className="text-muted-foreground">{isSearchResult ? "검색 결과가 없습니다." : "글이 없습니다."}</p>
                </div>
            )}
        </div>
    )
}
