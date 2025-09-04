// "use client"
//
// import { useState, useEffect, useCallback } from "react"
// import { searchAPI } from "@/lib/api"
// import type { SearchSuggestion } from "@/lib/api"
//
// interface UseSearchProps {
//     debounceMs?: number
//     minQueryLength?: number
// }
//
// export function useSearch({ debounceMs = 300, minQueryLength = 2 }: UseSearchProps = {}) {
//     const [query, setQuery] = useState("")
//     const [suggestions, setSuggestions] = useState<SearchSuggestion[]>([])
//     const [popularSearches, setPopularSearches] = useState<SearchSuggestion[]>([])
//     const [searchHistory, setSearchHistory] = useState<SearchSuggestion[]>([])
//     const [loading, setLoading] = useState(false)
//     const [error, setError] = useState<string | null>(null)
//
//     // 디바운싱된 쿼리
//     const [debouncedQuery, setDebouncedQuery] = useState("")
//
//     useEffect(() => {
//         const timer = setTimeout(() => {
//             setDebouncedQuery(query)
//         }, debounceMs)
//
//         return () => clearTimeout(timer)
//     }, [query, debounceMs])
//
//     // 검색 제안 가져오기
//     useEffect(() => {
//         if (debouncedQuery.length >= minQueryLength) {
//             fetchSuggestions(debouncedQuery)
//         } else {
//             setSuggestions([])
//         }
//     }, [debouncedQuery, minQueryLength])
//
//     // 초기 데이터 로드
//     useEffect(() => {
//         fetchPopularSearches()
//         fetchSearchHistory()
//     }, [])
//
//     const fetchSuggestions = async (searchQuery: string) => {
//         try {
//             setLoading(true)
//             setError(null)
//
//             const data = await searchAPI.getSuggestions(searchQuery)
//             setSuggestions(data)
//         } catch (err) {
//             const errorMessage = err instanceof Error ? err.message : "검색 제안을 불러오는데 실패했습니다."
//             setError(errorMessage)
//             console.error("Failed to fetch suggestions:", err)
//         } finally {
//             setLoading(false)
//         }
//     }
//
//     const fetchPopularSearches = async () => {
//         try {
//             const data = await searchAPI.getPopularSearches(10)
//             setPopularSearches(data)
//         } catch (err) {
//             console.error("Failed to fetch popular searches:", err)
//         }
//     }
//
//     const fetchSearchHistory = async () => {
//         try {
//             const data = await searchAPI.getSearchHistory(10)
//             setSearchHistory(data)
//         } catch (err) {
//             console.error("Failed to fetch search history:", err)
//         }
//     }
//
//     const saveSearch = useCallback(async (searchQuery: string) => {
//         try {
//             await searchAPI.saveSearchHistory(searchQuery)
//             // 히스토리 새로고침
//             fetchSearchHistory()
//         } catch (err) {
//             console.error("Failed to save search history:", err)
//         }
//     }, [])
//
//     const clearQuery = () => {
//         setQuery("")
//         setSuggestions([])
//         setError(null)
//     }
//
//     return {
//         query,
//         setQuery,
//         debouncedQuery,
//         suggestions,
//         popularSearches,
//         searchHistory,
//         loading,
//         error,
//         saveSearch,
//         clearQuery,
//         refresh: fetchPopularSearches,
//     }
// }
