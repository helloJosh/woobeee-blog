"use client"

import { useState, useEffect, useCallback, useRef } from "react"

interface UseInfiniteScrollProps<T> {
    items: T[]
    itemsPerPage?: number
    hasMore?: boolean
    onLoadMore?: () => void
}

export function useInfiniteScroll<T>({
                                         items,
                                         itemsPerPage = 10,
                                         hasMore = true,
                                         onLoadMore,
                                     }: UseInfiniteScrollProps<T>) {
    const [displayedItems, setDisplayedItems] = useState<T[]>([])
    const [currentPage, setCurrentPage] = useState(1)
    const [isLoading, setIsLoading] = useState(false)
    const observerRef = useRef<IntersectionObserver | null>(null)
    const loadMoreRef = useRef<HTMLDivElement | null>(null)

    // 초기 아이템 로드
    useEffect(() => {
        const initialItems = items.slice(0, itemsPerPage)
        setDisplayedItems(initialItems)
        setCurrentPage(1)
    }, [items, itemsPerPage])

    // 더 많은 아이템 로드
    const loadMore = useCallback(async () => {
        if (isLoading || !hasMore) return

        setIsLoading(true)

        // 로딩 시뮬레이션 (실제로는 API 호출)
        await new Promise((resolve) => setTimeout(resolve, 500))

        const nextPage = currentPage + 1
        const startIndex = currentPage * itemsPerPage
        const endIndex = startIndex + itemsPerPage
        const newItems = items.slice(startIndex, endIndex)

        if (newItems.length > 0) {
            setDisplayedItems((prev) => [...prev, ...newItems])
            setCurrentPage(nextPage)
        }

        if (onLoadMore) {
            onLoadMore()
        }

        setIsLoading(false)
    }, [items, currentPage, itemsPerPage, isLoading, hasMore, onLoadMore])

    // Intersection Observer 설정
    useEffect(() => {
        if (observerRef.current) {
            observerRef.current.disconnect()
        }

        observerRef.current = new IntersectionObserver(
            (entries) => {
                const target = entries[0]
                if (target.isIntersecting && hasMore && !isLoading) {
                    loadMore()
                }
            },
            {
                threshold: 0.1,
                rootMargin: "100px",
            },
        )

        if (loadMoreRef.current) {
            observerRef.current.observe(loadMoreRef.current)
        }

        return () => {
            if (observerRef.current) {
                observerRef.current.disconnect()
            }
        }
    }, [loadMore, hasMore, isLoading])

    // 더 보기 여부 계산
    const hasMoreItems = displayedItems.length < items.length

    return {
        displayedItems,
        isLoading,
        hasMoreItems,
        loadMoreRef,
        loadMore,
    }
}
